package fr.leflodu62.textflowserver.packets.login;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x06, serverSide = false)
public class LoginSuccessPacket extends Packet {

	public LoginSuccessPacket(String token) {
		super(new Object[] {token});
	}
	
}
