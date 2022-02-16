package fr.leflodu62.textflowserver.packets.connection;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id=0x08, serverSide = false)
public class ForcedCloseConnectionPacket extends Packet {

	public ForcedCloseConnectionPacket(String reason) {
		super(new Object[] {reason});
	}

}
