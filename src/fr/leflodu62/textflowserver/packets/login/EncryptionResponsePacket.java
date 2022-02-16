package fr.leflodu62.textflowserver.packets.login;

import java.security.PrivateKey;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import fr.leflodu62.textflowserver.ClientConnection;
import fr.leflodu62.textflowserver.packets.Packet;
import fr.leflodu62.textflowserver.packets.PacketInfo;
import fr.leflodu62.textflowserver.secure.RSAHelper;

@PacketInfo(id = 0x02, serverSide = true)
public class EncryptionResponsePacket extends Packet {

	public EncryptionResponsePacket(SealedObject secretKey, SealedObject verifyToken) {
		super(new Object[] {secretKey, verifyToken});
	}
	
	@Override
	public void processData(ClientConnection clientConnection, Packet packet) {
		try {
			final PrivateKey privateKey = clientConnection.getTextFlowServerInstance().getEncryptionStarter().getPrivate();
			final SecretKey secretKey = (SecretKey) RSAHelper.decrypt(getSecretKey(), privateKey);
			final String verifyToken = (String) RSAHelper.decrypt(getVerifyToken(), privateKey);
			
			if(clientConnection.getVerifyToken().equals(verifyToken)) {
				clientConnection.setSecretKey(secretKey);
				clientConnection.enableEncryption();
				clientConnection.setVerifyToken(null);
				
				clientConnection.sendPacket(new EncryptionReadyPacket());
			} else {
				System.out.println("pas même token");
				clientConnection.sendPacket(new LoginFailedPacket("Token de vérification invalide."), () -> clientConnection.getTextFlowServerInstance().closeConnection(clientConnection));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public SealedObject getSecretKey() {
		return (SealedObject) data[0];
	}
	
	public SealedObject getVerifyToken() {
		return (SealedObject) data[1];
	}

}
