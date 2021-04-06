
package com.kwic.security.seed.old.util;

import java.security.*;

public class  SecurityUtil {
	
	private static final String DEFAULT_CRYPT_KEY	= "s@lesinfo5539qhrwleks";
	
    /**
     *	byte[] ret = HashUtil.digest("MD5", "abcd".getBytes()); 
     *  처럼 호출
     */
    public static byte[] digest(String alg, byte[] input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(alg);
        return md.digest(input);
    }

    public static String getCryptoMD5String(String inputValue) throws Exception {
        if( inputValue == null ) throw new Exception("Can't conver to Message Digest 5 String value!!");
        byte[] ret = digest("MD5", inputValue.getBytes());
        String result = Base64Util.encode(ret);    
        return result;
    }
    
    
    public static String seedEncodeData(String inputValue,String encKey) throws Exception {
    	Crypto crypt = new Crypto();
    	String result="";
    	if("".equals(inputValue.trim())){
    		result	=	"";
    	}else{
    	
    		result= crypt.encodeData(inputValue,encKey);
    	}
        return result;
    }
    public static String seedEncodeData(String inputValue) throws Exception {
    	Crypto crypt = new Crypto();
    	String result="";
    	if("".equals(inputValue.trim())){
    		result	=	"";
    	}else{
    	
    		result= crypt.encodeData(inputValue,DEFAULT_CRYPT_KEY);
    	}
        return result;
    }
    
    public static String seedDecodeData(String inputValue,String encKey) throws Exception {
    	Crypto crypt = new Crypto();
    	
    	String result="";
    	if("".equals(inputValue.trim())){
    		result	=	"";
    	}else{
    		result=  crypt.decodeData(inputValue,encKey);
    	}
        return result;
    }
    public static String seedDecodeData(String inputValue) throws Exception {
    	Crypto crypt = new Crypto();
    	
    	String result="";
    	if("".equals(inputValue.trim())){
    		result	=	"";
    	}else{
    		result=  crypt.decodeData(inputValue,DEFAULT_CRYPT_KEY);
    	}
        return result;
    }
}
