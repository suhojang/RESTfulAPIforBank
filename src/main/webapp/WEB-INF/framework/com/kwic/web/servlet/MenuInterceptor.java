package com.kwic.web.servlet;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;

import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

public class MenuInterceptor extends DefaultInterceptor{
	public static final String MENU_ELEMENT_NAME			= "MENU";
	public static final String MENU_ID_ATTRIBUTE_NAME		= "id";
	public static final String TARGET_MENU_ATTRIBUTE_NAME	= "TARGET_MENU";
	public static final String ORIGIN_MENU_ATTRIBUTE_NAME	= "ORIGIN_MENU";
    private String attributeName;
    private String keyName;
    /**
     * default constructor
     * **/
    public MenuInterceptor(){
    }
    
    public void setAttributeName(String attributeName){
        this.attributeName = attributeName;
    }
    public void setKeyName(String keyName){
        this.keyName = keyName;
    }
    
	@SuppressWarnings("unchecked")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
		String uri	= getRequestURI(request);
    	if(!isContainedUri(uri))
    		return true;
    	
		if(attributeName==null || "".equals(attributeName) || request.getSession().getAttribute(attributeName)==null || ((Map<String,Object>)(request.getSession().getAttribute(attributeName))).get(keyName)==null)
			return true;

		JXParser menuTree	= (JXParser)((Map<String,Object>)(request.getSession().getAttribute(attributeName))).get(keyName);
    	if(menuTree==null)
			return true;
		
    	String menuId	= "";
    	String oMenuId	= "";
    	Element menu	= null;
    	try{
    		uri	= StringUtil.replace(uri,"/","");
    		if(uri.indexOf("?")>=0)
    			uri	= uri.substring(0,uri.indexOf("?"));
    		
	    	menu	= menuTree.getElement("//"+MENU_ELEMENT_NAME+"[@"+MENU_ID_ATTRIBUTE_NAME+"='"+uri+"']");
	    	if(menu==null){
	    		return true;
	    	}else if("Y".equals(menuTree.getAttribute(menu, "use"))){
	    		menuId	= uri;
	    	}else{
	    		oMenuId	= menuTree.getAttribute(menu, "id");
	    		menuId	= menuTree.getAttribute(menu, "target");
	    	}
	    	if(menuId==null)
	    		return true;
    	}catch(Exception e){
    		logger.error("", e);
    	}
    	request.setAttribute(TARGET_MENU_ATTRIBUTE_NAME,menuId);
    	request.setAttribute(ORIGIN_MENU_ATTRIBUTE_NAME,oMenuId);
    	return true;
    }
	
}
