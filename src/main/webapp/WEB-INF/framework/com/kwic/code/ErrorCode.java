package com.kwic.code;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode {
	public static final String _0000	= "0000";
	public static final String _0001	= "0001";
	public static final String _0002	= "0002";
	public static final String _0003	= "0003";
	public static final String _0004	= "0004";
	public static final String _0100	= "0100";
	public static final String _0101	= "0101";
	public static final String _0200	= "0200";
	public static final String _0201	= "0201";
	public static final String _1010	= "1010";
	public static final String _1011	= "1011";
	public static final String _9999	= "9999";
	
	public static final Map<String, String> ERROR_MESSAGE	= new HashMap<String, String>();
	static {
		ERROR_MESSAGE.put(_0000, "");
		ERROR_MESSAGE.put(_0001, "요청 데이터 복호화 오류");
		ERROR_MESSAGE.put(_0002, "요청 데이터 KSNET전문 Parsing 오류");
		ERROR_MESSAGE.put(_0003, "요청 데이터 validation 오류");
		ERROR_MESSAGE.put(_0004, "요청 데이터 Parsing 오류");
		ERROR_MESSAGE.put(_0100, "KSNET 서버 연결 시간 초과");
		ERROR_MESSAGE.put(_0101, "KSNET 서버 연결 오류");
		ERROR_MESSAGE.put(_0200, "KSNET 응답 데이터 Parsing 오류");
		ERROR_MESSAGE.put(_0201, "응답 데이터 암호화 오류");
		ERROR_MESSAGE.put(_1010, "통신 서버 오류");
		ERROR_MESSAGE.put(_1011, "KS-NET 통지 데이터 저장 오류");
		ERROR_MESSAGE.put(_9999, "기타오류");
	}
}
