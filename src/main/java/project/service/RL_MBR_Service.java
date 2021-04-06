package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * 상사정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface RL_MBR_Service {

	/**
	 * 상사정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_MBR_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보 총 개수 조회
	 * @param param
	 * @throws Exception
	 */
	int RL_MBR_S2000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보별 은행정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_MBR_S1001J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보 상세조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> RL_MBR_V1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보 은행 상세조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> RL_MBR_V1001J(Map<String,Object> param) throws Exception;
	
	/**
	 * 상사정보 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void RL_MBR_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 상사정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void RL_MBR_D1000J(Map<String, Object> param) throws DefinedException;
}
