package com.kwic.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sms {
	static Map<String,String> ERRORS	= new HashMap<String,String>();
	static{
		ERRORS.put("0","성공");
		ERRORS.put("1","TIMEOUT");
		ERRORS.put("A","핸드폰 호 처리 중");
		ERRORS.put("B","음영지역");
		ERRORS.put("C","power off");
		ERRORS.put("D","메시지 저장개수 초과");
		ERRORS.put("2","잘못된 전화번호");
		ERRORS.put("a","일시 서비스 정지");
		ERRORS.put("b","기타 단말기 문제");
		ERRORS.put("c","착신 거절");
		ERRORS.put("d","기타");
		ERRORS.put("e","이통사 SMC 형식 오류");
		ERRORS.put("s","메시지 스팸차단(NPro 내부)");
		ERRORS.put("n","수신번호 스팸차단(NPro 내부)");
		ERRORS.put("r","회신번호 스팸차단(NPro 내부)");
		ERRORS.put("t","스팸차단 중 2개 이상 중복 차단(NPro 내부)");
		ERRORS.put("Z","메시지 접수시 기타 실패");
		ERRORS.put("f","아이하트 자체 형식 오류");
		ERRORS.put("g","SMS/LMS/MMS 서비스 불가 단말기");
		ERRORS.put("h","핸드폰 호 불가 상태");
		ERRORS.put("i","SMC 운영자가 메시지 삭제");
		ERRORS.put("j","이통사 내부 메시지 Que Full");
		ERRORS.put("k","이통사에서 spam 처리");
		ERRORS.put("l","www.nospam.go.kr 에 등록된 번호에 대해 아이하트에서 spam 처리한 건");
		ERRORS.put("m","아이하트에서 Spam 처리한 건");
		ERRORS.put("n","건수제안에 걸린 경우 (건수제안 계약이 되어 있는 경우)");
		ERRORS.put("o","메시지의 길이가 제안된 길이를 벗어난 경우");
		ERRORS.put("p","폰 번호가 형식에 어긋난 경우");
		ERRORS.put("q","필드 형식이 잘못된 경우 (예:데이터 내용이 없는 경우)");
		ERRORS.put("x","MMS 콘텐트의 정보를 참조할 수 없음");
		ERRORS.put("u","BARCODE 생성 실패");
		ERRORS.put("q","메시지 중복키 체크(NPro 내부)");
		ERRORS.put("y","하루에 한 수신번호에 보낼수 있는 메시지 수량초과(NPro 내부)");
		ERRORS.put("w","SMS 전송문자에 특정키워드가 없으면 SPAM 처리하여 메시지 전송제한(NPro 내부)");
		ERRORS.put("z","처리 되지 않은 기타오류");
	}
	/**
	 * sms전송오류 메시지
	 * */
	public static final String getErrorMessage(String errorcode){
		return errorcode==null||ERRORS.get(errorcode)==null?"알 수 없는 오류(코드="+errorcode+")":ERRORS.get(errorcode);
	}
	
	
	public synchronized static String sendSms(Map<String, String> map,String msg) throws Exception{
		if(msg.getBytes("euc-kr").length<=80)
			return sendSms(map);
		int idx1	= 0;
		List<String> msgList	= new ArrayList<String>();
		for(int i=0;i<msg.length();i++){
			if(msg.substring(idx1,i).getBytes("euc-kr").length>=80){
				msgList.add(msg.substring(idx1,i));
				idx1	= i;
			}
		}
		if(idx1<msg.length())
			msgList.add(msg.substring(idx1));
		
		String rst	= null;
		for(int i=0;i<msgList.size();i++){
			map.put("MSG_TXT", msgList.get(i));
			rst	= sendSms(map);
			if(!"SMOK".equals(rst)){
				return rst;
			}
		}
		return rst;
	}
//	public static void main(String[] args) throws Exception{
//		String msg	= "123456789012345678901234567890123456789012345678901234567890123456789012345678901";
//		sendSms(new HashMap<String, String>(),msg);
//	}
	
	
	
	public synchronized static String sendSms(Map<String, String> map) throws Exception{
		
		String msg = "";
		
		//----------------------------------------------------------------------
		// Url Connection Post���
		//----------------------------------------------------------------------
        StringBuffer sb = new StringBuffer();
		StringBuffer sbQry = new StringBuffer();
	    sbQry.append("ComID="	 + URLEncoder.encode("kwic","euc-kr"));
		sbQry.append("&ComPass=" + URLEncoder.encode("kwic5539","euc-kr"));
		//2015.10.14 SMS ���� ��ȣ ��å �������� ���Ͽ� ������ ��� ��ȭ��ȣ�� 02-1588-5976 �����ͷ� ����. (������ SMS ��ȭ��ȣ�� �̸� ��� �Ǿ� �־�� �Ѵ�.)
		String sphone2 = "1588";
		String sphone3 = "5976";
		sbQry.append("&sphone1=" + URLEncoder.encode("","euc-kr"));
		sbQry.append("&sphone2=" + URLEncoder.encode(sphone2,"euc-kr"));
		sbQry.append("&sphone3=" + URLEncoder.encode(sphone3,"euc-kr"));
		
		sbQry.append("&rphone1=" + URLEncoder.encode((String)map.get("rphone1"),"euc-kr"));
		sbQry.append("&rphone2=" + URLEncoder.encode((String)map.get("rphone2"),"euc-kr"));
		sbQry.append("&rphone3=" + URLEncoder.encode((String)map.get("rphone3"),"euc-kr"));
		sbQry.append("&UserID="  + URLEncoder.encode("fctools","euc-kr"));
		sbQry.append("&SDate="   + URLEncoder.encode("00000000","euc-kr"));
		sbQry.append("&STime="   + URLEncoder.encode("000000","euc-kr"));
		sbQry.append("&Msg="	 + URLEncoder.encode((String)map.get("MSG_TXT"),"euc-kr"));

	    String qry = sbQry.toString();
	    
	    DataOutputStream dos = null;
        HttpURLConnection connect = null;
		try {
			connect = (HttpURLConnection)(new URL("http://sms.kwic.co.kr/iHeart/SmsSend.asp")).openConnection();
			connect.setRequestMethod("POST");
			connect.setDoInput(true);
			connect.setDoOutput(true);
			connect.setUseCaches(false);

			dos = new DataOutputStream(connect.getOutputStream());
			dos.writeBytes(qry);
			dos.flush();
			dos.close();

			BufferedReader in = new BufferedReader( new InputStreamReader(connect.getInputStream(),"euc-kr"));
			String s ="";
			while( (s = in.readLine()) != null ){
			   sb.append(s);
			}
			in.close();
			msg = sb.toString();
		}catch (Exception e) {
			throw e;
		}finally{
			try{if(connect!=null)connect.disconnect();}catch(Exception e){connect=null;}
			try{if(dos!=null)dos.close();}catch(Exception e){dos=null;}
		}
		return msg;
	}
}
