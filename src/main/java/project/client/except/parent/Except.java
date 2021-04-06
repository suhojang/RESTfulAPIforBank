package project.client.except.parent;

import java.util.Map;

public interface Except {
	/**
	 * @Method		: exceptRequest
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 요청데이터 예외 처리 
	 * @변경이력		: 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public abstract Map<String, Object> exceptRequest(Map<String, Object> requestMap) throws Exception;
	
	/**
	 * @Method		: exceptResponse
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 응답 데이터 예외 처리 
	 * @변경이력		: 
	 * @param responseMap
	 * @return
	 * @throws Exception
	 */
	public abstract Map<String, Object> exceptResponse(Map<String, Object> responseMap) throws Exception;
}
