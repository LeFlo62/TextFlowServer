package fr.leflodu62.textflowserver.secure;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public class RSAHelper {

	public static PublicKey getPublicKey(File publicKeyFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		final byte[] keyBytes = Files.readAllBytes(publicKeyFile.toPath());

		final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		final KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public static PrivateKey getPrivateKey(File privateKeyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		final byte[] keyBytes = Files.readAllBytes(privateKeyFile.toPath());

		final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		final KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	
	public static SealedObject encrypt(Serializable plain, PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException{
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return new SealedObject(plain, cipher);
	}
	
	public static Object decrypt(SealedObject encrypted, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, ClassNotFoundException, BadPaddingException, IOException {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return encrypted.getObject(cipher);
	}
	
	public static KeyPair genKeyPair() throws NoSuchAlgorithmException {
		return KeyPairGenerator.getInstance("RSA").generateKeyPair();
	}
	
	public static void writePublicKeyToFile(PublicKey publicKey, File file) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		final X509EncodedKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(publicKey, X509EncodedKeySpec.class);
		Files.write(file.toPath(), spec.getEncoded());
	}
	
	public static void writePrivateKeyToFile(PrivateKey privateKey, File file) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		final PKCS8EncodedKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(privateKey, PKCS8EncodedKeySpec.class);
		Files.write(file.toPath(), spec.getEncoded());
	}

}
