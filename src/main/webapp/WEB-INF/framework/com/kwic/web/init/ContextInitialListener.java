package com.kwic.web.init;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.dom4j.Element;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.kwic.web.schedule.Scheduler;
import com.kwic.xml.parser.JXParser;

public class ContextInitialListener extends ContextLoaderListener{
	protected static org.slf4j.Logger logger	= LoggerFactory.getLogger(ContextInitialListener.class);
	
	private static Map<String,InitService> taskMap	= new Hashtable<String,InitService>();
	private static String configPath;
	private static JXParser initConfig;
	
	public ContextInitialListener() {
	}
	public ContextInitialListener(WebApplicationContext context) {
		super(context);
	}
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		logger.info(">> ContextInitialListener try to deploy services");
		
		/**
		 * init-service.xml [service] 구동
		 */
		configPath	= event.getServletContext().getRealPath("/WEB-INF/config/init/init-service.xml");
		if(!new File(configPath).exists())
			return;
		try{
			loadConfig();
			Element[] services	= initConfig.getElements("//service");
			for(int i=0;i<services.length;i++){
				logger.debug("\t>"+initConfig.getValue(initConfig.getElement(services[i],"name"))+" : "+new Boolean(initConfig.getValue(initConfig.getElement(services[i],"runnable"))).booleanValue());
				startService(services[i],new Boolean(initConfig.getValue(initConfig.getElement(services[i],"runnable"))).booleanValue());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		
		
		
		logger.info(">> ContextInitialListener deploy services completely.");
	}
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info(">> context initial services destroyed");
		
		//스케쥴 xml 데몬 종료
		try {
			Scheduler.getInstance().shutdownAllApplication();
		} catch (Exception e) {
			logger.error("schedule xml shutdown failed.", e);
		}
		
		Iterator<String> iter	= taskMap.keySet().iterator();
		String name	= null;
		while(iter.hasNext()) {
			name	= iter.next();
			try {
				taskMap.get(name).terminate();
			}catch(Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		super.contextDestroyed(event);
	}
	
	public static void loadConfig() {
		try{
			initConfig	= new JXParser(new File(configPath));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static boolean startService(Element serviceElement,boolean runnable) {
		InitService	service		= null;
		Class<?> serviceClass	= null;
		Map<String,Object> params	= null;
		boolean rst	= false;
		try {
			serviceClass	= Class.forName(initConfig.getValue(initConfig.getElement(serviceElement,"service-class")));
			service			= (InitService)serviceClass.newInstance();
			taskMap.put(initConfig.getValue(initConfig.getElement(serviceElement,"name")),service);
			service.setServiceName(initConfig.getValue(initConfig.getElement(serviceElement,"name")));
			
			Element[] paramElements	= initConfig.getElements(initConfig.getElement(serviceElement,"init-params"),"param");
			params	= new HashMap<String,Object>();
			for(int j=0;j<paramElements.length;j++){
				logger.debug("\tparam : "+initConfig.getAttribute(paramElements[j],"name")+"="+initConfig.getValue(paramElements[j]));
				params.put(initConfig.getAttribute(paramElements[j],"name") , initConfig.getValue(paramElements[j]));
			}
			params.put("init-start"	, initConfig.getValue(initConfig.getElement(serviceElement,"init-start")));
			params.put("init-time"	, initConfig.getValue(initConfig.getElement(serviceElement,"init-time")));
			params.put("sleep-time"	, initConfig.getValue(initConfig.getElement(serviceElement,"sleep-time")));
			params.put("run-times"	, initConfig.getValue(initConfig.getElement(serviceElement,"run-times")));
			
			service.init(params);
			if(runnable)
				service.service();
			rst	= true;
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return rst;
	}
	
	public static boolean startService(String serviceName) {
		boolean rst	= false;
		try {
			if(initConfig==null)
				loadConfig();
			
			Element serviceElement	= initConfig.getElement("//service[name='"+serviceName+"']");
			startService(serviceElement,true);
			
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return rst;
	}

	public static void pauseService(String serviceName) {
		if(taskMap.get(serviceName)==null)
			return;
		taskMap.get(serviceName).pauseTask();
	}
	
	public static void resumeService(String serviceName) {
		if(taskMap.get(serviceName)==null)
			return;
		taskMap.get(serviceName).resumeTask();
	}
	
	public static void terminateService(String serviceName) {
		if(taskMap.get(serviceName)==null)
			return;
		taskMap.get(serviceName).terminate();
	}
	public static void removeTask(String serviceName) {
		if(taskMap.get(serviceName)==null)
			return;
		taskMap.remove(serviceName);
	}
	
	public static InitService getService(String serviceName) {
		return taskMap.get(serviceName);
	}
	
}
