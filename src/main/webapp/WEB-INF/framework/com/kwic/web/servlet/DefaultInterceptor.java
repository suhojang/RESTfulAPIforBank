package com.kwic.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.Arrays;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;

public class DefaultInterceptor extends HandlerInterceptorAdapter{
	protected Logger logger = LoggerFactory.getLogger(DefaultInterceptor.class);
	
	public static final int FILTER_MAIN_INCLUDE	= 1;
	public static final int FILTER_MAIN_EXCLUDE	= 2;
	
	public static final int[] INVALID_URI_CHARACTERS	= new int[]{0x3C,0x3E,0x27,0x22,0x40,0x5B,0x5D};

	public static final String AJAX_PATTERN	= "/**/*A";
	public static final String DOWNLOAD_PATTERN	= "/**/*E";
	public static final String EXCEL_PATTERN	= "/**/*E";

	public static final int URI_PATTERN_VIEW		= 1;
	public static final int URI_PATTERN_AJAX		= 2;
	public static final int URI_PATTERN_DOWNLOAD	= 3;
	public static final int URI_PATTERN_EXCEL		= 4;
	
	protected PathMatcher pathMatcher = new AntPathMatcher();
	
	protected ArrayList<String> includeUriPattern;
	protected ArrayList<String> excludeUriPattern;
	protected int filterType;
	
    public void setIncludeUrlPattern(ArrayList<String> includeUriPattern){
        this.includeUriPattern = includeUriPattern;
    }
    public void setExcludeUrlPattern(ArrayList<String> excludeUriPattern){
        this.excludeUriPattern = excludeUriPattern;
    }
    public void setFilterType(int filterType){
        this.filterType = filterType;
    }
    /**<pre>
     * Controller의 처리 직후 시행된다.
     * Controller내에서의 오류 발생 시 실행되지 않음에 유의한다.
     * </pre>
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler controller
     * @param modelandview ModelAndView
     * **/
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelandview)throws Exception{
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
    public void afterCompletion(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object handler, Exception exception) throws Exception{
    }
    
    /**
     * 지정된 uri패턴과 일치하는지 검사
     * */
    public boolean isContainedUri(String uri){
		if(filterType==FILTER_MAIN_EXCLUDE){
			if( isExcludeUri(uri) && !isIncludeUri(uri) )
        		return false;
		}else{
			if(isExcludeUri(uri) || !isIncludeUri(uri))
        		return false;
		}
		return true;
    }
    
    /**
     * includeUriPattern 분석
     * */
    public boolean isIncludeUri(String url){
    	return isCheckedUri(url,includeUriPattern);
    }
    /**
     * checkUrlPattern 분석
     * */
    public boolean isCheckedUri(String url,ArrayList<String> checkUrlPattern){
    	if(url==null)
    		return false;
    	String includeUrl	= "";
    	if(checkUrlPattern==null || checkUrlPattern.size()==0)
    		return false;
    	for(int i=0;i<checkUrlPattern.size();i++){
    		if(checkUrlPattern.get(i)==null)
    			return false;
        	includeUrl	= (String)checkUrlPattern.get(i);
        	if( pathMatcher.match(includeUrl, url ) )
        		return true;
    	}
        return false;
    }
    
    /**
     * excludeUriPattern 분석
     * */
    public boolean isExcludeUri(String url){
    	return isCheckedUri(url,excludeUriPattern);
    } 
    
    public int getUriPattern(String uri){
    	if(pathMatcher.match(AJAX_PATTERN, uri ))
    		return URI_PATTERN_AJAX;
    	else if(pathMatcher.match(DOWNLOAD_PATTERN, uri ))
    		return URI_PATTERN_DOWNLOAD;
    	else if(pathMatcher.match(EXCEL_PATTERN, uri ))
    		return URI_PATTERN_EXCEL;
    	else
    		return URI_PATTERN_VIEW;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map cloneParameters(HttpServletRequest request){
    	Map map	= request.getParameterMap();
    	
    	Map savedParametersMap	= new HashMap();
    	
		Iterator<String> itr	= map.keySet().iterator();
		String key	= null;
		while(itr.hasNext()){
			key	= itr.next();
			savedParametersMap.put(key, map.get(key));
		}
    	return savedParametersMap;
    }
    
    public String getRequestURI(HttpServletRequest request){
    	char[] uriChars	= request.getRequestURI().toString().toCharArray();
    	StringBuffer filter = new StringBuffer("");
    	for(int i=0;i<uriChars.length;i++){
        	if(!Arrays.contains(INVALID_URI_CHARACTERS, uriChars[i])){
        		if(i!=uriChars.length-1 || uriChars[i]!='/')
        			filter.append(uriChars[i]);
        	}
    	}
    	if(filter.indexOf(";jsessionid=")>=0)
    		return filter.substring(0, filter.indexOf(";jsessionid="));
    	
    	return filter.toString();
    }
}
