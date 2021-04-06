package com.kwic.datasource;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class EncryptUtil {
	
	public void encrypt(){
		BufferedReader br		= null;
		FileWriter fw			= null;
		String driverClassName	= null;
		String url				= null;
		String username			= null;
		String password			= null;
		String line	= System.getProperty("line.separator");
		try{
			br	= new BufferedReader(new InputStreamReader(System.in));
			if((driverClassName=br.readLine())!=null)
				driverClassName	= driverClassName.trim();
			
			if((url=br.readLine())!=null)
				url	= url.trim();
			
			if((username=br.readLine())!=null)
				username	= username.trim();
			
			if((password=br.readLine())!=null)
				password	= password.trim();
			
			StringBuffer sb	= new StringBuffer();
			
			sb.append(line+line+"============================= ENCRYPT TEXT ================================="+line).append(line);
			sb.append("[driverClassName]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(driverClassName)).append(line);
			sb.append(line+"[url]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(url)).append(line);
			sb.append(line+"[username]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(username)).append(line);
			sb.append(line+"[password]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(password)).append(line);
			sb.append(line+"============================================================================"+line+line).append(line);
			
			fw	= new FileWriter("encrypt.txt");
			fw.write(sb.toString());
			fw.flush();fw.close();
			
			br.readLine();
			
		}catch(Exception e){
			br=null;fw=null;
		}finally{
			try{if(br!=null)br.close();}catch(Exception e){br=null;}
			try{if(fw!=null)fw.close();}catch(Exception e){fw=null;}
		}
	}
	
	public static void main(String[] args){
		new EncryptUtil().encrypt();
	}
}
