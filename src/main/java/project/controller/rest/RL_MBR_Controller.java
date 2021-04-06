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
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.CMN_CMN_Service;
import project.service.RL_MBR_Service;

/**
 * RL_GRP_Controller
 * 상사관리
 * 
 * @date 2020.09.15
 * @author 장수호
 *
 */
@RestController
public class RL_MBR_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "RL_MBR_Service")
	private RL_MBR_Service service;
	
	/**
	 * 상사 정보 조회
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/mbr/sel", produces="application/json; charset=UTF-8")
	public void mbr_sel(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
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
			
			//2.페이지 설정
			if (requestMap.get("CURRPAGENO")==null || "".equals(String.valueOf(requestMap.get("CURRPAGENO"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			else 
				requestMap.put("pageNo", requestMap.get("CURRPAGENO"));
				
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			
			//조회결과 총 개수
			int offset	= Integer.parseInt(String.valueOf(requestMap.get("limit"))) * (Integer.parseInt(String.valueOf(requestMap.get("pageNo"))));
			//3.상사 총 개수 조회
			int total	= service.RL_MBR_S2000J(requestMap);
			//다음페이지 유무
			responseMap.put("NEXTYN", (offset < total ? "Y" : "N"));
			
			//4.상사조회
			List<Map<String,Object>> list		= service.RL_MBR_S1000J(requestMap);
			List<Map<String,Object>> members	= new ArrayList<Map<String,Object>>();
			Map<String,Object> member			= null;
			
			Map<String,Object> params			= null;
			for (int i = 0; i < list.size(); i++) {
				member	= new HashMap<String,Object>();
				
				/********************************** 상사 정보 ***********************************/
				member.put("MEMBIZNO", 		String.valueOf(list.get(i).get("MBRINF_CNO")));
				member.put("MEMNM", 		String.valueOf(list.get(i).get("MBRINF_CNM")));
				/**********************************& 상사 정보 &***********************************/
				
				params	= new HashMap<String,Object>();
				params.put("MBRINF_SEQ", String.valueOf(list.get(i).get("MBRINF_SEQ")));
				
				//5.상사별 은행정보 조회
				List<Map<String,Object>> bankList	= service.RL_MBR_S1001J(params);
				
				List<Map<String,Object>> banks		= new ArrayList<Map<String,Object>>();
				Map<String,Object> bank				= null;
				
				for (int j = 0; j < bankList.size(); j++) {
					bank	= new HashMap<String,Object>();
					
					/********************************** 은행 정보 ***********************************/
					bank.put("CHANNEL",		String.valueOf(bankList.get(j).get("MBRBNK_CHNL")));	//전용선 코드
					bank.put("BANKCD", 		String.valueOf(bankList.get(j).get("MBRBNK_BNKCD")));	//은행코드
					bank.put("FBSCODE",		String.valueOf(bankList.get(j).get("MBRBNK_FBSCD")));	//FBS업체코드
					bank.put("RECCODEYN", 	String.valueOf(bankList.get(j).get("MBRBNK_RCYN")));	//복기부호사용여부
					/**********************************& 은행 정보 &***********************************/
					
					banks.add(bank);
				}
				//6.은행 정보 담기
				member.put("BANK", banks);
				
				//7.상사 정보 담기
				members.add(member);
			}
			//8. 상사정보 담기
			responseMap.put("REC", members);
			
			responseMap.put(RT_CD,	ErrorCode._0000);
			responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._0000));
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._9999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._9999);
				responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._9999));
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * 상사정보 등록
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/mbr/reg", produces="application/json; charset=UTF-8")
	public void mbr_reg(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
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
			
			//국민은행 식별코드 처리
			if ("004".equals(requestMap.get("BANKCD"))) {
				requestMap.put("MBRBNK_DISCD", 	requestMap.get("FBSCODE"));		//식별코드(국민은행은 FBS코드와 동일)
			}
			requestMap.put("MBRBNK_CHNL", 	StringUtil.nvl(requestMap.get("CHANNEL"), properties.getString("channel")));		//채널 설정
			
			//2.시퀀스획득
			requestMap.put("MBRINF_SEQ", cmnService.nextval("MBRINF_SEQ"));	//상사 일련번호
			requestMap.put("MBRBNK_SEQ", cmnService.nextval("MBRBNK_SEQ"));	//상사은행 일련번호
			
			//3.DB저장
			service.RL_MBR_I1000J(requestMap);
			
			responseMap.put(RT_CD,	ErrorCode._0000);
			responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._0000));
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._9999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._9999);
				responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._9999));
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * 상사정보 삭제
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/mbr/del", produces="application/json; charset=UTF-8")
	public void mbr_del(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
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
			
			//2.DB 삭제
			service.RL_MBR_D1000J(requestMap);
			
			responseMap.put(RT_CD,	ErrorCode._0000);
			responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._0000));
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._9999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._9999);
				responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._9999));
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
