package com.kwic.security.sha256;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

public class SHA256 {
	
	public static byte[] encrypt(String plainText,String encoding) throws Exception{
		return encrypt(plainText.getBytes(encoding));
	}
	public static byte[] encrypt(byte[] plainBytes) throws Exception{
	    MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
	    mDigest.update(plainBytes);
	    return mDigest.digest() ;
	}
	public static String encryptHex(String plainText,String encoding) throws Exception{
		return encryptHex(plainText.getBytes(encoding));
	}
	public static String encryptHex(byte[] plainBytes) throws Exception{
		byte[] encBytes	= encrypt(plainBytes);
	    StringBuffer sbuf = new StringBuffer();
	    for(int i=0; i < encBytes.length; i++)
	        sbuf.append(Integer.toString((encBytes[i] & 0xff) + 0x100, 16).substring(1)) ;
	    return sbuf.toString();
	}
	public static String encryptBase64(String plainText,String encoding) throws Exception{
		return encryptBase64(plainText.getBytes(encoding));
	}
	public static String encryptBase64(byte[] plainBytes) throws Exception{
		byte[] encBytes	= encrypt(plainBytes);
		return Base64.encodeBase64String(encBytes);
	}
}
