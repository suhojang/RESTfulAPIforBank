package project.client.except;

import java.util.Map;

import project.client.except.parent.Except;

/**
 * @파일명	: CommonExcept
 * @작성일	: 2020. 9. 17.
 * @작성자	: 장수호
 * @설명		: 공통 예외 처리
 * @변경이력	:
 */
public class CommonExcept implements Except {

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
		return responseMap;
	}

}
