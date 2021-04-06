/**
  * TCP CLIENT
  * @author   장정훈
  * @date      2007. 11. 09
  * @package com.kwic.telegram.tcp
  *
  * @description TCP CLIENT
  */
package com.kwic.telegram.tcp;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;

import com.kwic.io.JOutputStream;


/**<pre>
* TCP CLIENT .
*</pre>
*
*@author  Jang,Junghoon
*@since 1.3.*
*/
public class JTcpManager{
	/**<pre>
	* system line separater String
	* </pre>
	*/
	public static final	String	_LINE_SP	= System.getProperty("line.separator");
	/**<pre>
	* TCP Connection Time-out : milli-second
	* </pre>
	*/
	public static final	int		time_out	= 60000;
	/**<pre>
	* Character InputStream buffer size
	* </pre>
	*/
	public static final int		buf_size	= 1024;
	/**<pre>
	* length type is full-length(header-length+body-length)
	* </pre>
	*/
	public static final int _LENGTH_TYPE_FULLSIZE	= 1;
	/**<pre>
	* length type is body-length only
	* </pre>
	*/
	public static final int _LENGTH_TYPE_BODYONLY	= 2;
	/**
	* JTcpManager instance 
	*/
	private static JTcpManager instance	= new JTcpManager();
	
	/**
	* Default Constructor - do nothing any more.
	*/
	private JTcpManager(){
		
	}
	/**
	* return Singlton pattern instance.<p>
	*/
	public static JTcpManager getInstance( ){
		/*if(instance == null){
			synchronized (JTcpManager.class){
				instance = new JTcpManager( );
			}
		}*/
		return instance;
	}
	/**<pre>
	* Tcp Message Sending.
	*</pre>
	*@param String ipAddress peer's ip address
	*@param int port peer's port
	*@param String message sending message
	*@return String receiving message
	*@throws Exception
	*/
	public String sendMessage(String ipAddress,int port, String message) throws Exception{
		return sendMessage(ipAddress, port, message, time_out);
	}
	
	public String sendMessage(String ipAddress,int port, String message, int timeout) throws Exception{
		//receive message buffer
		StringBuffer sb		= new StringBuffer();
		
		PrintWriter		pw			= null;
		BufferedReader	br			= null;
		Socket			client		= null;
		char[]			buf			= new char[buf_size];
		try{
			client			= new Socket( ipAddress , port );
			client.setSoTimeout(timeout);
			
			pw				= new PrintWriter( client.getOutputStream(), true );
			br				= new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			/******************************************************************************************
			* message sending - Do not append CR or LF~!!! It can occur telegram problem by peer.
			******************************************************************************************/
			pw.print(message);
			pw.flush();
			//receiving size
			int size	= 0;
			//receive message
			while( (size = br.read(buf,0,buf_size ) )!=-1){
				sb.append(buf,0,size);
			}

		}catch(java.net.ConnectException ce){
			throw new Exception("Connection Fail : "+ce.getMessage(),ce );
		}catch(Exception ex){
			throw new Exception(ex);
		}finally{
			try{br.close();}catch(Exception e){br=null;}
			try{pw.close();}catch(Exception e){pw=null;}
			try{client.close();}catch(Exception e){client=null;}
		}
		return sb.toString();
	}
	
	/**<pre>
	* Tcp Message Sending.
	*</pre>
	*@param String ipAddress peer's ip address
	*@param int port peer's port
	*@param String message sending message
	*@return String receiving message
	*@throws Exception
	*/
	public byte[] sendMessage(String ipAddress,int port, byte[] message,boolean getBytes) throws Exception{
		//receive message buffer
		OutputStream	os			= null;
		Socket			client		= null;
		JOutputStream jos			= null;
		byte[] result	= null;
		try{
			client			= new Socket( ipAddress , port );
			client.setSoTimeout(time_out);
			
			os	= client.getOutputStream();
			/******************************************************************************************
			* message sending - Do not append CR or LF~!!! It can occur telegram problem by peer.
			******************************************************************************************/
			os.write(message);
			os.flush();
			jos	= new JOutputStream();
			jos.write(client.getInputStream());
			
			result	= jos.getBytes();
		}catch(java.net.ConnectException ce){
			throw new Exception("Connection Fail : "+ce.getMessage(),ce );
		}catch(Exception ex){
			throw new Exception(ex);
		}finally{
			try{jos.close();}catch(Exception e){jos=null;}
			try{os.close();}catch(Exception e){os=null;}
			try{client.close();}catch(Exception e){client=null;}
		}
		return result;
	}
	/**<pre>
	* Tcp Message Sending.
	*</pre>
	*@param String ipAddress peer's ip address
	*@param int port peer's port
	*@param String message sending message
	*@return String receiving message
	*@throws Exception
	*/
	public byte[] sendMessage2(String ipAddress,int port, byte[] message,boolean getBytes,String endString) throws Exception{
		//receive message buffer
		OutputStream	os			= null;
		Socket			client		= null;
		byte[] result	= null;
		try{
			client			= new Socket( ipAddress , port );
			client.setSoTimeout(time_out);
			
			os	= client.getOutputStream();
			/******************************************************************************************
			* message sending - Do not append CR or LF~!!! It can occur telegram problem by peer.
			******************************************************************************************/
			os.write(message);
			os.flush();
			
			ByteBuffer bb	= ByteBuffer.allocate(1024);
			byte[] bytes	= new byte[1024];
			int size		= -1;
			StringBuffer sb	= new StringBuffer();
			while( (size=client.getInputStream().read(bytes))>0 ){
				bb.put(bytes, 0, size);
				result	= new byte[bb.position()];
				System.arraycopy(bb.array(), 0, result, 0, result.length);
				sb.setLength(0);
				sb.append(new String(result));
				if(sb.indexOf(endString)>0)
					break;
			}
		}catch(java.net.ConnectException ce){
			throw new Exception("Connection Fail : "+ce.getMessage(),ce );
		}catch(Exception ex){
			throw new Exception(ex);
		}finally{
			try{os.close();}catch(Exception e){os=null;}
			try{client.close();}catch(Exception e){client=null;}
		}
		return result;
	}
	
	
	
	/**<pre>
	* Tcp Message Sending when peer is anylink tcp gateway. 
	* There is not EOF signal. So read stream fully by byte-length.
	* step : 	
	* 			1. read header stream
	* 			2. abstract byte-length area in header
	* 			3. read fully body stream
	* condition : 
	* 			1. header byte length
	* 			2. start position of byte-length area in header
	* 			3. size of byte-length area in header
	* 			4. byte-length type (full-length or body-length only)
	*</pre>
	*@param String ipAddress peer's ip address
	*@param int port peer's port
	*@param String message sending message
	*@param int headerLength
	*@param int startPosofLengthArea
	*@param int lengthAreaSize
	*@return String receiving message
	*@throws Exception
	*/
	public String sendMessageWithoutEOF(String ipAddress,int port, String sHeader ,String sBody
			,int headerLength,int startPosOfLengthArea,int lengthAreaSize,int lengthType) throws Exception{
		byte[] rHeader				= null;//new byte[headerLength];
		byte[] rBody				= null;
		
		PrintWriter		pw			= null;
		InputStream		is			= null;
		Socket			client		= null;
		
		try{
			client			= new Socket( ipAddress , port );
			client.setSoTimeout(time_out);
			
			pw				= new PrintWriter( client.getOutputStream(), true );
			is				= client.getInputStream();
			/******************************************************************************************
			* message sending - Do not append CR or LF~!!! It can occur telegram problem by peer.
			******************************************************************************************/
			pw.print(sHeader);
			pw.print(sBody);
			pw.flush();

			/******************************************************************************************
			* message receving - read stream fully.
			******************************************************************************************/
			rHeader				= new byte[headerLength];
			//receiving size
			int size			= 0;
			//read header
			size				= is.read(rHeader,0,rHeader.length);
		    if(size < 0){
		    	throw new EOFException("Could not read header stream.");
		    }
		    
			byte[] streanLength	= new byte[lengthAreaSize];
			
			System.arraycopy(rHeader, startPosOfLengthArea, streanLength, 0, lengthAreaSize) ;
			
			int lengthAreaVal	= Integer.parseInt(new String(streanLength));
			
			if(lengthType==_LENGTH_TYPE_FULLSIZE){
				// body length = full length - header length
				lengthAreaVal	= lengthAreaVal - headerLength;
			}else if(lengthType==_LENGTH_TYPE_BODYONLY){
				//do nothing
			}else{
				throw new Exception("Unknown length-type ["+lengthType+"].");
			}
			
			rBody				= new byte[lengthAreaVal];
			//read body
			size				= is.read(rBody,0,rBody.length);
		    if(size < 0){
		    	throw new EOFException("Could not read body stream.");
		    }
			
		}catch(java.net.ConnectException ce){
			throw new Exception("Connection Fail : "+ce.getMessage(),ce );
		}catch(Exception ex){
			throw new Exception(ex);
		}finally{
			try{is.close();}catch(Exception e){is=null;}
			try{pw.close();}catch(Exception e){pw=null;}
			try{client.close();}catch(Exception e){client=null;}
		}
		return new String(rHeader).concat(new String(rBody));
	}	
}
