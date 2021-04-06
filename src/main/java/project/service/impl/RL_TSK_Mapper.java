package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명	: RL_TSK_Mapper
 * @작성일	: 2020. 9. 22.
 * @작성자	: 장수호
 * @설명		: Task 업무 수행
 * @변경이력	:
 */
@Mapper("RL_TSK_Mapper")
public interface RL_TSK_Mapper {
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
	 * @throws Exception
	 */
	void RL_TSK_U1000J(Map<String, Object> param) throws Exception;

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
	 * @throws Exception
	 */
	void RL_TSK_I3000J(Map<String, Object> param) throws Exception;
}
