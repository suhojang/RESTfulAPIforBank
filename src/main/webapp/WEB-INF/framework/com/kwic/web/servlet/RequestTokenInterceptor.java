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
import com.kwic.support.CryptoKeyGenerator;
import com.kwic.web.controller.Controllers;

public class RequestTokenInterceptor extends DefaultInterceptor{
	public static final String ATTRIBUTE_NAME	= "KWIC_REQUEST_TOKEN_ATTRIBUTE";
	
	private ArrayList<String> checkUrlPattern;
	
    public void setCheckUrlPattern(ArrayList<String> checkUrlPattern){
        this.checkUrlPattern = checkUrlPattern;
    }
    
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	String uri	= getRequestURI(request);
    	
    	boolean match	= true;
		if(!isExcludeUri(uri) && isCheckedUri(uri,checkUrlPattern)){
			String ssToken	= (String)request.getSession().getAttribute(ATTRIBUTE_NAME);
			String rqToken	= (String)request.getParameter(ATTRIBUTE_NAME);
			if(ssToken==null || !ssToken.equals(rqToken))
				match	= false;
		}
		if( (!isExcludeUri(uri) && (isIncludeUri(uri) || isCheckedUri(uri,checkUrlPattern))) || request.getSession().getAttribute(ATTRIBUTE_NAME)==null){
			request.getSession().setAttribute(ATTRIBUTE_NAME,CryptoKeyGenerator.getRandomKey(CryptoKeyGenerator.ALGORITHM_AES256,new int[]{CryptoKeyGenerator.KEY_TYPE_NUM,CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL,CryptoKeyGenerator.KEY_TYPE_ENG_SMALL}));
		}
		//정상토큰이라면 return;
		if(match)
			return true;

    	//비정상토큰에 대한 오류 처리
    	int uriPattern	= getUriPattern(uri);
		
    	if(uriPattern==URI_PATTERN_AJAX){
    		Map<String,Object> obj	= new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD, "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOTOCKEN);
			obj.put(Controllers.RESULT_MSG, "사용이 만료된 요청입니다.");
    			
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
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("사용이 만료된 요청입니다."));
    		modelAndView.addObject(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOTOCKEN);
    		modelAndView.addObject(Controllers.REQUEST_ERROR_MSG, "사용이 만료된 요청입니다.");
    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, "사용이 만료된 요청입니다.");
    		throw new ModelAndViewDefiningException(modelAndView);
    	}
    }

}
