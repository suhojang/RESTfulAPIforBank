package project.client.execute;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.math.Calculator;
import com.kwic.telegram.tcp.JTcpManager;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.client.parser.parent.ClientParser;
import project.service.CMN_CMN_Service;
import project.service.RL_TSK_Service;

public class KsnetOpenManager {
	private static Logger logger = LoggerFactory.getLogger(KsnetOpenManager.class);
	
	private static KsnetOpenManager instance;

	private RL_TSK_Service service;
	private CMN_CMN_Service cmnService;
	
	private EgovPropertyService properties;
	
	public static KsnetOpenManager getInstance() throws Exception {
		synchronized (KsnetOpenManager.class){
			if(instance == null){
				instance = new KsnetOpenManager();
			}
		}
		return instance;
	}
	
	private KsnetOpenManager() throws Exception {
		try {
			this.service		= (RL_TSK_Service) ContextLoader.getCurrentWebApplicationContext().getBean("RL_TSK_Service");
			this.cmnService		= (CMN_CMN_Service) ContextLoader.getCurrentWebApplicationContext().getBean("CMN_CMN_Service");
			this.properties		= (EgovPropertyService)ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * @Method		: KsnetOpenStart
	 * @작성일 		: 2020. 9. 22.
	 * @작성자 		: 장수호
	 * @Method 설명	: 개시업무 수행
	 * @변경이력		: 
	 * @param isAll
	 * @throws Exception
	 */
	public void KsnetOpenStart(boolean isAll) throws Exception {
		try {
			if (isAll) {
				KsnetOpenInit();
			}
			
			List<Map<String,Object>> list	= service.RL_TSK_S1000J(new HashMap<String,Object>());
			for (int i = 0; i < list.size(); i++) {
				KsnetOpenNet(list.get(i));
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: KsnetOpenInit
	 * @작성일 		: 2020. 9. 22.
	 * @작성자 		: 장수호
	 * @Method 설명	: 개시여부, 개시일자 초기화
	 * @변경이력		: 
	 * @throws Exception
	 */
	public void KsnetOpenInit() throws Exception {
		Map<String, Object> param	= new HashMap<String, Object>();
		try {
			param.put("MBRBNK_OPDT", new SimpleDateFormat("yyyyMMdd").format(new Date()));
			param.put("MBRBNK_OPYN", "N");
			
			service.RL_TSK_U1000J(param);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: KsnetOpenNet
	 * @작성일 		: 2020. 9. 22.
	 * @작성자 		: 장수호
	 * @Method 설명	: ks-net 개시전문 송/수신
	 * @변경이력		: 
	 * @param info
	 * @throws Exception
	 */
	private void KsnetOpenNet(Map<String, Object> info) throws Exception {
		Map<String,Object> requestMap		= new HashMap<String,Object>();
		Map<String,Object> ksnetResponseMap	= new HashMap<String,Object>();
		
		ClientParser parser	= null;
		
		byte[] requestBytes	= null;
		String responseData = null;
		
		try {
			String MESSAGE_CODE	= "0800";
			String SERVICE_CODE	= "100";
			
			//mapping data
			requestMap.put("DISCD", 	info.get("MBRBNK_DISCD"));	//식별코드
			requestMap.put("FBSCD", 	info.get("MBRBNK_FBSCD"));	//업체코드
			requestMap.put("OUTBANKCD", info.get("MBRBNK_BNKCD"));	//은행코드
			requestMap.put("SEQNO", 	cmnService.nextpfrno(String.valueOf(info.get("MBRBNK_SEQ"))));	//전문번호
			
			//parsing class create
			Class<?> parserClass = null;
			try {
				parserClass	= Class.forName("project.client.parser." + info.get("MBRBNK_CHNL") + "Parser");
			} catch (Exception e) {
				parserClass	= Class.forName("project.client.parser.CommonParser");
			}
			
			//requestBytes parsing
			try {
				parser			= (ClientParser) parserClass.newInstance();
				requestBytes	= parser.requestParse(MESSAGE_CODE, SERVICE_CODE, requestMap, properties.getString("peer-encoding"));
				
				logger.debug("개시전문 요청 : "+new String(requestBytes));
			} catch (Exception e) {
				throw e;
			}
			
			//ks-net request
			try {
				
				if (!"T".equals(properties.getString("service-type"))) {
					String ip		= properties.getString("ksnet-tcp-ip");
					int port 		= Integer.parseInt(properties.getString("ksnet-tcp-"+properties.getString("service-type")+"-port"));
					int timeout 	= (int) Calculator.calculate(properties.getString("ksnet-tcp-timeout"));
					
					responseData 	= JTcpManager.getInstance().sendMessage(
							ip, 
							port, 
							new String(requestBytes, properties.getString("peer-encoding")), 
							timeout
							);
					
				} else {
					responseData	= "02403TOT 02403TOT040810100100000120200922161758    0000                             004                                                                                                                                                                                                                     ";
				}
			} catch (Exception e) {
				throw e;
			}
			
			//response parsing
			try {
				ksnetResponseMap	= parser.responseParse(MESSAGE_CODE, SERVICE_CODE, responseData.getBytes(properties.getString("peer-encoding")), properties.getString("peer-encoding"));
			} catch (Exception e) {
				throw e;
			}
			
			try {
				//개시여부, 개시일자 변경
				if ("0000".equals(ksnetResponseMap.get("BANKRESPCODE"))) {
					info.put("MBRBNK_OPDT", new SimpleDateFormat("yyyyMMdd").format(new Date()));
					info.put("MBRBNK_OPYN", "Y");
					
					service.RL_TSK_U1000J(info);
				}
			} catch (Exception e) {
				throw new Exception("[" + ksnetResponseMap.get("H_MSG_CD") + "-" + ksnetResponseMap.get("H_SVR_CD") + "] 개시여부, 개시일자 변경 중 오류가 발생하였습니다.");
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
}
