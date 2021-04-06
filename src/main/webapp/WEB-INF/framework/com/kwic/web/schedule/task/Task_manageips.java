package com.kwic.web.schedule.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

@Component
public class Task_manageips extends Task{
	private static Logger log = LoggerFactory.getLogger(Task_manageips.class);
	
	private static String MANAGE_IPS;
	
	public static String getManageIps(){
		try{
		if(MANAGE_IPS==null)
			load();
		}catch(Exception e){
			log.error("Loading managed IPs is failed.", e);
		}
		return MANAGE_IPS;
	}
	
	public Task_manageips() throws Exception {
		super();
	}
	
	protected void execute(Map<String,String> params) throws Exception{
		try{
			load();
		}catch(Exception e){
			throw e;
		}finally{
			logger.debug(key+" .:. "+new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(java.util.Calendar.getInstance().getTime()));
		}
	}
	
	private static void load() throws Exception{
		String path	= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/config/ManageIPs.cfg");
		StringBuffer sb	= new StringBuffer();
		BufferedReader br	= null;
		String line	= null;
		try{
			br	= new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
			
			while((line=br.readLine())!=null){
				sb.append(line).append(System.getProperty("line.separator"));
			}
			MANAGE_IPS	= sb.toString();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(br!=null)br.close();}catch(Exception ex){}
		}
	}
}