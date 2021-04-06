package com.kwic.service;

import java.util.Hashtable;
import java.util.Map;

import com.kwic.datasource.Transactions;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.security.aes.AES;
import com.kwic.security.seed.old.util.SecurityUtil;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

public class ServiceIMPL extends EgovAbstractServiceImpl{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private long statusId;
	private Map<String,Transactions> txStatus	= new Hashtable<String,Transactions>();
	
	public synchronized String beginTransaction(String txName){
		if(statusId>9999999999L)
			statusId	= 0;
		String id	= String.valueOf(statusId++);
		txStatus.put(id, new Transactions(txName));
		return id;
	}
	
	public void commit(String id){
		txStatus.get(id).commit();
		txStatus.remove(id);
	}
	public void rollback(String id){
		txStatus.get(id).rollback();
		txStatus.remove(id);
	}
	public String aesEncrypt(String plain) throws Exception{
		return AES.encode(plain, AES.DEFAULT_KEY,AES.TYPE_256,"UTF-8",AES.MODE_CBC);
	}
	public String aesDecrypt(String enc) throws Exception{
		String plain	= null;
		try{
			plain	= AES.decode(enc, AES.DEFAULT_KEY,AES.TYPE_256,"UTF-8",AES.MODE_CBC);
		}catch(javax.crypto.IllegalBlockSizeException e){
			plain	= SecurityUtil.seedDecodeData(enc);
		}catch(javax.crypto.BadPaddingException e){
			plain	= SecurityUtil.seedDecodeData(enc);
		}
		return plain;
	}
	
}
