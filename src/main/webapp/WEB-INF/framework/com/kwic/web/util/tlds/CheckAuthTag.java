package com.kwic.web.util.tlds;

import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.kwic.xml.parser.JXParser;

/**
 *<pre>
 *jsp 에서 권한목록에 따라 화면 component의 visible여부 결정
 *  [사용 예]
 *      <kwic:auth uriXml="${sessionScope.USR_AUTHS}" contain="N" authUri="/STS_WRS_010000">
 *        <input type='button' value='권한없는 버튼1'/>
 *      </kwic:auth>
 *      <kwic:auth uriXml="${sessionScope.USR_AUTHS}" authUri="/STS_WRS_010000">
 *        <input type='button' value='권한있는 버튼1'/>
 *      </kwic:auth>
 * 
 *   수정일         수정자                   수정내용
 *  -------        --------    ---------------------------
 *  2018. 02. 11    장정훈        최초작성
 * </pre>
 * @author 장정훈
 * @since 2019. 02. 11
 * @version 1.0
 * @see TagSupport
 * @since 1.6
 */
public class CheckAuthTag extends ConditionalTagSupport {
	/** serial version UID **/
    private static final long serialVersionUID = 1L;
    
    private JXParser uriJxp;
    private String authUri;
    private String contain	= "Y";//default=Y,  Y=권한이있다면, N=권한이없다면
    
    public void setAuthUri(String authUri){
    	this.authUri	= authUri;
    }
    public void setContain(String contain){
    	this.contain	= contain;
    }
    public void setUriXml(Object uriXml){
    	try{
	    	if(uriXml instanceof String)
	    		this.uriJxp	= new JXParser((String)uriXml);
	    	else if(uriXml instanceof JXParser)
	    		this.uriJxp	= (JXParser)uriXml;
    	}catch(Exception e){
    		uriJxp	= null;
    	}
    }
	@Override
	protected boolean condition(){
		boolean auth	= false;
    	try{
    		auth	= uriJxp.getElement("//URI[@id='"+authUri+"']")!=null?true:false;
    		if("Y".equals(contain))
    			return auth;
   			else
    			return !auth;
    	}catch(Exception e){
    		auth	= false;
    	}
    	return auth;
	}
}
