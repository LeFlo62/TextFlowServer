package fr.leflodu62.textflowserver.secure;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class AESHelper {
		
	private static final int KEY_SIZE = 256;
	
	public static SecretKey genKey() throws NoSuchAlgorithmException {
		final KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(KEY_SIZE);
		return generator.generateKey();
	}
	
	public static SealedObject encrypt(Serializable plain, SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IOException {
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return new SealedObject(plain, cipher);
	}
	
	public static Object decrypt(SealedObject object, SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IOException, ClassNotFoundException, BadPaddingException {
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return object.getObject(cipher);
	}

}
