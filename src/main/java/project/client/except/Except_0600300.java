package project.client.except;

import java.util.Map;

import project.client.except.parent.Except;

/**
 * @파일명	: Except_0600300
 * @작성일	: 2020. 9. 17.
 * @작성자	: 장수호
 * @설명		: 잔액 조회 요청/응답 데이터 처리 
 * @변경이력	:
 */
public class Except_0600300 implements Except {

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
	public Map<String, Object> exceptResponse(Map<String, Object> responseMap) throws Exception {
		if ("-".equals(responseMap.get("BLC_CD"))) {
			responseMap.put("BALANCE", String.valueOf(responseMap.get("BLC_CD")) + String.valueOf(responseMap.get("BALANCE")));
		}
		responseMap.remove("BLC_CD");	//잔액부호 데이터 삭제
		
		return responseMap;
	}

}
