package com.kwic.web.schedule.task;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;

import project.client.execute.KsnetOpenManager;

/**
 * @파일명	: Task_ksnetinit
 * @작성일	: 2020. 9. 22.
 * @작성자	: 장수호
 * @설명		: 개시여부, 개시일자 초기화 
 * @변경이력	:
 */
@Component
public class Task_ksnetinit extends Task{
	private static Logger logger = LoggerFactory.getLogger(Task_ksnetinit.class);
	
	public Task_ksnetinit() throws Exception {
		super();
	}
	
	protected void execute(Map<String,String> params) throws Exception {
		try {
			KsnetOpenManager.getInstance().KsnetOpenInit();
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
}