package com.skeeper.core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class CryptorImpl {
	private static final byte[] salt = { (byte) 0x14, (byte) 0x61, (byte) 0xa5,
			(byte) 0x90, (byte) 0x3d, (byte) 0x6b, (byte) 0x3b, (byte) 0x09 };
	private static final PBEParameterSpec paramSpec = new PBEParameterSpec(
			salt, 20);

	public static String encrypt(String text, char[] password) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(password);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey secret = skf.generateSecret(keySpec);
		Cipher c = Cipher.getInstance("PBEWithMD5AndDES");
		c.init(Cipher.ENCRYPT_MODE, secret, paramSpec);
		byte[] encrypted = c.doFinal(text.getBytes());
		text = null; // remove text from memory
		return Base64.encodeBytes(encrypted);
	}

	public static String decrypt(String encrypted, char[] password)
			throws Exception {
		byte[] bytes = Base64.decode(encrypted);
		PBEKeySpec keySpec = new PBEKeySpec(password);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey secret = skf.generateSecret(keySpec);
		Cipher c = Cipher.getInstance("PBEWithMD5AndDES");
		c.init(Cipher.DECRYPT_MODE, secret, paramSpec);
		byte[] decrypted = c.doFinal(bytes);
		return new String(decrypted);
	}

}

// //////////////////////////////////////////////////////////////////////
// $Log: CryptorImpl.java,v $
// Revision 1.2 2006/02/15 04:59:00 luzgin
// CVS log added
//