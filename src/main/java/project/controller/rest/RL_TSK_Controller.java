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
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.client.execute.KsnetOpenManager;
import project.service.CMN_CMN_Service;

/**
 * @파일명	: RL_TSK_Controller
 * @작성일	: 2020. 9. 22.
 * @작성자	: 장수호
 * @설명		: 개시전문 수동처리 type : 1 => 개시전문 송/수신 미완료 건  9 => 개시전문 송/수신(전체) 
 * @변경이력	:
 */
@RestController
public class RL_TSK_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/tsk/exec", produces="application/json; charset=UTF-8")
	public void van_trs_sel(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		DefinedException ex	= null;
		try {
			try {	
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				ex	= new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
				ex.setStatusCode(ErrorCode._0004);
				throw ex;
			}
			
			KsnetOpenManager.getInstance().KsnetOpenStart("9".equals(String.valueOf(requestMap.get("type"))));
			
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
			try {cmnService.log(getLogData(responseMap, requestContent, null, null, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
