package project.service;

import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * @파일명	: RL_DPS_Service
 * @작성일	: 2020. 9. 17.
 * @작성자	: 장수호
 * @설명		: 이체 정보 저장(지급이체, 이체결과조회) 
 * @변경이력	:
 */
public interface RL_DPS_Service {

	/**
	 * @Method		: RL_DPS_I1000J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 요청 정보 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_U1000J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 응답 정보 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_U1001J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 요청 시각 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_U1001J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_U1002J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 서버 송신 시각 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_U1002J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_V1000J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래고유번호에 해당하는 지급이체 요청 정보 가져오기
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws DefinedException
	 */
	Map<String,Object> RL_DPS_V1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * @Method		: RL_DPS_I1001J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 처리결과 조회 요청 정보 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_I1001J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_U1003J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: ksnet 이체 처리결과 요청 시각 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_U1003J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_U1004J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 처리결과 서버 송신 시각 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_U1004J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * @Method		: RL_DPS_U1005J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: ksnet 이체 처리결과 응답 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	void RL_DPS_U1005J(Map<String, Object> param) throws DefinedException;
}
