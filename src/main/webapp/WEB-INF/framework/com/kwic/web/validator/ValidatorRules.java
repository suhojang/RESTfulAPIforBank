package com.kwic.web.validator;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.web.context.ContextLoader;

import com.kwic.exception.DefinedException;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

public class ValidatorRules {
	protected Logger logger = LoggerFactory.getLogger(ValidatorRules.class);
	
	private static ValidatorRules instance;
	
	private Hashtable<String,String> validatorActionInfos;
	private Hashtable<String,JXParser> validatorXmlInfos	= new Hashtable<String,JXParser>();
	private Hashtable<String,String> validatorScriptInfos	= new Hashtable<String,String>();
	private Hashtable<String,Long> validatorModified		= new Hashtable<String,Long>();
	
	private String baseFolder;
	
	private ValidatorRules() throws Exception {
		baseFolder	= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/validator");
		loadValidatorActions(false);
	}
	
	public static ValidatorRules getInstance() throws Exception {
		synchronized(ValidatorRules.class) {
			if(instance==null)
				instance	= new ValidatorRules();
			return instance;
		}
	}
	
	private synchronized void loadValidatorActions(boolean reload) throws Exception{
		if(!reload && validatorActionInfos!=null)
			return;
		
		validatorActionInfos	= new Hashtable<String,String>();
		File[] files	= new File(baseFolder).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(file.getName().toLowerCase().endsWith(".xml"))
					return true;
				return false;
			}
		});
		
		JXParser validatorXml	= null;
		Element[] actions		= null;
		String fName			= null;
		String uri				= null;
		
		for(int i=0;i<files.length;i++) {
			validatorXml	= new JXParser(files[i]);
			fName			= files[i].getName();
			fName			= fName.substring(0,fName.indexOf(".xml"));
			actions			= validatorXml.getElements("//action");
			for(int j=0;j<actions.length;j++) {
				uri	= validatorXml.getAttribute(actions[j], "uri");
				if(validatorActionInfos.get(uri)!=null)
					throw new DefinedException("Parameter validator action ["+uri+"]가 중복됩니다. 이미 등록된 uri는 action이 아닌 ref 태그를 사용하십시오.");
				validatorActionInfos.put(uri, fName);
				logger.debug("Check parameter validator action ["+fName+"->"+uri+"].");
			}
		}
	}
	
	private synchronized void loadValidator(String referid) throws Exception{
		File validatorFile	= new File(baseFolder,referid+".xml");
		if(!validatorFile.exists())
			return;
		
		if(validatorXmlInfos.get(referid)!=null && validatorScriptInfos.get(referid)!=null && validatorModified.get(referid)!=null && validatorModified.get(referid).longValue()==validatorFile.lastModified())
			return;
		else if(validatorXmlInfos.get(referid)!=null && validatorScriptInfos.get(referid)!=null && validatorModified.get(referid)!=null && validatorModified.get(referid).longValue()!=validatorFile.lastModified())
			loadValidatorActions(true);
		
		StringBuffer script	= new StringBuffer();
		script.append("var _form_validator_rules = [");
		JXParser validatorXml	= new JXParser(validatorFile);
		
		Element form	= validatorXml.getElement("//form");
		if(form!=null) {
			Element[] inputs	= validatorXml.getElements(form,"input");
			
			String id			= null;
			String type			= null;
			String maxlength	= null;
			String readonly		= null;
			String disabled		= null;
			String placeholder	= null;
			String value		= null;
			String title		= null;
			String desc			= null;
			
			for(int i=0;i<inputs.length;i++) {
				id			= validatorXml.getAttribute(inputs[i],"id");
				type		= validatorXml.getAttribute(inputs[i],"type");
				maxlength	= validatorXml.getAttribute(inputs[i],"maxlength");
				readonly	= validatorXml.getAttribute(inputs[i],"readonly");
				disabled	= validatorXml.getAttribute(inputs[i],"disabled");
				placeholder	= validatorXml.getAttribute(inputs[i],"placeholder");
				value		= validatorXml.getAttribute(inputs[i],"value");
				title		= validatorXml.getAttribute(inputs[i],"title");
				desc		= validatorXml.getAttribute(inputs[i],"desc");
				
				script.append(i==0?"":",").append("{")
				.append(id==null?"id:'undefined'":"id:'").append(id).append("'")
				.append(type==null?"":", type:'").append(type).append("'")
				.append(maxlength==null?"":", maxlength:'").append(maxlength).append("'")
				.append(readonly==null?"":", readonly:'").append(readonly).append("'")
				.append(disabled==null?"":", disabled:'").append(disabled).append("'")
				.append(placeholder==null?"":", placeholder:'").append(placeholder).append("'")
				.append(value==null?"":", value:\"").append(StringUtil.replace(value,"\"","'")).append("\"")
				.append(title==null?"":", title:\"").append(StringUtil.replace(title,"\"","'")).append("\"")
				.append(desc==null?"":", desc:\"").append(StringUtil.replace(desc,"\"","'")).append("\"")
				.append("}\n");
			}
		}
		script.append("];\n");
		
		Map<String,String> uriMap	= new HashMap<String,String>();
		script.append("var _action_validator_rules = {\n");
		
		String id		= null;
		String type		= null;
		String required	= null;
		String minBytes	= null;
		String maxBytes	= null;
		String fixBytes	= null;
		String dateFormat	= null;
		String desc		= null;
		String focus	= null;
		String target	= null;
		String rexp		= null;
		String typeName	= null;
		String uri		= null;
		String encoding	= null;
		
		Element[] actions	= validatorXml.getElements("//action");
		Element[] params	= null;
		for(int i=0;i<actions.length;i++) {
			uri				= validatorXml.getAttribute(actions[i],"uri");
			if(uriMap.get(uri)!=null)
				continue;
			uriMap.put(uri,"Y");
			
			encoding		= validatorXml.getAttribute(actions[i],"encoding");
			
			script.append(uriMap.size()==1?"":",").append("'").append(uri).append("':[");
			params	= validatorXml.getElements(actions[i],"param");
			for(int j=0;j<params.length;j++) {
				id			= validatorXml.getAttribute(params[j],"id");
				type		= validatorXml.getAttribute(params[j],"type");
				required	= validatorXml.getAttribute(params[j],"required");
				minBytes	= validatorXml.getAttribute(params[j],"minBytes");
				maxBytes	= validatorXml.getAttribute(params[j],"maxBytes");
				fixBytes	= validatorXml.getAttribute(params[j],"fixBytes");
				dateFormat	= validatorXml.getAttribute(params[j],"dateFormat");
				desc		= validatorXml.getAttribute(params[j],"desc");
				focus		= validatorXml.getAttribute(params[j],"focus");
				target		= validatorXml.getAttribute(params[j],"target");
				rexp		= validatorXml.getAttribute(params[j],"rexp");
				typeName	= validatorXml.getAttribute(params[j],"typeName");
				if(validatorXml.getAttribute(params[j],"encoding")!=null && !"".equals(validatorXml.getAttribute(params[j],"encoding")))
					encoding	= validatorXml.getAttribute(params[j],"encoding");
				script.append(j==0?"":",").append("{")
				.append(id==null?"id:'undefined'":"id:'").append(id).append("'")
				.append(encoding==null?"":", encoding:'").append(encoding).append("'")
				.append(type==null?"":", type:'").append(type).append("'")
				.append(required==null?"":", required:'").append(required).append("'")
				.append(minBytes==null?"":", minBytes:'").append(minBytes).append("'")
				.append(maxBytes==null?"":", maxBytes:'").append(maxBytes).append("'")
				.append(fixBytes==null?"":", fixBytes:'").append(fixBytes).append("'")
				.append(dateFormat==null?"":", dateFormat:'").append(dateFormat).append("'")
				.append(focus==null?"":", focus:'").append(focus).append("'")
				.append(target==null?"":", target:'").append(target).append("'")
				.append(typeName==null?"":", placeholder:'").append(typeName).append("'")
				.append(rexp==null?"":", rexp:\"").append(rexp).append("\"")
				.append(desc==null?"":", desc:\"").append(StringUtil.replace(desc,"\"","'")).append("\"")
				.append("}\n");
			}
			script.append("]\n");
		}
		
		Element[] refs	= validatorXml.getElements("//ref");
		Element action	= null;
		JXParser refValidatorXml	= null;
		File refFile	= null;
		for(int i=0;i<refs.length;i++) {
			uri				= validatorXml.getAttribute(refs[i],"uri");
			if(uriMap.get(uri)!=null)
				continue;
			uriMap.put(uri,"Y");
			if(validatorActionInfos.get(uri)==null) {
				logger.warn("Reference action not found. ref-uri ["+uri+"]");
				continue;
			}
			
			refFile	= new File(baseFolder,validatorActionInfos.get(uri)+".xml");
			if(!refFile.exists()) {
				logger.warn("Reference validation not found. referer ["+validatorActionInfos.get(uri)+"]");
				continue;
			}
				
			refValidatorXml	= new JXParser(refFile);
			
			action	= refValidatorXml.getElement("//action[@uri='"+uri+"']");
			encoding	= refValidatorXml.getAttribute(action,"encoding");
			script.append(uriMap.size()==1?"":",").append("'").append(uri).append("':[");
			params	= refValidatorXml.getElements(action,"param");
			for(int j=0;j<params.length;j++) {
				id			= refValidatorXml.getAttribute(params[j],"id");
				type		= refValidatorXml.getAttribute(params[j],"type");
				required	= refValidatorXml.getAttribute(params[j],"required");
				minBytes	= refValidatorXml.getAttribute(params[j],"minBytes");
				maxBytes	= refValidatorXml.getAttribute(params[j],"maxBytes");
				fixBytes	= refValidatorXml.getAttribute(params[j],"fixBytes");
				dateFormat	= refValidatorXml.getAttribute(params[j],"dateFormat");
				desc		= refValidatorXml.getAttribute(params[j],"desc");
				focus		= refValidatorXml.getAttribute(params[j],"focus");
				target		= refValidatorXml.getAttribute(params[j],"target");
				rexp		= refValidatorXml.getAttribute(params[j],"rexp");
				typeName	= refValidatorXml.getAttribute(params[j],"typeName");
				if(refValidatorXml.getAttribute(params[j],"encoding")!=null && !"".equals(refValidatorXml.getAttribute(params[j],"encoding")))
					encoding	= refValidatorXml.getAttribute(params[j],"encoding");
				script.append(j==0?"":",").append("{")
				.append(id==null?"id:'undefined'":"id:'").append(id).append("'")
				.append(encoding==null?"":", encoding:'").append(encoding).append("'")
				.append(type==null?"":", type:'").append(type).append("'")
				.append(required==null?"":", required:'").append(required).append("'")
				.append(minBytes==null?"":", minBytes:'").append(minBytes).append("'")
				.append(maxBytes==null?"":", maxBytes:'").append(maxBytes).append("'")
				.append(fixBytes==null?"":", fixBytes:'").append(fixBytes).append("'")
				.append(dateFormat==null?"":", dateFormat:'").append(dateFormat).append("'")
				.append(focus==null?"":", focus:'").append(focus).append("'")
				.append(target==null?"":", target:'").append(target).append("'")
				.append(typeName==null?"":", placeholder:'").append(typeName).append("'")
				.append(rexp==null?"":", rexp:\"").append(rexp).append("\"")
				.append(desc==null?"":", desc:\"").append(StringUtil.replace(desc,"\"","'")).append("\"")
				.append("}\n");
			}
			script.append("]\n");
		}
		
		script.append("};\n");
		script.append("$(document).ready(function() {$.preventInput(_form_validator_rules);});");
		
		validatorXmlInfos.put(referid, validatorXml);
		validatorModified.put(referid, validatorFile.lastModified());
		validatorScriptInfos.put(referid, script.toString());
		
		logger.debug("Parameter validator ["+referid+"] loaded.");
	}

	public String getScriptValidator(String referid) throws Exception {
		loadValidator(referid);
		
		String script	= validatorScriptInfos.get(referid);
		if(script==null)
			script	= "var _form_validator_rules = [];\nvar _action_validator_rules = {};";

		return script;
	}
	
	public JXParser getValidator(String uri) throws Exception {
		if(validatorActionInfos.get(uri)==null)
			return null;
		
		String referid	= validatorActionInfos.get(uri);
		loadValidator(referid);
		
		return validatorXmlInfos.get(referid);
	}
}
