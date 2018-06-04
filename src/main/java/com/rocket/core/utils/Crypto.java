package com.rocket.core.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;

public abstract class Crypto {

	private static final byte[] SALT = { -128, -64, -32, -16, -8, -4, -2, 22, 31, 2, 4, 8, 16, 32, 64, 127 };

	public static String encrypt(String input) throws Exception {
		SecretKeySpec key = new SecretKeySpec(SALT, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
		return BaseEncoding.base64().encode(encrypted);
	}

	public static String decrypt(String input) throws Exception {
		SecretKeySpec key = new SecretKeySpec(SALT, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(BaseEncoding.base64().decode(input));
		return new String(decrypted, StandardCharsets.UTF_8);
	}

}
