package fr.leflodu62.textflowserver.packets.register;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x0F, serverSide = false)
public class RegistrationFailedPacket extends Packet {

	public RegistrationFailedPacket(String reason) {
		super(new Object[] {reason});
	}

}
