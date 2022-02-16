package fr.leflodu62.textflowserver.packets.register;

import java.util.logging.Level;

import org.mindrot.jbcrypt.BCrypt;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.ClientConnection.State;
import fr.leflodu62.textflowserver.TextFlowServer;
import fr.leflodu62.textflowserver.data.UserData;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;
import fr.leflodu62.textflowserver.packets.login.LoginSuccessPacket;
import fr.leflodu62.textflowserver.secure.Token;

@PacketInfo(id = 0x0E, serverSide = true)
public class RegisterPacket extends Packet {

	public RegisterPacket(String username, String password) {
		super(new Object[] {username, password});
	}
	
	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		clientConnection.getTextFlowServerInstance().getDBHandler().addUser(getUsername(), BCrypt.hashpw(getPassword(), BCrypt.gensalt()));
		clientConnection.setState(State.LOGGED);
		TextFlowServer.LOGGER.log(Level.INFO, getUsername() + " : " + clientConnection.getSocket().getInetAddress().getHostName() + " is now logged.");
		clientConnection.getTextFlowServerInstance().broadcast("§d§n" + getUsername() + "§d s'est connecté.", clientConnection);
		//TODO: recup database data
		clientConnection.sendPacket(new LoginSuccessPacket(Token.getToken(new UserData(getUsername()), Token.DEFAULT_EXPIRATION)), () -> clientConnection.setTokenSent(true));
	}
	
	public String getUsername() {
		return (String) data[0];
	}
	
	private String getPassword() {
		return (String) data[1];
	}

}
