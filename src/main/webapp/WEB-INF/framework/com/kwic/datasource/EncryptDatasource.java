package com.kwic.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.kwic.security.aes.AES;
/**
 * context-datasource.xml 에 정의된 암호화된 정보로 dataSource 생성
 * context-datasource.xml을 작성할 암호화 문자열 생성방법은 main 메소드 참조
 * 
 *   &lt;bean id="dataSource" class="com.kwic.datasource.EncryptDatasource" destroy-method="close"&gt;
 *       &lt;property name="driverClassName" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/&gt;
 *       &lt;property name="url" value="jYfPTVHEoF3cQrJpBdD6WZhPinYkF/7WwtvWmFAGnrrBCo1/1Ale3nrEiu/olJiVZpb5ngpB4j4zPTz3giuVia3OEb1Ft3LuwOcc24lDH7w=" /&gt;
 *       &lt;property name="username" value="K7XSlnC6YwymHviyoTVaHg=="/&gt;
 *       &lt;property name="password" value="Ep08QaBd/fNBJ0KTGC8U4g=="/&gt;
 *   &lt;/bean&gt;
 * 
 * */
public class EncryptDatasource extends BasicDataSource {
	private static final String	ENCRYPT_KEY	= "ABCDEfghijk12345zxcvECXStyui0987";
	
	private String encDriverClassName;
	private String encUrl;
	private String encUsername;
	private String encPassword;
	private String cryptoKey;
	/**
	 * driverClassName 복호화
	 * */
	@Override
	public synchronized void setDriverClassName(String encDriverClassName){
		this.encDriverClassName	= encDriverClassName;
	}
	
	/**
	 * url 복호화
	 * */
	@Override
    public synchronized void setUrl(String encUrl) {
		this.encUrl	= encUrl;
    }

	/**
	 * username 복호화
	 * */
	@Override
    public void setUsername(String encUsername) {
		this.encUsername	= encUsername;
    }
	
	/**
	 * password 복호화
	 * */
	@Override
    public void setPassword(String encPassword) {
		this.encPassword	= encPassword;
    }
	/**
	 * encryption key
	 * */
    public void setCryptoKey(String cryptoKey) {
    	this.cryptoKey	= cryptoKey;
    }
	
	@Override
	protected DataSource createDataSource() throws SQLException{
        if (closed)
            throw new SQLException("Data source is closed");
        if (dataSource != null)
            return (dataSource);

		cryptoKey	= cryptoKey==null?EncryptDatasource.ENCRYPT_KEY:cryptoKey;
		super.setDriverClassName(decrypt(encDriverClassName,cryptoKey));
		super.setUrl(decrypt(encUrl,cryptoKey));
		super.setUsername(decrypt(encUsername,cryptoKey));
		super.setPassword(decrypt(encPassword,cryptoKey));
		
		return super.createDataSource();
	}
	/**
	 * 암호화
	 * */
	public static final String encrypt(String plain){
		return encrypt(plain,EncryptDatasource.ENCRYPT_KEY);
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
		return decrypt(encrypt,EncryptDatasource.ENCRYPT_KEY);
	}
	public static final String decrypt(String encrypt,String cryptoKey){
		String plain	= encrypt;
		try{
			plain	= AES.decode(encrypt, cryptoKey,AES.TYPE_256);
		}catch(Exception e){
		}
		return plain;
	}
	
	public static void main(String[] args) {
	}
}
