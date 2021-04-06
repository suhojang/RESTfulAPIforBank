package com.kwic.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.math.Calculator;
import com.kwic.web.controller.Controllers;
import com.kwic.web.websocket.WebSocketMessage;

import egovframework.rte.fdl.property.EgovPropertyService;

public class SessionManager {
	public static final String KWIC_SESSION_ID_ATTRIBUTE_NAME	= "HTTP.SESSION.ID";
	
	private static SessionManager instance;
	
	private Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private Map<String,SessionInfo> sessions	= new Hashtable<String,SessionInfo>();
	
	private SessionManager(){}
	
	public static SessionManager getInstance(){
		synchronized(SessionManager.class){
			if(instance==null){
				instance	= new SessionManager();
			}
			return instance;
		}
	}
	
	public void create(HttpSession httpSession){
		String httpSessionId	= httpSession.getId();
		httpSession.setAttribute(KWIC_SESSION_ID_ATTRIBUTE_NAME, httpSessionId);
		
		SessionInfo info	= new SessionInfo();
		info.setHttpSession(httpSession);
		
		sessions.put(httpSessionId, info);
		logger.debug("session created : "+sessions.size()+" sessions.");
	}
	
	public SessionInfo getSessionInfo(String httpSessionId){
		return sessions.get(httpSessionId);
	}
	
	public void putEmergencyNotice(Map<String,String> notice){
		Iterator<String> iter	= sessions.keySet().iterator();
		while(iter.hasNext()){
			sessions.get(iter.next()).putEmergencyNotice(notice);
		}
	}
	
	public void setWebSocketSession(WebSocketSession webSocketSession) throws JsonGenerationException, JsonMappingException, IOException{
		if(webSocketSession==null || webSocketSession.getAttributes()==null || webSocketSession.getAttributes().get(SessionManager.KWIC_SESSION_ID_ATTRIBUTE_NAME)==null)
			return;
		String httpSessionId	= String.valueOf(webSocketSession.getAttributes().get(SessionManager.KWIC_SESSION_ID_ATTRIBUTE_NAME));
		
		if(sessions.get(httpSessionId)==null){
			WebSocketMessage message	= new WebSocketMessage();
			message.setMessageId(WebSocketMessage.STATUS_FAIL);
			message.setMessageId("E0002");
			message.setMessage("Unauthorized web-socket access.");
			webSocketSession.sendMessage(new TextMessage(message.toJsonString()));
			webSocketSession.close();
			return;
		}
		sessions.get(httpSessionId).setWebSocketSession(webSocketSession);
		logger.debug("websocket created : "+sessions.size()+" sessions.");
	}
	
	public void removeWebSocket(WebSocketSession webSocketSession) throws IOException{
		String httpSessionId	= String.valueOf(webSocketSession.getAttributes().get(SessionManager.KWIC_SESSION_ID_ATTRIBUTE_NAME));
		
		if(webSocketSession.isOpen())
			webSocketSession.close();

		if(sessions.get(httpSessionId)==null)
			return;
		
		sessions.get(httpSessionId).getWebSocketSessions().remove(webSocketSession);
	}
	
	public void invalidate(String httpSessionId){
		if(sessions.get(httpSessionId)==null)
			return;

		try{
			sessions.get(httpSessionId).invalidate();
		}catch(IOException e){
			logger.error("An error occured when remove websocket. ["+httpSessionId+"]");
		}
		sessions.remove(httpSessionId);
		logger.debug("session invalidated : "+sessions.size()+" sessions remained.");
	}
	
	public void resetLastConnectedTime(String httpSessionId){
		if(sessions.get(httpSessionId)!=null)
			sessions.get(httpSessionId).resetLastConnectedTime();
	}

	public void checkSessions() throws Exception{
		EgovPropertyService properties	= (EgovPropertyService)ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");
		
		long passedTimeMillis	= 0;
		long sessionTimeout		= (long)Calculator.calculate(properties.getString("sessionclient-sessionTimeout"))*1000;
		long timeoutForWarning	= (long)Calculator.calculate(properties.getString("sessionclient-timeoutForWarning"))*1000;
		
		WebSocketMessage message	= new WebSocketMessage();
		message.setStatus(WebSocketMessage.STATUS_ACCEPT);
		message.setMessageId("W0001");//timeout warning
		message.setMessage(String.valueOf(timeoutForWarning/1000));
		TextMessage txtMessage	= new TextMessage(message.toJsonString());
		
		for(SessionInfo info:sessions.values()){
			try{
				passedTimeMillis	= Calendar.getInstance(Locale.KOREA).getTimeInMillis()-info.getLastConnectedTime();
				
				if(passedTimeMillis>(sessionTimeout-timeoutForWarning)){
					for(WebSocketSession ws:info.getWebSocketSessions()){
						ws.sendMessage(txtMessage);
					}
				}
			}catch(Exception e){
				logger.error(e);
			}
		}
	}
	
	public List<String> getMatchValues(String[] keys,String[] matchValues){
		List<String> values	= new ArrayList<String>();
		StringBuffer sb	= new StringBuffer();
		boolean isMatch	= true;
		Map<?,?> attr	= null;
		for(SessionInfo info:sessions.values()){
			sb.setLength(0);
			isMatch	= true;
			if(info.getHttpSession().getAttribute(Controllers.SESSION_KEY)!=null){
				attr	= (Map<?,?>)info.getHttpSession().getAttribute(Controllers.SESSION_KEY);
				for(int i=0;i<keys.length;i++){
					if(matchValues[i]==null)
						continue;
					if(!matchValues[i].equals(attr.get(keys[i]))){
						isMatch	= false;
						break;
					}
				}
				if(isMatch){
					for(int i=0;i<keys.length;i++){
						sb.append(i==0?"":"_").append(String.valueOf(attr.get(keys[i])));
					}
					values.add(sb.toString());
				}
			}
		}		
		return values;
	}
	
	public Map<String,String> getAllUsers(String useridKey){
		Map<String,String> values	= new HashMap<String,String>();
		Map<?,?> user			= null;
		Iterator<String> iter	= sessions.keySet().iterator();
		String httpSessionId	= null;
		SessionInfo info		= null;
		while(iter.hasNext()){
			httpSessionId	= iter.next();
			info	= sessions.get(httpSessionId);
			if(info.getHttpSession().getAttribute(Controllers.SESSION_KEY)!=null){
				user	= (Map<?,?>)info.getHttpSession().getAttribute(Controllers.SESSION_KEY);
				values.put(String.valueOf(user.get(useridKey)),httpSessionId);
			}
		}
		return values;
	}
	
}
