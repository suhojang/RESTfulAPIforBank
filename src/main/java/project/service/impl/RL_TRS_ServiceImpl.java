package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.service.ServiceIMPL;

import project.service.RL_TRS_Service;

/**
 * @파일명	: RL_TRS_ServiceImpl
 * @작성일	: 2020. 9. 21.
 * @작성자	: 장수호
 * @설명		: 거래내역 조회 
 * @변경이력	:
 */
@Service("RL_TRS_Service")
public class RL_TRS_ServiceImpl extends ServiceIMPL implements RL_TRS_Service {

	@Resource(name="RL_TRS_Mapper")
	private RL_TRS_Mapper dao;

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
	@Override
	public List<Map<String,Object>> RL_TRS_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		return dao.RL_TRS_S1000J(param);
	}
	
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
	@Override
	public int RL_TRS_S2000J(Map<String, Object> param) throws Exception {
		return dao.RL_TRS_S2000J(param);
	}

	/**
	 * @Method		: RL_TRS_U1000J
	 * @작성일 		: 2020. 9. 21.
	 * @작성자 		: 장수호
	 * @Method 설명	: 조회정보 저장
	 * @변경이력		: 
	 * @param requestMap
	 * @throws Exception
	 */
	@Override
	public void RL_TRS_U1000J(Map<String, Object> param) throws Exception {
		dao.RL_TRS_U1000J(param);
	}
}
