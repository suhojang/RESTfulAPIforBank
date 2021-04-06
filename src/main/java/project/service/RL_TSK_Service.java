package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * @파일명	: RL_TSK_Service
 * @작성일	: 2020. 9. 22.
 * @작성자	: 장수호
 * @설명		: 스케쥴 Task 처리 
 * @변경이력	:
 */
public interface RL_TSK_Service {
	/**
	 * @Method		: RL_TSK_S1000J
	 * @작성일 		: 2020. 9. 22.
	 * @작성자 		: 장수호
	 * @Method 설명	: 업무개시 대상 목록 조회  
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> RL_TSK_S1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * @Method		: RL_TSK_U1000J
	 * @작성일 		: 2020. 9. 22.
	 * @작성자 		: 장수호
	 * @Method 설명	: 개시여부, 개시일자 변경
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_TSK_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_TSK_S2000J
	 * @작성일 		: 2020. 9. 24.
	 * @작성자 		: 장수호
	 * @Method 설명	: 결번 요청 데이터 조회
	 * @변경이력		: 
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> RL_TSK_S2000J() throws Exception;
	
	/**
	 * @Method		: RL_TSK_S3000J
	 * @작성일 		: 2020. 9. 24.
	 * @작성자 		: 장수호
	 * @Method 설명	: 상사별 은행정보 조회
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> RL_TSK_S3000J(Map<String, Object> param) throws Exception;
	
	/**
	 * @Method		: RL_TSK_I3000J
	 * @작성일 		: 2020. 9. 24.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 결번 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_TSK_I3000J(Map<String, Object> param) throws Exception;
}
