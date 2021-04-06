package com.kwic.web.schedule.task;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;

import project.client.execute.KsnetOpenManager;

@Component
public class Task_ksnetopen extends Task{
	private static Logger logger = LoggerFactory.getLogger(Task_ksnetopen.class);
	
	public Task_ksnetopen() throws Exception {
		super();
	}
	
	protected void execute(Map<String,String> params) throws Exception {
		try {
			KsnetOpenManager.getInstance().KsnetOpenStart(false);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
}