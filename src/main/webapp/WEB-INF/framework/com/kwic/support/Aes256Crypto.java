package com.kwic.support;

import com.kwic.security.aes.AES;

/**
 * aes 256 crypto
 * 
 * key size : any size (32 bytes recommended)
 * */
public class Aes256Crypto extends CryptoKeyGenerator{
	
	public static final String encode(String plainText,String key) throws Exception{
		return encode(plainText,key,"UTF-8");
	}
	public static final String encode(String plainText,String key,String enc) throws Exception{
		return AES.encode(plainText, key,AES.TYPE_256,enc,AES.MODE_ECB_NOPADDING);
	}
	public static final String decode(String encodedText,String key) throws Exception{
		return decode(encodedText,key,"UTF-8");
	}
	public static final String decode(String encodedText,String key,String enc) throws Exception{
		return AES.decode(encodedText, key,AES.TYPE_256,enc,AES.MODE_ECB_NOPADDING);
	}
}
