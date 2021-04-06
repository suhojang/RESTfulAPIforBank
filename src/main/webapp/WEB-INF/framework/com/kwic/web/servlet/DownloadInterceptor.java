package com.kwic.web.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.kwic.exception.DefinedException;
import com.kwic.util.StringUtil;

public class DownloadInterceptor extends DefaultInterceptor{
	public static final String SESSION_ID_ATTRIBUTE_NAME	= "KWIC_SESSION_ID_ATTRIBUTE_NAME";
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception{
    	String uri	= getRequestURI(request);
    	if(!isContainedUri(uri))
    		return true;
    	String downCookieName	= getParam(request,"downCookieName",100);
    	
    	if(!"".equals(downCookieName))
    		request.getSession().setAttribute(downCookieName, "N");
    	
		return true;
    }
	
    /**<pre>
     * Request에 대한 모든 처리가 완료된 후 실행된다.
     * 1. session에 현재 request URI를 저장한다.
     * 2. session에 현재 servletPath(jsp 경로)를 저장한다.
     * 3. RequestHolder에 저장한 정보를 제거한다.
     * </pre>
     * @param httpservletrequest HttpServletRequest
     * @param httpservletresponse HttpServletResponse
     * @param handler controller
     * @param exception Exception
     * **/
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception{
    	String uri	= getRequestURI(request);
    	if(!isContainedUri(uri))
    		return;
    	String downCookieName	= getParam(request,"downCookieName",100);
		String downCompleteValue	= getParam(request,"downCompleteValue",100);
    	if(!"".equals(downCookieName))
    		request.getSession().setAttribute(downCookieName, downCompleteValue);
    }
 	
	public String getParam(HttpServletRequest request,String paramId,int maxByteSize) throws Exception{
		String param	= checkParameter(request,paramId);
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramId+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public String checkParameter(HttpServletRequest request, String paramId) throws Exception{
		return checkParameter(String.valueOf(request.getParameter(paramId)==null?"":request.getParameter(paramId)), paramId);
	}
	public String checkParameter(String param, String paramId) throws Exception{
		if(param==null)
			return "";
		
		String lowerParam	= param.toLowerCase(Locale.KOREA);
		String[] keywords	= new String[]{
				"<script","javascript","<iframe","<object","<applet","<embed","<form"
				,"select","insert","update","delete","merge","drop","create","declare"
				,"/*","*/","--"
		};
		for(int i=0;i<keywords.length;i++)
			if(lowerParam.indexOf(keywords[i])>=0)
				throw new DefinedException("입력값으로 ["+StringUtil.replace(StringUtil.replace(keywords[i],"<","&lt;"),">","&gt;")+"]를 사용할 수 없습니다.");
		
		return param;
	}
}
