package com.kwic.web.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.web.controller.Controllers;
import com.kwic.xml.parser.JXParser;

public class AuthInterceptor extends DefaultInterceptor{
	public static final String URI_ELEMENT_NAME			= "URI";
	public static final String URI_ID_ATTRIBUTE_NAME	= "id";
	
    /** session attribute name **/
    private String attributeName;
    /** redirect url **/
    private String redirectUrl;
    private String keyName;
    
    /**
     * default constructor
     * **/
    public AuthInterceptor(){
    }
    public void setAttributeName(String attributeName){
        this.attributeName	= attributeName;
    }
    public void setKeyName(String keyName){
        this.keyName = keyName;
    }
    public void setRedirectUrl(String redirectUrl){
        this.redirectUrl				= redirectUrl;
    }
	@SuppressWarnings("unchecked")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	String uri	= getRequestURI(request);

    	if(!isContainedUri(uri))
    		return true;
		
		try{
			String errMsg	= null;
			if(attributeName==null || "".equals(attributeName) || keyName==null || "".equals(keyName)){
				errMsg	= "접근권한 설정에 오류가 있습니다. 관리자에 문의하여 주십시오.";
			}else if(attributeName!=null && !"".equals(attributeName) && request.getSession().getAttribute(attributeName)==null || ((Map<String,Object>)(request.getSession().getAttribute(attributeName))).get(keyName)==null){
				errMsg	= "해당 경로에 대한 접근권한이 없습니다. 관리자에 문의하여 주십시오.";
			}else{
				JXParser uriTree	= (JXParser)((Map<String,Object>)(request.getSession().getAttribute(attributeName))).get(keyName);
		    	Element uriE	= null;
		    	if(uriTree!=null){
		    		try{
		    			uriE	= uriTree.getElement("//"+URI_ELEMENT_NAME+"[@"+URI_ID_ATTRIBUTE_NAME+"='"+uri+"']");
						if(uriE==null)
							errMsg	= "해당 경로에 대한 접근 권한이 없습니다.\n\n다시 로그인하여 주십시오.";
						else if(!"Y".equals(uriTree.getAttribute(uriE, "use")))
							errMsg	= "허가되지 않은 경로입니다.\n\n관리자에게 문의하여 주십시오.";
					}catch(Exception e) {
						logger.error("접근권한 확인 중 오류가 발생하였습니다.", e);
						errMsg	= "접근권한 확인 중 오류가 발생하였습니다.";
					}
		    	}else{
					errMsg	= "해당 경로에 대한 접근권한이 없습니다. 관리자에 문의하여 주십시오.";
		    	}
			}
	    	if(errMsg==null)
	    		return true;
	    	
	    	int uriPattern	= getUriPattern(uri);

	    	if(uriPattern==URI_PATTERN_AJAX){
	    		Map<String,Object> obj	= new HashMap<String,Object>();
				obj.put(Controllers.RESULT_CD, "N");
				obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOAUTH);
				obj.put(Controllers.RESULT_MSG, errMsg);
	    			
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
	    		modelAndView.addObject(Controllers.EXCEPTION, new Exception(errMsg));
	    		modelAndView.addObject(Controllers.REDIRECT_TYPE, "DOWNLOAD" );
	    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, "UNAUTHORIZED");
	    		throw new ModelAndViewDefiningException(modelAndView);
	    	}else{
	    		ModelAndView modelAndView	= new ModelAndView("redirect:"+redirectUrl);
	    		modelAndView.addObject(Controllers.EXCEPTION, new Exception(errMsg));
	    		modelAndView.addObject(Controllers.REQUEST_ERROR_MSG, "NO_SESSION");
	    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, errMsg);
	    		throw new ModelAndViewDefiningException(modelAndView);
	    	}
		}catch(ModelAndViewDefiningException me){
			throw me;
		}catch(Exception e){
			return false;
		}
    }
}
