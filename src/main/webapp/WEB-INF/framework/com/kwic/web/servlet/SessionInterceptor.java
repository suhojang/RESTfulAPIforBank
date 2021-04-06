package com.kwic.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.security.aes.JSecurity;
import com.kwic.web.controller.Controllers;

public class SessionInterceptor extends DefaultInterceptor{
	public static final String SESSION_ID_ATTRIBUTE_NAME	= "KWIC_SESSION_ID_ATTRIBUTE_NAME";
	
    /** session attribute name **/
    private String atrtributeName;
    /** redirect url **/
    private String redirectUrl;
	protected ArrayList<String> exceptSessionUriPattern;

    public void setAtrtributeName(String atrtributeName){
        this.atrtributeName				= atrtributeName;
    }
    public void setRedirectUrl(String redirectUrl){
        this.redirectUrl				= redirectUrl;
    }
    public void setExceptSessionUriPattern(ArrayList<String> exceptSessionUriPattern){
        this.exceptSessionUriPattern = exceptSessionUriPattern;
    }
    
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	String uri	= getRequestURI(request);
    	/**** session life time initialize *****************/
    	if(!isCheckedUri(uri,exceptSessionUriPattern))
    		SessionManager.getInstance().resetLastConnectedTime(request.getSession().getId());
    	/***************************************************/
    	
    	if(!isContainedUri(uri))
    		return true;

    	Object sessionAttribute	= request.getSession().getAttribute(atrtributeName);
    	
    	if(sessionAttribute!=null)
    		return true;
    	
    	int uriPattern	= getUriPattern(uri);

    	//ajax response
    	if(uriPattern==URI_PATTERN_AJAX){
    		Map<String,Object> obj	= new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD, "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOSESSION);
			obj.put(Controllers.RESULT_MSG, "로그인 정보를 찾을 수 없습니다.\n\n다시 로그인하여 주십시오.");
			obj.put(JSecurity.CRYPT_KEY_ID, request.getSession().getAttribute(JSecurity.CRYPT_KEY_ID)==null?'N':'Y');
    			
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
    	}else if(uriPattern==URI_PATTERN_DOWNLOAD || uriPattern==URI_PATTERN_EXCEL){
    		ModelAndView modelAndView	= new ModelAndView("redirect:"+redirectUrl);
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("로그인 정보를 찾을 수 없습니다.\n\n다시 로그인하여 주십시오."));
    		modelAndView.addObject(Controllers.REDIRECT_TYPE, "DOWNLOAD" );
    		modelAndView.addObject(Controllers.REQUEST_ERROR_MSG, "NO_SESSION");
    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, "로그인 정보를 찾을 수 없습니다.\n\n다시 로그인하여 주십시오.");
    		request.setAttribute("REQUEST_URI", getRequestURI(request));
    		throw new ModelAndViewDefiningException(modelAndView);
    	}else{
        	
    		ModelAndView modelAndView	= new ModelAndView("redirect:"+redirectUrl);
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("로그인 정보를 찾을 수 없습니다.\n\n다시 로그인하여 주십시오."));
    		modelAndView.addObject(Controllers.REQUEST_ERROR_MSG, "NO_SESSION");
    		modelAndView.addObject("REQUEST_URI", getRequestURI(request));
    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, "로그인 정보를 찾을 수 없습니다.\n\n다시 로그인하여 주십시오.");
    		throw new ModelAndViewDefiningException(modelAndView);
    	}
    }
}
