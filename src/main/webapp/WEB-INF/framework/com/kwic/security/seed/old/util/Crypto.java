
package com.kwic.security.seed.old.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.kwic.security.seed.old.encrypt.CryptoSDKv20;

/**
 * @author freetome
 *
 * SEED 방식으로 암복호화하기 위한 클래스 
 * String to String 방식으로 변환한다.   
 */
public class Crypto {

    public String encodeData(String strplain, String encKey) throws Exception{
        String strResult=null;
        byte [] plain =strplain.getBytes();
        byte [] cipher = new byte[2 * (plain.length  + 100) ];
        
        int rtnCode= CryptoSDKv20.MemEncryptV20(plain,  0, plain.length, cipher, 0, encKey, 2);
                
        if( rtnCode <= 0) {
        	switch(rtnCode) {
				case 0:
					throw new Exception("암호화 에러: 평문이 아닙니다.");
				case -1:
					throw new Exception("암호화 에러: 암호화 algorithm is not supported.");
				case -2:
					throw new Exception("암호화 에러: 암호화문이 적합하지 않습니다.");				
				default:
					throw new Exception("암호화 에러: " + rtnCode);
        	}
        }
                
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(cipher);
        strResult = baos.toString("8859_1");
                                
        if (strResult == null || strResult.trim().equals("")) return "";
        strResult = strResult.trim();
        return strResult;
    }
    
    public byte[] encodeData(byte[] plain,  String encKey) throws Exception{
        String strResult=null;
        byte [] cipher = new byte[2 * (plain.length  + 100) ];
        int rtnCode= CryptoSDKv20.MemEncryptV20(plain,  0, plain.length, cipher, 0, encKey, 2);
        
        if( rtnCode <= 0) {
        	switch(rtnCode) {
				case 0:
					throw new Exception("암호화 에러: 평문이 아닙니다.");
				case -1:
					throw new Exception("암호화 에러: 암호화 algorithm is not supported.");
				case -2:
					throw new Exception("암호화 에러: 암호화문이 적합하지 않습니다.");				
				default:
					throw new Exception("암호화 에러: " + rtnCode);
        	}
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(cipher);        
        strResult = baos.toString("8859_1");
        
        if (strResult == null || strResult.trim().equals("")) return plain;
        strResult = strResult.trim();
        
        return strResult.getBytes("8859_1");
    }
    
    public void encodeFile(String strplain, String encKey, String enFile) throws Exception{
        byte [] plain =strplain.getBytes();
        
        int rtnCode= CryptoSDKv20.Mem2FileEncryptV20(plain,  0, plain.length, enFile, encKey, 2);
                
        if( rtnCode <= 0) {
        	switch(rtnCode) {
    			case 0:
    				throw new Exception("암호화 에러: 평문이 아닙니다.");
    			case -1:
    				throw new Exception("암호화 에러: " + enFile + " already exists or cannot be created.");
    			case -2:
    				throw new Exception("암호화 에러: algorithm is not supported ");
    			default:
    				throw new Exception("암호화 에러: " + rtnCode);
        	}
        }
    }
    
    public String decodeData( String strCiper, String enckey ) throws Exception{
        String rtn=null;
        byte [] cipher  = null;
        
        cipher = strCiper.trim().getBytes("8859_1");
                
        byte [] plain = new byte[cipher.length ];
        int rtnCode = CryptoSDKv20.MemDecryptV20(cipher,  0, cipher.length, plain, 0, enckey);
      
        if( rtnCode <= 0) {
        	switch(rtnCode) {
				case 0:
					throw new Exception("복호화 에러: 복호화할 수 없는 내용입니다.");
				case -1:
					throw new Exception("복호화 에러: plain 부분의 버퍼 사이즈가 부족합니다.");
				case -2:
					throw new Exception("복호화 에러: 암호키가 틀렸습니다.");
				default:
					throw new Exception("복호화 에러: " + rtnCode);
        	}
        }
        
        rtn = new String(plain, 0, rtnCode);
        if (rtn == null || rtn.trim().equals("")) return strCiper;
        rtn =rtn.trim();
        return rtn;
        
    }
    
    public byte[] decodeData( byte[] cipher, String enckey ) throws Exception{
        byte [] plain = new byte[cipher.length ];
        int rtnCode = CryptoSDKv20.MemDecryptV20(cipher,  0, cipher.length, plain, 0, enckey);
      
        if( rtnCode <= 0) {
            switch(rtnCode) {
    			case 0:
    				throw new Exception("복호화 에러: 복호화할 수 없는 내용입니다.");
    			case -1:
    				throw new Exception("복호화 에러: plain 부분의 버퍼 사이즈가 부족합니다.");
    			case -2:
    				throw new Exception("복호화 에러: 암호키가 틀렸습니다!");
    			default:
    				throw new Exception("복호화 에러: " + rtnCode);
            }
        }
        byte [] rtnByte = new byte[rtnCode] ;
        System.arraycopy(plain,0, rtnByte, 0, rtnCode);
        return rtnByte;        
    }    
    
    public String decodeFile( String enFile, String enckey ) throws Exception{
        File input = null;
        try {
        	input = new File(enFile);
        } catch(Exception e) {
        	throw new Exception(enFile + " 생성시 Exception 발생");
        }
                
        byte[] plain = new byte[(int)input.length() + 100];
        int rtnCode = CryptoSDKv20.File2MemDecryptV20(enFile, plain, 0, enckey);
      
        if( rtnCode <= 0) {
        	switch(rtnCode) {
        		case 0:
        			throw new Exception("복호화 에러: " + enFile + "은 복호화할 수 없는 파일입니다.");
        		case -1:
        			throw new Exception("복호화 에러: " + enFile + "을 읽을수 없습니다.");
        		case -2:
        			throw new Exception("복호화 에러: plain 부분의 버퍼 사이즈가 부족합니다.");
        		case -3:
        			throw new Exception("복호화 에러: 암호키가 틀렸습니다.");
        		default:
        			throw new Exception("복호화 에러: " + rtnCode);
        	}
        }
        
        return new String(plain).trim();        
    }
    
    public String getFileData(String file) throws Exception {
    	StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
    	try {    		
    		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    		String line = null;
    		int linecnt = 0;
    		while ((line = br.readLine()) != null) {
    			if(linecnt > 0) sb.append(System.getProperty("line.separator"));
            	sb.append(line);
            	linecnt++;
            }
    		if(br != null) br.close();
    	} catch(FileNotFoundException fnfe) {
    		throw new Exception("FileNotFoundException 발생: " + fnfe.getMessage());
    	} catch(IOException ioe) {
    		throw new Exception("IOException 발생: " + ioe.getMessage());
    	} catch(Exception e) {
    		throw new Exception("Exception 발생: " + e.getMessage());
    	} finally {
			try {
				if(br != null) br.close();
			} catch(Exception e) {
				br = null;
			}
    	}
    	
    	return sb.toString();
    }
    
    public void saveFileData(String endData, String file) throws Exception {
    	try {
    		BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));

    		bout.write(endData.getBytes());
    		bout.flush();
    		bout.close();    		
    	} catch(Exception e) {
    		throw new Exception(file + " 파일 저장 오류: " + e.getMessage());    		
    	}
    }

	/**
	*	사용자로부터 암호화키를 받은후 복호화작업을 진행한다.
	*	@param	decodeBR	BufferedReader
	*	@return				복호화 성공여부
	*/
	public String makeDecode(String encodedStr,String encKey) {
		if(encKey == null || encKey.trim().length() < 1) {
    		return null;
    	}
		
		try {
			return decodeData(encodedStr, encKey);
		} catch(Exception e) {
			 return null;
    	}		
	}
}