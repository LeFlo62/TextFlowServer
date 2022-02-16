package fr.leflodu62.textflowserver.packets.connection;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x0C, serverSide = false)
public class RefreshTokenPacket extends Packet {

	public RefreshTokenPacket(String oldToken, String newToken) {
		super(new Object[] {oldToken, newToken});
	}

}
