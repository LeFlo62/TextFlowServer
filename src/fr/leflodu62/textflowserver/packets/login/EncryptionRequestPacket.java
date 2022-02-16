package fr.leflodu62.textflowserver.packets.login;

import java.security.PublicKey;

import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;

@PacketInfo(id = 0x01, serverSide = false)
public class EncryptionRequestPacket extends Packet {

	public EncryptionRequestPacket(PublicKey publicKey, String verifyToken) {
		super(new Object[] {publicKey, verifyToken});
	}

}
