package project.service;

import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * @파일명	: RL_TCP_Service
 * @작성일	: 2020. 9. 21.
 * @작성자	: 장수호
 * @설명		: KS-NET TCP 통지데이터 저장
 * @변경이력	:
 */
public interface RL_TCP_Service {
	/**
	 * @Method		: RL_TCP_I1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 요청 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_TCP_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_TCP_U1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 응답 데이터 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_TCP_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_TCP_V1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 상사정보조회 
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> RL_TCP_V1000J(Map<String, Object> param) throws Exception;
}
