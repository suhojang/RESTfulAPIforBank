<%@page import="com.kwic.web.controller.Controllers"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	response.setHeader("Content-Type", "application/json; charset=UTF-8");
	response.getWriter().append("{\""+Controllers.RT_CD+"\":\""+HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"\",");
	response.getWriter().append("\""+Controllers.RT_MSG+"\":\"일시적으로 에러가 발생하였습니다. 잠시 후에 다시 시도해 주시기 바랍니다.\"}");
	response.getWriter().close();
%>