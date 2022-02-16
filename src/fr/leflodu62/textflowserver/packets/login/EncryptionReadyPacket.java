package fr.leflodu62.textflowserver.packets.login;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x03, serverSide = false)
public class EncryptionReadyPacket extends Packet {

	public EncryptionReadyPacket() {
		super(null);
	}

}
