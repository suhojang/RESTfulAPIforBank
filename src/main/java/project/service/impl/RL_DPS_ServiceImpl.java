package project.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.RL_DPS_Service;

/**
 * @파일명	: RL_DPS_ServiceImpl
 * @작성일	: 2020. 9. 17.
 * @작성자	: 장수호
 * @설명		: 이체 정보 저장(지급이체, 이체결과조회) 
 * @변경이력	:
 */
@Service("RL_DPS_Service")
public class RL_DPS_ServiceImpl extends ServiceIMPL implements RL_DPS_Service {

	@Resource(name="RL_DPS_Mapper")
	private RL_DPS_Mapper dao;

	/**
	 * @Method		: RL_DPS_I1000J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 요청 정보 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_I1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "지급 이체 요청 데이터 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_DPS_U1000J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 지급이체 응답 정보 저장 
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_U1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "지급 이체 응답 데이터 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_DPS_U1001J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 요청 시각 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_U1001J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_U1001J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "이체 요청 시각 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_DPS_U1002J
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: 서버 송신 시각 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_U1002J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_U1002J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "서버 송신 시각 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
		
	}

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
	@Override
	public Map<String, Object> RL_DPS_V1000J(Map<String, Object> param) throws Exception {
		return dao.RL_DPS_V1000J(param);
	}

	/**
	 * @Method		: RL_DPS_I1001J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 처리결과 조회 요청 정보 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_I1001J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_I1001J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "지급 이체 결과 조회 요청 데이터 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_DPS_U1003J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: ksnet 이체 처리결과 요청 시각 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_U1003J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_U1003J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "지급 이체 결과 조회 요청 시각 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_DPS_U1004J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: 이체 처리결과 서버 송신 시각 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_U1004J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_U1004J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "지급 이체 결과 조회 서버 송신 시각 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * @Method		: RL_DPS_U1005J
	 * @작성일 		: 2020. 9. 18.
	 * @작성자 		: 장수호
	 * @Method 설명	: ksnet 이체 처리결과 응답 데이터 저장
	 * @변경이력		: 
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_DPS_U1005J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.RL_DPS_U1005J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "지급 이체 결과 조회 응답 데이터 저장 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}
}
