package project.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.kwic.security.aes.AESCipher;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.CMN_CMN_Service;
import project.service.RL_MBR_Service;
import project.service.RL_TRS_Service;

/**
 * @파일명	: RL_TRS_Controller
 * @작성일	: 2020. 9. 21.
 * @작성자	: 장수호
 * @설명		: 거래내역 조회
 * @변경이력	:
 */
@RestController
public class RL_TRS_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "RL_MBR_Service")
	private RL_MBR_Service service;
	
	@Resource(name = "RL_TRS_Service")
	private RL_TRS_Service trsService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/van/trs/sel", produces="application/json; charset=UTF-8")
	public void van_trs_sel(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		List<Map<String,Object>> list		= null;
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
			
			//3.페이지 설정
			if (requestMap.get("CURRPAGENO")==null || "".equals(String.valueOf(requestMap.get("CURRPAGENO"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			else 
				requestMap.put("pageNo", requestMap.get("CURRPAGENO"));
				
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//4.고객사정보 조회
			Map<String,Object> info	= service.RL_MBR_V1001J(requestMap);
			if (info == null) {
				throw new DefinedException("등록 되지 않은 상사 정보 입니다.");
			}
//			requestMap.put("MBRINF_SEQ", info.get("MBRINF_SEQ"));	//상사일련번호
			requestMap.put("MBRBNK_SEQ", info.get("MBRBNK_SEQ"));	//상사 은행일련번호
			requestMap.put("ENC_ACCTNO", AESCipher.encode(String.valueOf(requestMap.get("ACCTNO")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//계좌번호
			
			//조회결과 총 개수
			int offset	= Integer.parseInt(String.valueOf(requestMap.get("limit"))) * (Integer.parseInt(String.valueOf(requestMap.get("pageNo"))));
			//거래내역 총 개수 조회
			int total	= trsService.RL_TRS_S2000J(requestMap);
			
			//다음페이지 유무
			responseMap.put("NEXTYN", (offset < total ? "Y" : "N"));
			
			//5.거래내역 조회
			list		= trsService.RL_TRS_S1000J(requestMap);

			List<Map<String,Object>> trans	= new ArrayList<Map<String,Object>>();
			Map<String,Object> tran			= null;
			for (int i = 0; i < list.size(); i++) {
				tran	= new HashMap<String,Object>();
				
				tran.put("TRXNO", 			list.get(i).get("TRSHIS_TRSNO"));		//전문번호
				tran.put("BANKCD", 			list.get(i).get("TRSHIS_TRBKCD"));		//은행코드
				tran.put("ACCTNO", 			AESCipher.decode(String.valueOf(list.get(i).get("TRSHIS_ACCNO")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));	//계좌번호
				tran.put("CURRCD", 			"KRW");									//거래계좌통화코드
				tran.put("TRXDATE", 		list.get(i).get("TRSHIS_TRSDT"));		//거래일자
				tran.put("TRXTIME", 		list.get(i).get("TRSHIS_TRSTM"));		//거래시간
				tran.put("TRXTYPE", 		list.get(i).get("TRSHIS_TRSKN"));		//거래구분코드
				tran.put("TRXTYPENM", 		list.get(i).get("TRSHIS_TRSKN_NM"));	//거래구분명
				tran.put("TRXAMT", 			list.get(i).get("TRSHIS_TRSAMT"));		//거래금액
				tran.put("BALANCE", 		list.get(i).get("TRSHIS_ACCAMT"));		//거래 후 잔액
				tran.put("DESC", 			list.get(i).get("TRSHIS_NTC"));			//적요
				tran.put("BRANCHCD", 		list.get(i).get("TRSHIS_BRNCD"));		//거래지점코드
				tran.put("BANKRESPCODE", 	list.get(i).get("TRSHIS_BRSCD"));		//은행응답코드
				tran.put("BANKRESPDESC", 	list.get(i).get("TRSHIS_BRSCD_NM"));	//은행응답코드메시지
				
				trans.add(tran);
			}
			responseMap.put("REC", trans);
			
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
			//조회정보 저장
			try {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> param	= new HashMap<String, Object>();
					param.put("TRSHIS_SEQ", list.get(i).get("TRSHIS_SEQ"));
					param.put("ISSUENO", 	requestMap.get("ISSUENO"));
					
					trsService.RL_TRS_U1000J(param);
				}
			}catch(Exception e){
				logger.error(e);
			}
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, null, null, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
