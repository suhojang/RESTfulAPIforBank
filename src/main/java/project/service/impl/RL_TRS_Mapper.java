package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명	: RL_TRS_Mapper
 * @작성일	: 2020. 9. 21.
 * @작성자	: 장수호
 * @설명		: 거래내역 조회 
 * @변경이력	:
 */
@Mapper("RL_TRS_Mapper")
public interface RL_TRS_Mapper {

	/**
	 * @Method		: RL_TRS_S1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 조회 
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_TRS_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * @Method		: RL_TRS_S2000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 총개수 조회
	 * @변경이력		: 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	int RL_TRS_S2000J(Map<String, Object> param) throws Exception;

	/**
	 * @Method		: RL_TRS_U1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 조회정보 저장 
	 * @변경이력		: 
	 * @param param
	 */
	void RL_TRS_U1000J(Map<String, Object> param) throws Exception;
}
