package com.kwic.web.init;

import java.util.Map;

public interface Handler {
	Map<String,Object> getServiceParams();
	void put(String name,Object obj) throws Exception;
	void handle() throws Exception;
}
