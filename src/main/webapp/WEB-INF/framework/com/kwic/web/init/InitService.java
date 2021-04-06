package com.kwic.web.init;

import java.util.Map;

public interface InitService {
	void setServiceName(String serviceName);
	String getServiceName();
	
	void init(Map<String,Object> params) throws Exception;
	void service() throws Exception;
	void execute() throws Exception;
	void pauseTask();
	void resumeTask();
	boolean isRun();
	boolean isPaused();
	void terminate();
}
