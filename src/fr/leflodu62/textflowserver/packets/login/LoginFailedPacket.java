package fr.leflodu62.textflowserver.packets.login;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id=0x05, serverSide = false)
public class LoginFailedPacket extends Packet {

	public LoginFailedPacket(String reason) {
		super(new Object[] {reason});
	}

}
