package project.server.parser;

import java.util.HashMap;
import java.util.Map;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.security.aes.AESCipher;

import project.service.RL_TCP_Service;

public class Parser_0200300 extends Parser {
	private static Logger logger = LoggerFactory.getLogger(Parser_0200300.class);
	
	private RL_TCP_Service service;
	
	/**
	 * @Method		: saveRequest
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: KS-NET 통지데이터 저장 
	 * @변경이력		: 
	 * @throws Exception
	 */
	@Override
	public void saveRequest() throws Exception {
		service			= (RL_TCP_Service) getBean("RL_TCP_Service");
		
		try {
			Map<String,Object> requestMap	= super.getReqMap();
			Map<String,Object> params		= new HashMap<String,Object>();
			
			Map<String,Object> info	= service.RL_TCP_V1000J(requestMap);
			
			params.put("TRSHIS_SEQ", 	super.getSerial());					//거래내역 일련번호
			params.put("MBRBNK_SEQ", 	info.get("MBRBNK_SEQ"));			//상사 은행 일련번호
			params.put("TRSHIS_SNDCNT",	requestMap.get("H_SND_CNT"));		//송신횟수
			params.put("TRSHIS_TRSNO",	requestMap.get("H_SEQ_NO"));		//전문번호
			params.put("TRSHIS_SNDDT",	requestMap.get("H_SND_DT"));		//전송일자
			params.put("TRSHIS_SNDTM",	requestMap.get("H_SND_TM"));		//전송시간
			params.put("TRSHIS_RSCD",	requestMap.get("H_RS_CD"));			//응답코드
			params.put("TRSHIS_BRSCD",	requestMap.get("BANKRESPCODE"));	//은행응답코드
			params.put("TRSHIS_SCHDT",	requestMap.get("H_SH_DT"));			//조회일자
			params.put("TRSHIS_SCHNO",	requestMap.get("H_SH_NO"));			//조회번호
			params.put("TRSHIS_ACCNO",	AESCipher.encode(String.valueOf(requestMap.get("ACC_NO")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//계좌번호
			params.put("TRSHIS_ASMCNT",	requestMap.get("MAKE_CNT"));		//조립건수
			params.put("TRSHIS_TRSKN",	requestMap.get("TRS_KN"));			//거래구분코드
			params.put("TRSHIS_TRSAMT",	requestMap.get("TRS_AMT"));			//거래금액
			params.put("TRSHIS_ACCAMT",	requestMap.get("BLC_AMT"));			//계좌잔액
			params.put("TRSHIS_NTC",	requestMap.get("DSC"));				//적요
			params.put("TRSHIS_CHKNO",	requestMap.get("CHK_NO"));			//수표번호
			params.put("TRSHIS_CASH",	requestMap.get("CASH"));			//현금
			params.put("TRSHIS_CHKAMT",	requestMap.get("CHK_AMT"));			//타행수표금액
			params.put("TRSHIS_ETCAMT",	requestMap.get("ETC_AMT"));			//가계수표,기타금액
			params.put("TRSHIS_VACCNO",	requestMap.get("VACC_NO"));			//가상계좌번호
			params.put("TRSHIS_TRSDT",	requestMap.get("TRS_DT"));			//거래일자
			params.put("TRSHIS_TRSTM",	requestMap.get("TRS_TM"));			//거래시간
			params.put("TRSHIS_BKTRNO",	requestMap.get("BKTR_NO"));			//통장거래 일련번호
			params.put("TRSHIS_TRBKCD",	requestMap.get("BNK_CD3"));			//거래은행코드
			params.put("TRSHIS_BRNCD",	requestMap.get("BRN_CD"));			//입금지점코드
			params.put("TRSHIS_ORGRQ",	AESCipher.encode(new String(super.getRequest(), super.getEncoding()), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));		//거래내역통지 요청 원본데이터
			
			service.RL_TCP_I1000J(params);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	/**
	 * @Method		: saveResponse
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: KS-NET 응답 데이터 저장
	 * @변경이력		: 
	 * @throws Exception
	 */
	@Override
	public void saveResponse() throws Exception {
		try {
			Map<String,Object> params		= new HashMap<String,Object>();
			params.put("TRSHIS_SEQ", 	super.getSerial());		//거래내역 일련번호
			params.put("TRSHIS_BRSCD", 	super.getResMap().get("BANKRESPCODE"));		//은행응답 코드
			params.put("TRSHIS_ORGRS", 	AESCipher.encode(new String(super.getResponse(), super.getEncoding()), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));		//거래내역통지 응답 원본데이터
			
			service.RL_TCP_U1000J(params);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
}
