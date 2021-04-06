package project.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.kwic.exception.DefinedException;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.util.StringUtil;
import com.kwic.web.init.Handler;

import project.client.properties.KSNETProperties;
import project.server.exception.PropertyNotFoundException;
import project.server.parser.Parser;
import project.service.CMN_CMN_Service;

public class KsnetTcpHandler extends Thread implements Handler {
	private static Logger logger		= LoggerFactory.getLogger(KsnetTcpHandler.class);
	
	private Map<String, Object> param = new HashMap<String, Object>();
	
	private Socket socket 	= null;
	private String id 		= null;
	
	private int bufSize 	= 0;
	private int byteSize 	= 0;
	private Parser parser;
	
	private Map<String, Object> initServiceParams;
	
	private CMN_CMN_Service cmnService;

	@Override
	public void put(String name, Object obj) throws Exception {
		param.put(name, obj);
	}

	@Override
	public void handle() throws Exception {
		socket = (Socket) param.get("client-socket");
		id     = (String) param.get("client-id");
		this.start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getServiceParams() {
		return (Map<String, Object>) param.get("service-params");
	}
	
	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}
	
	private int getInitParamsInt(String propertyName) throws PropertyNotFoundException{
		if(initServiceParams.get(propertyName) == null || "".equals(initServiceParams.get(propertyName))){
			throw new PropertyNotFoundException(propertyName);
		}
		return Integer.parseInt(String.valueOf(initServiceParams.get(propertyName)));
	}
	
	private String getInitParamsString(String propertyName) throws PropertyNotFoundException{
		if(initServiceParams.get(propertyName) == null || "".equals(initServiceParams.get(propertyName))){
			throw new PropertyNotFoundException(propertyName);
		}
		return String.valueOf(initServiceParams.get(propertyName));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		KsnetTcpServer.addClientSocket(id, socket);
		
		cmnService	= (CMN_CMN_Service) getBean("CMN_CMN_Service");
		
		Map<String, Object> requestMap	= null;
		Map<String, Object> responseMap	= new HashMap<String, Object>();
		
		byte[] bufBytes			= null;
		byte[] requestBytes		= null;
		int size	= 0;
		
		try {
			initServiceParams = (Map<String, Object>) param.get("service-params");
			
			bufSize					= getInitParamsInt("bufSize");
			byteSize				= getInitParamsInt("byteSize");
			String parserPrefix		= getInitParamsString("parser-prefix");	//parser 클래스 project.server.parser.Parser_
			String peerEncoding		= getInitParamsString("peer-encoding");	//KS-NET Encoding정보
			int msgcdStartIndex    	= getInitParamsInt("msgcd-start-index");
			int msgcdSize			= getInitParamsInt("msgcd-size");
			int svrcdStartIndex    	= getInitParamsInt("svrcd-start-index");
			int svrcdSize			= getInitParamsInt("svrcd-size");
			
			byte[] msgcdBytes = new byte[msgcdSize];
			byte[] svrcdBytes = new byte[svrcdSize];
			
			bufBytes		= new byte[bufSize];
			requestBytes	= new byte[byteSize];
			
			size 			= socket.getInputStream().read(bufBytes, 0, bufBytes.length);
			if (size < 0) {
				throw new DefinedException("Connection closed by peer [" + size + "]");
			}
			
			logger.debug("KS-NET 거래내역 통지 데이터 :: "+new String(bufBytes));
			
			System.arraycopy(bufBytes, 0, requestBytes, 0, requestBytes.length);
			
			System.arraycopy(requestBytes, msgcdStartIndex, msgcdBytes, 0, msgcdBytes.length);
			System.arraycopy(requestBytes, svrcdStartIndex, svrcdBytes, 0, svrcdBytes.length);
			
			parser = createParser(parserPrefix, msgcdBytes, svrcdBytes);
			parser.setSysParam(initServiceParams);
			parser.setHandler(this);
			parser.setSerial(String.valueOf(cmnService.nextval("TRSHIS_SEQ")));
			parser.setMessageCode(new String(msgcdBytes));
			parser.setServiceCode(new String(svrcdBytes));
			parser.setEncoding(peerEncoding);
			parser.setRequest(requestBytes);
			
			//통지 데이터 parsing
			try {
				requestMap = parser.parseRequest();
			} catch (DefinedException de) {
				logger.error("Request parsing error." , de);
				throw de;
			} catch (Exception e) {
				logger.error("Request parsing error." , e);
				throw new DefinedException("Request parsing error." + e.getMessage());
			}
			
			//통지 업무별 데이터 저장
			try {
				parser.saveRequest();
			} catch (DefinedException de) {
				throw de;
			} catch (Exception e) {
				logger.error(String.format("Parsing request and DB saving are failed. parameters=%s", requestMap), e);
				throw new DefinedException("Fail to save request data." + e.getMessage());
			}
			responseMap.putAll(requestMap);
			responseMap.put("H_MSG_CD", StringUtil.replaceIndexStr(String.valueOf(responseMap.get("H_MSG_CD")), 2, KSNETProperties._RES_MESSAGE_CODE));

			parser.setMessageCode(String.valueOf(responseMap.get("H_MSG_CD")));
			parser.setResMap(responseMap);
			
			response();
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			KsnetTcpServer.removeClientSocket(id);
		}
	}
	
	private Parser createParser(String parserPrefix, byte[] msgBytes, byte[] svrBytes) throws Exception {
		String parserName = null;
		String bzcd = null;
		try {
			bzcd = new String(msgBytes) + new String(svrBytes);
			parserName = parserPrefix + bzcd;
			Class<?> parserClass = Class.forName(parserName);
			return (Parser) parserClass.newInstance();
		} catch (Exception e) {
			logger.error(String.format("Parser class %s cannot found, because of invalid business code.(bzcd=%s)", parserName, bzcd), e);
			throw new DefinedException("Undefined parser class[" + parserName + "]." + e.getMessage());
		}
	}
	
	private void response() throws Exception {
		try {
			byte[] responseBytes	= parser.parseResponse();
			
			socket.getOutputStream().write(responseBytes);
			socket.getOutputStream().flush();
		
			parser.saveResponse();
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
}
