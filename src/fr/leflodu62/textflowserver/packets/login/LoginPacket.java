package fr.leflodu62.textflowserver.packets.login;

import java.util.logging.Level;

import org.mindrot.jbcrypt.BCrypt;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.ClientConnection.State;
import fr.leflodu62.textflowserver.TextFlowServer;
import fr.leflodu62.textflowserver.data.UserData;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;
import fr.leflodu62.textflowserver.secure.Token;

@PacketInfo(id = 0x04, serverSide = true)
public class LoginPacket extends Packet {

	public LoginPacket(String username, String password) {
		super(new Object[] {username, password});
	}
	
	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		final String encryptedPassword = clientConnection.getTextFlowServerInstance().getDBHandler().getEncryptedPassword(getUsername());
		if(encryptedPassword == null) {
			throw new NullPointerException("encrypted password is null !");
		}
		if(BCrypt.checkpw(getPassword(), encryptedPassword)) {
			clientConnection.setState(State.LOGGED);
			TextFlowServer.LOGGER.log(Level.INFO, getUsername() + " : " + clientConnection.getSocket().getInetAddress().getHostName() + " is now logged.");
			clientConnection.getTextFlowServerInstance().broadcast("§d§n" + getUsername() + "§d s'est connecté.", clientConnection);
			//TODO: recup database data
			clientConnection.sendPacket(new LoginSuccessPacket(Token.getToken(new UserData(getUsername()), Token.DEFAULT_EXPIRATION)), () -> clientConnection.setTokenSent(true));
		} else {
			clientConnection.sendPacket(new LoginFailedPacket("Mot de passe incorrecte !"), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
		}
	}
	
	public String getUsername() {
		return (String) data[0];
	}
	
	private final String getPassword() {
		return (String) data[1];
	}

}
