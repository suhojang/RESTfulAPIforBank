package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * 단지정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface RL_GRP_Service {

	/**
	 * 단지정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> RL_GRP_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 단지정보 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void RL_GRP_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 단지정보 수정
	 * @param requestMap
	 * @throws DefinedException
	 */
	void RL_GRP_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 단지정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void RL_GRP_D1000J(Map<String, Object> param) throws DefinedException;
}
