package com.kwic.security.des;


import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 3DES (Triple Data Encryption Standard) crypto
 * CBC
 * PKCS5Padding
 * 
 * key size : 24 bytes
 * iv key size : 8 bytes
 * */
public class TripleDES {
	public static final byte[] DEFAULT_IV	= new byte[]{0x12, 0x34, 0x56, 0x78, 0x04, 0x24, 0x65, 0x43};
	/**
	 * 평문을 64 bits 씩 나누어 암호화
	 * */
	public static final int MODE_ECB	= 1;
	/**
	 * 출력 암호문이 다음 평문 블록에 영향을 줌
	 * */
	public static final int MODE_CBC	= 2;
	
	private static byte[] encrypt(byte[] keyBytes, byte[] ivBytes, byte[] plainBytes) throws Exception{
		KeySpec spec = new DESedeKeySpec(keyBytes);//keyBytes must be 24 bytes
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey theKey = keyFactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		
		IvParameterSpec ivParameter = new IvParameterSpec(ivBytes);
		cipher.init(Cipher.ENCRYPT_MODE, theKey, ivParameter);
		byte[] encrypted = cipher.doFinal(plainBytes);
		
		return encrypted;
	}
	public static String encryptBase64(byte[] keyBytes, byte[] ivBytes, byte[] plainBytes) throws Exception{
		return Base64.encodeBase64String(encrypt(keyBytes, ivBytes, plainBytes));
	}
	public static String encryptBase64(byte[] keyBytes, byte[] ivBytes, String plainText, String encoding) throws Exception{
		return Base64.encodeBase64String(encrypt(keyBytes, ivBytes, plainText.getBytes(encoding)));
	}
	
	private static byte[] decrypt(byte[] keyBytes, byte[] ivBytes, byte[] encryptBytes) throws Exception{
		DESedeKeySpec spec = new DESedeKeySpec(keyBytes);//keyBytes must be 24 bytes
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey theKey = keyFactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		
		IvParameterSpec ivParameter = new IvParameterSpec(ivBytes);
		cipher.init(Cipher.DECRYPT_MODE, theKey, ivParameter);
		byte[] encrypted = cipher.doFinal(encryptBytes);
		
		return encrypted;
	}
	public static byte[] decryptBase64(byte[] keyBytes, byte[] ivBytes, String encryptBase64) throws Exception{
		return decrypt(keyBytes, ivBytes, Base64.decodeBase64(encryptBase64));
	}
	public static String decryptBase64(byte[] keyBytes, byte[] ivBytes, String encryptBase64,String encoding) throws Exception{
		return new String(decrypt(keyBytes, ivBytes, Base64.decodeBase64(encryptBase64)),encoding);
	}
}
