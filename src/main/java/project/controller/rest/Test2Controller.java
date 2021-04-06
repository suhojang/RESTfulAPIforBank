package project.controller.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kwic.code.ErrorCode;
import com.kwic.web.controller.Controllers;

@RestController
public class Test2Controller extends Controllers{
	@GetMapping(value="/rest/api/products", produces="application/json; charset=UTF-8")
	public void products_sel(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		System.out.println("requestContent :: " + requestContent);
		
		responseMap.put(RT_CD,	ErrorCode._0000);
		responseMap.put(RT_MSG,	ErrorCode.ERROR_MESSAGE.get(ErrorCode._0000));
		response(response, responseMap);
	}
}
