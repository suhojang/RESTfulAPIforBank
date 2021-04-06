package com.kwic.web.init;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import com.kwic.math.Calculator;

public abstract class InitServiceImpl extends Thread implements InitService{
	protected Logger logger = LoggerFactory.getLogger(InitServiceImpl.class);
	
	protected Map<String,Object> params;
	private String serviceName;
	private boolean isRun	= true;
	private boolean isHold	= true;
	
	public void setServiceName(String serviceName){
		this.serviceName	= serviceName;
	}
	public String getServiceName(){
		return serviceName;
	}
	public void init(Map<String,Object> params) throws Exception{
		this.params	= params;
	}
	public Object getParam(String name){
		return params.get(name);
	}
	public boolean getParamBoolean(String name){
		return Boolean.valueOf(String.valueOf(params.get(name))).booleanValue();
	}
	public String getParamString(String name){
		return String.valueOf(params.get(name));
	}
	public int getParamInt(String name){
		int param	= -1;
		try {
			param	= (int)Calculator.calculate(String.valueOf(params.get(name)));
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	public long getParamLong(String name){
		long param	= -1;
		try {
			param	= (long)Calculator.calculate(String.valueOf(params.get(name)));
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	public float getParamFloat(String name){
		float param	= -1;
		try {
			param	= (float)Calculator.calculate(String.valueOf(params.get(name)));
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	public double getParamDouble(String name){
		double param	= -1;
		try {
			param	= (double)Calculator.calculate(String.valueOf(params.get(name)));
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	public Map<String,Object> getInitialParams(){
		return params;
	}
	public void service() throws Exception {
		this.start();
		logger.info(getServiceName()+" service started.");
	}
	public static Object getBean(String name){
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}
	public void terminate(){
		logger.info(getServiceName()+" terminated");
		isRun	= false;
	}
	public void pauseTask(){
		logger.debug(getServiceName()+" pause.");
		isHold	= true;
	}
	public void resumeTask(){
		logger.debug(getServiceName()+" resume.");
		isHold	= false;
	}
	public boolean isRun(){
		return isRun;
	}
	public boolean isPaused(){
		return isHold;
	}
	
	public void run(){
		try{
			final boolean initStart	= params.get("init-start")==null?false:getParamBoolean("init-start");
			final long initSeconds	= params.get("init-time")==null?0L:getParamLong("init-time");
			final long sleepSeconds	= params.get("sleep-time")==null?0L:getParamLong("sleep-time");
			final long runTimes		= params.get("run-times")==null?0L:getParamLong("run-times");
			int runCount	= 0;
			
			if(initStart)
				resumeTask();
			else
				pauseTask();
			if(initSeconds>0)
				Thread.sleep(initSeconds*1000);
			
			long currentSleepTimes	= 0;
			while(isRun()) {
				if(runTimes>0 && runCount>=runTimes)
					break;
				while(isRun() && isPaused()) {
					try{Thread.sleep(1*1000);}catch(Exception e){logger.error(e.getMessage(),e);}
				}
				try {
					execute();
					if(runTimes>0)
						runCount++;
				}catch(Exception e) {
					logger.error(e.getMessage(),e);
				}
				if(sleepSeconds<=0)
					continue;
				currentSleepTimes	= 0;
				while(currentSleepTimes<sleepSeconds){
					if(!isRun())
						break;
					while(isRun() && isPaused()) {
						try{Thread.sleep(1*1000);}catch(Exception e){logger.error(e.getMessage(),e);}
						if(currentSleepTimes>initSeconds)
							currentSleepTimes	= initSeconds;
						
						currentSleepTimes++;
					}
					try{Thread.sleep(1*1000);}catch(Exception e){logger.error(e.getMessage(),e);}
					currentSleepTimes++;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		ContextInitialListener.removeTask(serviceName);
	}
}
