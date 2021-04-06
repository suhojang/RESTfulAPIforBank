package com.kwic.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.io.JOutputStream;
import com.kwic.io.JXL;
import com.kwic.io.ZipArchive;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.security.aes.AES;
import com.kwic.security.aes.AESCipher;
import com.kwic.security.aes.JSecurity;
import com.kwic.security.seed.old.util.SecurityUtil;
import com.kwic.support.CryptoKeyGenerator;
import com.kwic.telegram.web.JHttpClient;
import com.kwic.util.StringUtil;
import com.kwic.util.pdf.Html2Pdf;
import com.kwic.web.servlet.RequestTokenInterceptor;

import egovframework.rte.fdl.property.EgovPropertyService;

public class Controllers {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final int ROW_PER_PAGE	= 20;
	
	public static final String REQUEST_ERROR_MSG	= "REQUEST_ERROR_MSG";
	public static final String RT_CD	= "TRXRESPCODE";
	public static final String RT_MSG	= "TRXRESPDESC";
	public static final String BNK_RT_CD	= "BANKRESPCODE";
	public static final String BNK_RT_MSG	= "BANKRESPDESC";
    public static String SESSION_KEY  = "USER_SESSIONS"; 
	
	public static final Map<String,String> CONTENT_TYPE_IMAGE	= new HashMap<String,String>();
	static{
		CONTENT_TYPE_IMAGE.put("3ds","image/x-3ds");
		CONTENT_TYPE_IMAGE.put("art","image/x-jg");
		CONTENT_TYPE_IMAGE.put("bmp","image/bmp");
		CONTENT_TYPE_IMAGE.put("btif","image/prs.btif");
		CONTENT_TYPE_IMAGE.put("cgm","image/cgm");
		CONTENT_TYPE_IMAGE.put("cmx","image/x-cmx");
		CONTENT_TYPE_IMAGE.put("dib","image/bmp");
		CONTENT_TYPE_IMAGE.put("djv","image/vnd.djvu");
		CONTENT_TYPE_IMAGE.put("djvu","image/vnd.djvu");
		CONTENT_TYPE_IMAGE.put("dmg","application/x-apple-diskimage");
		CONTENT_TYPE_IMAGE.put("dwg","image/vnd.dwg");
		CONTENT_TYPE_IMAGE.put("dxf","image/vnd.dxf");
		CONTENT_TYPE_IMAGE.put("fbs","image/vnd.fastbidsheet");
		CONTENT_TYPE_IMAGE.put("fh","image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fh4","image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fh5","image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fh7","image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fhc","image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fpx","image/vnd.fpx");
		CONTENT_TYPE_IMAGE.put("fst","image/vnd.fst");
		CONTENT_TYPE_IMAGE.put("g3","image/g3fax");
		CONTENT_TYPE_IMAGE.put("gif","image/gif");
		CONTENT_TYPE_IMAGE.put("ico","image/x-icon");
		CONTENT_TYPE_IMAGE.put("ief","image/ief");
		CONTENT_TYPE_IMAGE.put("iso","application/x-iso9660-image");
		CONTENT_TYPE_IMAGE.put("jpe","image/jpeg");
		CONTENT_TYPE_IMAGE.put("jpeg","image/jpeg");
		CONTENT_TYPE_IMAGE.put("jpg","image/jpeg");
		CONTENT_TYPE_IMAGE.put("ktx","image/ktx");
		CONTENT_TYPE_IMAGE.put("mac","image/x-macpaint");
		CONTENT_TYPE_IMAGE.put("mdi","image/vnd.ms-modi");
		CONTENT_TYPE_IMAGE.put("mmr","image/vnd.fujixerox.edmics-mmr");
		CONTENT_TYPE_IMAGE.put("npx","image/vnd.net-fpx");
		CONTENT_TYPE_IMAGE.put("odi","application/vnd.oasis.opendocument.image");
		CONTENT_TYPE_IMAGE.put("oti","application/vnd.oasis.opendocument.image-template");
		CONTENT_TYPE_IMAGE.put("pbm","image/x-portable-bitmap");
		CONTENT_TYPE_IMAGE.put("pct","image/pict");
		CONTENT_TYPE_IMAGE.put("pcx","image/x-pcx");
		CONTENT_TYPE_IMAGE.put("pgm","image/x-portable-graymap");
		CONTENT_TYPE_IMAGE.put("pic","image/pict");
		CONTENT_TYPE_IMAGE.put("pict","image/pict");
		CONTENT_TYPE_IMAGE.put("png","image/png");
		CONTENT_TYPE_IMAGE.put("pnm","image/x-portable-anymap");
		CONTENT_TYPE_IMAGE.put("pnt","image/x-macpaint");
		CONTENT_TYPE_IMAGE.put("ppm","image/x-portable-pixmap");
		CONTENT_TYPE_IMAGE.put("psd","image/vnd.adobe.photoshop");
		CONTENT_TYPE_IMAGE.put("qti","image/x-quicktime");
		CONTENT_TYPE_IMAGE.put("qtif","image/x-quicktime");
		CONTENT_TYPE_IMAGE.put("ras","image/x-cmu-raster");
		CONTENT_TYPE_IMAGE.put("rgb","image/x-rgb");
		CONTENT_TYPE_IMAGE.put("rlc","image/vnd.fujixerox.edmics-rlc");
		CONTENT_TYPE_IMAGE.put("sgi","image/sgi");
		CONTENT_TYPE_IMAGE.put("sid","image/x-mrsid-image");
		CONTENT_TYPE_IMAGE.put("svg","image/svg+xml");
		CONTENT_TYPE_IMAGE.put("svgz","image/svg+xml");
		CONTENT_TYPE_IMAGE.put("t3","application/x-t3vm-image");
		CONTENT_TYPE_IMAGE.put("tga","image/x-tga");
		CONTENT_TYPE_IMAGE.put("tif","image/tiff");
		CONTENT_TYPE_IMAGE.put("tiff","image/tiff");
		CONTENT_TYPE_IMAGE.put("uvg","image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("uvi","image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("uvvg","image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("uvvi","image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("wbmp","image/vnd.wap.wbmp");
		CONTENT_TYPE_IMAGE.put("wdp","image/vnd.ms-photo");
		CONTENT_TYPE_IMAGE.put("webp","image/webp");
		CONTENT_TYPE_IMAGE.put("xbm","image/x-xbitmap");
		CONTENT_TYPE_IMAGE.put("xif","image/vnd.xiff");
		CONTENT_TYPE_IMAGE.put("xpm","image/x-xpixmap");
		CONTENT_TYPE_IMAGE.put("xwd","image/x-xwindowdump");
		CONTENT_TYPE_IMAGE.put("pdf","application/pdf");
	}

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
    /**
	 * ServletOutputStream 으로 이미지를 직접 write 하는 방식을 취한다.
	 * 이미지의 경로가 보안사항일 경우 사용한다.
	 * 
	 * @param imagePathP 다운로드할 이미지경로
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void imageView( String imagePathP , HttpServletResponse response ) throws Exception{
    	String imagePath	= StringUtils.replace(imagePathP, "\\", File.separator);
    	imagePath			= StringUtils.replace(imagePath, "/", File.separator);
    	
    	String extType		= "$%^&*()!@#$";
    	if(imagePath.lastIndexOf(".")>=0){
    		extType	= imagePath.substring(imagePath.lastIndexOf(".")+1);
    	}

    	String contentType	= "";
    	response.setStatus(HttpServletResponse.SC_OK);
    	contentType	= CONTENT_TYPE_IMAGE.get(extType.toLowerCase());
    	if(contentType==null)
    		throw new Exception("The file is not an image.");
    	
    	File imgFile	= new File(imagePath);
		if(!imgFile.exists() || imgFile.isDirectory())
			throw new Exception("["+imagePath+"] 파일을 찾을 수 없습니다.");

    	imageView(new FileInputStream(imgFile),response,contentType);
    }
    
    /**
	 * ServletOutputStream 으로 이미지를 직접 write 하는 방식을 취한다.
	 * 이미지의 경로가 보안사항일 경우 사용한다.
	 * 
	 * @param is 다운로드할 이미지 stream
	 * @param response 
	 * @param imageContentType 이미지 타입
	 * @exception IOException
	 */
    public void imageView( InputStream is , HttpServletResponse response, String imageContentType ) throws Exception{
    	response.setContentType(imageContentType);
    	write(is , response.getOutputStream());
	}
    public void imageView( byte[] bytes , HttpServletResponse response, String imageContentType ) throws Exception{
    	response.setContentType(imageContentType);
    	response.setContentLength(bytes.length);
    	write(bytes , response.getOutputStream());
	}
    public String getImageContentType(String fileName) throws Exception{
    	String extType	= null;
    	String contentType	= "";
    	if(fileName.lastIndexOf(".")>=0)
    		extType	= fileName.substring(fileName.lastIndexOf(".")+1);
    	else
    		extType	= fileName;
    	contentType	= CONTENT_TYPE_IMAGE.get(extType.toLowerCase());
    	if(contentType==null)
    		throw new Exception("The file is not an image.");
    	return contentType;
    }

    /**
	 * 파일다운로드를 담당한다.
	 * @param filePath 다운로드할 파일경로
	 * @param fileName 저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void fileDown( String filePath , String fileName, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        String browser = getBrowser(request);
        String encodedFilename = "";
        
        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        }
        response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename="+encodedFilename);
    	
		File fFile	= new File(filePath);
		if(!fFile.exists() || fFile.isDirectory()){
			return;
		}
		write(new FileInputStream(fFile),response.getOutputStream());
	}
    /**
	 * 압축 파일다운로드를 담당한다.
	 * @param filePath 다운로드할 파일경로
	 * @param fileName 저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void zipDown( byte[][] bytes, String[] names,long[] lastModifieds, String fileName, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        String browser = getBrowser(request);
        String encodedFilename = "";
        
        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        }
        response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename="+encodedFilename);
        
        ZipArchive.write(bytes,names,lastModifieds,response.getOutputStream(), ZipArchive._COMPRESS_LEVEL_DEFAULT,"UTF-8");
//		ZipStreamService.write(bytes,names,lastModifieds,response.getOutputStream(), ZipStreamService._COMPRESS_LEVEL_DEFAULT);
	}
   

    /**
	 * 파일다운로드를 담당한다.
	 * @param bytes 다운로드할 파일 bytes
	 * @param fileName 저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void fileDown( byte[] bytes,String fileName, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        String browser = getBrowser(request);
        String encodedFilename = "";
        
        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        }
        response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/octet-stream");
    	response.setHeader("Content-Length", String.valueOf(bytes.length));
        response.setHeader("Content-Disposition", "attachment; filename="+encodedFilename);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    	
		write(bytes,response.getOutputStream());
	}
    /**
	 * 파일다운로드를 담당한다.
	 * @param bytes 다운로드할 파일 bytes
	 * @param fileName 저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void imageFileDown( byte[] bytes,String fileName, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        String browser = getBrowser(request);
        String encodedFilename = "";
        
        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        }
        response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", getImageContentType(fileName));
    	response.setHeader("Content-Length", String.valueOf(bytes.length));
        response.setHeader("Content-Disposition", "attachment; filename="+encodedFilename);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    	
		write(bytes,response.getOutputStream());
	}
    /**
	 * 엑셀 다운로드를 담당한다.
	 * @param jxl 다운로드할 엑셀 객체
	 * @param fileName 저장될 파일명
	 * @param encoding 인코딩
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void excelDown(HttpServletResponse response,JXL jxl,String fileName) throws Exception{
    	excelDown(response,jxl,fileName,"UTF-8");
    }
    public void excelDown(HttpServletResponse response,JXL jxl,String fileName,String encoding) throws Exception{
		jxl.write();jxl.close();
		byte[] bytes	= jxl.getBytes();
		response.setHeader("Content-Disposition","attachment;filename="+URLEncoder.encode(fileName.toLowerCase().indexOf(".xls")<0?fileName+".xls":fileName,encoding==null?"UTF-8":encoding)+";");
		response.setContentType("application/octet-stream;charset="+encoding);
		response.setContentLength(bytes.length);
		
		write(bytes,response.getOutputStream());
    }
    /**
	 * html 엑셀 다운로드를 담당한다.
	 * @param html 다운로드할 HTML 엑셀 문자열
	 * @param fileName 저장될 파일명
	 * @param encoding 인코딩
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
    public void htmlExcelDown(HttpServletResponse response,String html,String fileName) throws Exception{
    	htmlExcelDown(response,html,fileName,"UTF-8");
    }
    public void htmlExcelDown(HttpServletResponse response,String html,String fileName,String encoding) throws Exception{
		response.setHeader("Content-Disposition",StringUtil.removeLine("attachment;filename="+URLEncoder.encode(fileName.toLowerCase().indexOf(".xls")<0?fileName+".xls":fileName,encoding==null?"UTF-8":encoding)+";"));
		response.setContentType("application/octet-stream;charset="+encoding);
		response.setContentLength(html.getBytes().length);
		write(html.getBytes(),response.getOutputStream());
    }

    /**
	 * OutputStream 으로 write.
	 * 
	 * @param is 입력 stream
	 * @param os 출력 stream
	 * @exception IOException
	 */
    public static void write( InputStream is , OutputStream os ) throws IOException{
    	if(is==null || os==null){
    		return;
    	}
    	byte[] buf	= new byte[1024];
    	int iReadSize			= 0;
    	try{
    		while( (iReadSize=is.read(buf))!=-1 ){
    			os.write(buf,0,iReadSize);
    		}
    		os.flush();
    	}catch(IOException ie){
    		throw ie;
    	}finally{
    		try{if(is!=null){is.close();}}catch(IOException ie){}
    		try{if(os!=null){os.close();}}catch(IOException ie){}
    	}
    }    
    /**
 	 * OutputStream 으로 write.
 	 * 
 	 * @param is 입력 stream
 	 * @param os 출력 stream
 	 * @exception IOException
 	 */
     public static void write( byte[] bytes , OutputStream os ) throws IOException{
     	if(bytes==null || os==null)
     		return;
     	try{
 			os.write(bytes);
     		os.flush();
     	}catch(IOException ie){
     		throw ie;
     	}finally{
     		try{if(os!=null){os.close();}}catch(IOException ie){}
     	}
     }    
    /**
     * 브라우저 구분 얻기.
     * @param request
     * @return
     */
    private static String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        if(header==null){
            return "MSIE";
        }else if (header.indexOf("MSIE") > -1) {
            return "MSIE";
        } else if (header.indexOf("Chrome") > -1) {
            return "Chrome";
        } else if (header.indexOf("Opera") > -1) {
            return "Opera";
        }
        return "MSIE";
    }
    /**
     * 모바일 여부 확인
     * @param request
     * */
    public static boolean isMobile(HttpServletRequest request) {
    	String userAgent = request.getHeader("user-agent");
    	boolean mobile1 = userAgent.matches(".*(iPhone|iPod|Android|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|Opera Mini|Opera Mobi|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson).*");
    	boolean mobile2 = userAgent.matches(".*(LG|SAMSUNG|Samsung).*");
    	if(mobile1 || mobile2) {
    		return true;
    	}
    	return false;
    }
	
	public static final String RESULT_CD		= "RESULT_CD";
	public static final String RESULT_ERCD		= "RESULT_ERCD";
	public static final String RESULT_MSG		= "RESULT_MSG";
	public static final String PARAMETERS		= "PARAMETERS";
	public static final String EXCEPTION		= "exception";
	public static final String REDIRECT_URI		= "REDIRECT_URI";
	public static final String REDIRECT_TYPE	= "REDIRECT_TYPE";
	
	public static final String RESULT_ERCD_NOSESSION	= "NO_SESSION";
	public static final String RESULT_ERCD_NOTOCKEN	= "NO_TOCKEN";
	public static final String RESULT_ERCD_NOAUTH	= "NO_AUTH";
	public static final String RESULT_ERCD_IVALIDPARAM	= "INVALID_PARAM";
	
	public void error(Model model,Exception e){
		logger.error(e.getMessage(), e);
		if(e instanceof DefinedException)
			model.addAttribute(REQUEST_ERROR_MSG, e.getMessage());
		else
			model.addAttribute(REQUEST_ERROR_MSG, "오류가 발생하였습니다.");
	}
	public void writeError(HttpServletResponse response,Exception e) throws Exception{
		logger.error(e.getMessage(), e);
		String msg	= "오류가 발생하였습니다.";
		if(e instanceof DefinedException)
			msg	= e.getMessage();
		
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "text/html; charset=UTF-8");
    	
		response.getWriter().append(msg);
		response.getWriter().close();
	}
	public void writeMobileError(HttpServletResponse response,String error,String url) throws Exception{
		StringBuffer sb	= new StringBuffer();
		sb.append("")
		.append("<!doctype html>\n")
		.append("<html lang=\"kr\">\n")
		.append("<head>\n")
		.append("	<meta charset=\"UTF-8\">\n")
		.append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n")
		.append("	<meta http-equiv=\"X-UA-Compatible\" content=\"IE=10,chrome=1\">\n")
		.append("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\" />\n")
		.append("	<title>::출금동의 증빙시스템::</title>\n")
		.append("	<link href=\"/mobile/css/main.css\" rel=\"stylesheet\" type=\"text/css\">\n")
		.append("	<script type=\"text/javascript\" src=\"/mobile/js/jquery-1.11.2.min.js\"></script>\n")
		.append("	<script type=\"text/javascript\" src=\"/mobile/js/jquery-ui.min.js\"></script>\n")
		.append("	<script type=\"text/javascript\" src=\"/mobile/js/jquery.animateSprite.min.js\"></script>\n")
		.append("	<script type=\"text/javascript\" src=\"/mobile/js/common.js\"></script>\n")
		.append("	<script type=\"text/javascript\" src=\"/mobile/js/web.js\"></script>\n")
		.append("\n")
		.append("	<script type=\"text/javascript\">\n")
		.append("	function fn_init(){\n")
		.append("		malert('"+StringUtil.replace(error,"\n","<br/>")+"',function(){\n")
		;
		if(url!=null && !"".equals(url))
			sb.append("			$(location).attr('href','"+url+"');\n");
		sb.append("		});\n")
		.append("	}\n")
		.append("	$(document).ready(function(){fn_init();});\n")
		.append("	</script>\n")
		.append("</head>\n")
		.append("<body>\n")
		.append("</body>\n")
		.append("</html>\n")
		;
		
		response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "text/html; charset=UTF-8");
    	
		response.getWriter().append(sb.toString());
		response.getWriter().close();
	}
	/**
	 * response for ajax (jsonp)
	 * 
	 * */
	public void ajaxResponse(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String,Object> result	= new HashMap<String,Object>();
		result.put(RequestTokenInterceptor.ATTRIBUTE_NAME, request.getSession().getAttribute(RequestTokenInterceptor.ATTRIBUTE_NAME));
		result.put(JSecurity.CRYPT_KEY_ID, request.getSession().getAttribute(JSecurity.CRYPT_KEY_ID)==null?'N':'Y');
		result.put(RESULT_CD, "Y");
//		result.put(RESULT_MSG, "정상처리되었습니다.");
		result.put(RESULT_MSG, "");
		ajaxResponse(response, result,checkParameter(request,"callback"),"UTF-8");
	}
	public void ajaxResponse(HttpServletRequest request,HttpServletResponse response,Exception e) throws Exception{
		logger.error(e.getMessage(), e);
		Map<String,Object> result	= new HashMap<String,Object>();
		result.put(RequestTokenInterceptor.ATTRIBUTE_NAME, request.getSession().getAttribute(RequestTokenInterceptor.ATTRIBUTE_NAME));
		result.put(JSecurity.CRYPT_KEY_ID, request.getSession().getAttribute(JSecurity.CRYPT_KEY_ID)==null?'N':'Y');
		result.put(RESULT_CD, "N");
		if(e instanceof DefinedException)
			result.put(RESULT_MSG, e.getMessage());
		else
			result.put(RESULT_MSG, "오류가 발생하였습니다.");
		request.setAttribute("ERROR_OBJECT", e);
		ajaxResponse(response, result,checkParameter(request,"callback"),"UTF-8");
	}
	
	public void ajaxResponse(HttpServletRequest request,HttpServletResponse response, Map<String,Object> obj) throws Exception{
		obj.put(RequestTokenInterceptor.ATTRIBUTE_NAME, request.getSession().getAttribute(RequestTokenInterceptor.ATTRIBUTE_NAME));
		obj.put(JSecurity.CRYPT_KEY_ID, request.getSession().getAttribute(JSecurity.CRYPT_KEY_ID)==null?'N':'Y');
		ajaxResponse(response, obj,checkParameter(request,"callback"),"UTF-8");
	}
	public void ajaxResponse(HttpServletResponse response, Map<String,Object> obj,String callback) throws Exception{
		ajaxResponse(response, obj,callback,"UTF-8");
	}
	public void ajaxResponse(HttpServletResponse response, Map<String,Object> obj,String callback,String encoding) throws Exception{
		if(obj.get(RESULT_CD)==null){
			obj.put(RESULT_CD,"Y");
			obj.put(RESULT_MSG, "");
		}
		
    	String jsonString = new ObjectMapper().writeValueAsString(obj);
    	
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/json; charset="+encoding);
    	
		response.getWriter().append(callback==null||"".equals(callback)?"":callback).append("(").append(jsonString).append(")");
		response.getWriter().close();
	}
	public void jsonResponseNoMessage(HttpServletRequest request,HttpServletResponse response, Map<String,Object> obj) throws Exception{
		jsonResponseNoMessage(response, obj,checkParameter(request,"callback"),"UTF-8");
	}
	public void jsonResponseNoMessage(HttpServletResponse response, Map<String,Object> obj,String callback) throws Exception{
		jsonResponseNoMessage(response, obj,callback,"UTF-8");
	}
	public void jsonResponseNoMessage(HttpServletResponse response, Map<String,Object> obj,String callback,String encoding) throws Exception{
    	String jsonString = new ObjectMapper().writeValueAsString(obj);
    	
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/json; charset="+encoding);
    	
		response.getWriter().append(jsonString);
		response.getWriter().close();
	}
	
	/**
	 * response for ajax (jsonp)
	 * 
	 * */
	public void jsonResponse(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String,Object> result	= new HashMap<String,Object>();
		result.put(RESULT_CD, "Y");
//		result.put(RESULT_MSG, "정상처리되었습니다.");
		result.put(RESULT_MSG, "");
		jsonResponse(response, result,"UTF-8");
	}
	public void jsonResponse(HttpServletRequest request,HttpServletResponse response,Exception e) throws Exception{
		logger.error(e.getMessage(), e);
		Map<String,Object> result	= new HashMap<String,Object>();
		result.put(RESULT_CD, "N");
		if(e instanceof DefinedException)
			result.put(RESULT_MSG, e.getMessage());
		else
			result.put(RESULT_MSG, "오류가 발생하였습니다.");
		request.setAttribute("ERROR_OBJECT", e);
		jsonResponse(response, result,"UTF-8");
	}
	/**
	 * response for json
	 * */
	public void jsonResponse(HttpServletRequest request,HttpServletResponse response, Map<String,Object> obj) throws Exception{
		jsonResponse(response, obj,"UTF-8");
	}
	public void jsonResponse(HttpServletResponse response, Map<String,Object> obj,String encoding) throws Exception{
		if(obj.get(RESULT_CD)==null){
			obj.put(RESULT_CD,"Y");
//			obj.put(RESULT_MSG, "정상처리되었습니다.");
			obj.put(RESULT_MSG, "");
		}
		
    	String jsonString = new ObjectMapper().writeValueAsString(obj);
    	
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/json; charset="+encoding);
    	
		response.getWriter().append(jsonString);
		response.getWriter().close();
	} 
	
	public void response(HttpServletResponse response, Map<String,Object> obj) throws Exception {
		response(response, obj, "UTF-8");
	}
	
	public void response(HttpServletResponse response, Map<String,Object> obj, String encoding) throws Exception {
		if(obj.get(RT_CD)==null){
			obj.put(RT_CD,	ErrorCode._0000);
			obj.put(RT_MSG, "");
		}
		
    	String jsonString = new ObjectMapper().writeValueAsString(obj);
    	
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setHeader("Content-Type", "application/json; charset="+encoding);
    	
		response.getWriter().append(jsonString);
		response.getWriter().close();
	}
	
	/**
	 * response for pdf
	 * */
	public void html2PdfResponse(HttpServletRequest request,HttpServletResponse response, String html,String baseDomain,String fileName,String prtKn) throws Exception{
		String projectPath	= request.getSession().getServletContext().getRealPath("/");
		String[] fontPath	= new String[]{
				 projectPath+"common/fonts/MALGUN.TTF"
				 ,projectPath+"common/fonts/NanumGothic-Bold.ttf"
				};
		
		JOutputStream jos	= new JOutputStream();
		Html2Pdf.parse(jos, html,fontPath,baseDomain);
		if("D".equals(prtKn)){
			response.setContentType("application/pdf;charset=UTF-8");
			response.setContentLength(jos.getBytes().length);
			response.setHeader("Content-Disposition",StringUtil.removeLine("attachment;filename="+URLEncoder.encode(fileName,"UTF-8")+";"));
			response.setHeader("Pragma","dummy=bogus");
			response.setHeader("Cache-Control","private");
		}else{
			response.setContentType("application/pdf;charset=UTF-8");
			response.setContentLength(jos.getBytes().length);
			response.setHeader("Content-Disposition",StringUtil.removeLine("inline; filename="+URLEncoder.encode(fileName,"UTF-8")+";"));
		}
		response.getOutputStream().write(jos.getBytes());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
	/**
	 * response for pdf
	 * */
	public void html2PngResponse(HttpServletRequest request,HttpServletResponse response, String html,String baseDomain,String fileName,String prtKn) throws Exception{
		String projectPath	= request.getSession().getServletContext().getRealPath("/");
		String[] fontPath	= new String[]{
				 projectPath+"common/fonts/MALGUN.TTF"
				 ,projectPath+"common/fonts/NanumGothic-Bold.ttf"
				};
		
		JOutputStream jos	= new JOutputStream();
		Html2Pdf.parse(jos, html,fontPath,baseDomain);
		
		if("D".equals(prtKn)){
			response.setContentType("application/pdf;charset=UTF-8");
			response.setContentLength(jos.getBytes().length);
			response.setHeader("Content-Disposition","attachment;filename="+URLEncoder.encode(fileName,"UTF-8")+";");
			response.setHeader("Pragma","dummy=bogus");
			response.setHeader("Cache-Control","private");
		}else{
			response.setContentType("application/pdf;charset=UTF-8");
			response.setContentLength(jos.getBytes().length);
			response.setHeader("Content-Disposition","inline; filename="+URLEncoder.encode(fileName,"UTF-8")+";");
		}
		response.getOutputStream().write(jos.getBytes());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
	/**
	 * get pdf binary
	 * */
	public byte[] html2Pdf(HttpServletRequest request, String html,String baseDomain,String fileName,String prtKn) throws Exception{
		String projectPath	= request.getSession().getServletContext().getRealPath("/");
		String[] fontPath	= new String[]{
				 projectPath+"common/fonts/MALGUN.TTF"
				 ,projectPath+"common/fonts/NanumGothic-Bold.ttf"
				};
		
		JOutputStream jos	= new JOutputStream();
		Html2Pdf.parse(jos, html,fontPath,baseDomain);
		return jos.getBytes();
	}
	public String checkParameter(HttpServletRequest request, String paramId) throws Exception{
		return checkParameter(request.getParameter(paramId), paramId);
	}
	public String checkParameter(Map<String,Object> request, String paramId) throws Exception{
		return checkParameter(String.valueOf(request.get(paramId)==null?"":request.get(paramId)), paramId);
	}
	public String checkParameter(String param, String paramId) throws Exception{
		if(param==null)
			return "";
		
		String lowerParam	= param.toLowerCase(Locale.KOREA);
		String[] keywords	= new String[]{
				"<script","javascript","<iframe","<object","<applet","<embed","<form"
				,"select","insert","update","delete","merge","drop","create","declare"
				,"/*","*/","--"
		};
		for(int i=0;i<keywords.length;i++)
			if(lowerParam.indexOf(keywords[i])>=0)
				throw new DefinedException("입력값으로 ["+StringUtil.replace(StringUtil.replace(keywords[i],"<","&lt;"),">","&gt;")+"]를 사용할 수 없습니다.");
		
		return param;
	}
	public static String xssCheck(HttpServletRequest request, String paramId) throws Exception{
		return xssCheck(request.getParameter(paramId), paramId);
	}
	public static String xssCheck(String param, String paramId) throws Exception{
		if(param==null)
			return "";
		
		String lowerParam	= param.toLowerCase(Locale.KOREA);
		String[] keywords	= new String[]{
				"<script","javascript","<iframe","<object","<applet","<embed","<form"
		};
		for(int i=0;i<keywords.length;i++)
			if(lowerParam.indexOf(keywords[i])>=0)
				throw new DefinedException("입력값으로 ["+StringUtil.replace(StringUtil.replace(keywords[i],"<","&lt;"),">","&gt;")+"]를 사용할 수 없습니다.");
		return param;
	}
	
	public String getParam(HttpServletRequest request,String paramId) throws Exception{
		return getParam(request,paramId,-1);
	}
	public String getParam(Map<String,Object> request,String paramId) throws Exception{
		return getParam(request,paramId,-1);
	}
	public String getParam(HttpServletRequest request,String paramId,int maxByteSize) throws Exception{
		String param	= checkParameter(request,paramId);
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramId+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public String getParam(Map<String,Object> request,String paramId,int maxByteSize) throws Exception{
		String param	= checkParameter(request,paramId);
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramId+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public Map<String,String> getParamMap(HttpServletRequest request){
		Map<String,String> paramMap	= new HashMap<String,String>();
		Enumeration<?> e	= request.getParameterNames();
		String name	= null;
		while(e.hasMoreElements()){
			name	= (String)e.nextElement();
			paramMap.put(name, request.getParameter(name));
		}
		return paramMap;
	}
	public String getDecodeParam(HttpServletRequest request,String paramId) throws Exception{
		return new String(java.net.URLDecoder.decode(checkParameter(request,paramId),"UTF-8"));
	}
	public String getDecodeParam(Map<String,Object> request,String paramId) throws Exception{
		return new String(java.net.URLDecoder.decode(checkParameter(request,paramId),"UTF-8"));
	}
	public String getParam(HttpServletRequest request,String paramId,String paramName,boolean required) throws Exception{
		return getParam(request,paramId,paramName,required,-1);
	}
	public String getParam(Map<String,Object> request,String paramId,String paramName,boolean required) throws Exception{
		return getParam(request,paramId,paramName,required,-1);
	}
	public String getParam(HttpServletRequest request,String paramId,String paramName,boolean required,int maxByteSize) throws Exception{
		String param	= checkParameter(request,paramId);
		if(required && "".equals(param))
			throw new DefinedException(paramName+" 입력되지 않았습니다.");
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramName+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public String getParam(Map<String,Object> request,String paramId,String paramName,boolean required,int maxByteSize) throws Exception{
		String param	= checkParameter(request,paramId);
		if(required && "".equals(param))
			throw new DefinedException(paramName+" 입력되지 않았습니다.");
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramName+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public String getDecodeParam(HttpServletRequest request,String paramId,String paramName,boolean required) throws Exception{
		return getDecodeParam(request,paramId,paramName,required,-1);
	}
	public String getDecodeParam(Map<String,Object> request,String paramId,String paramName,boolean required) throws Exception{
		return getDecodeParam(request,paramId,paramName,required,-1);
	}
	public String getDecodeParam(HttpServletRequest request,String paramId,String paramName,boolean required,int maxByteSize) throws Exception{
		String param	= new String(java.net.URLDecoder.decode(checkParameter(request,paramId),"UTF-8"));
		if(required && "".equals(param))
			throw new DefinedException(paramName+" 입력되지 않았습니다.");
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramName+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public String getDecodeParam(Map<String,Object> request,String paramId,String paramName,boolean required,int maxByteSize) throws Exception{
		String param	= new String(java.net.URLDecoder.decode(checkParameter(request,paramId),"UTF-8"));
		if(required && "".equals(param))
			throw new DefinedException(paramName+" 입력되지 않았습니다.");
		if(maxByteSize>0 && param.getBytes().length>maxByteSize)
			throw new DefinedException(paramName+" 제한 크기를 초과하였습니다.");
		return param;
	}
	public String[] getPagingValue(HttpServletRequest request) throws Exception{
		int PAGE_NO			= Integer.parseInt(getParam(request,"PAGE_NO","페이지번호가",true));
		int ROW_PER_PAGE	= Integer.parseInt(getParam(request,"ROW_PER_PAGE","페이지당 표시 건수가",true));
		if(ROW_PER_PAGE>50)
			throw new DefinedException("페이지당 표시 건수가가 올바르지 않습니다.");
		int ST_ROW_NO		= (PAGE_NO-1)*ROW_PER_PAGE+1;
		int ED_ROW_NO		= PAGE_NO*ROW_PER_PAGE;
		
		return new String[]{String.valueOf(ST_ROW_NO),String.valueOf(ED_ROW_NO)};
	}
	public void setPagingValue(Map<String,String> param,HttpServletRequest request) throws Exception{
		int PAGE_NO			= Integer.parseInt(getParam(request,"PAGE_NO","페이지번호가",true));
		int ROW_PER_PAGE	= Integer.parseInt(getParam(request,"ROW_PER_PAGE","페이지당 표시 건수가",true));
		if(ROW_PER_PAGE>100)
			throw new DefinedException("페이지당 표시 건수가 제한 건수를 초과하였습니다.");
		int ST_ROW_NO		= (PAGE_NO-1)*ROW_PER_PAGE+1;
		int ED_ROW_NO		= PAGE_NO*ROW_PER_PAGE;
		
		param.put("ST_ROW_NO", String.valueOf(ST_ROW_NO));
		param.put("ED_ROW_NO", String.valueOf(ED_ROW_NO));
	}
	/**
	 * 접속 IP 확인
	 * */
	public static String getRemoteIP(HttpServletRequest request){
		String ip	= request.getHeader("WL-Proxy-Client-IP");
		if(ip==null || "".equals(ip))
			ip	= request.getHeader("Proxy-Client-IP");
		if(ip==null || "".equals(ip))
   			ip	= request.getHeader("X-Forwarded-For");
		if(ip==null || "".equals(ip))
			ip = request.getRemoteAddr();
		if("0:0:0:0:0:0:0:1".equals(ip))
			ip	= "127.0.0.1";
		return ip;
	}
	/**
	 * 접속 브라우져 종류 확인
	 * */
	public String getRemoteBrowser(HttpServletRequest request){
		String user_agent = request.getHeader("user-agent");
		
		// 웹브라우저 종류 조회
		String webKind = "";
		if (user_agent.toUpperCase().indexOf("GECKO") != -1) {
			if (user_agent.toUpperCase().indexOf("NESCAPE") != -1) {
				webKind = "Netscape (Gecko/Netscape)";
			} else if (user_agent.toUpperCase().indexOf("FIREFOX") != -1) {
				webKind = "Mozilla Firefox (Gecko/Firefox)";
			} else {
				webKind = "Mozilla (Gecko/Mozilla)";
			}
		} else if (user_agent.toUpperCase().indexOf("MSIE") != -1) {
			if (user_agent.toUpperCase().indexOf("OPERA") != -1) {
				webKind = "Opera (MSIE/Opera/Compatible)";
			} else {
				webKind = "Internet Explorer (MSIE/Compatible)";
			}
		} else if (user_agent.toUpperCase().indexOf("SAFARI") != -1) {
			if (user_agent.toUpperCase().indexOf("CHROME") != -1) {
				webKind = "Google Chrome";
			} else {
				webKind = "Safari";
			}
		} else if (user_agent.toUpperCase().indexOf("THUNDERBIRD") != -1) {
			webKind = "Thunderbird";
		} else {
			webKind = "Other Web Browsers";
		}
		return webKind;
	}
	/**
	 * 접속 브라우져 버젼 확인
	 * */
	public String getRemoteBrowserVersion(HttpServletRequest request){
		String user_agent = request.getHeader("user-agent");
		
		// 웹브라우저 버전 조회
		String webVer = "";
		String [] arr = {"MSIE", "OPERA", "NETSCAPE", "FIREFOX", "SAFARI"};
		for (int i = 0; i < arr.length; i++) {
			int s_loc = user_agent.toUpperCase().indexOf(arr[i]);
			if (s_loc != -1) {
				int f_loc = s_loc + arr[i].length();
				webVer = user_agent.toUpperCase().substring(f_loc, f_loc+5);
				webVer = webVer.replaceAll("/", "").replaceAll(";", "").replaceAll("^", "").replaceAll(",", "").replaceAll("//.", "");
			}
		}
		return webVer;
	}
	public String remoteRequest(String url, HttpServletRequest request) throws Exception{
		String jsonString	= null;
		try{
			jsonString	= JHttpClient.getInstance().doPost(url, getParamMap(request), "UTF-8").trim();
		}catch(Exception e){
			throw e;
		}
		return jsonString;
	}
	public void remoteRequest(String url, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
		try{
			String jsonString	= JHttpClient.getInstance().doPost(url, getParamMap(request), "UTF-8").trim();
			String callback	= checkParameter(request,"callback");
	    	response.setStatus(HttpServletResponse.SC_OK);
	    	response.setHeader("Content-Type", "application/json; charset=UTF-8");
	    	
			response.getWriter().append(callback==null||"".equals(callback)?"":callback).append("(").append(jsonString).append(")");
			response.getWriter().close();
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getVariablePath(HttpServletRequest request,String fixUrl){
		String var	= StringUtil.replace(request.getRequestURI(),fixUrl,"");
		if(var.endsWith("/"))
			var	= var.substring(0,var.length()-2);
		return var;
	}
	public String enc(String plain,String encKey) throws Exception{
		return enc(plain,encKey,"UTF-8");
	}
	public String enc(String plain,String encKey,String encoding) throws Exception{
		return AES.encode(plain, encKey,AES.TYPE_256,encoding,AES.MODE_ECB_NOPADDING);		
	}
	
	public String dec(String dec,String encKey) throws Exception{
		return dec(dec,encKey,"UTF-8");
	}
	public String dec(String dec,String encKey,String encoding) throws Exception{
		return AES.decode(dec, encKey,AES.TYPE_256,encoding,AES.MODE_ECB_NOPADDING);		
	}
	
	
	//세무주치의 jsp 분기용
	public static String SMARTTAX_PAGE_HOST(HttpServletRequest request,EgovPropertyService properties){
		String prefix	= (String)request.getSession().getAttribute("SMARTTAX_SKIN_PREFIX");
		if(prefix==null)
			request.getSession().setAttribute("SMARTTAX_SKIN_PREFIX",properties.getString("defaultSmartTaxView"));
		return prefix;
	}
	/**
	 * host 추출
	 * */
	public static String getHostName(HttpServletRequest request){
		String url	= request.getRequestURL().toString();
		url	= StringUtil.replace(StringUtil.replace(url,"https://",""),"http://","");
		if(url.indexOf("/")>0)
			url	= url.substring(0,url.indexOf("/"));
		if(url.indexOf(":")>0)
			url	= url.substring(0,url.indexOf(":"));
		return url;
	}
	/**
	 * host 추출
	 * */
	public static String getFullHostName(HttpServletRequest request){
		String url	= request.getRequestURL().toString();
		boolean isSsl	= url.indexOf("https:")>=0?true:false;
		url	= StringUtil.replace(StringUtil.replace(url,"https://",""),"http://","");
		if(url.indexOf("/")>0)
			url	= url.substring(0,url.indexOf("/"));
		return (isSsl?"https://":"http://")+url;
	}
	/**
	 * referer host 추출
	 * */
	public static String getReferer(HttpServletRequest request){
		String refer	= request.getHeader("referer");
		if(refer==null)
			return "";
		refer	= StringUtil.replace(StringUtil.replace(refer,"https://",""),"http://","");
		if(refer.indexOf("/")>0)
			refer	= refer.substring(0,refer.indexOf("/"));
		if(refer.indexOf(":")>0)
			refer	= refer.substring(0,refer.indexOf(":"));
		return refer;
	}
	
	public static String getReferUri(HttpServletRequest request){
		String refer	= request.getHeader("referer");
		if(refer==null)
			return "";
		refer	= StringUtil.replace(StringUtil.replace(refer,"https://",""),"http://","");
		if(refer.indexOf("/")>0)
			refer	= refer.substring(refer.indexOf("/"));
		if("".equals(refer.trim()))
			refer	= "index";
		return refer;
	}
	
	public static String getLinkType(HttpServletRequest request){
		String linkType	= getReferUri(request);
		if(linkType.startsWith("/sb"))
			linkType	= "sb";
		else if(linkType.startsWith("/ad"))
			linkType	= "ad";
		else
			linkType	= "";
		return linkType;
	}
	/**
	 * 도메인 이동을 위한 기본정보 추출
	 * */
	public Map<String,Object> getDefaultMovingInfo(HttpServletRequest request){
		Map<String,Object> param	= new HashMap<String,Object>();
		String token	= CryptoKeyGenerator.getRandomKey(CryptoKeyGenerator.ALGORITHM_AES256,new int[]{CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL,CryptoKeyGenerator.KEY_TYPE_NUM});
		
		param.put("ATLGN_TKN"	, token);//이동 토큰
		param.put("ATLGN_YN"	, "N");//이동 토큰
		param.put("CSTINF_NO"	, "");//자동로그인 회원번호
		param.put("ATLGN_SRCHST"	, getHostName(request));
		param.put("ATLGN_IP"	, getRemoteIP(request));
		
		return param;
	}
	
	public static String getDestUrl(HttpServletRequest request,String host,String uri){
		String fullUrl	= "";
		if(!uri.startsWith("/"))
			uri	= "/"+uri;
		String serverPortStr	= "/80/443/".indexOf(String.valueOf(request.getServerPort()))<0?(":"+String.valueOf(request.getServerPort())):"";

		if(request.getServerName().startsWith("t-"))//그외 개발계
			fullUrl	= "https://t-"+host+serverPortStr+uri;
		else if(request.getServerName().startsWith("d-"))//로컬 개발계
			fullUrl	= "http://d-"+host+serverPortStr+uri;
		else//운영계
			fullUrl	= "https://"+host+serverPortStr+uri;
		
		return fullUrl;
	}
	public void setPageRow(Map<String,Object> param) throws Exception{
		String pageNo			= getParam(param,"pageNo",5);
		String rowPerPage		= getParam(param,"rowPerPage",3);
		if(pageNo==null || "".equals(pageNo))
			pageNo	= "1";
		if(rowPerPage==null || "".equals(rowPerPage))
			rowPerPage	= properties.getString("pageSize");
		String stno	= String.valueOf((Integer.parseInt(pageNo)-1)*Integer.parseInt(rowPerPage)+1);
		String edno	= String.valueOf(Integer.parseInt(pageNo)*Integer.parseInt(rowPerPage));
		param.put("stno", stno);
		param.put("edno", edno);
	}
	
	public String aesEncrypt(String plain) throws Exception{
		return AES.encode(plain, AES.DEFAULT_KEY,AES.TYPE_256,"UTF-8",AES.MODE_CBC);
	}
	public String aesDecrypt(String enc) throws Exception{
		String plain	= null;
		try{
			plain	= AES.decode(enc, AES.DEFAULT_KEY,AES.TYPE_256,"UTF-8",AES.MODE_CBC);
		}catch(javax.crypto.IllegalBlockSizeException e){
			plain	= SecurityUtil.seedDecodeData(enc);
		}catch(javax.crypto.BadPaddingException e){
			plain	= SecurityUtil.seedDecodeData(enc);
		}
		return plain;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getLogData(Map<String,Object> responseMap, String requestContent, HttpServletRequest request) throws DefinedException, JsonParseException, JsonMappingException, IOException {
		Map<String,Object> logParam		= new HashMap<String,Object>();
		Map<String,Object> requestMap	= null;
		
		String LOG_REQ		= null;
		String LOG_RES		= null;
		try {
			requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			
			LOG_REQ			=  AESCipher.encode(requestContent, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
			LOG_RES			=  AESCipher.encode(new ObjectMapper().writeValueAsString(responseMap), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		} catch (Exception e) {
			requestMap	= new HashMap<String,Object>();
		}
		
		logParam.put("log_time", 	requestMap.get("timestamp")==null ? new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) : requestMap.get("timestamp"));
		logParam.put("log_kn", 		StringUtil.nvl(responseMap.get("TRANS_KN"), "RELAY"));
		logParam.put("log_ip", 		getRemoteIP(request));
		logParam.put("log_url", 	request.getServletPath());
		logParam.put("log_req", 	StringUtil.nvl(LOG_REQ));
		logParam.put("log_res", 	StringUtil.nvl(LOG_RES));
		
		return logParam;
	}
	

	@SuppressWarnings("unchecked")
	protected Map<String,Object> getLogData(Map<String,Object> responseMap, String requestContent, String chnl_req, String chnl_res, HttpServletRequest request) throws DefinedException, JsonParseException, JsonMappingException, IOException {
		Map<String,Object> logParam		= new HashMap<String,Object>();
		Map<String,Object> requestMap	= null;
		
		String LOG_REQ		= null;
		String LOG_RES		= null;
		String LOG_CHNL_REQ	= null;
		String LOG_CHNL_RES	= null;
		try {
			requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			
			LOG_REQ			=  AESCipher.encode(requestContent, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
			LOG_RES			=  AESCipher.encode(new ObjectMapper().writeValueAsString(responseMap), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
			if (chnl_req != null) {
				LOG_CHNL_REQ	=  AESCipher.encode(chnl_req, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
			}
			if (chnl_res != null) {
				LOG_CHNL_RES	=  AESCipher.encode(chnl_res, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
			}
			
		} catch (Exception e) {
			requestMap	= new HashMap<String,Object>();
		}
		
		logParam.put("log_time", 	requestMap.get("timestamp")==null ? new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) : requestMap.get("timestamp"));
		logParam.put("log_kn", 		StringUtil.nvl(responseMap.get("TRANS_KN"), "RELAY"));
		logParam.put("log_ip", 		getRemoteIP(request));
		logParam.put("log_url", 	request.getServletPath());
		logParam.put("log_issno", 	StringUtil.nvl(requestMap.get("ISSUENO")));
		logParam.put("log_req", 	StringUtil.nvl(LOG_REQ));
		logParam.put("log_res", 	LOG_RES);
		logParam.put("log_chnl_req", 	LOG_CHNL_REQ);
		logParam.put("log_chnl_res", 	LOG_CHNL_RES);
		
		return logParam;
	}
}
