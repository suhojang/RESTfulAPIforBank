package com.kwic.security.aes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import org.apache.commons.codec.binary.Base64;

/*
 * need JCE library
 * must override local_policy.jar, US_export_policy.jar in \jre\lib\security\
 * 
 * */
public class AES {
	public static final String DEFAULT_KEY	= "0^2/2a4T5!H@1#9%GDGsjbjip!@$752$";
	/**
	 * 128bit encryption
	 * 암호키 사이즈 16 bytes
	 * */
	public static final int TYPE_128	= 16;
	/**
	 * 192bit encryption
	 * 암호키 사이즈 24 bytes
	 * */
	public static final int TYPE_192	= 24;
	/**
	 * 256bit encryption
	 * 암호키 사이즈 32 bytes
	 * */
	public static final int TYPE_256	= 32;
	/**
	 * AES ECB PKCS5Padding
	 * */
	public static final int MODE_ECB	= 1;
	/**
	 * AES CBC PKCS5Padding
	 * */
	public static final int MODE_CBC	= 2;
	/**
	 * AES CBC NoPadding
	 * */
	public static final int MODE_ECB_NOPADDING	= 3;
	
	/**
	 * Aes128은 16바이트, Aes256은 32바이트 크기의 키가 필요합니다.
	 * 사용자가 입력한 키 크기가 이보다 작을 경우 패딩될 키 목록입니다.
	 * */
	private static final char[] keypadding	= {
		'a','1','b','C','k','!','e','*'
		,'f','K','D','8','s','4','W','p'
		,'G','a','d','#','G','7','&','E'
		,'U','l','J','j','i','W','2','Q'
	};
	/**
	 * default iv factor
	 * */
	private static final byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	/**
	 * 사용자가 입력한 키를 검사하고 알고리즘에 맞도록 키사이즈를 변경합니다.
	 * */
	private static String initKey(String key,int blockSize,String enc) throws Exception{
		byte[] keys		= key.getBytes(enc);
		String newKey	= null;
		if(keys.length>blockSize){
			byte[] k	= new byte[blockSize];
			System.arraycopy(keys, 0, k, 0, blockSize);
			newKey	= new String(k);
			
			if(newKey.getBytes().length!=blockSize)
				newKey	= initKey(newKey.substring(0,newKey.length()-2),blockSize,enc);
			return newKey;
		}

		int keyBlock	= keys.length%blockSize==0?keys.length/blockSize:keys.length/blockSize+1;
		
		StringBuffer sb	= new StringBuffer();
		sb.append(key);

		for(int i=keys.length;i<keyBlock*blockSize;i++){
			sb.append(keypadding[i%blockSize]);
		}
		return sb.toString();
	}
	private static byte[] padECBNoPaddingText(byte[] bytes) throws Exception{
		int size	= bytes.length;
		int block	= 1;
		block		= size/16;
		if(size%16>0)
			block++;
		
		byte[] newBytes	= new byte[block*16];
		System.arraycopy(bytes, 0, newBytes, 0, size);
		
		for(int i=size;i<block*16;i++)
			newBytes[i]	= 0x00;
		return newBytes;
	}
	private static byte[] removeECBNoPaddingText(byte[] bytes) throws Exception{
		int idx	= bytes.length;
		for(int i=0;i<bytes.length;i++){
			if(bytes[i]==0x00){
				idx	= i;
				break;
			}
		}
		byte[] newBytes	= new byte[idx];
		System.arraycopy(bytes, 0, newBytes, 0, idx);
		return newBytes;
	}
	/**
	 * 암호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * */
	public static String encode(String str, String okey,int blockSize) throws Exception {
		return encode(str, okey,blockSize,"UTF-8",MODE_CBC);
	}
	/**
	 * 암호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * */
	public static String encode(String str, String okey,int blockSize,String enc) throws Exception {
		return encode(str, okey,blockSize,enc,MODE_CBC);
	}
	/**
	 * 암호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * */
	public static String encode(String str, String okey,int blockSize,int mode) throws Exception {
		return encode(str, okey,blockSize,"UTF-8",mode);
	}
	/**
	 * 암호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * */
	public static String encode(String str, String okey,int blockSize,String enc,int mode) throws Exception {
		return encode(str, okey,blockSize,"UTF-8",mode,ivBytes);
	}
	/**
	 * 암호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * @param byte[] ivFactor iv factor bytes
	 * */
	public static String encode(String str, String okey,int blockSize,String enc,int mode,byte[] ivFactor) throws Exception {
		if(str==null || "".equals(str))
			return str;
		byte[] textBytes = str.getBytes(enc);
		return encode(textBytes, okey,blockSize,enc,mode,ivFactor);
	}
	/**
	 * 암호화
	 * @param byte[] textBytes 평문 bytes
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * @param byte[] ivFactor iv factor bytes
	 * */
	public static String encode(byte[] textBytes, String okey,int blockSize,String enc,int mode,byte[] ivFactor) throws Exception {
		String key	= initKey(okey,blockSize,enc);
		return encode(textBytes, key.getBytes(enc),blockSize,enc,mode,ivFactor);
	}
	/**
	 * 암호화
	 * @param byte[] textBytes 평문 bytes
	 * @param byte[] keyBytes 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * @param byte[] ivFactor iv factor bytes
	 * */
	public static String encode(byte[] textBytes, byte[] keyBytes,int blockSize,String enc,int mode,byte[] ivFactor) throws Exception {
		if(textBytes==null)
			return null;
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = null;
		if(mode==MODE_ECB){
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey);
		}else if(mode==MODE_ECB_NOPADDING){
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			textBytes	= padECBNoPaddingText(textBytes);
			cipher.init(Cipher.ENCRYPT_MODE, newKey);
		}else if(mode==MODE_CBC){
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivFactor);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
		}else{
			throw new NoSuchAlgorithmException("Unknown encryption mode ["+mode+"].");
		}
		return Base64.encodeBase64String(cipher.doFinal(textBytes));
	}
	/**
	 * 복호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * */
	public static String decode(String str, String okey,int blockSize) throws Exception {
		return decode(str, okey,blockSize,"UTF-8",MODE_CBC);
	}
	/**
	 * 복호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * */
	public static String decode(String str, String okey,int blockSize,String enc) throws Exception {
		return decode(str, okey,blockSize,enc,MODE_CBC);
	}
	/**
	 * 복호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * */
	public static String decode(String str, String okey,int blockSize,int mode) throws Exception {
		return decode(str, okey,blockSize,"UTF-8",mode);
	}
	/**
	 * 복호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * */
	public static String decode(String str, String okey,int blockSize,String enc,int mode) throws Exception{
		return decode(str, okey,blockSize,"UTF-8",mode,ivBytes);
	}
	/**
	 * 복호화
	 * @param String str 평문 문자열
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * @param byte[] ivFactor iv factor bytes
	 * */
	public static String decode(String str, String okey,int blockSize,String enc,int mode,byte[] ivFactor) throws Exception {
		if(str==null || "".equals(str))
			return str;
		byte[] textBytes = Base64.decodeBase64(str);
		return decode(textBytes, okey,blockSize,"UTF-8",mode,ivBytes);
	}
	/**
	 * 복호화
	 * @param byte[] textBytes 평문 bytes
	 * @param String okey 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * @param byte[] ivFactor iv factor bytes
	 * */
	public static String decode(byte[] textBytes, String okey,int blockSize,String enc,int mode,byte[] ivFactor) throws Exception {
		String key	= initKey(okey,blockSize,enc);
		return decode(textBytes, key.getBytes(enc),blockSize,"UTF-8",mode,ivBytes);
	}
	/**
	 * 복호화
	 * @param byte[] textBytes 평문 bytes
	 * @param byte[] keyBytes 암호화 키
	 * @param int blockSize 암호화 키 사이즈 (AES.TYPE_128,AES.TYPE_192,AES.TYPE_256)
	 * @param String enc 인코딩
	 * @param int mode 암호화 방식 (AES.MODE_ECB,AES.MODE_CBC,AES.MODE_ECB_NOPADDING)
	 * @param byte[] ivFactor iv factor bytes
	 * */
	public static String decode(byte[] textBytes, byte[] keyBytes,int blockSize,String enc,int mode,byte[] ivFactor) throws Exception {
		if(textBytes==null)
			return null;
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher 	= null;
		byte[] dBytes	= null;
		if(mode==MODE_ECB){
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey);
			dBytes	= cipher.doFinal(textBytes);
		}else if(mode==MODE_ECB_NOPADDING){
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, newKey);
			dBytes	= cipher.doFinal(textBytes);
			dBytes	= removeECBNoPaddingText(dBytes);
		}else if(mode==MODE_CBC){
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
			dBytes	= cipher.doFinal(textBytes);
		}else{
			throw new NoSuchAlgorithmException("Unknown encryption mode ["+mode+"].");
		}
		return new String(dBytes, enc);
	}
}