package com.kwic.web.websocket;

import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.math.Calculator;
import com.kwic.web.servlet.SessionInfo;
import com.kwic.web.servlet.SessionManager;

import egovframework.rte.fdl.property.EgovPropertyService;

public class WebSocketHandler extends TextWebSocketHandler{
	private Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
	
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession websocketSession) throws Exception {
		SessionManager.getInstance().setWebSocketSession(websocketSession);
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage textMessage){
		String httpSessionId	= String.valueOf(session.getAttributes().get(SessionManager.KWIC_SESSION_ID_ATTRIBUTE_NAME));
		String msg	= textMessage.getPayload();
		logger.debug("Message received. : "+msg);
		
		SessionInfo info	= SessionManager.getInstance().getSessionInfo(httpSessionId);
		WebSocketMessage message	= null;
		try {
			message = WebSocketMessage.create(msg);
			message.setStatus(WebSocketMessage.STATUS_ACCEPT);
		} catch (Exception e) {
			if(message!=null){
				message.setStatus(WebSocketMessage.STATUS_FAIL);
				message.setMessageId("E0003");
				message.setMessage("message parsing error.");
			}
			logger.error(e);
		}
		if("W0002".equals(message.getMessageId())){//로그아웃
			message.setMessage("Logout successfully.");
			try{
				session.sendMessage(new TextMessage(message.toJsonString()));
				logger.debug("Message send : "+message.toJsonString());
			}catch(Exception e){
				logger.error(e);
			}
			SessionManager.getInstance().invalidate(session.getId());
		}else if("P0002".equals(message.getMessageId())){//세션연장
			message.setMessage("Extends session timeout successfully.");
			try{
				session.sendMessage(new TextMessage(message.toJsonString()));
				logger.debug("Message send : "+message.toJsonString());
			}catch(Exception e){
				logger.error(e);
			}
			SessionManager.getInstance().resetLastConnectedTime(session.getId());
		}else{
			try{
				long passedTimeMillis	= Calendar.getInstance().getTimeInMillis()-info.getLastConnectedTime();
				long sessionTimeout		= (long)Calculator.calculate(properties.getString("sessionclient-sessionTimeout"))*1000;
				long timeoutForWarning	= (long)Calculator.calculate(properties.getString("sessionclient-timeoutForWarning"))*1000;
				
				message.setStatus(WebSocketMessage.STATUS_ACCEPT);
				
				if(passedTimeMillis>(sessionTimeout-timeoutForWarning)){
					message.setMessageId("W0001");//timeout warning
					message.setMessage(String.valueOf(timeoutForWarning/1000));
				}
				logger.debug("Message send : "+message.toJsonString());
				session.sendMessage(new TextMessage(message.toJsonString()));
			}catch(Exception e){
				logger.error(e);
			}
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		SessionManager.getInstance().setWebSocketSession(null);
	}
}
