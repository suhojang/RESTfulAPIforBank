package com.kwic.web.servlet;

import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;

import com.kwic.log.Tracker;
import com.kwic.math.Calculator;
import com.kwic.security.aes.AES;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

public class ActionTracker extends DefaultInterceptor{
	
    /** session attribute name **/
    private String sessionAtrtributeName;
    /** 사용자정보를 획득할 수 있는 메소드명 **/
    private String keyMethodName;
    /** tracker id */
    private String id;
    /** 암호화 파라미터 목록 */
    private String encryptParams;
    private long maxLength	= 10*1024*1024;
    private String logPath;
    private Tracker tracker;

    public void setSessionAtrtributeName(String sessionAtrtributeName){
        this.sessionAtrtributeName	= sessionAtrtributeName;
    }
    public void setId(String id){
        this.id	= id;
    }
    
    public void setMaxLength(String maxLength) throws Exception{
    	maxLength	= StringUtil.replace(maxLength, "K", "*1024");
    	maxLength	= StringUtil.replace(maxLength, "M", "*1024*1024");
    	maxLength	= StringUtil.replace(maxLength, "G", "*1024*1024*1024");
    	this.maxLength	= (long) Calculator.calculate(maxLength);
    }
    public void setLogPath(String logPath){
    	if(logPath.startsWith("/"))
    		logPath	= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(logPath);
    	this.logPath	= logPath;
    }
    public void setKeyMethodName(String keyMethodName){
        this.keyMethodName	= keyMethodName;
    }
    public void setEncryptParams(String encryptParams){
        this.encryptParams				= ","+encryptParams+",";
    }
    
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	return true;
    }
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception{
    	String uri	= getRequestURI(request);
    	
    	if(!isContainedUri(uri))
    		return;
    	
    	StringBuffer sb	= new StringBuffer();
    	//1. ip 
    	//2. user agent
    	sb.append(" ").append(Controllers.getRemoteIP(request)).append(" ").append(uri).append(" [").append(request.getHeader("User-Agent")).append("]").append(" - ");
		if(sessionAtrtributeName!=null && request.getSession().getAttribute(sessionAtrtributeName)!=null){
	    	Object sessionAttribute	= request.getSession().getAttribute(sessionAtrtributeName);
	    	String id		= null;
	    	if(sessionAttribute!=null){
	    		try{
	        		Method method	= sessionAttribute.getClass().getMethod(keyMethodName);
	        		id	= (String)method.invoke(sessionAttribute, new Object[]{});
				} catch (Exception e) {
				}
	    	}
	    	//3. 로그인 id
	    	sb.append("[").append(id).append("] ");
		}else{
	    	sb.append("[").append("no session").append("] ");
		}
		//4. 처리결과
		Exception exObj	= null;
		if(ex!=null){
			exObj	= ex;
		}
		if(request.getAttribute("ERROR_OBJECT")!=null){
			exObj	= (Exception)request.getAttribute("ERROR_OBJECT");
		}
    	sb.append(exObj==null?"[SUCC]":"[FAIL]").append(exObj==null?"":StringUtil.replace(exObj.getMessage(), "\n", "§"));
    	
    	//5. request parameters
		Enumeration<?> e	= request.getParameterNames();
		String key	= null;
		StringBuffer paramSb	= new StringBuffer();
		paramSb.append("{");
		while(e.hasMoreElements()){
			if(key!=null)
				paramSb.append(",");
			key	= String.valueOf(e.nextElement());
			try{
				if(encryptParams.indexOf(","+key+",")>=0)
					paramSb.append("\"").append(key).append("\"").append(":").append("\"").append(AES.encode(request.getParameter(key), AES.DEFAULT_KEY ,AES.TYPE_256)).append("\"");
				else
					paramSb.append("\"").append(key).append("\"").append(":").append("\"").append(request.getParameter(key)).append("\"");
			}catch(Exception exp){
				logger.error(exp);
			}
		}
		paramSb.append("}");
		
		sb.append(paramSb);
		
		if(tracker==null)
			tracker	= Tracker.getLogger(id,logPath,maxLength);
		tracker.track(sb.toString());
		
    }

}
