package fr.leflodu62.textflowserver.packets.message;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x0B, serverSide = false)
public class ServerMessagePacket extends Packet {

	public ServerMessagePacket(String message) {
		super(new Object[] {message});
	}

}
