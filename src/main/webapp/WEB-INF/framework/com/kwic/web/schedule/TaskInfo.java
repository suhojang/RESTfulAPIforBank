package com.kwic.web.schedule;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kwic.web.schedule.task.Task;

public class TaskInfo {
	private FileSystemXmlApplicationContext context;
	private String key;
	private boolean isRunning;
	private Task task;
	private boolean shutdown;
	
	public void setContext(FileSystemXmlApplicationContext context){
		this.context	= context;
	}
	public void setKey(String key){
		this.key	= key;
	}
	public void setRunning(boolean isRunning){
		this.isRunning	= isRunning;
	}
	public void setTask(Task task){
		this.task	= task;
	}
	
	public FileSystemXmlApplicationContext getContext(){
		return context;
	}
	public String getKey(){
		return key;
	}
	public boolean isRunning(){
		return isRunning;
	}
	public Task getTask(){
		return task;
	}
	public void shutdown(){
		shutdown	= true;
	}
	public boolean isShutdown(){
		return shutdown;
	}
	
}
