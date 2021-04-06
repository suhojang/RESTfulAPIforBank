package com.kwic.web.schedule.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.math.Calculator;
import com.kwic.security.aes.AESCipher;
import com.kwic.telegram.tcp.JTcpManager;
import com.kwic.util.StringUtil;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.client.parser.parent.ClientParser;
import project.service.CMN_CMN_Service;
import project.service.RL_TSK_Service;

/**
 * @파일명	: Task_ksnetinvl
 * @작성일	: 2020. 12. 7.
 * @작성자	: 장수호
 * @설명		: ksnet 거래내역통지 결번 송신
 * @변경이력	:
 */
@Component
public class Task_ksnetinvl extends Task{
	private static Logger logger = LoggerFactory.getLogger(Task_ksnetinvl.class);
	
	private static final String _TRS_MESSAGE_CODE	= "0200";
	private static final String _TRS_SERVICE_CODE	= "640";
	
	private RL_TSK_Service service;
	private CMN_CMN_Service cmnService;
	
	private EgovPropertyService properties;
	
	public Task_ksnetinvl() throws Exception {
		super();
		try {
			service			= (RL_TSK_Service) ContextLoader.getCurrentWebApplicationContext().getBean("RL_TSK_Service");
			cmnService		= (CMN_CMN_Service) ContextLoader.getCurrentWebApplicationContext().getBean("CMN_CMN_Service");
			properties		= (EgovPropertyService)ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * @Method		: execute
	 * @작성일 		: 2020. 12. 7.
	 * @작성자 		: 장수호
	 * @Method 설명	: 결번 데이터 조회
	 * @변경이력		: 
	 * @param params
	 * @throws Exception
	 */
	protected void execute(Map<String,String> params) throws Exception {
		List<Map<String, Object>> invl_list	= new ArrayList<Map<String, Object>>();
		try {
			List<Map<String,Object>> list	= service.RL_TSK_S2000J();
			
			Map<String, Object> invl		= new HashMap<String, Object>();
			for (int i = 0; i < list.size(); i++) {
				int diff_seq	= Integer.parseInt(String.valueOf(list.get(i).get("DIFF_SEQ")));	//은행일련번호 비교
				int diff_snddt	= Integer.parseInt(String.valueOf(list.get(i).get("DIFF_TRSDT")));	//거래일자 비교
				int diff_trsno	= Integer.parseInt(String.valueOf(list.get(i).get("DIFF_TRSNO")));	//전문번호 비교
				
				int af_bnkseq	= Integer.parseInt(String.valueOf(StringUtil.nvl(list.get(i).get("AF_BNKSEQ"),"0")));
				int bf_bnkseq	= Integer.parseInt(String.valueOf(list.get(i).get("BF_BNKSEQ")));
				
				int af_trsno	= Integer.parseInt(String.valueOf(StringUtil.nvl(list.get(i).get("AF_TRSNO"),"0"))); 
				int bf_trsno	= Integer.parseInt(String.valueOf(list.get(i).get("BF_TRSNO")));
				
				String af_trsdt		= String.valueOf(list.get(i).get("AF_TRSDT"));
				String bf_trsdt		= String.valueOf(list.get(i).get("BF_TRSDT"));
				if ((af_bnkseq == 0 && bf_trsno != 1) || (diff_seq != 0 && bf_trsno != 1)) {
					for (int j = 1; j < bf_trsno; j++) {
						invl	= new HashMap<String, Object>();
						invl.put("MBRBNK_SEQ", 	bf_bnkseq);
						invl.put("TRSDT", 		bf_trsdt);
						invl.put("TRSNO", 		StringUtil.addChar(String.valueOf(j), 6, "0", true));
						
						invl_list.add(invl);
					}
				}
				
				if (diff_seq == 0) {
					if (diff_snddt == 0 && diff_trsno > 1) {
						for (int j = af_trsno+1; j < bf_trsno; j++) {
							invl	= new HashMap<String, Object>();
							invl.put("MBRBNK_SEQ",	af_bnkseq);
							invl.put("TRSDT", 		af_trsdt);
							invl.put("TRSNO", 		StringUtil.addChar(String.valueOf(j), 6, "0", true));
							
							invl_list.add(invl);
						}
					}
				}
			}
			
			startTransINVL(invl_list);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * @Method		: startTransINVL
	 * @작성일 		: 2020. 12. 7.
	 * @작성자 		: 장수호
	 * @Method 설명	: ks-net 결번 요청
	 * @변경이력		: 
	 * @param list
	 * @throws Exception
	 */
	private void startTransINVL(List<Map<String,Object>> list) throws Exception {
		Map<String,Object> param	= new HashMap<String,Object>();
		Map<String,Object> info		= new HashMap<String,Object>();
		
		Map<String,Object> ksnetResponseMap	= new HashMap<String,Object>();
		
		ClientParser parser	= null;
		Class<?> parserClass = null;
		
		byte[] requestBytes	= null;
		String responseData	= null;
		try {
			for (int i = 0; i < list.size(); i++) {
				param	= new HashMap<String, Object>();
				param.put("MBRBNK_SEQ", list.get(i).get("MBRBNK_SEQ"));
				
				info	= service.RL_TSK_S3000J(param);
				
				param.put("DISCD", 		info.get("MBRBNK_DISCD"));	//식별코드
				param.put("FBSCD", 		info.get("MBRBNK_FBSCD"));	//업체코드
				param.put("BANKCD", 	info.get("MBRBNK_BNKCD"));	//은행코드
				param.put("SEQNO", 		cmnService.nextpfrno(String.valueOf(info.get("MBRBNK_SEQ"))));	//전문번호
				param.put("TRSDT", 		list.get(i).get("TRSDT"));	//원거래일자
				param.put("TRSNO", 		list.get(i).get("TRSNO"));	//원거래 전문번호
				
				//parsing class
				try {
					parserClass	= Class.forName("project.client.parser." + info.get("MBRBNK_CHNL") + "Parser");
				} catch (Exception e) {
					parserClass	= Class.forName("project.client.parser.CommonParser");
				}
				
				param.put("MBRBNK_SEQ", 	info.get("MBRBNK_SEQ"));
				param.put("TRSHIS_SEQ", 	String.valueOf(cmnService.nextval("TRSHIS_SEQ")));
				//request data parsing and save
				try {
					parser			= (ClientParser) parserClass.newInstance();
					requestBytes	= parser.requestParse(_TRS_MESSAGE_CODE, _TRS_SERVICE_CODE, param, properties.getString("peer-encoding"));

				} catch (Exception e) {
					logger.error(e);
					throw new Exception("KS-NET 요청 전문 Parsing 중 오류가 발생 하였습니다.");
				}
				
				//request data send and response
				try {
					if (!"T".equals(properties.getString("service-type"))) {
						String ip		= properties.getString("ksnet-tcp-ip");
						int port 		= Integer.parseInt(properties.getString("ksnet-tcp-"+properties.getString("service-type")+"-port"));
						int timeout 	= (int) Calculator.calculate(properties.getString("ksnet-tcp-timeout"));
						
						responseData 	= JTcpManager.getInstance().sendMessage(
								ip, 
								port, 
								new String(requestBytes, properties.getString("peer-encoding")), 
								timeout
								);
					} else {
						responseData	= "02403TOT 02403TOT040210300100000120180803131226    0000        000001               004             20413024154    01308100000000010000000962038072810115튠븃곰찔샵(             00000000010000000000000000000000000000025691000355405  20180803131144      0810810115                                      ";
					}
				} catch (Exception e) {
					logger.error(e);
					throw new Exception("KS-NET TCP 통신 중 오류가 발생하였습니다.");
				}
				
				//response data parsing and save
				try {
					ksnetResponseMap	= parser.responseParse(_TRS_MESSAGE_CODE, _TRS_SERVICE_CODE, responseData.getBytes(properties.getString("peer-encoding")), properties.getString("peer-encoding"));
				} catch (Exception e) {
					logger.error(e);
					throw new Exception("KS-NET 응답 전문 Parsing 중 오류가 발생하였습니다.");
				}
				
				try {
					logger.info("ksnetResponseMap :: "+ksnetResponseMap);
					save(ksnetResponseMap, param, requestBytes, responseData);
				} catch (Exception e) {
					throw new Exception("[" + ksnetResponseMap.get("H_MSG_CD") + "-" + ksnetResponseMap.get("H_SVR_CD") + "] 응답 데이터 저장 중 오류가 발생하였습니다.");
				}
				
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * @Method		: save
	 * @작성일 		: 2020. 12. 7.
	 * @작성자 		: 장수호
	 * @Method 설명	: 요청/응답 데이터 저장
	 * @변경이력		: 
	 * @param ksnetResponseMap
	 * @param requestMap
	 * @param requestBytes
	 * @param responseData
	 * @throws Exception
	 */
	public void save(Map<String, Object> ksnetResponseMap, Map<String,Object> requestMap, byte[] requestBytes, String responseData) throws Exception {
		Map<String,Object> param	= new HashMap<String,Object>();
		try {
			param.put("TRSHIS_SNDCNT", 	requestMap.get("H_SND_CNT"));
			param.put("TRSHIS_TRSNO", 	requestMap.get("BKTR_NO"));
			param.put("TRSHIS_SNDDT",	requestMap.get("H_SND_DT"));
			param.put("TRSHIS_SNDTM", 	requestMap.get("H_SND_TM"));
			param.put("TRSHIS_SCHDT", 	requestMap.get("H_SH_DT"));
			param.put("TRSHIS_SCHNO", 	requestMap.get("H_SH_NO"));
			param.put("TRSHIS_TRSDT", 	requestMap.get("TRS_DT"));
			param.put("TRSHIS_INLRQ", 	AESCipher.encode(new String(requestBytes, properties.getString("peer-encoding")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));
			
			param.put("TRSHIS_SEQ", 	requestMap.get("TRSHIS_SEQ"));
			param.put("MBRBNK_SEQ", 	requestMap.get("MBRBNK_SEQ"));
			param.put("TRSHIS_RSCD", 	ksnetResponseMap.get("BANKRESPCODE"));
			param.put("TRSHIS_BRSCD", 	ksnetResponseMap.get("BANKRESPCODE"));
			param.put("TRSHIS_ACCNO", 	AESCipher.encode(String.valueOf(ksnetResponseMap.get("ACC_NO")),AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));
			param.put("TRSHIS_ASMCNT", 	ksnetResponseMap.get("MAKE_CNT"));
			param.put("TRSHIS_TRSKN", 	ksnetResponseMap.get("TRS_KN"));
			param.put("TRSHIS_TRSAMT", 	ksnetResponseMap.get("TRS_AMT"));
			param.put("TRSHIS_ACCAMT", 	ksnetResponseMap.get("BLC_AMT"));
			param.put("TRSHIS_NTC", 	ksnetResponseMap.get("DSC"));
			param.put("TRSHIS_CHKNO", 	ksnetResponseMap.get("CHK_NO"));
			param.put("TRSHIS_CASH", 	ksnetResponseMap.get("CASH"));
			param.put("TRSHIS_CHKAMT", 	ksnetResponseMap.get("CHK_AMT"));
			param.put("TRSHIS_ETCAMT", 	ksnetResponseMap.get("ETC_AMT"));
			param.put("TRSHIS_VACCNO", 	ksnetResponseMap.get("VACC_NO"));
			param.put("TRSHIS_TRSTM", 	ksnetResponseMap.get("TRS_TM"));
			param.put("TRSHIS_BKTRNO", 	ksnetResponseMap.get("BKTR_NO"));
			param.put("TRSHIS_TRBKCD", 	ksnetResponseMap.get("BNK_CD3"));
			param.put("TRSHIS_BRNCD", 	ksnetResponseMap.get("BRN_CD"));
			param.put("TRSHIS_INLRS", AESCipher.encode(responseData, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));
			
			service.RL_TSK_I3000J(param);
		} catch (Exception e) {
			throw e;
		}
	}
}