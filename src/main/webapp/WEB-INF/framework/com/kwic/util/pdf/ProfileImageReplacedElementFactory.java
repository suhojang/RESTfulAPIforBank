package com.kwic.util.pdf;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.telegram.web.JURLConnection;
import com.lowagie.text.Image;

public class ProfileImageReplacedElementFactory implements ReplacedElementFactory{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ReplacedElementFactory superFactory;
	
	public ProfileImageReplacedElementFactory(ReplacedElementFactory superFactory){
		this.superFactory	= superFactory;
	}
	
	@Override
	public ReplacedElement createReplacedElement(LayoutContext layoutcontext, BlockBox blockbox,
			UserAgentCallback useragentcallback, int cssWidth, int cssHeight) {

		Element element	= blockbox.getElement();
		
		if(element==null)
			return null;
		
		String nName	= element.getNodeName();
		String url		= element.getAttribute("url");
		if("img".equals(nName) && url!=null && !"".equals(url)){
			try {
				byte[] bytes	= JURLConnection.connect( url,null, null,30*1000, true);
				Image img		= Image.getInstance(bytes);
				FSImage fImg	= new ITextFSImage(img);
				
				if(fImg!=null){
					if(cssWidth!=-1 || cssHeight!=-1)
						fImg.scale(cssWidth,cssHeight);
					return new ITextImageElement(fImg);
				}
			}catch (Exception e){
				logger.error(e);
			}
		}
		return superFactory.createReplacedElement(layoutcontext, blockbox, useragentcallback, cssWidth, cssHeight);
	}

	@Override
	public void remove(Element element) {
		superFactory.remove(element);
	}

	@Override
	public void reset() {
		superFactory.reset();
	}

	@Override
	public void setFormSubmissionListener(FormSubmissionListener formsubmissionlistener) {
		superFactory.setFormSubmissionListener(formsubmissionlistener);
	}

}
