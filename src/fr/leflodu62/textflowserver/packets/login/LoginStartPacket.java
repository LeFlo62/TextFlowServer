package fr.leflodu62.textflowserver.packets.login;

import java.util.UUID;
import java.util.logging.Level;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.ClientConnection.State;
import fr.leflodu62.textflowserver.TextFlowServer;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x00, serverSide = true)
public class LoginStartPacket extends Packet {

	public LoginStartPacket(String username) {
		super(new Object[] {username});
	}

	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		TextFlowServer.LOGGER.log(Level.INFO, getUsername() + " : " + clientConnection.getSocket().getInetAddress().getHostName() + " tries to login");
		
		if(!clientConnection.getTextFlowServerInstance().getDBHandler().isInDatabase(getUsername())) {
			clientConnection.sendPacket(new LoginFailedPacket("Vous ne faites pas partie de ce serveur."), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
			return;
		}
		
		if(clientConnection.getTextFlowServerInstance().isConnected(getUsername())) {
			clientConnection.sendPacket(new LoginFailedPacket("Ce comtpe est déjà en ligne."), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
			return;
		}
		
		if(clientConnection.getTextFlowServerInstance().isServerFull()) {
			clientConnection.sendPacket(new LoginFailedPacket("Le serveur est complet."), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
			return;
		}
		
		final String verifyToken = UUID.randomUUID().toString();
		clientConnection.setUsername(getUsername());
		clientConnection.setVerifyToken(verifyToken);
		clientConnection.setState(State.ENCRYPTION);
		clientConnection.sendPacket(new EncryptionRequestPacket(
				clientConnection.getTextFlowServerInstance().getEncryptionStarter().getPublic(), verifyToken));
	}
	
	public String getUsername() {
		return (String) data[0];
	}

}
