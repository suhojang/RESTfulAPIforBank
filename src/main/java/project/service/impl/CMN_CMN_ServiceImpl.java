package project.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.service.ServiceIMPL;

import project.service.CMN_CMN_Service;

/**
 * @Class Name : CMN_CMN_ServiceImpl.java
 * @Description : 공통사항에 관한 데이터처리  Business Implement Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 *   2019.11.13        장정훈          최초 생성
 *
 * @author 장정훈
 * @since 2019.11.13
 * @version 1.0
 * @see
 *
 */

@Service("CMN_CMN_Service")
public class CMN_CMN_ServiceImpl extends ServiceIMPL implements CMN_CMN_Service {


	@Resource(name="CMN_CMN_Mapper")
	private CMN_CMN_Mapper dao;

	/**
	 * 시퀀스번호 증가
	 * @param param - 시퀀스명
	 * @return int 증가된 시퀀스번호
	 * @exception Exception
	 */
	@Override
	public String nextval(String sqname) throws Exception{
		Map<String,String> param	= new HashMap<String,String>();
		param.put("sqname", sqname);
		param.put("RESULT", "");
		dao.nextval(param);
		return param.get("RESULT");
	}
	/**
	 * 현재 시퀀스번호 
	 * @param param - 시퀀스명
	 * @return int 현재 시퀀스 번호
	 * @exception Exception
	 */
	@Override
	public String currentval(String sqname) throws Exception{
		Map<String,String> param	= new HashMap<String,String>();
		param.put("sqname", sqname);
		param.put("RESULT", "");
		dao.currentval(param);
		return param.get("RESULT");
	}
	
	/**
	 * 요청 로그 등록
	 */
	@Override
	public void log(Map<String, Object> param) throws Exception {
		dao.log(param);
	}
	
	/**
	 * 전문번호 채번
	 */
	@Override
	public String nextpfrno(String seq) throws Exception {
		Map<String,String> param	= new HashMap<String,String>();
		param.put("sqno", seq);
		param.put("RESULT", "");
		dao.nextpfrno(param);
		
		return param.get("RESULT");
	}
	
	/**
	 * 공통코드 조회
	 */
	@Override
	public Map<String, Object> CMN_CMN_S10000(Map<String, Object> params) throws Exception {
		return dao.CMN_CMN_S10000(params);
	}
	
	/**
	 * @Method		: CMN_CMN_S10001
	 * @작성일 		: 2020. 9. 23.
	 * @작성자 		: 장수호
	 * @Method 설명	: 거래고유번호 체크
	 * @변경이력		: 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> CMN_CMN_S10001(Map<String, Object> params) throws Exception {
		return dao.CMN_CMN_S10001(params);
	}
}
