package project.client.parser.parent;

import java.util.Map;

/**
 * 
 * @파일명	: ClientParser
 * @작성일	: 2020. 9. 16.
 * @작성자	: 장수호
 * @설명		: client parser interface
 * @변경이력	:
 */
public interface ClientParser {
	public byte[] requestParse(String MESSAGE_CODE, String SERVICE_CODE, Map<String, Object> params, String encoding) throws Exception;
	public Map<String,Object> responseParse(String MESSAGE_CODE, String SERVICE_CODE, byte[] response, String encoding) throws Exception;
}
