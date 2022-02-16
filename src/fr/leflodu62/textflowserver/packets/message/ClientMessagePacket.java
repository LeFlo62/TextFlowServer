package fr.leflodu62.textflowserver.packets.message;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id=0x07,serverSide = true)
public class ClientMessagePacket extends Packet {

	public ClientMessagePacket(String message) {
		super(new Object[] {message});
	}
	
	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		final String username = clientConnection.getUsername();
		
		final String message = getMessage();
		
		if(message.startsWith("/")) {
			clientConnection.getTextFlowServerInstance().getCommandRegistry().processCommand(clientConnection, message);
			return;
		}
		
		clientConnection.getTextFlowServerInstance().broadcastMessage(clientConnection, username, message);
	}
	
	public String getMessage() {
		return (String) data[0];
	}

}
