package com.kwic.util.pdf;


import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import com.kwic.util.StringUtil;
import com.lowagie.text.pdf.BaseFont;

public class Html2Pdf {
	
	public static final String decodeHtml(String html) throws UnsupportedEncodingException{
		html	= URLDecoder.decode(html,"UTF-8");
		html	= StringUtil.replace(html, "<JPV-IMG", "<img");
		html	= StringUtil.replace(html, "<br></br>", "<br/>");
		html	= StringUtil.replace(html, "<br>", "<br/>");
		html	= StringUtil.replace(html, "<col>", "<col/>");
		html	= StringUtil.replace(html, "&nbsp;", "&#160;");
		html	= StringUtil.replace(html,"&apos;","\'");
		html	= StringUtil.replace(html,"&quot;","\"");
		html	= StringUtil.replace(html,"&#63;","?");
		html	= StringUtil.replace(html,"&#47;","/");
		html	= StringUtil.replace(html,"&#38;","&");
		html	= StringUtil.replace(html,"</br>","");
		return html;
	}
	
	public static final void parse(OutputStream os, String htmlString,String[] fontPath,String baseURL) throws Exception{
		if(baseURL!=null && baseURL.endsWith("/"))
			baseURL	= baseURL.substring(0,baseURL.length()-1);

		String html	= decodeHtml(htmlString);
		html	= StringUtil.replace(html,"<img","<JPV-IMG");

		StringBuffer sb	= new StringBuffer().append(html);
		int idx	= -1;
		int idx2	= -1;
		while((idx=sb.indexOf("<JPV-IMG", idx+1))>=0){
			idx2	= sb.indexOf(">",idx);
			if(idx2<0)
				break;
			if(!"/".equals(sb.substring(idx2-1,idx2))){
				sb.insert(idx2, '/');
			}
		}
		while((idx=sb.indexOf("<col ", idx+1))>=0){
			idx2	= sb.indexOf(">",idx);
			if(idx2<0)
				break;
			if(!"/".equals(sb.substring(idx2-1,idx2))){
				sb.insert(idx2, '/');
			}
		}
		html	= sb.toString();
		html	= StringUtil.replace(html, "</img>", "");
		html	= StringUtil.replace(html, "</col>", "");
		html	= StringUtil.replace(html, "<JPV-IMG","<img");
		
		ITextRenderer renderer = new ITextRenderer();
		for(int i=0;i<fontPath.length;i++)
			renderer.getFontResolver().addFont(fontPath[i], BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//			renderer.getFontResolver().addFont(fontPath[i], "UTF-8", BaseFont.NOT_EMBEDDED);
		
		ResourceLoaderUserAgent callback	= new ResourceLoaderUserAgent(renderer.getOutputDevice());
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		renderer.getSharedContext().setReplacedElementFactory(new ProfileImageReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory()));
		
		InputSource is = new InputSource(new BufferedReader(new StringReader(html)));
	    org.w3c.dom.Document dom = XMLResource.load(is).getDocument();

	    String[] srcTags	= {"img","script"};
	    String[] hrefTags	= {"link"};
	    NodeList list		= null;
	    Element e			= null;
	    for(int i=0;i<srcTags.length;i++){
			list	= dom.getElementsByTagName(srcTags[i]);
			for(int j=0;j<list.getLength();j++){
				e	= (Element)(list.item(j));
				if(e.getAttribute("src")!=null && !e.getAttribute("src").startsWith("http"))
					e.setAttribute("src", baseURL+(e.getAttribute("src").startsWith("/")?"":"/")+e.getAttribute("src"));
			}
	    }
	    for(int i=0;i<hrefTags.length;i++){
			list	= dom.getElementsByTagName(hrefTags[i]);
			for(int j=0;j<list.getLength();j++){
				e	= (Element)(list.item(j));
				if(e.getAttribute("href")!=null && !e.getAttribute("href").startsWith("http"))
					e.setAttribute("href", baseURL+(e.getAttribute("href").startsWith("/")?"":"/")+e.getAttribute("href"));
			}
	    }
	    
		renderer.setDocument(dom,baseURL);
		renderer.layout();
		renderer.createPDF(os);

		os.close();
	}

}









