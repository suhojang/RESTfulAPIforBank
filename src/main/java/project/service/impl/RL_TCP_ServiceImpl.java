package project.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.RL_TCP_Service;

/**
 * @파일명	: RL_TCP_ServiceImpl
 * @작성일	: 2020. 9. 21.
 * @작성자	: 장수호
 * @설명		: KS-NET TCP 통지데이터 저장
 * @변경이력	:
 */
@Service("RL_TCP_Service")
public class RL_TCP_ServiceImpl extends ServiceIMPL implements RL_TCP_Service {

	@Resource(name="RL_TCP_Mapper")
	private RL_TCP_Mapper dao;

	/**
	 * @Method		: RL_TCP_I1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 요청 데이터 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_TCP_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_TCP_I1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "거래내역 통지 요청 데이터 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1011);
			throw ex;
		}
	}
	
	/**
	 * @Method		: RL_TCP_U1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래내역 통지 응답 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_TCP_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_TCP_U1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "거래내역 통지 응답 데이터 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1011);
			throw ex;
		}
	}
	
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
	@Override
	public Map<String, Object> RL_TCP_V1000J(Map<String, Object> param) throws Exception {
		return dao.RL_TCP_V1000J(param);
	}
}
