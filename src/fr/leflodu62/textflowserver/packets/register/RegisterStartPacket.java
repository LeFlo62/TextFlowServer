package fr.leflodu62.textflowserver.packets.register;

import java.util.UUID;
import java.util.logging.Level;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.ClientConnection.State;
import fr.leflodu62.textflowserver.PendingRegistration;
import fr.leflodu62.textflowserver.TextFlowServer;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;
import fr.leflodu62.textflowserver.packets.login.EncryptionRequestPacket;

@PacketInfo(id = 0x0D, serverSide = true)
public class RegisterStartPacket extends Packet {

	public RegisterStartPacket(String username) {
		super(new Object[] { username });
	}

	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		if(clientConnection.getTextFlowServerInstance().getDBHandler().isInDatabase(getUsername())) {
			clientConnection.sendPacket(new RegistrationFailedPacket("Ce pseudo est déjà utilisé !"), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
			return;
		}
		
		final String verifyToken = UUID.randomUUID().toString();
		clientConnection.setUsername(getUsername());
		clientConnection.setVerifyToken(verifyToken);
		TextFlowServer.LOGGER.log(Level.INFO, getUsername() + " : "
				+ clientConnection.getSocket().getInetAddress().getHostAddress() + " tries to register !");
		clientConnection.setState(State.REGISTERING);
		clientConnection.getTextFlowServerInstance().broadcast("§a" + getUsername()
				+ " veut vous rejoindre ! Pour l'accepter faites" + System.lineSeparator() + "/accept " + getUsername(),
				clientConnection);
		clientConnection.getTextFlowServerInstance().addPendingRegistration(
				new PendingRegistration(clientConnection.getTextFlowServerInstance(), getUsername(), (accepted) -> {
					if (accepted) {
						clientConnection.sendPacket(new EncryptionRequestPacket(
								clientConnection.getTextFlowServerInstance().getEncryptionStarter().getPublic(),
								verifyToken));
					} else {
						clientConnection
								.sendPacket(new RegistrationFailedPacket("Votre inscription n'a pas été retenue."), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
					}
				}));

	}

	public String getUsername() {
		return (String) data[0];
	}

}
