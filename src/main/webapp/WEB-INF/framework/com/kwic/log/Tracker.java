package com.kwic.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.kwic.util.StringUtil;

public class Tracker {
	public static Map<String,Tracker> map	= new Hashtable<String,Tracker>();
	
	public static final String LINE		= System.getProperty("line.separator");
	private FileWriter writer;
	private String path;
	private String cPath;
	private long maxLength;
	
	private SimpleDateFormat sf	= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS",Locale.KOREA);
	private SimpleDateFormat dailySf	= new SimpleDateFormat("yyyyMMdd");
	private String yymmdd;
	private int dailySeq	= 0;
	private Tracker(long maxLength){
		this.maxLength	= maxLength;
	}
	private Tracker(String path,long maxLength){
		this.path	= path;
		this.maxLength	= maxLength;
		sf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
	public static Tracker getLogger(String id,String path,long maxLength){
		synchronized(Tracker.class){
			if(map.get(id)==null)
				map.put(id, new Tracker(path,maxLength));
			return map.get(id);
		}
	}
	public static Tracker getLogger(String id,String path){
		synchronized(Tracker.class){
			if(map.get(id)==null)
				map.put(id, new Tracker(path,10*1024*1024));
			return map.get(id);
		}
	}
	public static Tracker getLogger(String id){
		synchronized(Tracker.class){
			if(map.get(id)==null)
				map.put(id, new Tracker(10*1024*1024));
			return map.get(id);
		}
	}
	public void setPath(String path){
		this.path	= path;
	}
	public void track(Object msg){
		write(msg);
	}
	private synchronized void write(Object msg){
		setWriter();
		StringBuffer sb	= new StringBuffer();
		try{
			if(msg instanceof Throwable){
				sb.append(((Throwable)msg).toString()+": "+((Throwable)msg).getMessage()+LINE);
				StackTraceElement[] arr	= ((Throwable)msg).getStackTrace();
				for(int i=0;i<arr.length;i++)
					sb.append(arr[i].toString()+LINE);
			}else{
				sb.append(String.valueOf(msg)+LINE);
			}
			writer.write("["+sf.format(Calendar.getInstance(Locale.KOREA).getTime())+"] "+sb.toString());
			writer.flush();
		}catch(Exception e){
			sb.setLength(0);
		}
	}
	
	private synchronized void setWriter(){
		String tPath		= getPath();
		
		if(!tPath.equals(cPath)){
			cPath	= tPath;
			if(writer!=null)
				try{writer.close();}catch(Exception e){}
			
	    	boolean policy		= true;
			if(!new File(cPath).getParentFile().exists())
				policy	= new File(cPath).getParentFile().mkdirs();
    	    if(policy){
    			try{writer	= new FileWriter(cPath,true);}catch(Exception e){writer=null;}
    	    }else{
    	    	writer	= null;
    	    }
		}
	}
	public String getPath(){
		dailySf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		String yyyyMMdd	= dailySf.format(Calendar.getInstance(Locale.KOREA).getTime());
		if(yymmdd==null){
			yymmdd	= yyyyMMdd;
		}else if(!yymmdd.equals(yyyyMMdd)){
			dailySeq	= 0;
		}
		
		String yy	= yyyyMMdd.substring(2,4);
		String yyyy	= yyyyMMdd.substring(0,4);
		String MM	= yyyyMMdd.substring(4,6);
		String dd	= yyyyMMdd.substring(6,8);
		
		String tPath	= "";
		tPath	= StringUtil.replace(path, "%yyyy%", yyyy);
		tPath	= StringUtil.replace(tPath, "%yy%", yy);
		tPath	= StringUtil.replace(tPath, "%MM%", MM);
		tPath	= StringUtil.replace(tPath, "%dd%", dd+'-'+dailySeq);
		
		File cFile	= null;
		while(true){
			cFile	= new File(tPath);
			if(cFile.exists() && cFile.length()>=maxLength)
				tPath	= StringUtil.replace(tPath, dd+'-'+dailySeq , dd+'-'+(++dailySeq) );
			else
				break;
		}
		return tPath;
	}
}
