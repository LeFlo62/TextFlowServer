package fr.leflodu62.textflowserver.secure;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.json.JSONObject;

import fr.leflodu62.textflowserver.data.UserData;

public class Token {

	public static final int DEFAULT_EXPIRATION = 10*60;
	
	private static final String ALG = "HmacSHA256";
	private static final Charset CHARSET = StandardCharsets.UTF_16;
	private static final int KEY_SIZE = 256;
	private static final Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
	private static final String BASE64_HEADER = BASE64_URL_ENCODER.encodeToString(("{\"typ\":\"JWT\",\"alg\":\"" + ALG + "\"}").getBytes(CHARSET));
	
	private static SecretKey key = null;
	
	public static void init() {
		try {
			final KeyGenerator generator = KeyGenerator.getInstance(ALG);
			generator.init(KEY_SIZE);
			key = generator.generateKey();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getToken(UserData data, long duration) {
		final JSONObject json = data.toJson();
		final long time = System.currentTimeMillis()/1000;
		json.put("iat", time).put("exp", (time+duration));
		
		return getToken(json.toString());
	}
	
	private static String getToken(String data) {
		final String firstParts = BASE64_HEADER + "." + BASE64_URL_ENCODER.encodeToString(data.getBytes(CHARSET));
		final String token = firstParts + "." + encrypt(ALG, firstParts);
		return token;
	}
	
	public static boolean isValid(String token) {
		final String[] parts = token.split("\\.");
		
		if(parts.length == 3) {
			final String alg = new JSONObject(new String(BASE64_URL_DECODER.decode(parts[0]), CHARSET)).getString("alg");
			
			if(encrypt(alg, parts[0] + "." + parts[1]).equals(parts[2])) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isExpired(String token) {
		return new JSONObject(new String(BASE64_URL_DECODER.decode(token.split("\\.")[1]), CHARSET)).getLong("exp")*1000 < System.currentTimeMillis();
	}
	
	public static String renewToken(String previousToken, long duration) {
		if(isValid(previousToken)) {
			final JSONObject json = new JSONObject(BASE64_URL_DECODER.decode(previousToken.split("\\.")[1]));
			json.remove("exp");
			json.remove("iat");
			final long time = System.currentTimeMillis()/1000;
			json.put("iat", time).put("exp", (time+duration));
			return getToken(json.toString());
		}
		return previousToken;
	}
	
	private static String encrypt(String alg, String plain) {
		try {
			final Mac mac = Mac.getInstance(alg);
			mac.init(key);
			return BASE64_URL_ENCODER.encodeToString(mac.doFinal(plain.getBytes(CHARSET)));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
