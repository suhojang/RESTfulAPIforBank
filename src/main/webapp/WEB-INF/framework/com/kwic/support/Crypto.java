package com.kwic.support;

import com.kwic.security.seed.SeedUtil;

/**
 * seed 128 crypto
 * 
 * key size : any size (16 bytes recommended)
 * */
public class Crypto {
    private static String paddingKey(String key){
        byte bytes[] = key.getBytes();
        int len = bytes.length / 16;
        if(bytes.length % 16 != 0)
            len++;
        byte returnBytes[] = new byte[16 * len];
        System.arraycopy(bytes, 0, returnBytes, 0, bytes.length);
        for(int i = bytes.length; i < returnBytes.length; i++)
            returnBytes[i] = 49;

        return new String(returnBytes);
    }

    private static byte[] trim(byte bytes[]){
        int idx = bytes.length;
        for(int i = 0; i < bytes.length; i++)
        {
            if(bytes[i] != 0)
                continue;
            idx = i;
            break;
        }

        byte returnBytes[] = new byte[idx];
        System.arraycopy(bytes, 0, returnBytes, 0, idx);
        return returnBytes;
    }

    public static byte[] encryptBytes(String plain, String key, String encoding) throws Exception{
        if(plain == null || plain.getBytes().length == 0)
        {
            return null;
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte encrypt[] = util.encrypt(plain, key, encoding, true);
            return encrypt;
        }
    }

    public static String encrypt(String plain, String key, String encoding) throws Exception {
        if(plain == null || plain.getBytes().length == 0)
        {
            return plain;
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte encrypt[] = util.encrypt(plain, key, encoding, true);
            return new String(encrypt);
        }
    }

    public static byte[] decryptBytes(String encrypt, String key, String encoding) throws Exception {
        if(encrypt == null || encrypt.getBytes().length == 0)
        {
            return null;
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte decrypt[] = util.decrypt(encrypt, key, encoding, true);
            return trim(decrypt);
        }
    }

    public static String decrypt(String encrypt, String key, String encoding) throws Exception {
        if(encrypt == null || encrypt.getBytes().length == 0)
        {
            return encrypt;
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte decrypt[] = util.decrypt(encrypt, key, encoding, true);
            return new String(trim(decrypt));
        }
    }
    
    public static void main(String[] args) throws Exception {
    	String plain	= "1234";
    	String key		= "154678$DSAA1234l";
    	String encoding	= "UTF-8";
    	
    	String encode	= Crypto.encrypt(plain, key, encoding);
    	System.out.println(encode);
    	
    	String decode	= new String(Crypto.decryptBytes(encode, key, encoding));
    	System.out.println("decode :: " + decode);
    }
	
}
