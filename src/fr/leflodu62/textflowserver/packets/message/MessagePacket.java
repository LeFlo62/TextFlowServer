package fr.leflodu62.textflowserver.packets.message;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id=0x09, serverSide = false)
public class MessagePacket extends Packet {

	public MessagePacket(String username, String message) {
		super(new Object[] {username, message});
	}

}
