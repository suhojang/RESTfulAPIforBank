package com.kwic.io;

import java.io.OutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipArchive {
	/**
	 * 압축HIGH - 9
	 * */
	public static final int _COMPRESS_LEVEL_HIGH	= 9;
	/**
	 * 압축COMMON - 5
	 * */
	public static final int _COMPRESS_LEVEL_COMMON	= 5;
	/**
	 * 압축LOW - 3
	 * */
	public static final int _COMPRESS_LEVEL_LOW		= 3;
	/**
	 * 압축DEFAULT
	 * */
	public static final int _COMPRESS_LEVEL_DEFAULT	= _COMPRESS_LEVEL_COMMON;
	/**
	 * 압축파일 확장자
	 * */
	public static final String _EXTENSION			= ".zip";
	
	public static final void write(byte[][] bytesArr,String[] entryNameArr,long[] lastModified,OutputStream os, int compressLevel,String encoding) throws Exception{
		ZipArchiveOutputStream zos	= null;
		try{
			zos	= new ZipArchiveOutputStream(os);
			zos.setEncoding(encoding);
			zos.setLevel(compressLevel);
			for(int i=0;i<bytesArr.length;i++){
				write(bytesArr[i],entryNameArr[i],lastModified[i],zos); 
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{if(zos!=null)zos.close();}catch(Exception e){zos=null;}
			try{if(os!=null)os.close();}catch(Exception e){os=null;}
		}
	}
	
	private static final void write(byte[] bytes,String entryName,long lastModified,ZipArchiveOutputStream zos) throws Exception{
		ZipArchiveEntry		zentry		= null;
		
		if(bytes==null)
			throw new Exception("Contents is null.");
		if(entryName==null)
			throw new Exception("Entry name is null.");
		if(zos==null)
			throw new Exception("OutputStream is null.");

		try{
			zentry		= new ZipArchiveEntry(entryName);
			zentry.setTime(lastModified);
			zentry.setSize(bytes.length);
			zos.putArchiveEntry(zentry);
			zos.write(bytes, 0, bytes.length);
			zos.closeArchiveEntry();
		}catch(Exception e){
			throw e;
		}
	}
}
