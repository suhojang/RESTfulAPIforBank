package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.RL_GRP_Service;

/**
 * 단지정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("RL_GRP_Service")
public class RL_GRP_ServiceImpl extends ServiceIMPL implements RL_GRP_Service {

	@Resource(name="RL_GRP_Mapper")
	private RL_GRP_Mapper dao;

	/**
	 * 단지정보 조회
	 */
	@Override
	public List<Map<String,Object>> RL_GRP_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		return dao.RL_GRP_S1000J(param);
	}
	
	/**
	 * 단지정보 등록
	 */
	@Override
	public void RL_GRP_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			//해당하는 단지가 이미 존재하는지 확인
			Map<String,Object> info	= dao.RL_GRP_V1000J(param);
			if (info != null)
				throw new DefinedException("이미 등록 된 단지정보 입니다.");
			
			dao.RL_GRP_I1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "단지정보 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * 단지정보 수정
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_GRP_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> info	= dao.RL_GRP_V1000J(param);
			if (info == null)
				throw new DefinedException("등록되지 않은 단지 정보 입니다.");
			
			dao.RL_GRP_U1000J(param);
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "단지정보 수정 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * 단지정보 삭제
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_GRP_D1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> info	= dao.RL_GRP_V1000J(param);
			if (info == null)
				throw new DefinedException("등록되지 않은 단지 정보 입니다.");
			
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "단지정보 삭제 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

}
