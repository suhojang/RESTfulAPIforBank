package com.kwic.web.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.exception.DefinedException;
import com.kwic.util.StringUtil;

import net.sf.json.JSONObject;

public class WebSocketMessage extends HashMap<String,Object>{
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_FAIL		= "E";
	public static final String STATUS_ACCEPT	= "S";
	
	public static final String WEBSOCKET_KEY_STATUS		= "ws_status";
	public static final String WEBSOCKET_KEY_ID			= "ws_id";
	public static final String WEBSOCKET_KEY_MESSAGE	= "ws_message";
	public static final String WEBSOCKET_KEY_NOTICE_TITLE	= "ws_notice_title";
	public static final String WEBSOCKET_KEY_NOTICE_CONTENT	= "ws_notice_content";
	public static final String WEBSOCKET_KEY_REMAINED	= "remained";
	public static final String WEBSOCKET_KEY_SETTINGS	= "settings";
	
	public WebSocketMessage(){
		
	}
	public void setStatus(String status){
		put(WEBSOCKET_KEY_STATUS, status);
	}
	public String getStatus(){
		return String.valueOf(get(WEBSOCKET_KEY_STATUS));
	}
	public void setMessageId(String messageId){
		put(WEBSOCKET_KEY_ID, messageId);
	}
	public String getMessageId(){
		return String.valueOf(get(WEBSOCKET_KEY_ID));
	}
	public void setMessage(String message){
		put(WEBSOCKET_KEY_MESSAGE, message);
	}
	public void setNoticeTitle(String title){
		put(WEBSOCKET_KEY_NOTICE_TITLE, title);
	}
	public void setNoticeContent(String content){
		put(WEBSOCKET_KEY_NOTICE_CONTENT, content);
	}
	public String getMessage(){
		return String.valueOf(get(WEBSOCKET_KEY_MESSAGE));
	}
	public void setRemained(long remained){
		put(WEBSOCKET_KEY_REMAINED, remained);
	}
	public long getRemained(){
		return Long.parseLong(String.valueOf(get(WEBSOCKET_KEY_REMAINED)==null?"-1":get(WEBSOCKET_KEY_REMAINED)));
	}
	public void setSettings(Map<String,Object> settings){
		put(WEBSOCKET_KEY_SETTINGS,settings);
	}
	public String toJsonString() throws JsonGenerationException, JsonMappingException, IOException{
		return new ObjectMapper().writeValueAsString(this);
	}
	public static WebSocketMessage create(HttpServletRequest request) throws Exception{
		WebSocketMessage message	= new WebSocketMessage();
		message.setStatus(getParam(request,WEBSOCKET_KEY_STATUS,100));
		message.setMessageId(getParam(request,WEBSOCKET_KEY_ID,100));
		message.setMessage(getParam(request,WEBSOCKET_KEY_MESSAGE,20000));
		
		return message;
	}
	public static WebSocketMessage create(String json) throws Exception{
		JSONObject obj	= JSONObject.fromObject(json);
		WebSocketMessage message	= new WebSocketMessage();
		message.setStatus(getParam(WEBSOCKET_KEY_STATUS,obj.getString(WEBSOCKET_KEY_STATUS),100));
		message.setMessageId(getParam(WEBSOCKET_KEY_ID,obj.getString(WEBSOCKET_KEY_ID),100));
		message.setMessage(getParam(WEBSOCKET_KEY_MESSAGE,obj.getString(WEBSOCKET_KEY_MESSAGE),20000));
		
		return message;
	}
	
	public static String getParam(HttpServletRequest request, String paramId,int maxByteSize) throws Exception{
		return getParam(paramId,request.getParameter(paramId),maxByteSize);
	}
	
	public static String getParam(String id,String value,int maxByteSize) throws Exception{
		if(value==null)
			return "";
		
		if(maxByteSize>0 && value.getBytes().length>maxByteSize)
			throw new DefinedException(id+" 제한 크기를 초과하였습니다.");
		
		String lowerParam	= value.toLowerCase(Locale.KOREA);
		String[] keywords	= new String[]{
				"<script","javascript","<iframe","<object","<applet","<embed","<form"
				,"select","insert","update","delete","merge","drop","declare"
				,"/*","*/","--"
		};
		for(int i=0;i<keywords.length;i++)
			if(lowerParam.indexOf(keywords[i])>=0)
				throw new DefinedException("입력값으로 ["+StringUtil.replace(StringUtil.replace(keywords[i],"<","&lt;"),">","&gt;")+"]를 사용할 수 없습니다.");
		
		return value;
	}
	
}
