package project.controller.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.kwic.security.aes.AESCipher;
import com.kwic.telegram.tcp.JTcpManager;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.client.except.parent.Except;
import project.client.parser.parent.ClientParser;
import project.client.rec.Reconstruct;
import project.controller.rest.layout.LayoutFilter;
import project.service.CMN_CMN_Service;
import project.service.RL_DPS_Service;
import project.service.RL_MBR_Service;

/**
 * @파일명	: RL_DPS_Controller
 * @작성일	: 2020. 9. 16.
 * @작성자	: 장수호
 * @설명		: 이체 처리(지급이체, 이체결과조회)
 * @변경이력	:
 */
@RestController
public class RL_DPS_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "RL_MBR_Service")
	private RL_MBR_Service service;
	
	@Resource(name = "RL_DPS_Service")
	private RL_DPS_Service dpsService;
	
	/**
	 * @Method		: van_dps
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 처리
	 * @변경이력		: 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/van/dps/reg", produces="application/json; charset=UTF-8")
	public void van_dps_reg(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		Map<String,Object> ksnetResponseMap	= new HashMap<String,Object>();
		
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
			
			String recCode	= Reconstruct.getKBRecKRWCode(
					String.valueOf(requestMap.get("OUTAMT")),
					new SimpleDateFormat("yyyyMMdd").format(new Date()),
					String.valueOf(requestMap.get("SEQNO")),
					String.valueOf(requestMap.get("OUTACCTNO")),
					String.valueOf(requestMap.get("INACCTNO"))
					);
			requestMap.put("RECCD", recCode);	//복기부호
			
			//4.요청 데이터 저장
			try {
				//시퀀스번호 획득
				requestMap.put("TRSRQ_SEQ", cmnService.nextval("TRSRQ_SEQ"));
				
				requestSave(MESSAGE_CODE, SERVICE_CODE, requestContent, requestMap, info);
			} catch (Exception e) {
				throw new Exception("[" + MESSAGE_CODE + "-" + SERVICE_CODE + "] 요청 데이터 저장 중 오류가 발생하였습니다.");
			}
			
			//5.채널별 Parsing Class 설정
			Class<?> parserClass = null;
			try {
				parserClass	= Class.forName("project.client.parser." + info.get("MBRBNK_CHNL") + "Parser");
			} catch (Exception e) {
				parserClass	= Class.forName("project.client.parser.CommonParser");
			}
			
			//6.업무별 예외 처리 Class 설정
			Class<?> exceptClass = null;
			try {
				exceptClass	= Class.forName("project.client.except.Except_" + requestMap.get("MESSAGECODE") + requestMap.get("SERVICECODE"));
			} catch (Exception e) {
				exceptClass	= Class.forName("project.client.except.CommonExcept");
			}
			
			//7.ksnet 요청전문 parsing
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
			
			//8.ksnet 요청시각 저장
			try {
				dpsService.RL_DPS_U1001J(requestMap);
			} catch (Exception e) {
				throw e;
			}
			
			//9.ksnet 전송
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
					responseData	= "         02403TOT050110100100000220200715133142    0000                             005             06011001201            3474  0000020000000+00043304857280461210104017653 000000000133142(주)롯데칠성음                                     (주)롯데칠성음      004                                      ";
				}
				
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET TCP 통신 중 오류가 발생하였습니다.");
				ex.setStatusCode(ErrorCode._0101);
				throw ex;
			}
			
			//10.ksnet 응답전문 parsing
			try {
				ksnetResponseMap	= parser.responseParse(MESSAGE_CODE, SERVICE_CODE, responseData.getBytes(properties.getString("peer-encoding")), properties.getString("peer-encoding"));
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET 응답 전문 Parsing 중 오류가 발생하였습니다.");
				ex.setStatusCode(ErrorCode._0200);
				throw ex;
			}
			
			//11.응답 데이터 저장
			try {
				responseSave(MESSAGE_CODE, SERVICE_CODE, requestMap, responseData, ksnetResponseMap);
			} catch (Exception e) {
				throw new Exception("[" + ksnetResponseMap.get("H_MSG_CD") + "-" + ksnetResponseMap.get("H_SVR_CD") + "] 응답 데이터 저장 중 오류가 발생하였습니다.");
			}
			
			//12.ksnet 응답 전문 예외처리
			try {
				except			= (Except) exceptClass.newInstance();
				responseMap		= except.exceptResponse(ksnetResponseMap);
			} catch (Exception e) {
				logger.error(e);
				throw new DefinedException("응답 데이터 예외 처리 중 오류가 발생 하였습니다.");
			}
			
			//13.은행응답 메시지 설정
			Map<String,Object> params	= null;
			Map<String,Object> common	= null;
			try {
				params	= new HashMap<String,Object>();
				params.put("CLSCD", "0005");
				params.put("DTLCD", responseMap.get(BNK_RT_CD));
				
				common	= cmnService.CMN_CMN_S10000(params);
				responseMap.put(BNK_RT_MSG, common.get("CDDTL_NM"));
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
			//서버송신 시각 저장
			try {dpsService.RL_DPS_U1002J(requestMap);}catch(Exception e){logger.error(e);}
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, new String(requestBytes, properties.getString("peer-encoding")), responseData, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * @Method		: van_dps_sel
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 처리 결과 조회
	 * @변경이력		: 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/van/dps/sel", produces="application/json; charset=UTF-8")
	public void van_dps_sel(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		Map<String,Object> ksnetResponseMap	= new HashMap<String,Object>();
		
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
			
			//2.원거래 이체정보 조회
			Map<String,Object> trs	= dpsService.RL_DPS_V1000J(requestMap);
			if (trs == null) {
				throw new DefinedException("존재하지 않은 거래고유번호 입니다.");
			}
			requestMap.put("O_SEQNO", 	trs.get("TRSRQ_PRFNO"));	//원거래 전문번호
			requestMap.put("OUTBANKCD", trs.get("TRSRQ_OBKCD"));	//출금은행코드
			
			//3.고객사정보 조회
			Map<String,Object> info	= service.RL_MBR_V1001J(requestMap);
			if (info == null) {
				throw new DefinedException("등록 되지 않은 상사 정보 입니다.");
			}
			requestMap.put("DISCD", 	info.get("MBRBNK_DISCD"));	//식별코드
			requestMap.put("FBSCD", 	info.get("MBRBNK_FBSCD"));	//업체코드
			requestMap.put("SEQNO", 	cmnService.nextpfrno(String.valueOf(info.get("MBRBNK_SEQ"))));	//전문번호
			
			//3.요청 데이터 저장
			try {
				//시퀀스번호 획득
				requestMap.put("TRSRQ_SEQ", trs.get("TRSRQ_SEQ"));
				
				requestSave(MESSAGE_CODE, SERVICE_CODE, requestContent, requestMap, info);
			} catch (Exception e) {
				throw new Exception("[" + MESSAGE_CODE + "-" + SERVICE_CODE + "] 요청 데이터 저장 중 오류가 발생하였습니다.");
			}
			
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
			
			//7.ksnet 처리결과 요청시각 저장
			try {
				dpsService.RL_DPS_U1003J(requestMap);
			} catch (Exception e) {
				throw e;
			}
			
			//8.ksnet 전송
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
					responseData	= "REALTIME 02384TOT050610100100000120200910090101    0000                             005             90006206011001937    14889000118352 0000649000000000000000               174816WOUT81                      081                                                                                          ";
				}
				
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET TCP 통신 중 오류가 발생하였습니다.");
				ex.setStatusCode(ErrorCode._0101);
				throw ex;
			}
			
			//9.ksnet 응답전문 parsing
			try {
				ksnetResponseMap	= parser.responseParse(MESSAGE_CODE, SERVICE_CODE, responseData.getBytes(properties.getString("peer-encoding")), properties.getString("peer-encoding"));
			} catch (Exception e) {
				logger.error(e);
				ex	= new DefinedException("KS-NET 응답 전문 Parsing 중 오류가 발생하였습니다.");
				ex.setStatusCode(ErrorCode._0200);
				throw ex;
			}
			
			//10.응답 데이터 저장
			try {
				responseSave(MESSAGE_CODE, SERVICE_CODE, requestMap, responseData, ksnetResponseMap);
			} catch (Exception e) {
				throw new Exception("[" + ksnetResponseMap.get("H_MSG_CD") + "-" + ksnetResponseMap.get("H_SVR_CD") + "] 응답 데이터 저장 중 오류가 발생하였습니다.");
			}
			
			//11.ksnet 응답 전문 예외처리
			try {
				except			= (Except) exceptClass.newInstance();
				responseMap		= except.exceptResponse(ksnetResponseMap);
			} catch (Exception e) {
				logger.error(e);
				throw new DefinedException("응답 데이터 예외 처리 중 오류가 발생 하였습니다.");
			}
			
			//12.은행응답 메시지 설정
			Map<String,Object> params	= null;
			Map<String,Object> common	= null;
			try {
				params	= new HashMap<String,Object>();
				params.put("CLSCD", "0005");
				params.put("DTLCD", responseMap.get(BNK_RT_CD));
				
				common	= cmnService.CMN_CMN_S10000(params);
				responseMap.put(BNK_RT_MSG, common.get("CDDTL_NM"));
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
			//서버송신 시각 저장
			try {dpsService.RL_DPS_U1004J(requestMap);}catch(Exception e){logger.error(e);}
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, new String(requestBytes, properties.getString("peer-encoding")), responseData, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * @Method		: requestSave
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 요청 데이터 저장 
	 * @변경이력		: 
	 * @param msgCode
	 * @param serviceCode
	 * @param params
	 * @throws Exception
	 */
	public void requestSave(String msgCode, String serviceCode, String requestContent, Map<String, Object> requestMap, Map<String, Object> info) throws Exception {
		try {
			if ("0100".equals(msgCode) && "100".equals(serviceCode)) {
				transRqeustSave(requestContent, requestMap, info);
			} else if ("0600".equals(msgCode) && "101".equals(serviceCode)) {
				transResultRqeustSave(requestContent, requestMap);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: transRqeustSave
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 요청 데이터 저장 
	 * @변경이력		: 
	 * @param params
	 * @throws Exception
	 */
	public void transRqeustSave(String requestContent, Map<String, Object> requestMap, Map<String, Object> info) throws Exception {
		Map<String, Object> dbParams	= new HashMap<String, Object>();
		try {
			dbParams.put("TRSRQ_SEQ", 		requestMap.get("TRSRQ_SEQ"));	//이체요청 일련번호
			dbParams.put("MBRINF_SEQ", 		info.get("MBRINF_SEQ"));		//상사 일련번호
			dbParams.put("TRSRQ_MSGCD",		requestMap.get("MESSAGECODE"));	//메시지코드
			dbParams.put("TRSRQ_SVRCD", 	requestMap.get("SERVICECODE"));	//업무구분코드
			dbParams.put("TRSRQ_ISUNO", 	requestMap.get("ISSUENO"));		//거래고유번호
			dbParams.put("TRSRQ_PRFNO", 	requestMap.get("SEQNO"));		//전문번호
			dbParams.put("TRSRQ_RCCD", 		requestMap.get("RECCD"));		//복기부호
			dbParams.put("TRSRQ_OBKCD", 	requestMap.get("OUTBANKCD"));	//출금 은행코드
			dbParams.put("TRSRQ_OACNO", 	AESCipher.encode(String.valueOf(requestMap.get("OUTACCTNO")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//출금 계좌번호
			dbParams.put("TRSRQ_OPW", 		AESCipher.encode(String.valueOf(requestMap.get("OUTACCTPW")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//출금 계좌비밀번호
			dbParams.put("TRSRQ_OAMT", 		requestMap.get("OUTAMT"));		//출금 금액
			dbParams.put("TRSRQ_ODSC", 		requestMap.get("OUTDESC"));		//출금 계좌적요
			dbParams.put("TRSRQ_IBKCD",		requestMap.get("INBANKCD"));	//입금 은행코드
			dbParams.put("TRSRQ_IACNO",		AESCipher.encode(String.valueOf(requestMap.get("INACCTNO")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//입금 계좌번호
			dbParams.put("TRSRQ_IDSC",		requestMap.get("INDESC"));		//입금 계좌적요
			dbParams.put("TRSRQ_RQTM",		new SimpleDateFormat("HHmmss").format(new Date()));		//이체요청시각
			dbParams.put("TRSRQ_CMSCD",		requestMap.get("CMSCD"));		//CMS코드
			dbParams.put("TRSRQ_SRYN",		requestMap.get("SALARYYN"));	//급여구분
			dbParams.put("TRSRQ_TRSRQ",		AESCipher.encode(requestContent, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//이체요청 원본데이터
			
			dpsService.RL_DPS_I1000J(dbParams);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @Method		: transResultRqeustSave
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 처리결과 요청 데이터 저장 
	 * @변경이력		: 
	 * @param params
	 * @throws Exception
	 */
	public void transResultRqeustSave(String requestContent, Map<String, Object> requestMap) throws Exception {
		Map<String, Object> dbParams	= new HashMap<String, Object>();
		try {
			dbParams.put("TRSRQ_SEQ", 		requestMap.get("TRSRQ_SEQ"));	//이체요청 일련번호
			dbParams.put("TRSRQ_RESRQ", 	AESCipher.encode(requestContent, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));		//결과조회 요청 원본 데이터
			
			dpsService.RL_DPS_I1001J(dbParams);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: responseSave
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 응답 데이터 저장
	 * @변경이력		: 
	 * @param msgCode
	 * @param serviceCode
	 * @param params
	 * @throws Exception
	 */
	public void responseSave(String msgCode, String serviceCode, Map<String, Object> requestMap, String responseData, Map<String, Object> ksnetResponseMap) throws Exception {
		try {
			if ("0100".equals(msgCode) && "100".equals(serviceCode)) {
				transResponseSave(requestMap, responseData, ksnetResponseMap);
			} else if ("0600".equals(msgCode) && "101".equals(serviceCode)) {
				transResponseRqeustSave(requestMap, responseData, ksnetResponseMap);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: transResponseSave
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 응답 데이터 저장 
	 * @변경이력		: 
	 * @param params
	 * @throws Exception
	 */
	public void transResponseSave(Map<String, Object> requestMap, String responseData, Map<String, Object> ksnetResponseMap) throws Exception {
		Map<String, Object> dbParams	= new HashMap<String, Object>();
		try {
			dbParams.put("TRSRQ_SEQ", 		requestMap.get("TRSRQ_SEQ"));			//이체요청 일련번호
			dbParams.put("TRSRQ_SIGN", 		ksnetResponseMap.get("BLC_CD"));		//출금 후 잔액부호
			dbParams.put("TRSRQ_BLNC", 		String.valueOf(Long.parseLong(String.valueOf(ksnetResponseMap.get("BALANCE")))));		//출금 후 잔액
			dbParams.put("TRSRQ_FEE", 		String.valueOf(Long.parseLong(String.valueOf(ksnetResponseMap.get("FEE")))));			//수수료
			dbParams.put("TRSRQ_TRSRS", 	AESCipher.encode(responseData, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//이체응답 원본데이터
			dbParams.put("TRSRQ_RSYN", 		"Y");	//이체요청 은행 응답여부
			dbParams.put("TRSRQ_RSCD", 		ksnetResponseMap.get("BANKRESPCODE"));	//이체요청 은행 응답코드

			dpsService.RL_DPS_U1000J(dbParams);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: transResponseRqeustSave
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 처리결과조회 응답 데이터 저장
	 * @변경이력		: 
	 * @param params
	 * @throws Exception
	 */
	public void transResponseRqeustSave(Map<String, Object> requestMap, String responseData, Map<String, Object> ksnetResponseMap) throws Exception {
		Map<String, Object> dbParams	= new HashMap<String, Object>();
		try {
			dbParams.put("TRSRQ_SEQ", 		requestMap.get("TRSRQ_SEQ"));			//이체요청 일련번호
			dbParams.put("TRSRQ_OACNO", 	AESCipher.encode(String.valueOf(ksnetResponseMap.get("OUT_ACC")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));		//출금 계좌번호
			dbParams.put("TRSRQ_IACNO", 	AESCipher.encode(String.valueOf(ksnetResponseMap.get("IN_ACC")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));		//입금 계좌번호
			dbParams.put("TRSRQ_OAMT", 		String.valueOf(Long.parseLong(String.valueOf(ksnetResponseMap.get("OUT_AMT")))));		//출금 금액
			dbParams.put("TRSRQ_FEE", 		String.valueOf(Long.parseLong(String.valueOf(ksnetResponseMap.get("FEE")))));			//수수료
			dbParams.put("TRSRQ_PAYNBR",	ksnetResponseMap.get("PAY_NBR"));		//처리결과 지급번호
			dbParams.put("TRSRQ_TRSTM", 	ksnetResponseMap.get("TRS_TM"));		//처리결과 이체시각
			dbParams.put("TRSRQ_PRCCD", 	ksnetResponseMap.get("PRC_CD"));		//처리결과 처리코드
			dbParams.put("TRSRQ_PYRNBR",	ksnetResponseMap.get("PYR_NBR"));		//처리결과 납부자번호
			dbParams.put("TRSRQ_BNKCD", 	ksnetResponseMap.get("IN_BNK"));		//처리결과 은행코드
			dbParams.put("TRSRQ_RESRS", 	AESCipher.encode(responseData, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//이체응답 원본데이터
			dbParams.put("TRSRQ_RSRSYN", 	"Y");	//결과조회 은행 응답여부
			dbParams.put("TRSRQ_RSRSCD", 	ksnetResponseMap.get("BANKRESPCODE"));	//결과조회 은행 응답코드

			dpsService.RL_DPS_U1005J(dbParams);
		} catch (Exception e) {
			throw e;
		}
	}
}
