package project.server.parser;

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
import com.kwic.web.init.Handler;
import com.kwic.xml.parser.JXParser;

import project.client.properties.KSNETProperties;

public abstract class Parser {
	private Handler handler;
	private String messageCode;
	private String serviceCode;
	private String serial;
	private String encoding;
	private byte[] requestBytes;
	private byte[] responseBytes;
	private Map<String, Object> reqMap = new HashMap<String, Object>();
	private Map<String, Object> resMap = new HashMap<String, Object>();
	private Map<String, Object> sysParam;
	
	private static Logger logger = LoggerFactory.getLogger(Parser.class);
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setSysParam(Map<String, Object> sysParam) {
		this.sysParam = sysParam;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getSerial() {
		return serial;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}
	
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public void setRequest(byte[] bytes) {
		this.requestBytes = bytes;
	}
	
	public void setResponse(byte[] bytes) {
		this.responseBytes = bytes;
	}
	
	public void setResMap(Map<String, Object> resMap) {
		this.resMap = resMap;
	}
	
	public byte[] getRequest() {
		return requestBytes;
	}
	
	public byte[] getResponse() {
		return responseBytes;
	}
	
	public Map<String, Object> getReqMap() {
		return reqMap;
	}

	public Map<String, Object> getResMap() {
		return resMap;
	}

	public String getMessageCode() {
		return messageCode;
	}
	
	public String geServiceCode() {
		return serviceCode;
	}

	public Handler getHandler() {
		return handler;
	}

	public Map<String, Object> getSysParam() {
		return sysParam;
	}
	
	public abstract void saveRequest() throws Exception;
	public abstract void saveResponse() throws Exception;
	
	private int tmpIndex = 0;
	private byte[] tmpBytes = null;
	
	public Map<String, Object> parseRequest() throws Exception {
		String xmlNm 		= null;
		String structPath 	= null;
		JXParser jxp		= null;
		
		List<byte[]> byteList	= new ArrayList<byte[]>();
		ByteBuffer bf			= null;
		try {
			tmpIndex 	= 0;
			tmpBytes	= requestBytes;
			if (requestBytes.length != KSNETProperties.SEVICE_LENGTH.get("ksnet-" + messageCode + serviceCode)) {
				throw new DefinedException("KSNET 데이터의 길이["+requestBytes.length+"]가 올바르지 않습니다. [정상 : "+KSNETProperties.SEVICE_LENGTH.get("ksnet-" + messageCode + serviceCode)+"]");
			}
			
			xmlNm 		= "ksnet-" + messageCode + "-" + serviceCode + ".xml";
			structPath 	= StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/")	+ File.separator + xmlNm;
			
			jxp = new JXParser(new File(structPath));
			
			reqMap	= parseRequest(jxp, jxp.getRootElement()); 
			
			getByteRequest(jxp.getRootElement(), jxp, byteList);
			int totSz = 0;
			for (int i = 0; i < byteList.size(); i++){
				totSz += byteList.get(i).length;
			}

			bf = ByteBuffer.allocate(totSz);
			for (int i = 0; i < byteList.size(); i++){
				bf.put(byteList.get(i));
			}
			
			setRequest(bf.array());
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return reqMap;
	}
	
	private Map<String, Object> parseRequest(JXParser jxp, Element parent) throws Exception {
		Element[] fields	= null;
		
		String type 		= null;
		String name 		= null;
		String response		= null;
		int length 			= 0;

		String defaultVal 	= null;

		Map<String,Object> parsingMap	= new HashMap<String,Object>();
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
						parsingMap.put(name, new String(bytes, encoding).trim());
					} else if ("N".equals(type)) {
						parsingMap.put(name, String.valueOf(Long.parseLong(new String(bytes, encoding).trim())));
					}
					
					if (defaultVal != null && !"".equals(defaultVal) && (parsingMap.get(name) == null || "".equals(String.valueOf(parsingMap.get(name))))) {
						parsingMap.put(name, defaultVal);
					}
				}
				tmpIndex += length;
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		
		return parsingMap;
	}
	
	public void getByteRequest(Element structParent, JXParser jxp, List<byte[]> byteList) throws Exception {
		Element[] fields			= null;
		
		String type 			= null;
		String name 			= null;
		String defaultVal 		= null;

		String val			= null;
		byte[] bytesVal 	= null;
		int length 			= 0;
		try {
			fields 			= jxp.getElements(structParent, "field");
			
			for (int i = 0; i < fields.length; i++) {
				name 			= jxp.getAttribute(fields[i], "name");
				defaultVal    	= jxp.getAttribute(fields[i], "default");
				
				type			= jxp.getAttribute(fields[i], "type");
				length 			= Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				
				val		= String.valueOf(reqMap.get(name));
				if (defaultVal != null && (val == null || "".equals(val))) {
					val = defaultVal;
				}
				reqMap.put(name, val);
				
				if ("C".equals(type)) {
					bytesVal		= ByteUtil.addByte((reqMap.get(name)==null ? "" : String.valueOf(reqMap.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
				} else if ("N".equals(type)) {
					if (reqMap.get(name)==null || "".equals(reqMap.get(name))) {
						bytesVal		= ByteUtil.addByte("".getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
					} else {
						bytesVal		= ByteUtil.addByte(String.valueOf(reqMap.get(name)).getBytes(encoding), ByteUtil.APPEND_CHARACTER_ZERO, length, true, encoding);
					}
				} else if ("D".equals(type)) {
					bytesVal		= new SimpleDateFormat("yyyyMMdd").format(new Date()).getBytes(encoding);
				} else if ("T".equals(type)) {
					bytesVal		= new SimpleDateFormat("HHmmss").format(new Date()).getBytes(encoding);
				}
				
				byteList.add(bytesVal);
			}
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	public byte[] parseResponse() throws Exception {
		String xmlNm 		= null;
		String structPath 	= null;
		JXParser jxp		= null;
		
		List<byte[]> byteList	= new ArrayList<byte[]>();
		ByteBuffer bf			= null;
		try {
			xmlNm 		= "ksnet-" + messageCode + "-" + serviceCode + ".xml";
			structPath 	= StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/")	+ File.separator + xmlNm;
			
			jxp = new JXParser(new File(structPath));

			parseResponse(jxp.getRootElement(), jxp, byteList);
			
			int totSz = 0;
			for (int i = 0; i < byteList.size(); i++){
				totSz += byteList.get(i).length;
			}

			bf = ByteBuffer.allocate(totSz);
			for (int i = 0; i < byteList.size(); i++){
				bf.put(byteList.get(i));
			}
			
			setResponse(bf.array());
		} catch (Exception e) {
			throw e;
		}
		
		return bf.array();
	}
	
	private void parseResponse(Element structParent, JXParser jxp, List<byte[]> byteList) throws Exception {
		Element[] fields			= null;
		
		String type 			= null;
		String name 			= null;
		String defaultVal 		= null;

		String val			= null;
		byte[] bytesVal 	= null;
		int length 			= 0;
		try {
			fields 			= jxp.getElements(structParent, "field");
			
			for (int i = 0; i < fields.length; i++) {
				name 			= jxp.getAttribute(fields[i], "name");
				defaultVal    	= jxp.getAttribute(fields[i], "default");
				
				type			= jxp.getAttribute(fields[i], "type");
				length 			= Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				
				val		= String.valueOf(resMap.get(name));
				if (defaultVal != null && (val == null || "".equals(val))) {
					val = defaultVal;
				}
				resMap.put(name, val);
				
				if ("C".equals(type)) {
					bytesVal		= ByteUtil.addByte((resMap.get(name)==null ? "" : String.valueOf(resMap.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
				} else if ("N".equals(type)) {
					if (resMap.get(name)==null || "".equals(resMap.get(name))) {
						bytesVal		= ByteUtil.addByte("".getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
					} else {
						bytesVal		= ByteUtil.addByte(String.valueOf(resMap.get(name)).getBytes(encoding), ByteUtil.APPEND_CHARACTER_ZERO, length, true, encoding);
					}
				} else if ("D".equals(type)) {
					bytesVal		= new SimpleDateFormat("yyyyMMdd").format(new Date()).getBytes(encoding);
				} else if ("T".equals(type)) {
					bytesVal		= new SimpleDateFormat("HHmmss").format(new Date()).getBytes(encoding);
				}
				
				byteList.add(bytesVal);
			}
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}
}
