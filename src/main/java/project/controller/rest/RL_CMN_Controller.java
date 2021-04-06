package project.controller.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.math.Calculator;
import com.kwic.telegram.tcp.JTcpManager;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.client.except.parent.Except;
import project.client.parser.parent.ClientParser;
import project.controller.rest.layout.LayoutFilter;
import project.service.CMN_CMN_Service;
import project.service.RL_MBR_Service;

/**
 * @파일명	: RL_CMN_Controller
 * @작성일	: 2020. 9. 16.
 * @작성자	: 장수호
 * @설명		: 잔액조회, 예금주조회 처리
 * @변경이력	:
 */
@RestController
public class RL_CMN_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "RL_MBR_Service")
	private RL_MBR_Service service;
	
	/**
	 * @Method		: van_cmn
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: 잔액조회, 예금주조회 처리
	 * @변경이력		: 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/van/cmn", produces="application/json; charset=UTF-8")
	public void van_cmn(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		ClientParser parser	= null;
		Except except		= null;
		byte[] requestBytes	= null;
		
		String responseData = null;
		try {
			DefinedException ex	= null;
			try {	
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				ex	= new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
				ex.setStatusCode(ErrorCode._0004);
				throw ex;
			}
			
			String MESSAGE_CODE	= String.valueOf(requestMap.get("MESSAGECODE"));
			String SERVICE_CODE	= String.valueOf(requestMap.get("SERVICECODE"));
			
			//1.validate
			LayoutFilter.getInstance().validate(MESSAGE_CODE + "-" + SERVICE_CODE, LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2.거래고유번호 체크
			Map<String,Object> logInfo	= cmnService.CMN_CMN_S10001(requestMap);
			if (Integer.parseInt(String.valueOf(logInfo.get("CNT"))) > 0) {
				throw new DefinedException("이미 존재하는 거래고유번호 입니다.");
			}
			
			//3.고객사정보 조회
			Map<String,Object> info	= service.RL_MBR_V1001J(requestMap);
			if (info == null) {
				throw new DefinedException("등록 되지 않은 상사 정보 입니다.");
			}
			
			requestMap.put("DISCD", 	info.get("MBRBNK_DISCD"));	//식별코드
			requestMap.put("FBSCD", 	info.get("MBRBNK_FBSCD"));	//업체코드
			requestMap.put("SEQNO", 	cmnService.nextpfrno(String.valueOf(info.get("MBRBNK_SEQ"))));	//전문번호
			
			//4.채널별 Parsing Class 설정
			Class<?> parserClass = null;
			try {
				parserClass	= Class.forName("project.client.parser." + info.get("MBRBNK_CHNL") + "Parser");
			} catch (Exception e) {
				parserClass	= Class.forName("project.client.parser.CommonParser");
			}
			
			//5.업무별 예외 처리 Class 설정
			Class<?> exceptClass = null;
			try {
				exceptClass	= Class.forName("project.client.except.Except_" + requestMap.get("MESSAGECODE") + requestMap.get("SERVICECODE"));
			} catch (Exception e) {
				exceptClass	= Class.forName("project.client.except.CommonExcept");
			}
			
			//6.ksnet 요청전문 parsing
			try {
				parser			= (ClientParser) parserClass.newInstance();

				MESSAGE_CODE	= String.valueOf(requestMap.get("MESSAGECODE"));
				SERVICE_CODE	= String.valueOf(requestMap.get("SERVICECODE"));
				
				requestBytes	= parser.requestParse(MESSAGE_CODE, SERVICE_CODE, requestMap, properties.getString("peer-encoding"));
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET 요청 전문 Parsing 중 오류가 발생 하였습니다.");
				ex.setStatusCode(ErrorCode._0002);
				throw ex;
			}
			
			//7.ksnet 전송
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
					String code	= String.valueOf(requestMap.get("MESSAGECODE")) + String.valueOf(requestMap.get("SERVICECODE"));
					if ("0600300".equals(code)) {
						//잔액 조회
						responseData	= "TXEB9KSV 00000751030610300100000520200518110752    0000                             003             28103803604016 -0000000300000                                       0000000300000                                                                                                                       ";
					} else if ("0600400".equals(code)) {
						//예금주 조회
						responseData	= "         02430TOT050610400 00000120180803130947    0000                             005             08030525691000355405  튠븃곰찔샵(           3068124501                         0050000000001000                                                                                                         ";
					} else if ("0600100".equals(code)) {
						//지급이체 처리결과 조회
						responseData	= "REALTIME 02384TOT050610101100000120200910090101    0000                             005             90006206011001937    14889000118352 0000649000000000000000               174816WOUT81  081                                                                                                              ";
					}
				}
				
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET TCP 통신 중 오류가 발생하였습니다.");
				ex.setStatusCode(ErrorCode._0101);
				throw ex;
			}
			
			//8.ksnet 응답전문 parsing
			try {
				responseMap		= parser.responseParse(MESSAGE_CODE, SERVICE_CODE, responseData.getBytes(properties.getString("peer-encoding")), properties.getString("peer-encoding"));
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET 응답 전문 Parsing 중 오류가 발생하였습니다.");
				ex.setStatusCode(ErrorCode._0200);
				throw ex;
			}
			
			//9.ksnet 응답 전문 예외처리
			try {
				except			= (Except) exceptClass.newInstance();
				responseMap		= except.exceptResponse(responseMap);
			} catch (Exception e) {
				logger.error(e);
				throw new DefinedException("응답 데이터 예외 처리 중 오류가 발생 하였습니다.");
			}
			
			//10.은행응답 메시지 설정
			Map<String,Object> params	= null;
			Map<String,Object> common	= null;
			try {
				params	= new HashMap<String,Object>();
				params.put("CLSCD", "0005");
				params.put("DTLCD", responseMap.get(BNK_RT_CD));
				
				common	= cmnService.CMN_CMN_S10000(params);
				responseMap.put(BNK_RT_MSG, common.get("CDDTL_NM"));	//은행응답코드 메시지
			} catch (Exception e) {
				logger.error(e);
				throw new DefinedException("은행 응답 메세지 조회 중 오류가 발생하였습니다.");
			}
				
			responseMap.put(RT_CD,	ErrorCode._0000);
			responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._0000));
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._9999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
				responseMap.put(BNK_RT_CD, "");
				responseMap.put(BNK_RT_MSG, "");
			} else {
				responseMap.put(RT_CD,	ErrorCode._9999);
				responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._9999));
				responseMap.put(BNK_RT_CD, "");
				responseMap.put(BNK_RT_MSG, "");
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, new String(requestBytes, properties.getString("peer-encoding")), responseData, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
