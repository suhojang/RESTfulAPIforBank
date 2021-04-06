package com.kwic.telegram.push;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONObject;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

public class APNSUtil {
	
	public static void push(String certPath, String certPassword,String tokenId,String cmd,String title,String msg,Map<String,String> param) throws Exception{
    	JSONObject obj	= new JSONObject();
    	if(param!=null)
    		obj.putAll(param);
    	String json	= obj.toString();
		try{
			Map<String,Object> map	= new HashMap<String,Object>();
			map.put("P_CMD_ID"	, cmd);
			map.put("P_SND_TM"	, new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA).format(Calendar.getInstance().getTime()));
	    	map.put("P_TTL"	, URLEncoder.encode(title,"UTF-8"));
	    	map.put("P_MSG"	, URLEncoder.encode(msg,"UTF-8"));
	    	map.put("P_EXTRA"	, URLEncoder.encode(json,"UTF-8"));
			
	    	obj	= new JSONObject();
    		obj.putAll(map);
	    	
    		String message	= obj.toString();
			
			ApnsService service	= APNS.newService().withCert(certPath, certPassword).withSandboxDestination().build();
			String payload	= APNS.newPayload().alertBody(message).build();
			   
			service.push(tokenId, payload);
		}catch(Exception e) {
			throw e;
		}
	}
	
	public static void push(String certPath, String certPassword,List<String> tokenIds,String cmd,String title,String msg,Map<String,String> param) throws Exception{
    	JSONObject obj	= new JSONObject();
    	if(param!=null)
    		obj.putAll(param);
    	String json	= obj.toString();
		try{
			Map<String,Object> map	= new HashMap<String,Object>();
			map.put("P_CMD_ID"	, cmd);
			map.put("P_SND_TM"	, new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA).format(Calendar.getInstance().getTime()));
	    	map.put("P_TTL"	, URLEncoder.encode(title,"UTF-8"));
	    	map.put("P_MSG"	, URLEncoder.encode(msg,"UTF-8"));
	    	map.put("P_EXTRA"	, URLEncoder.encode(json,"UTF-8"));
			
	    	obj	= new JSONObject();
    		obj.putAll(map);
	    	
    		String message	= obj.toString();
			
			ApnsService service	= APNS.newService().withCert(certPath, certPassword).withSandboxDestination().build();
			String payload	= APNS.newPayload().alertBody(message).build();
			   
			service.push(tokenIds, payload);
		}catch(Exception e) {
			throw e;
		}
	}
}
