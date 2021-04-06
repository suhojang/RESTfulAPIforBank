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
import project.controller.rest.layout.LayoutFilter;
import project.service.CMN_CMN_Service;
import project.service.RL_GRP_Service;

/**
 * RL_GRP_Controller
 * 단지관리
 * 
 * @date 2020.09.15
 * @author 장수호
 *
 */
@RestController
public class RL_GRP_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "RL_GRP_Service")
	private RL_GRP_Service service;
	
	/**
	 * 단지 정보 등록
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rest/grp/reg", produces="application/json; charset=UTF-8")
	public void grp_reg(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			
			//2. 시퀀스 획득
			requestMap.put("GRPINF_SEQ", cmnService.nextval("GRPINF_SEQ"));
			
			//3.DB 저장
			service.RL_GRP_I1000J(requestMap);
			
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
