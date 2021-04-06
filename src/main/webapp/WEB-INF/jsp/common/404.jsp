<%@page import="com.kwic.web.controller.Controllers"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	response.setHeader("Content-Type", "application/json; charset=UTF-8");
	response.getWriter().append("{\""+Controllers.RT_CD+"\":\""+HttpServletResponse.SC_NOT_FOUND+"\",");
	response.getWriter().append("\""+Controllers.RT_MSG+"\":\"엔드포인트 URL의 오류가 있는지 API 문서를 참고하여 확인 바랍니다.\"}");
	response.getWriter().close();
%>