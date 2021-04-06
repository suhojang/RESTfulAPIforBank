package project.client.parser;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.springframework.web.context.ContextLoader;

import com.kwic.exception.DefinedException;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

import project.client.parser.parent.ClientParser;
import project.client.properties.KSNETProperties;

/**
 * @파일명	: KSNETParser
 * @작성일	: 2020. 9. 16.
 * @작성자	: 장수호
 * @설명		: KS-NET 요청/응답 전문 Parsing Class
 * @변경이력	:
 */
public class KSNETParser implements ClientParser{
	private Logger logger = LoggerFactory.getLogger(KSNETParser.class);
	
	/**
	 * @Method		: requestParse
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: KS-NET 요청 전문 parsing
	 * @변경이력		: 
	 * @param MESSAGE_CODE
	 * @param SERVICE_CODE
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	@Override
	public byte[] requestParse(String MESSAGE_CODE, String SERVICE_CODE, Map<String, Object> params, String encoding) throws Exception {
		String xmlNm 		= null;
		String structPath 	= null;
		JXParser jxp		= null;
		
		List<byte[]> byteList	= new ArrayList<byte[]>();
		ByteBuffer bf			= null;
		try {
			xmlNm 		= "ksnet-" + MESSAGE_CODE + "-" + SERVICE_CODE + ".xml";
			structPath 	= StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/")	+ File.separator + xmlNm;
			
			jxp = new JXParser(new File(structPath));

			requestParse(jxp.getRootElement(), params, encoding, jxp, byteList);
			
			int totSz = 0;
			for (int i = 0; i < byteList.size(); i++){
				totSz += byteList.get(i).length;
			}

			bf = ByteBuffer.allocate(totSz);
			for (int i = 0; i < byteList.size(); i++){
				bf.put(byteList.get(i));
			}
		} catch (Exception e) {
			throw e;
		}
		
		return bf.array();
	}

	/**
	 * @Method		: requestParse
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: KS-NET 요청 전문 parsing
	 * @변경이력		: 
	 * @param structParent
	 * @param params
	 * @param encoding
	 * @param jxp
	 * @param byteList
	 * @throws Exception
	 */
	private void requestParse(Element structParent, Map<String, Object> params, String encoding, JXParser jxp, List<byte[]> byteList) throws Exception {
		Element[] fields			= null;
		
		String type 			= null;
		String name 			= null;
		String defaultVal 		= null;
		String mappingElement 	= null;

		String val			= null;
		byte[] bytesVal 	= null;
		int length 			= 0;
		try {
			fields 			= jxp.getElements(structParent, "field");
			
			for (int i = 0; i < fields.length; i++) {
				name 			= jxp.getAttribute(fields[i], "name");
				defaultVal    	= jxp.getAttribute(fields[i], "default");
				mappingElement	= jxp.getAttribute(fields[i], "mappingElement");
				
				type			= jxp.getAttribute(fields[i], "type");
				length 			= Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				
				//mappingElement 값 세팅
				val		= String.valueOf(params.get(mappingElement));
				if (mappingElement == null || "".equals(mappingElement)){
					val = defaultVal;
				}else if (defaultVal != null && (val == null || "".equals(val))) {
					val = defaultVal;
				}
				
				//request에 value 담기
				params.put(name, val);
				
				//type별 데이터 변환
				if ("C".equals(type)) {
					bytesVal		= ByteUtil.addByte((params.get(name)==null ? "" : String.valueOf(params.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
				} else if ("N".equals(type)) {
					bytesVal		= ByteUtil.addByte((params.get(name)==null ? "" : String.valueOf(params.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_ZERO, length, true, encoding);
				} else if ("D".equals(type)) {
					bytesVal		= new SimpleDateFormat("yyyyMMdd").format(new Date()).getBytes(encoding);
				} else if ("T".equals(type)) {
					bytesVal		= new SimpleDateFormat("HHmmss").format(new Date()).getBytes(encoding);
				}
				params.put(name, new String(bytesVal, encoding).trim());
				
				byteList.add(bytesVal);
			}
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	private int tmpIndex = 0;
	private byte[] tmpBytes = null;

	/**
	 * @Method		: responseParse
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: KS-NET 응답 전문 parsing
	 * @변경이력		: 
	 * @param MESSAGE_CODE
	 * @param SERVICE_CODE
	 * @param response
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String,Object> responseParse(String MESSAGE_CODE, String SERVICE_CODE, byte[] response, String encoding) throws Exception {
		String xmlNm 		= null;
		String structPath 	= null;
		JXParser jxp		= null;
		
		Map<String,Object> responseMap	= null;
		try {
			tmpIndex 	= 0;
			tmpBytes	= response;
			if (response.length != KSNETProperties.SEVICE_LENGTH.get("ksnet-" + MESSAGE_CODE + SERVICE_CODE)) {
				throw new DefinedException("KSNET 데이터의 길이["+response.length+"]가 올바르지 않습니다. [정상 : "+KSNETProperties.SEVICE_LENGTH.get("ksnet-" + MESSAGE_CODE + SERVICE_CODE)+"]");
			}
			
			MESSAGE_CODE	= StringUtil.replaceIndexStr(MESSAGE_CODE, 2, KSNETProperties._RES_MESSAGE_CODE);
			
			xmlNm 		= "ksnet-" + MESSAGE_CODE + "-" + SERVICE_CODE + ".xml";
			structPath 	= StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/")	+ File.separator + xmlNm;
			
			jxp = new JXParser(new File(structPath));
			
			responseMap	= responseParse(jxp, jxp.getRootElement(), encoding); 
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		
		return responseMap;
	}
	
	/**
	 * @Method		: responseParse
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: KS-NET 응답 전문 parsing
	 * @변경이력		: 
	 * @param jxp
	 * @param parent
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	private Map<String,Object> responseParse(JXParser jxp, Element parent, String encoding) throws Exception {
		Element[] fields	= null;
		
		String type 		= null;
		String name 		= null;
		String response		= null;
		int length 			= 0;

		String defaultVal 	= null;

		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			fields 			= jxp.getElements(parent, "field");
			for (int i = 0; i < fields.length; i++) {
				name 		= jxp.getAttribute(fields[i], "name");
				type 		= jxp.getAttribute(fields[i], "type");
				response 	= jxp.getAttribute(fields[i], "response");
				length		= Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				
				defaultVal = null;
				try {
					defaultVal = jxp.getAttribute(fields[i], "default");
				} catch (JaxenException e) {
					logger.error("Invalid default value.", e);
				}
				
				byte[] bytes = new byte[length];
				System.arraycopy(tmpBytes, tmpIndex, bytes, 0, length);
				
				if ("Y".equals(response)) {
					if ("C".equals(type)) {
						responseMap.put(name, new String(bytes, encoding).trim());
					} else if ("N".equals(type)) {
						responseMap.put(name, String.valueOf(Long.parseLong(new String(bytes, encoding).trim())));
					}
					
					if (defaultVal != null && !"".equals(defaultVal) && (responseMap.get(name) == null || "".equals(String.valueOf(responseMap.get(name))))) {
						responseMap.put(name, defaultVal);
					}
				}
				tmpIndex += length;
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		
		return responseMap;
	}
}
