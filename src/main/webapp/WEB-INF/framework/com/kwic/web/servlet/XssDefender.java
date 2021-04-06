package com.kwic.web.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.exception.DefinedException;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

public class XssDefender extends DefaultInterceptor{

	private String[] filter;
	
	public void setFilter(String filterString){
		this.filter	= filterString.split(",");
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	String uri	= getRequestURI(request);
    	
    	if(!isContainedUri(uri))
    		return true;

    	Enumeration<String> keys	= request.getParameterNames();
    	String key	= null;
    	String message	= null;
    	try{
    		//XSS, SQLInjection filter
	    	while(keys.hasMoreElements()){
	    		key	= keys.nextElement();
	    		checkParameter(request.getParameter(key));
	    	}
    	}catch(Exception e){
    		message	= e.getMessage();
    	}
    	if(message==null)
    		return true;
    	//비정상파라미터에 대한 오류 처리
    	int uriPattern	= getUriPattern(uri);
		
    	if(uriPattern==URI_PATTERN_AJAX){
    		Map<String,Object> obj	= new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD, "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_IVALIDPARAM);
			obj.put(Controllers.RESULT_MSG, message);
    			
	    	String jsonString = null;
	    	try{
	    		jsonString	= new ObjectMapper().writeValueAsString(obj);
		    	String callback	= request.getParameter("callback");
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().append(callback==null||"".equals(callback)?"":callback).append("(").append(jsonString).append(")");
				response.getWriter().close();
	    	}catch(Exception e){
	    		logger.error(e);
	    	}
	    	return false;
    	}else{
    		ModelAndView modelAndView	= new ModelAndView();
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception(message));
    		modelAndView.addObject(Controllers.REQUEST_ERROR_MSG, message);
    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, message);
    		throw new ModelAndViewDefiningException(modelAndView);
    	}
    }
	
	public void checkParameter(String param) throws Exception{
		if(param==null || "".equals(param))
			return;
		String lowerParam	= param.toLowerCase(Locale.KOREA);
		for(int i=0;i<filter.length;i++)
			if(lowerParam.indexOf(filter[i])>=0)
				throw new DefinedException("입력값으로 ["+StringUtil.replace(StringUtil.replace(StringUtil.replace(filter[i],"<","&lt;"),">","&gt;"),"\"","&quot;")+"]를 사용할 수 없습니다.");
	}
	
}
