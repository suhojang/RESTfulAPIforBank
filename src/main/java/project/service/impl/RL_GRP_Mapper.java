package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * 단지정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Mapper("RL_GRP_Mapper")
public interface RL_GRP_Mapper {

	/**
	 * 단지정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_GRP_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 단지정보 상세조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> RL_GRP_V1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 단지정보 등록
	 * @param param
	 * @throws Exception
	 */
	void RL_GRP_I1000J(Map<String, Object> param) throws Exception;

	/**
	 * 단지정보 수정
	 * @param param
	 * @throws Exception
	 */
	void RL_GRP_U1000J(Map<String, Object> param) throws Exception;

	/**
	 * 단지정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void RL_GRP_D1000J(Map<String, Object> param) throws Exception;

}
