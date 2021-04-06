package com.kwic.datasource;

import javax.naming.NamingException;

import org.springframework.jndi.JndiObjectFactoryBean;

import com.kwic.security.aes.AES;

public class EncryptJndiObjectFactoryBean extends JndiObjectFactoryBean{
	private static final String	ENCRYPT_KEY	= "ABCDEfghijk12345zxcvECXStyui0987";
	
	private String encJndiName;
	private String cryptoKey;
	
	/**
	 * set encrypted jndi name
	 * */
	public void setJndiName(String encJndiName) {
		this.encJndiName = encJndiName;
	}
	/**
	 * encryption key
	 * */
    public void setCryptoKey(String cryptoKey) {
    	this.cryptoKey	= cryptoKey;
    }

    @Override
    public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
		cryptoKey	= cryptoKey==null?EncryptJndiObjectFactoryBean.ENCRYPT_KEY:cryptoKey;
		super.setJndiName(decrypt(encJndiName,cryptoKey));
		super.afterPropertiesSet();
    }
    
	/**
	 * 암호화
	 * */
	public static final String encrypt(String plain){
		return encrypt(plain,EncryptJndiObjectFactoryBean.ENCRYPT_KEY);
	}
	public static final String encrypt(String plain,String cryptoKey){
		String encrypt	= plain;
		try{
			encrypt	= AES.encode(plain, cryptoKey,AES.TYPE_256);
		}catch(Exception e){
		}
		return encrypt;
	}
	/**
	 * 복호화
	 * */
	public static final String decrypt(String encrypt){
		return decrypt(encrypt,EncryptJndiObjectFactoryBean.ENCRYPT_KEY);
	}
	public static final String decrypt(String encrypt,String cryptoKey){
		String plain	= encrypt;
		try{
			plain	= AES.decode(encrypt, cryptoKey,AES.TYPE_256);
		}catch(Exception e){
		}
		return plain;
	}
}
