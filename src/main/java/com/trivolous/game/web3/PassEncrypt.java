package com.trivolous.game.web3;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.binary.Base64;

public class PassEncrypt {
	private SecretKey key;
	
	public PassEncrypt() {
		try {
			// only the first 8 Bytes of the constructor argument are used 
			// as material for generating the keySpec
			DESKeySpec keySpec = new DESKeySpec("RatBrain".getBytes("UTF8")); 
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			key = keyFactory.generateSecret(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}      
	}
	
	public String encode(String plainTextPassword) {
		String encrypedPwd = "";
		byte[] cleartext;
		try {
			cleartext = plainTextPassword.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypedPwd = Base64.encodeBase64String(cipher.doFinal(cleartext)).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}      
		return encrypedPwd;
	}
	
	
	public String decode(String plainTextPassword) {
		// DECODE encryptedPwd String
		String unencrypedPwd = "";
		try {
			byte[] encrypedPwdBytes = Base64.decodeBase64(plainTextPassword);
			Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
			unencrypedPwd = new String(plainTextPwdBytes, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		}      
		return unencrypedPwd;
	}
}
