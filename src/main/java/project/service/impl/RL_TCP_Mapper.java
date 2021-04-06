package project.service.impl;

import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명	: RL_TCP_Mapper
 * @작성일	: 2020. 9. 21.
 * @작성자	: 장수호
 * @설명		: KS-NET TCP 통지데이터 저장
 * @변경이력	:
 */
@Mapper("RL_TCP_Mapper")
public interface RL_TCP_Mapper {
	/**
	 * @Method		: RL_TCP_I1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 요청 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws Exception
	 */
	void RL_TCP_I1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * @Method		: RL_TCP_U1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 응답 데이터 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws Exception
	 */
	void RL_TCP_U1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * @Method		: RL_TCP_V1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 상사정보 조회 
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> RL_TCP_V1000J(Map<String, Object> param) throws Exception;
}
