package com.kwic.web.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.web.controller.Controllers;
import com.kwic.web.websocket.WebSocketMessage;

public class SessionInfo implements Serializable{
	private static final long serialVersionUID = 100042417L;

	private transient Logger logger = LoggerFactory.getLogger(SessionInfo.class);
	
	private HttpSession httpSession;
	private List<WebSocketSession> webSocketSessions	= new ArrayList<WebSocketSession>();
	private List<Map<String,String>> emergencyNoticeList	= new Vector<Map<String,String>>();	//긴급 공지
	private long lastConnectedTime	= 0L;
	
	public void setHttpSession(HttpSession httpSession){
		this.httpSession	= httpSession;
		resetLastConnectedTime();
	}
	public void setWebSocketSession(WebSocketSession webSocketSession) throws IOException{
		for(int i=webSocketSessions.size()-1;i>=0;i--){
			if(!webSocketSessions.get(i).isOpen()){
				webSocketSessions.remove(webSocketSessions.get(i));
			}
		}
		webSocketSessions.add(webSocketSession);
		logger.debug("["+httpSession.getId()+"] websocket : "+webSocketSessions.size());
	}
	public void resetLastConnectedTime(){
		lastConnectedTime	= Calendar.getInstance(Locale.KOREA).getTimeInMillis();
		logger.debug("["+httpSession.getId()+"] lastConnectedTime : "+lastConnectedTime);
	}
	public HttpSession getHttpSession(){
		return httpSession;
	}
	public List<WebSocketSession> getWebSocketSessions(){
		return webSocketSessions;
	}
	public long getLastConnectedTime(){
		return lastConnectedTime;
	}
	public void putEmergencyNotice(Map<String,String> notice){
		emergencyNoticeList.add(notice);
	}
	public Map<String,String> getEmergencyNotice(){
		if(emergencyNoticeList.size()==0)
			return null;
		Map<String,String> notice	= emergencyNoticeList.get(0);
		emergencyNoticeList.remove(0);
		return notice;
	}
	public void invalidate() throws IOException{
		emergencyNoticeList.clear();
		if(webSocketSessions!=null && webSocketSessions.size()>0){
			WebSocketMessage message	= new WebSocketMessage();
			message.setMessageId(WebSocketMessage.STATUS_ACCEPT);
			message.setMessageId("W0002");
			message.setMessage("Session invalidated.");
			for(int i=webSocketSessions.size()-1;i>=0;i--){
				if(webSocketSessions.get(i).isOpen()){
					webSocketSessions.get(i).sendMessage(new TextMessage(message.toJsonString()));
					webSocketSessions.get(i).close();
				}
				webSocketSessions.remove(i);
			}
		}
		logger.debug("["+(httpSession==null?"":httpSession.getId())+"] websocket : "+(webSocketSessions==null?0:webSocketSessions.size()));
		if(httpSession!=null)
			httpSession.removeAttribute(Controllers.SESSION_KEY);
	}
}
