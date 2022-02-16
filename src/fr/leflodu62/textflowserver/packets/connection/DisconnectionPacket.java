package fr.leflodu62.textflowserver.packets.connection;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x0A, serverSide = true)
public class DisconnectionPacket extends Packet {

	public DisconnectionPacket(String reason) {
		super(new Object[] {reason});
	}
	
	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		clientConnection.disconnect(getReason());
	}

	public String getReason() {
		return (String) data[0];
	}
	
}
