package com.kwic.sql;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;

public class SqlDebuger {
	  protected static Logger logger = LoggerFactory.getLogger(SqlDebuger.class);
	  
	  public static void debug(String msg){
		  logger.debug(msg);
	  }
}
