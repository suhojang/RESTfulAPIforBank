package project.client.parser;

import java.util.Map;

import project.client.parser.parent.ClientParser;

/**
 * 
 * @파일명	: CommonParser
 * @작성일	: 2020. 9. 16.
 * @작성자	: 장수호
 * @설명		: 공통 parser - 미구현
 * @변경이력	:
 */
public class CommonParser implements ClientParser {
	/**
	 * @Method		: requestParse
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: 요청 전문 parsing
	 * @변경이력		: 
	 * @param MESSAGE_CODE
	 * @param SERVICE_CODE
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	@Override
	public byte[] requestParse(String MESSAGE_CODE, String SERVICE_CODE, Map<String, Object> params, String encoding) throws Exception {
		return null;
	}

	/**
	 * @Method		: responseParse
	 * @작성일 		: 2020. 9. 16.
	 * @작성자 		: 장수호
	 * @Method 설명	: 응답 전문 parsing
	 * @변경이력		: 
	 * @param MESSAGE_CODE
	 * @param SERVICE_CODE
	 * @param response
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> responseParse(String MESSAGE_CODE, String SERVICE_CODE, byte[] response, String encoding) throws Exception {
		return null;
	}
}
