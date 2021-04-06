package com.kwic.web.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

import com.kwic.util.StringUtil;
import com.kwic.web.schedule.task.Task;
import com.kwic.xml.parser.JXParser;

import egovframework.rte.fdl.property.EgovPropertyService;

public class Scheduler{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static Scheduler instance;
	private static EgovPropertyService properties;
	private static JXParser schedules;
	private static String schedulepath;
	private static Map<String,TaskInfo> contextMap	= new HashMap<String,TaskInfo>();
	private static boolean startup	= false;
	
	private Scheduler(){
	}
	
	public static Scheduler getInstance(){
		synchronized(Scheduler.class){
			if(instance==null){
				instance	= new Scheduler();
			}
			return instance;
		}
	}
	
	public void setSchedulepath(final String schedulepath) throws Exception{
		new Thread(){
			public void run(){
				try{
					while(properties==null){
						try{properties	= (EgovPropertyService)ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");}catch(Exception e){logger.debug("Waiting for initializing.");}
						try{Thread.sleep(1*1000);}catch(Exception ex){}
					}
					if(schedulepath.startsWith("/WEB-INF"))
						Scheduler.schedulepath	= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(schedulepath);
					else
						Scheduler.schedulepath = schedulepath;
					
					if(properties.getBoolean("isMaster"))
						startupAllApplications();
				}catch(Exception e){
					logger.error("Could not run scheduler",e);
				}
			}
		}.start();
	}
	
	public boolean isSchedulerStartUp(){
		return startup;
	}
	
	public String getSchedulepath(){
		return schedulepath;
	}
	
	public ApplicationContext getContext(String key){
		return null;
	}
	
	public void startupAllApplications() throws Exception{
		logger.info("--------------- scheduler services start. --------------- ");
		loadAllXmlApplications();
		logger.info("--------------- scheduler services end. --------------- ");
	}
	public void shutdownAllApplication() throws Exception{
		if(!startup)
			return;
		String[] keys	= getCurrentKeys();
		for(int i=0;i<keys.length;i++){
			shutdown(keys[i],true);
		}
		shutdown();
		logger.info("--------------- Shutdown all scheduler services. --------------- ");
	}
	public static void shutdown(){
		startup	= false;
	}
	
	private void loadAllXmlApplications() throws Exception{
		startup	= true;
		
		schedules	= new JXParser(new File(schedulepath,"schedules.xml"));
		Map<String,Map<String,String>> list	= new HashMap<String,Map<String,String>>();
		
		Element[] nodes	= schedules.getElements("//entry");
		String key		= null;
		String field	= null;
		String entry	= null;
		for(int i=0;i<nodes.length;i++){
			entry	= schedules.getAttribute(nodes[i],"key");
			key		= entry.substring(0,entry.indexOf("_"));
			field	= entry.substring(entry.indexOf("_")+1);
			
			if(list.get(key)==null)
				list.put(key,new HashMap<String,String>());
			
			list.get(key).put(field, schedules.getValue(nodes[i]));
		}
		
		Iterator<String> iter	= list.keySet().iterator();
		while(iter.hasNext()){
			key	= iter.next();
			
			startup(list.get(key));
		}
	}

	private String baseScheduleText	= null;
	private String getBaseText() throws Exception{
		if(baseScheduleText!=null)
			return baseScheduleText;
		StringBuffer sb		= new StringBuffer();
		BufferedReader br	= null;
		String line			= null;
		try{
			br	= new BufferedReader(new InputStreamReader(new FileInputStream(new File(schedulepath,"schedule-base.xml"))));
			while((line=br.readLine())!=null)
				sb.append(line).append(System.getProperty("line.separator"));
			br.close();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(br!=null)br.close();}catch(Exception e){br=null;}
		}
		return sb.toString();
	}
	
	public synchronized void startup(Map<String,String> sinfo) throws Exception{
		String key	= sinfo.get("key");
//		if(!new File(schedulepath,"context-schedule-"+key+".xml").exists()){
			FileWriter fw		= null;
			try{
				File file	= new File(schedulepath,"context-schedule-"+key+".xml");
				
				fw	= new FileWriter(file);
				fw.write(StringUtil.replace(StringUtil.replace(getBaseText(), "$path$", schedulepath),"$key$",key));
				fw.flush();
			}catch(Exception e){
				throw e;
			}finally{
				try{if(fw!=null)fw.close();}catch(Exception e){fw=null;}
			}
//		}

		TaskInfo info	= null;
		File file		= new File(schedulepath,"context-schedule-"+key+".xml");
		
		if(contextMap.get(key)!=null && contextMap.get(key).getContext()!=null && (contextMap.get(key).getContext().isRunning()||contextMap.get(key).getContext().isActive()))
			shutdown(key);
		
		FileSystemXmlApplicationContext context	= null;
		context	= new FileSystemXmlApplicationContext(
				file.getCanonicalPath()
			);
		String [] names	= context.getBeanFactory().getBeanDefinitionNames();
		Task task	= null;
		for(int i=0;i<names.length;i++){
			if(context.getBean(names[i]) instanceof Task){
				task	= (Task)context.getBean(names[i]);
				task.setKey(key);
				task.setContextPath(schedulepath);
				loadProperties(task, sinfo);
			}
		}
		
		info	= new TaskInfo();
		info.setKey(key);
		info.setContext(context);
		//이전에 스케쥴이 구동된 적이 있다면 이전의 구동정보를 적용
		if(contextMap.get(key)!=null && contextMap.get(key).getTask()!=null && contextMap.get(key).isRunning()){
			info.setTask(contextMap.get(key).getTask());
			info.setRunning(contextMap.get(key).isRunning());
			
			contextMap.get(key).getTask().setCron(task.getCron());
			contextMap.get(key).getTask().setName(task.getName());
			contextMap.get(key).getTask().setBizName(task.getBizName());

		}else if(contextMap.get(key)!=null && contextMap.get(key).getTask()!=null){
			
			task.setLastStatus(contextMap.get(key).getTask().getLastStatus());
			task.setLastStartDate(contextMap.get(key).getTask().getLastStartDate());
			task.setLastEndDate(contextMap.get(key).getTask().getLastEndDate());
			task.setLastMessage(contextMap.get(key).getTask().getLastMessage());
			task.setSuccessCount(contextMap.get(key).getTask().getSuccessCount());
			task.setErrorCount(contextMap.get(key).getTask().getErrorCount());
			
			info.setTask(task);
			info.setRunning(false);
		}else{
			info.setTask(task);
			info.setRunning(false);
		}
		
		contextMap.put(key,info);
		
		logger.info("--------------- scheduler ["+key+"] started. --------------- ");
	}
	
	//설정파일 조회
	private void loadProperties(Task task, Map<String,String> sinfo){
		String key	= sinfo.get("key");
		Map<String,String> paramMap	= null;
		try{
			task.setParams(sinfo.get("params"));
			task.setName(sinfo.get("name"));
			
			paramMap	= parseParams(task.getParams());
			task.setParamMap(paramMap);
			task.setCron(sinfo.get("cron"));
			task.setIsLastDay("Y".equals(sinfo.get("lastday"))?true:false);
			task.setSingleJob("1".equals(sinfo.get("type"))?true:false);
		}catch(Exception e){
			logger.error("["+key+"] error occurs.", e);
		}
	}
	
	//구동중지
	public void shutdown(String key) throws Exception{
		shutdown(key,false);
	}
	
	//구동중지, remove=true라면 영구삭제
	public synchronized void shutdown(String key,boolean remove) throws Exception{
		if(contextMap.get(key)==null || contextMap.get(key).getContext()==null)
			return;
		if(contextMap.get(key).getContext().isRunning())
			contextMap.get(key).getContext().stop();
		if(contextMap.get(key).getContext().isActive())
			contextMap.get(key).getContext().close();
		
		contextMap.get(key).shutdown();
		
		if(remove && contextMap.get(key).isRunning())
			contextMap.get(key).getTask().setRemoved(true);
		else if(remove)
			contextMap.remove(key);
		
		logger.info("--------------- scheduler ["+key+"] terminated. --------------- ");
	}
	
	public synchronized void remove(String key){
		contextMap.remove(key);
	}
	
	public void setTask(String key, Task task){
		if(contextMap.get(key)==null)
			return;
		if(contextMap.get(key).getTask()!=null){
			task.setLastStatus(contextMap.get(key).getTask().getLastStatus());
			task.setLastStartDate(contextMap.get(key).getTask().getLastStartDate());
			task.setLastEndDate(contextMap.get(key).getTask().getLastEndDate());
			task.setLastMessage(contextMap.get(key).getTask().getLastMessage());
			task.setSuccessCount(contextMap.get(key).getTask().getSuccessCount());
			task.setErrorCount(contextMap.get(key).getTask().getErrorCount());
		}
		
		contextMap.get(key).setTask(task);
	}
	public Task getTask(String key){
		if(contextMap.get(key)==null)
			return null;
		return contextMap.get(key).getTask();
	}
	public String[] getCurrentKeys(){
		Object[] arr	= contextMap.keySet().toArray();
		String[] keys	= new String[arr.length];
		
		for(int i=0;i<arr.length;i++){
			keys[i]	= String.valueOf(arr[i]);
		}
		Arrays.sort(keys);
		return keys;
	}
	
	public void setRunning(String key, boolean isRunning){
		if(contextMap.get(key)==null)
			return;
		contextMap.get(key).setRunning(isRunning);
		if(isRunning)
			logger.debug("["+key+"] task on.");
		else
			logger.debug("["+key+"] task off.");
	}
	
	public boolean isRunning(String key){
		if(contextMap.get(key)==null)
			return false;
		return contextMap.get(key).isRunning();
	}
	public boolean isShutdown(String key){
		if(contextMap.get(key)==null)
			return false;
		return contextMap.get(key).isShutdown();
	}
	public Date getLastStartDate(String key){
		if(contextMap.get(key)==null || contextMap.get(key).getTask()==null)
			return null;
		return contextMap.get(key).getTask().getLastStartDate();
	}
	public Date getLastEndDate(String key){
		if(contextMap.get(key)==null || contextMap.get(key).getTask()==null)
			return null;
		return contextMap.get(key).getTask().getLastEndDate();
	}
	public int getLastStatus(String key){
		if(contextMap.get(key)==null || contextMap.get(key).getTask()==null)
			return Task.STATUS_NOTYET;
		return contextMap.get(key).getTask().getLastStatus();
	}
	public String getLastMessage(String key){
		if(contextMap.get(key)==null || contextMap.get(key).getTask()==null)
			return null;
		return contextMap.get(key).getTask().getLastMessage();
	}

	public void setLastStatus(String key,int status){
		if(contextMap.get(key)==null || contextMap.get(key).getTask()==null)
			return;
		contextMap.get(key).getTask().setLastStatus(status);
	}
	public void setLastMessage(String key,String message){
		if(contextMap.get(key)==null || contextMap.get(key).getTask()==null)
			return;
		contextMap.get(key).getTask().setLastMessage(message);
	}
	
	public static Map<String,String> parseParams(String params){
		Map<String,String> paramMap	= new HashMap<String,String>();
		if(params==null || "".equals(params.trim()) || params.indexOf("=")<0)
			return paramMap;
		String[] arr	= StringUtil.split(params, ",");
		String[] vals	= null;
		for(int i=0;i<arr.length;i++){
			vals	= StringUtil.split(arr[i], "=");	
			if(vals.length==2)
				paramMap.put(vals[0].toUpperCase().trim(), StringUtil.replace(vals[1].toUpperCase(),"$c;",","));
			else if(vals.length==1)
				paramMap.put(vals[0].toUpperCase().trim(), "");
		}
		return paramMap;
	}
}
