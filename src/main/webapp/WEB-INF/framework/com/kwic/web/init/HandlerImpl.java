package com.kwic.web.init;

import org.springframework.web.context.ContextLoader;

public abstract class HandlerImpl extends Thread implements Handler{
	
	public static Object getBean(String name){
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}
}
