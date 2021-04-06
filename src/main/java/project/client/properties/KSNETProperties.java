package project.client.properties;

import java.util.HashMap;
import java.util.Map;

public class KSNETProperties {
	public static final String _RES_MESSAGE_CODE	= "1";
	
	/**
	 * 업무별 length properties
	 */
	public static final Map<String, Integer> SEVICE_LENGTH	= new HashMap<String, Integer>();
	static {
		SEVICE_LENGTH.put("ksnet-0800100", 300);
		SEVICE_LENGTH.put("ksnet-0600300", 300);
		SEVICE_LENGTH.put("ksnet-0600400", 300);
		SEVICE_LENGTH.put("ksnet-0600101", 300);
		SEVICE_LENGTH.put("ksnet-0100100", 300);
		SEVICE_LENGTH.put("ksnet-0400100", 300);
		SEVICE_LENGTH.put("ksnet-0400200", 300);
		SEVICE_LENGTH.put("ksnet-0200300", 300);
		SEVICE_LENGTH.put("ksnet-0200640", 300);
	}
}
