package project.client.except;

import java.util.HashMap;
import java.util.Map;

import project.client.except.parent.Except;

/**
 * @파일명	: Except_0100100
 * @작성일	: 2020. 9. 17.
 * @작성자	: 장수호
 * @설명		: 지급 이체 요청/응답 데이터 처리 
 * @변경이력	:
 */
public class Except_0100100 implements Except {

	/**
	 * @Method		: exceptRequest
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 요청 예외 처리 
	 * @변경이력		: 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> exceptRequest(Map<String, Object> requestMap) throws Exception {
		return requestMap;
	}

	/**
	 * 
	 * @Method		: exceptResponse
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 응답 데이터 예외 처리 
	 * @변경이력		: 
	 * @param responseMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> exceptResponse(Map<String, Object> ksnetResponseMap) throws Exception {
		Map<String, Object> responseMap	= new HashMap<String, Object>();
		if ("-".equals(ksnetResponseMap.get("BLC_CD"))) {
			ksnetResponseMap.put("BALANCE", String.valueOf(ksnetResponseMap.get("BLC_CD")) + String.valueOf(ksnetResponseMap.get("BALANCE")));
		}
		
		responseMap.put("BANKRESPCODE", ksnetResponseMap.get("BANKRESPCODE"));
		responseMap.put("BALANCE", 	String.valueOf(Long.parseLong(new String(String.valueOf(ksnetResponseMap.get("BALANCE")).getBytes()).trim())));
		responseMap.put("FEE", 		String.valueOf(Long.parseLong(new String(String.valueOf(ksnetResponseMap.get("FEE")).getBytes()).trim())));
		
		return responseMap;
	}

}
