package fr.leflodu62.textflowserver.packets.message;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x10, serverSide = false)
public class MessageNotSentPacket extends Packet {

	public MessageNotSentPacket() {
		super(null);
	}

}
