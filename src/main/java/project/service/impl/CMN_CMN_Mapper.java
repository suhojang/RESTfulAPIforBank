package project.service.impl;

import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 공통사항에 관한 데이터처리 매퍼 클래스
 *
 * @author  장정훈
 * @since 2019.11.19
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *
 *          수정일          수정자           수정내용
 *  ----------------    ------------    ---------------------------
 *   2019.11.13        장정훈          최초 생성
 *
 * </pre>
 */
@Mapper("CMN_CMN_Mapper")
public interface CMN_CMN_Mapper {

	/**
	 * 시퀀스번호 증가
	 * @param param - 시퀀스명
	 * @return int 증가된 시퀀스번호
	 * @exception Exception
	 */
	void nextval(Map<String,String> param) throws Exception;
	/**
	 * 현재 시퀀스번호 
	 * @param param - 시퀀스명
	 * @return int 현재 시퀀스 번호
	 * @exception Exception
	 */
	void currentval(Map<String,String> param) throws Exception;
	
	/**
	 * 요청 로그 등록
	 * @param param
	 * @throws Exception
	 */
	void log(Map<String,Object> param) throws Exception;
	
	/**
	 * 전문번호 채번
	 * @param param
	 * @throws Exception
	 */
	void nextpfrno(Map<String,String> param) throws Exception;
	
	/**
	 * 공통코드 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> CMN_CMN_S10000(Map<String, Object> params) throws Exception;
	
	/**
	 * 거래고유번호 체크
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> CMN_CMN_S10001(Map<String, Object> params) throws Exception;
}
