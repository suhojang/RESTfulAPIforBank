package com.kwic.html.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormControl;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

/**
 * <pre>
 * Title		: JHTMLParser
 * Description	: HTML Parser
 * Date			: 
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC.
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈
 * @version	
 * @since 
 */
public class JHTMLParser {
	private Source src;
	
	public JHTMLParser(String htmlString){
		src	= new Source(htmlString);
	}
	public JHTMLParser(String filePath,boolean isURL) throws FileNotFoundException, IOException{
		src	= new Source(new FileInputStream(new File(filePath)));
	}
	public List<Element> getElementsByTagName(String tagName){
		return src.getAllElements(tagName);
	}
	public Element getElementById(String id){
		return src.getElementById(id);
	}
	
	public List<FormControl> getFormControl(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").equals(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='"+name+"']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}
	
	public List<FormControl> getFormControlStartsWith(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").startsWith(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='"+name+"...']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}
	public List<FormControl> getFormControlEndsWith(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").endsWith(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='..."+name+"']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}

	public String getFormValue(String elementName) throws Exception{
		return getFormValue(elementName,0);
	}
	
	public String getFormValue(String elementName,int idx) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		
		FormControl control	= list.get(idx);
		return control.getAttributesMap().get("value");
	}
	
	public String getFormValue(FormControl control) throws Exception{
		return control.getAttributesMap().get("value");
	}
	
	public String getFormType(String elementName) throws Exception{
		return getFormType(elementName,0);
	}
	public String getFormType(String elementName,int idx) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");

		FormControl control	= list.get(idx);
		return control.getAttributesMap().get("type");
	}
	public String getFormAttribute(String elementName,String attrName) throws Exception{
		return getFormAttribute(elementName,0,attrName);
	}
	public String getFormAttribute(String elementName,int idx,String attrName) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");

		FormControl control	= list.get(idx);
		return control.getAttributesMap().get(attrName);
	}
	public String getTdValue(int tbIdx,int trIdx,int tdIdx) throws Exception{
		List<Element> list	= src.getAllElements(HTMLElementName.TABLE);
		if(list.size()<=tbIdx)
			throw new Exception("TABLE의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element table	= list.get(tbIdx);
		
		list	= table.getAllElements(HTMLElementName.TR);
		if(list.size()<=trIdx)
			throw new Exception("TABLE["+tbIdx+"]-TR의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element tr	= list.get(trIdx);

		list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TABLE["+tbIdx+"]-TR["+trIdx+"]-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}
	
	public String getTdValue(Element tr,int tdIdx) throws Exception{
		List<Element> list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}

	public List<Element> getTRList(int tbIdx) throws Exception{
		List<Element> list	= src.getAllElements(HTMLElementName.TABLE);
		if(list.size()<=tbIdx)
			throw new Exception("TABLE의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element table	= list.get(tbIdx);
		
		list	= table.getAllElements(HTMLElementName.TR);
		
		return list;
	}
	
	public String getTdValue(String tbId,int trIdx,int tdIdx) throws Exception{
		Element table	= getElementById(tbId);

		List<Element> list	= table.getAllElements(HTMLElementName.TR);
		if(list.size()<=trIdx)
			throw new Exception("TABLE[id='"+tbId+"']-TR의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element tr	= list.get(trIdx);
		
		list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TABLE[id='"+tbId+"']-TR["+trIdx+"]-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}
	public String getTdValue(String trId,int tdIdx) throws Exception{
		Element tr	= getElementById(trId);

		List<Element> list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TR[id='"+trId+"']-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		
		Element td	= tr.getAllElements(HTMLElementName.TD).get(tdIdx);
		return td.getContent().toString();
	}
	public String getTdValue(String tdId){
		Element td	= getElementById(tdId);
		return td.getContent().toString();
	}
	
	public static void main(String[] args) throws Exception{
	}
	
}
