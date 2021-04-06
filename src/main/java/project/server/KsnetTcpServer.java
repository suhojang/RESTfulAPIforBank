package project.server;

import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.math.Calculator;
import com.kwic.support.CryptoKeyGenerator;
import com.kwic.util.StringUtil;
import com.kwic.web.init.Handler;
import com.kwic.web.init.InitServiceImpl;

public class KsnetTcpServer extends InitServiceImpl {
	private static Logger logger	= LoggerFactory.getLogger(KsnetTcpServer.class);
	
	private static ServerSocket serverSocket;
	private Constructor<?> handlerConstructor;
	
	private static Map<String, Socket> clients = new Hashtable<String, Socket>();
	private static boolean stop;
	
	/**
	 * 핸들러 식별자(ID) 생성시 사용하는 최종 시퀀스
	 */
	private static int sequence;

	@Override
	public void execute() throws Exception {
		Class<?> handlerClass 	= Class.forName(getParamString("handler-class"));
		handlerConstructor 		= handlerClass.getConstructor();
		
		Handler handler = null;
		Socket socket 	= null;
		String clientId = sequence();

		int port 	= getParamInt("port");
		int timeout = (int) Calculator.calculate(getParamString("timeout"));
		
		Map<String,Object> initParam 	= super.getInitialParams();
		try {
			serverSocket	= new ServerSocket(port);
			while (!stop && serverSocket != null && !serverSocket.isClosed()) {
				socket	= serverSocket.accept();
				socket.setSoTimeout(timeout);
				
				handler = (Handler) handlerConstructor.newInstance();
				handler.put("client-id",      clientId);    //핸들러의 식별자
				handler.put("client-socket",  socket);      //핸들러가 요청 및 응답을 위하 사용할  접속 통로인 소켓
				handler.put("service-params", initParam);	//핸들러의 설정값 파라미터				
				handler.handle();                           //핸들러 작동 시작
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			logger.debug(String.format("Listener service execution is closed. service name=%s, clientID=%s, port=%d", super.getServiceName(), clientId, port));
		}
		
	}
	
	@Override
	public void terminate() {
		String id 	= null;
		stop 		= true;
		Iterator<String> iter = clients.keySet().iterator();
		
		while (iter.hasNext()) {
			try {
				id = iter.next();
				clients.get(id).close();  //핸들러의 소켓을 차단한다.
				clients.remove(id);       //핸들러를 커넥션풀에서 제거한다.
			} catch (Exception e) {
				logger.error(String.format("Service termination is failed. service ID=%s", id), e);
			}
		}
		
		try {
			serverSocket.close();
			serverSocket = null;
		} catch (Exception e) {
			logger.error("Server socket closing is failed.", e);
		}
	}
	
	public static void addClientSocket(String id, Socket socket) {
		clients.put(id, socket);
	}
	
	public static void removeClientSocket(String id) {
		
		Socket socket =  clients.get(id);
		if(socket == null){
			return;
		}
		
		try {
			if(!socket.isClosed() && socket.getOutputStream() != null){
				socket.getOutputStream().close();
			}
		} catch (Exception e) {
			logger.error(String.format("Closing of output stream is failed. client ID=%s", id), e);
		}
		
		try {
			if(!socket.isClosed() && socket.getInputStream() != null){
				socket.getInputStream().close();
			}
		} catch (Exception e) {
			logger.error(String.format("Closing of input stream is failed. client ID=%s", id), e);
		}
		
		try {			
			clients.remove(id);
			
		} catch (Exception e) {
			logger.error(String.format("Disconnection is failed. client ID=%s", id), e);
		} finally {
			try {
				if (socket != null){
					socket.close();
					socket = null;
				}
			} catch (Exception e) {
				logger.error(String.format("Socket closing is failed. client ID=%s", id), e);
			}
		}
	}
	
	public synchronized static String sequence() {
		int MAX_VALUE = 99999999;
		if (sequence >= MAX_VALUE){
			sequence = 0;
		}
		String key = CryptoKeyGenerator.getRandomKey(
											CryptoKeyGenerator.ALGORITHM_SEED128
											,new int[] { CryptoKeyGenerator.KEY_TYPE_NUM
													   , CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL,
						                                 CryptoKeyGenerator.KEY_TYPE_ENG_SMALL });

		return key + "-" + StringUtil.addChar(String.valueOf(sequence++), String.valueOf(MAX_VALUE).length(), "0", true);
	}
	
	public static int currentRequestCount() {
		return clients.size();
	}
}
