package project.client.rec;

import java.math.BigDecimal;

import com.kwic.exception.DefinedException;
import com.kwic.util.StringUtil;

/**
 * @파일명	: Recstruct
 * @작성일	: 2020. 9. 17.
 * @작성자	: 장수호
 * @설명		: 복기부호 처리 
 * @변경이력	:
 */
public class Reconstruct {
	private static final String _appendChar	= "0";
	
	
	/**
	 * 
	 * 1. 출금금액 13자리 생성, 자릿수 부족시 [왼쪽]에 [13자리]까지 '0'붙임
	 * 2. 출금계좌 9자리생성, 자릿수 부족시 [오른쪽]에 [9자리]까지 '0'붙임
	 * 3. 전문번호 6자리생성, 자릿수 부족시 [왼쪽]에 [6자리]까지 '0'붙임
	 * 4. 거래일자 [오른쪽]에서 [4자리] 추출
	 * 5. 입금계좌 [왼쪽]에서 [4자리] 추출
	 * 6. 변수1	= 거래금액 + 거래일자 + 전문번호 + 출금계좌
	 * 7. 변수2	= (거래일자 [오른쪽]에서 [4자리 + 입금계좌 [왼쪽]에서 [4자리]) 문자열로 변환하여 5자리로 변환. 자릿수 부족시 [왼쪽]에 [5자리]까지 '0' 붙임
	 * 8. 최종결과 = (변수1 / 변수2) 문자열로 변환하여 6자리로 변환. 자릿수 부족시 [왼쪽]에 [6자리]까지 '0' 붙임
	 * 
	 * @Method		: getKBRecCode
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: KB국민은행 복기부호 산출(원화)
	 * @변경이력		: 
	 * @param transAmt	: 거래금액
	 * @param transDat	: 거래일자
	 * @param seqNo		: 전문번호
	 * @param outAcc	: 출금계좌
	 * @return
	 */
	public static String getKBRecKRWCode(String amt, String day, String seq, String account, String inaccount) throws Exception {
		if (!StringUtil.isNumber(amt)) {
			throw new DefinedException("거래금액을 잘못 입력 하였습니다.");
		}
		if (!StringUtil.isNumber(day) || day.length() < 8) {
			throw new DefinedException("거래일자를 잘못 입력 하였습니다.");
		}
		if (!StringUtil.isNumber(seq)) {
			throw new DefinedException("전문번호를 잘못 입력 하였습니다.");
		}
		if (!StringUtil.isNumber(account)) {
			throw new DefinedException("출금계좌를 잘못 입력 하였습니다.");
		}
		
		int amtLength	= 13;
		int accLength	= 9;
		int seqLength	= 6;
		int dayLength	= 4;
		int inAccLength	= 4;
		
		String convertAMT	= StringUtil.addChar(amt, amtLength, _appendChar, true);
		String convertACC	= StringUtil.addChar(account, accLength, _appendChar, false);
		String convertSEQ	= StringUtil.addChar(seq, seqLength, _appendChar, true);
		
		String convertDAY	= StringUtil.subChar(day, dayLength, false);
		String convertINACC	= StringUtil.subChar(inaccount, inAccLength, true);
		
		int var1	= Integer.parseInt(convertAMT) + Integer.parseInt(day) + Integer.parseInt(convertSEQ) + Integer.parseInt(convertACC);
		long var2	= Integer.parseInt(StringUtil.addChar(
				String.valueOf(Integer.parseInt(convertDAY) + Integer.parseInt(convertINACC)), 
				5, 
				_appendChar, 
				true
				)
			);

		BigDecimal decimal1	= new BigDecimal(var1);
		BigDecimal decimal2	= new BigDecimal(var2);
		
		BigDecimal decimal3	= decimal1.divide(decimal2, 0, BigDecimal.ROUND_DOWN);
		
		return StringUtil.addChar(String.valueOf(decimal3), 6, _appendChar, true);
	}
	
	/**
	 * @Method		: getKBRecCode
	 * @작성일 		: 2020. 9. 17.
	 * @작성자 		: 장수호
	 * @Method 설명	: KB국민은행 복기부호 산출(외화) 
	 * @변경이력		: 
	 * @param fbsCode	: 업체코드
	 * @param outAcc	: 출금계좌
	 * @param transDat	: 거래일자
	 * @param transAmt	: 거래금액
	 * @return
	 */
	public static String getKBRecCode(String fbsCode, String outAcc, String transDat, String transAmt) throws Exception {
		if (!StringUtil.isNumber(fbsCode)) {
			throw new DefinedException("업체코드를 잘못 입력 하였습니다.");
		}
		if (!StringUtil.isNumber(outAcc)) {
			throw new DefinedException("출금계좌번호를 잘못 입력 하였습니다.");
		}
		if (!StringUtil.isNumber(transDat)) {
			throw new DefinedException("거래일자를 잘못 입력 하였습니다.");
		}
		if (!StringUtil.isNumber(transAmt)) {
			throw new DefinedException("거래금액 잘못 입력 하였습니다.");
		}
		
		int startIndex	= 0;
		int startLength	= 3;
		int endIndex	= 6;
		int endLength	= 6;
		
		int outLength	= 4;
		
		StringBuffer acc	= new StringBuffer();
		String cvtAcc		= "";
		if (outAcc.length() < 12) {
			for (int i = 0; i < outAcc.length(); i++) {
				char chr	= outAcc.charAt(i);
				if(!StringUtil.isNumber(Character.toString(chr))) {
					acc.append("1");
				} else {
					acc.append(Character.toString(chr));
				}
			}
			cvtAcc	= acc.toString();
			cvtAcc	= StringUtil.addChar(acc.toString(), 12, _appendChar, false);
		} else {
			cvtAcc	= outAcc.substring(startIndex, (startIndex + startLength)) + outAcc.substring(endIndex, (endIndex + endLength));
		}
		
		int sum		=	 Integer.parseInt(fbsCode) + Integer.parseInt(cvtAcc) + Integer.parseInt(transAmt);
		long dateSum	= 0; 
		for (int i = 0; i < transDat.length(); i++) {
			dateSum	+= Integer.parseInt(Character.toString(transDat.charAt(i)));
		}
		
		BigDecimal decimal1	= new BigDecimal(sum);
		BigDecimal decimal2	= new BigDecimal(dateSum);
		
		BigDecimal var1		= decimal1.divide(decimal2, 14, BigDecimal.ROUND_DOWN);

		int var2	= 0;
		if (String.valueOf(var1).indexOf(".") >= 0) {
			var2	= String.valueOf(var1).indexOf(".");
		} else {
			var2	= String.valueOf(var1).length() - 1;
		}
		
		byte[] result	= var1.toString().substring(0, var2).getBytes();
		if (result.length < outLength) {
			String tmpStr	= StringUtil.addChar(new String(result), outLength, _appendChar, true);

			result	= new byte[4];
			System.arraycopy(tmpStr.getBytes(), 0, result, 0, outLength);
		}
		
		byte[] bytes	= new byte[outLength];
		System.arraycopy(result, result.length - bytes.length, bytes, 0, bytes.length);
		
		return new String(bytes); 
	}
	
	public static void main(String[] args) throws Exception {
	}
	
}
