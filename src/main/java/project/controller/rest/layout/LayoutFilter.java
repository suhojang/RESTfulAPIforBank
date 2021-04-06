package project.controller.rest.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.web.context.ContextLoader;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;
/**
 * json 전문의 유효성 검증
 * 
 * [검증항목]
 * field : maxBytes(최대 bytes 크기), required(필수입력항목), type(데이터타입, C[문자]/N[정수]/D[소수])
 * list : minOccurs(최소반복횟수), maxOccurs(최대반복횟수)
 * */
public class LayoutFilter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * request
	 * */
	public static final int DIRECTION_REQUEST	= 1;
	/**
	 * response
	 * */
	public static final int DIRECTION_RESPONSE	= 2;
	
	/**
	 * field type string
	 * */
	public static final char FIELD_TYPE_CHAR	= 'C';
	/**
	 * field type number
	 * */
	public static final char FIELD_TYPE_NUMBER	= 'N';
	/**
	 * field type number
	 * */
	public static final char FIELD_TYPE_DECIMAL	= 'D';
	/**
	 * data character set
	 * */
	public static final String CHARSET	= "UTF-8";
	
	private static LayoutFilter instance;
	private Hashtable<String,JXParser> layoutInfos	= new Hashtable<String,JXParser>();
	private Hashtable<String,Long> layoutModified	= new Hashtable<String,Long>();
	
	private LayoutFilter() {
	}
	
	public static LayoutFilter getInstance() {
		synchronized(LayoutFilter.class) {
			if(instance==null)
				instance	= new LayoutFilter();
			return instance;
		}
	}
	
	private synchronized void loadLayout(String layoutId, int direction) throws Exception{
		String layoutParent	= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/layout");
		String suffix	= (direction==DIRECTION_REQUEST)?"req":"res";
		
		File layout	= new File(layoutParent,layoutId+"-"+suffix+".xml");
		if(!layout.exists())
			throw new DefinedException("정의되지 않은 API ["+layoutId+"] 입니다.");
		
		if(layoutInfos.get(layoutId)!=null && layoutModified.get(layoutId)!=null && layoutModified.get(layoutId).longValue()==layout.lastModified())
			return;
		JXParser layoutXml	= new JXParser(layout);
		
		inspectLayoutGrammar(layoutXml, null);
		
		layoutModified.put(layoutId, layout.lastModified());
		layoutInfos.put(layoutId, layoutXml);
		
		logger.info("Layout ["+layoutId+"] loaded.");
	}
	/**
	 * 전문레이아웃정의 유효성 검증
	 * 
	 * */
	private void inspectLayoutGrammar(JXParser layoutXml, Element parent) throws Exception {
		if(parent==null)
			parent	= layoutXml.getRootElement();
		
		Element[] fields	= layoutXml.getElements(parent,"field");
		String name		= null;
		String maxBytes	= null;
		String required	= null;
		String type		= null;
		
		for(int i=0;i<fields.length;i++) {
			name		= layoutXml.getAttribute(fields[i], "name");
			if(name==null || "".equals(name))
				throw new DefinedException("name 속성값이 정의되지 않은 항목이 있습니다.");
			
			maxBytes	= layoutXml.getAttribute(fields[i], "maxBytes");
			if(maxBytes==null || "".equals(maxBytes))
				throw new DefinedException("["+name+"] 항목의 maxBytes 속성값이 정의되지 않았습니다.");
			
			if(!StringUtil.isPositiveNumeric(maxBytes))
				throw new DefinedException("["+name+"] 항목의 maxBytes 속성값 ["+maxBytes+"]이 올바르지 않습니다.");
			
			required	= layoutXml.getAttribute(fields[i], "required");
			if(required==null || "".equals(required))
				layoutXml.setAttribute(fields[i], "required","N");
				
			if(!"Y".equals(required) && !"N".equals(required))
				throw new DefinedException("["+name+"] 항목의 required 속성값 ["+required+"]이 올바르지 않습니다.");
			
			type		= layoutXml.getAttribute(fields[i], "type");
			if(type==null || "".equals(type))
				layoutXml.setAttribute(fields[i], "type","C");
			
			if(type.charAt(0)!=FIELD_TYPE_CHAR &&  type.charAt(0)!=FIELD_TYPE_NUMBER && type.charAt(0)!=FIELD_TYPE_DECIMAL)
				throw new DefinedException("["+name+"] 항목의 type 속성값 ["+type+"]이 올바르지 않습니다.");
			
		}
		Element[] lists	= layoutXml.getElements(parent,"list");
		String minOccurs	= null;
		String maxOccurs	= null;
		for(int i=0;i<lists.length;i++) {
			name		= layoutXml.getAttribute(lists[i], "name");
			if(name==null || "".equals(name))
				throw new DefinedException("name 속성값이 정의되지 않은 배열이 있습니다.");
			
			minOccurs	= layoutXml.getAttribute(lists[i],"minOccurs");
			if(minOccurs==null || "".equals(minOccurs))
				throw new DefinedException("["+name+"] 항목의 minOccurs 속성값이 정의되지 않았습니다.");
			
			if(!StringUtil.isPositiveNumeric(minOccurs))
				throw new DefinedException("["+name+"] 항목의 minOccurs 속성값 ["+minOccurs+"]이 올바르지 않습니다.");
			
			maxOccurs	= layoutXml.getAttribute(lists[i],"maxOccurs");
			if(maxOccurs==null || "".equals(maxOccurs))
				throw new DefinedException("["+name+"] 항목의 maxOccurs 속성값이 정의되지 않았습니다.");
			
			if(!StringUtil.isPositiveNumeric(maxOccurs))
				throw new DefinedException("["+name+"] 항목의 maxOccurs 속성값 ["+maxOccurs+"]이 올바르지 않습니다.");
			
			inspectLayoutGrammar(layoutXml, lists[i]);
		}
	}
	/**
	 * 요청전문이 layout정의에 맞는지 확인
	 * */
	public void validate(String layoutId,int direction, Map<String,Object> info) throws Exception{
		loadLayout(layoutId, direction);
		try {
			validate(layoutInfos.get(layoutId), null, info);
			validateRequestItem(layoutInfos.get(layoutId), info);
		} catch (Exception e) {
			if (e instanceof DefinedException) {
				DefinedException ex	= new DefinedException(e.getMessage());
				ex.setStatusCode(ErrorCode._0003);
				throw ex;
			} else {
				throw e;
			}
		}
	}
	/**
	 * 요청전문이 layout정의에 맞는지 확인
	 * */
	@SuppressWarnings("unchecked")
	private void validate(JXParser layoutXml, Element parent, Map<String,Object> info) throws Exception {
		if(parent==null)
			parent	= layoutXml.getRootElement();
		
		Element[] fields	= layoutXml.getElements(parent,"field");
		
		String name			= null;
		int maxBytes		= 0;
		boolean required	= false;
		char type			= FIELD_TYPE_CHAR;
		String val			= null;
		
		for(int i=0;i<fields.length;i++) {
			name		= layoutXml.getAttribute(fields[i], "name");
			maxBytes	= Integer.parseInt(layoutXml.getAttribute(fields[i], "maxBytes"));
			required	= "Y".equals(layoutXml.getAttribute(fields[i], "required"))?true:false;
			type		= layoutXml.getAttribute(fields[i], "type").charAt(0);
			
			val	= (info.get(name)==null)?null:String.valueOf(info.get(name));
			
			if(required && (val==null || "".equals(val)))
				throw new DefinedException("["+name+"] 항목은 필수입력 사항입니다.");
			if(val==null)
				info.put(name,"");
			
			if(maxBytes>0 && String.valueOf(info.get(name)).getBytes(CHARSET).length>maxBytes)
				throw new DefinedException("["+name+"] 항목의 최대크기("+CHARSET+" 기준)가 ["+maxBytes+"]Bytes를 초과하였습니다.");
			
			if(type==FIELD_TYPE_NUMBER && !StringUtil.isNumeric(val))
				throw new DefinedException("["+name+"] 항목은 정수만 가능합니다.");
			
			if(type==FIELD_TYPE_DECIMAL&& !StringUtil.isDecimal(val))
				throw new DefinedException("["+name+"] 항목은 실수만 가능합니다.");
		}
		
		Element[] lists	= layoutXml.getElements(parent,"list");
		List<Map<String,Object>> listObj	= null;
		Map<String,Object> obj	= null;
		int minOccurs	= 0;
		int maxOccurs	= 0;

		for(int i=0;i<lists.length;i++) {
			name		= layoutXml.getAttribute(lists[i], "name");
			minOccurs	= Integer.parseInt(layoutXml.getAttribute(lists[i],"minOccurs"));
			maxOccurs	= Integer.parseInt(layoutXml.getAttribute(lists[i],"maxOccurs"));
			
			obj			= (info.get(name) instanceof Map)?((Map<String,Object>)info.get(name)):null;
			
			if (obj == null) {
				listObj		= (info.get(name) instanceof List)?((List<Map<String,Object>>)info.get(name)):null;
				if(minOccurs>0 && (listObj==null || listObj.size()<minOccurs))
					throw new DefinedException("["+name+"] 배열의 최소 반복횟수는 ["+minOccurs+"]회 입니다.");
				
				if(maxOccurs>0 && (listObj==null || listObj.size()>maxOccurs))
					throw new DefinedException("["+name+"] 배열의 최대 반복횟수는 ["+maxOccurs+"]회 입니다.");
				
				if(listObj==null)
					info.put(name,new ArrayList<Map<String,Object>>());
				else {
					for(int j=0;j<listObj.size();j++) {
						validate(layoutXml, lists[i], listObj.get(j));
					}
				}
			} else {
				validate(layoutXml, lists[i], obj);
			}
		}
	}
	
	/**
	 * 정의되지 않은 요청항목 포함여부 확인
	 * */
	@SuppressWarnings("unchecked")
	private void validateRequestItem(JXParser layoutXml, Map<String,Object> info) throws Exception {
		Iterator<String> iter	= info.keySet().iterator();
		String fieldName		= null;
		Element fieldE			= null;
		Element listE			= null;
		List<Map<String,Object>> list	= null;
		Map<String,Object> obj	= null;
		
		while(iter.hasNext()) {
			fieldName	= iter.next();
			fieldE		= layoutXml.getElement("//field[@name='"+fieldName+"']");
			listE		= layoutXml.getElement("//list[@name='"+fieldName+"']");
			if(fieldE==null && listE==null)
				throw new DefinedException("요청정보에 정의되지 않은 항목 ["+fieldName+"]가 포함되어 있습니다.");
			if(listE!=null) {
				if (info.get(fieldName) instanceof Map) {
					obj			= (Map<String,Object>)info.get(fieldName);
					validateRequestItem(layoutXml, obj);
				} else if (info.get(fieldName) instanceof List) {
					list	= (List<Map<String, Object>>) info.get(fieldName);
					for(int i=0;list!=null && i<list.size();i++) {
						validateRequestItem(layoutXml, list.get(i));
					}	
				}
			}
		}
	}
	
}
