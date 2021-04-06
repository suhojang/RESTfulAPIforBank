package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.RL_MBR_Service;

/**
 * 상사정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("RL_MBR_Service")
public class RL_MBR_ServiceImpl extends ServiceIMPL implements RL_MBR_Service {

	@Resource(name="RL_MBR_Mapper")
	private RL_MBR_Mapper dao;

	/**
	 * 상사정보 조회
	 */
	@Override
	public List<Map<String,Object>> RL_MBR_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		return dao.RL_MBR_S1000J(param);
	}
	
	/**
	 * 상사정보 총 개수 조회
	 */
	@Override
	public int RL_MBR_S2000J(Map<String, Object> param) throws Exception {
		return dao.RL_MBR_S2000J(param);
	}

	
	/**
	 * 상사별 은행정보 조회
	 */
	@Override
	public List<Map<String,Object>> RL_MBR_S1001J(Map<String,Object> param) throws Exception {
		return dao.RL_MBR_S1001J(param);
	}


	/**
	 * 상사정보 상세조회
	 */
	@Override
	public Map<String, Object> RL_MBR_V1000J(Map<String, Object> param) throws Exception {
		return dao.RL_MBR_V1000J(param);
	}
	

	/**
	 * 상사은행 상세조회
	 */
	@Override
	public Map<String, Object> RL_MBR_V1001J(Map<String, Object> param) throws Exception {
		if (param.get("BANKCD")==null) {
			param.put("BANKCD", param.get("OUTBANKCD"));
		}
		return dao.RL_MBR_V1001J(param);
	}

	/**
	 * 상사정보 등록
	 */
	@Override
	public void RL_MBR_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			//해당하는 상사가 이미 존재하는지 확인
			Map<String,Object> info	= dao.RL_MBR_V1000J(param);
			if (info != null) {
				param.put("MBRINF_SEQ", info.get("MBRINF_SEQ"));
				List<Map<String,Object>> banks	= dao.RL_MBR_S1001J(param);
				if(banks.size() == 0) {
					dao.RL_MBR_I1001J(param);	//상사 은행정보 저장
					dao.RL_MBR_I1002J(param);	//전문번호 등록
				} else {
					throw new DefinedException("이미 등록 된 상사정보 입니다.");
				}
			} else {
				dao.RL_MBR_I1000J(param);	//상사 정보 저장
				dao.RL_MBR_I1001J(param);	//상사 은행정보 저장
				dao.RL_MBR_I1002J(param);	//전문번호 등록
			}
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "상사정보 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}

	/**
	 * 상사정보 삭제
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void RL_MBR_D1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> info	= dao.RL_MBR_V1000J(param);
			if (info == null)
				throw new DefinedException("등록되지 않은 상사정보 입니다.");
			
			dao.RL_MBR_D1001J(param);	//상사 은행정보 삭제
			dao.RL_MBR_D1000J(param);	//상사정보 삭제
		} catch (Exception e) {
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "상사정보 삭제 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._1010);
			throw ex;
		}
	}
}
