package fr.leflodu62.textflowserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.tuple.Pair;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketRegistry;
import fr.leflodu62.textflowserver.packets.connection.DisconnectionPacket;
import fr.leflodu62.textflowserver.packets.connection.ForcedCloseConnectionPacket;
import fr.leflodu62.textflowserver.packets.connection.RefreshTokenPacket;
import fr.leflodu62.textflowserver.packets.message.MessageNotSentPacket;
import fr.leflodu62.textflowserver.packets.message.MessagePacket;
import fr.leflodu62.textflowserver.secure.AESHelper;
import fr.leflodu62.textflowserver.secure.Token;

public class ClientConnection implements Runnable {

	private final TextFlowServer instance;

	private final Socket client;

	private final ConcurrentLinkedQueue<Pair<Packet, Runnable>> data;

	private State state = State.NONE;
	private String verifyToken;

	private boolean encryption;
	private boolean tokenSent;

	private SecretKey secretKey;

	private String username;

	public ClientConnection(TextFlowServer instance, Socket client) {
		this.instance = instance;
		this.client = client;

		this.data = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void run() {
		try {
			final OutputStream out = client.getOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(out);

			final InputStream in = client.getInputStream();
			final ObjectInputStream ois = new ObjectInputStream(in);

			while (!client.isClosed()) {
				for (byte i = 0; i < 8; i++) {
					final Pair<Packet, Runnable> bundle = data.poll();

					if (!client.isClosed() && bundle != null) {
						final Packet packetToSend = bundle.getLeft();
						final int id = PacketRegistry.getId(packetToSend.getClass());
						try {
							out.write(id);
							out.flush();

							final Object[] dataToSend = packetToSend.getData();
							if (null != dataToSend) {
								if (encryption) {
									final SealedObject data = AESHelper.encrypt(dataToSend, secretKey);
									oos.writeObject(data);
								} else {
									oos.writeObject(dataToSend);
								}
							}
							oos.flush();
							if(bundle.getRight() != null) {
								bundle.getRight().run();
							}	
						} catch (final Exception e) {
							TextFlowServer.LOGGER.log(Level.INFO, "Une erreur est survenue lors de l'envoie d'un packet: " + String.format("%#x", id));
						}
					}

					if (!client.isClosed() && in.available() > 0) {
						int id = -1;
						try {
							id = in.read();
							Object[] data = null;
							ois.skip(Integer.BYTES);
							if(in.available() > 0 && !PacketRegistry.getPacket(id).equals(DisconnectionPacket.class)) {
								if (encryption) {
									data = (Object[]) AESHelper.decrypt((SealedObject)ois.readObject(), secretKey);
									if(tokenSent) {
										final String token = (String) data[0];
										final boolean valid = Token.isValid(token);
										if(!valid || Token.isExpired(token)) {
											disconnect(valid ? "Token expiré." : "Token invalide.", true);
											continue;
										}
										sendPacket(new RefreshTokenPacket(token, Token.renewToken(token, Token.DEFAULT_EXPIRATION)));
										final Object[] oldData = data;
										data = new Object[data.length-1];
										System.arraycopy(oldData, 1, data, 0, data.length);
									}
								} else {
									final Object obj = ois.readObject();
									data = (Object[]) obj;
									
								}
							}
							PacketRegistry.processPacket(this, id, data);
						} catch (final Exception e) {
							TextFlowServer.LOGGER.log(Level.INFO, "Une erreur est survenue lors de la reception d'un packet: " + String.format("%#x", id));
							if(!client.isClosed() && PacketRegistry.isIdRegisteredTo(id, MessagePacket.class)) {
								sendPacket(new MessageNotSentPacket());
							}
						}
					}
				}
				Thread.sleep(125);
			}
			if(instance.stillConnected(this)) {
				instance.closeConnection(this);
			}
			if(state.equals(State.LOGGED)) {
				instance.broadcast("§d§n" + getUsername() + "§d s'est déconnecté.");
			}
		} catch (final Exception e) {
			e.printStackTrace();
			instance.closeConnection(this);
		}
	}
	
	public void sendPacket(Packet packet) {
		sendPacket(packet, null);
	}

	public void sendPacket(Packet packet, Runnable runnable) {
		this.data.add(Pair.of(packet, runnable));
	}
	
	public void disconnect(String reason) {
		disconnect(reason, false);
	}
	
	public void disconnect(String reason, boolean error) {
		TextFlowServer.LOGGER.log(Level.INFO, username + " : " + client.getInetAddress().getHostName() + " disconnected : " + reason);
		if(error) {
			sendPacket(new ForcedCloseConnectionPacket(reason), () -> instance.closeConnection(this));
		} else {
			instance.closeConnection(this);
		}
		data.clear();
	}
	
	public void close() throws IOException {
		client.close();
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

	public Socket getSocket() {
		return client;
	}

	public TextFlowServer getTextFlowServerInstance() {
		return instance;
	}

	public String getVerifyToken() {
		return verifyToken;
	}

	public void setVerifyToken(String verifyToken) {
		this.verifyToken = verifyToken;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void enableEncryption() {
		this.encryption = true;
	}
	
	public void setTokenSent(boolean tokenSent) {
		this.tokenSent = tokenSent;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public static enum State {
		NONE, REGISTERING, ENCRYPTION, LOGGED;
	}
}
