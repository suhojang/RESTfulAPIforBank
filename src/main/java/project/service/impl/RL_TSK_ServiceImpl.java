package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.RL_TSK_Service;

@Service("RL_TSK_Service")
public class RL_TSK_ServiceImpl extends ServiceIMPL implements RL_TSK_Service {

	@Resource(name="RL_TSK_Mapper")
	private RL_TSK_Mapper dao;
	
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
	@Override
	public List<Map<String, Object>> RL_TSK_S1000J(Map<String, Object> param) throws Exception {
		return dao.RL_TSK_S1000J(param);
	}
	
	/**
	 * @Method		: RL_TCP_U1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 개시여부, 개시일자 변경
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_TSK_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_TSK_U1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "개시여부, 개시일자 변경 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._9999);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_TSK_S2000J
	 * @작성일 		: 2020. 9. 24.
	 * @작성자 		: 장수호
	 * @Method 설명	: 결번 요청데이터 조회
	 * @변경이력		: 
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> RL_TSK_S2000J() throws Exception {
		return dao.RL_TSK_S2000J();
	}

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
	@Override
	public Map<String, Object> RL_TSK_S3000J(Map<String, Object> param) throws Exception {
		return dao.RL_TSK_S3000J(param);
	}

	/**
	 * @Method		: RL_TSK_I3000J
	 * @작성일 		: 2020. 9. 24.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 결번 데이터 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws Exception 
	 */
	@Override
	public void RL_TSK_I3000J(Map<String, Object> param) throws Exception {
		dao.RL_TSK_I3000J(param);
	}
}
