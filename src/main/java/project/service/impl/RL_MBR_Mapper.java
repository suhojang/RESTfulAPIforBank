package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * 상사정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Mapper("RL_MBR_Mapper")
public interface RL_MBR_Mapper {

	/**
	 * 상사정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_MBR_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보 총개수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	int RL_MBR_S2000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 상사정보별 은행정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_MBR_S1001J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보 상세조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> RL_MBR_V1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 상사 은행 상세조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> RL_MBR_V1001J(Map<String, Object> param) throws Exception;
	
	/**
	 * 상사정보 등록
	 * @param param
	 * @throws Exception
	 */
	void RL_MBR_I1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 상사 은행정보 등록
	 * @param param
	 * @throws Exception
	 */
	void RL_MBR_I1001J(Map<String, Object> param) throws Exception;
	
	/**
	 * 전문 초기번호 등록
	 * @param param
	 */
	void RL_MBR_I1002J(Map<String, Object> param) throws Exception;

	/**
	 * 상사정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void RL_MBR_D1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 상사 은행정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void RL_MBR_D1001J(Map<String, Object> param) throws Exception;
}
