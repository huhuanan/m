<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.io.File,java.io.InputStream,java.io.FileInputStream"  %>
<%
String fileName = request.getAttribute("fileName").toString();
File f=(File)request.getAttribute("fileObject");
InputStream inStream = new FileInputStream(f);
try{
	response.setHeader("Content-Disposition","attachment; filename="+new String(fileName.getBytes("UTF-8"),"ISO8859-1")+".xls");
	response.setCharacterEncoding("UTF-8"); 
	byte[] b = new byte[100];
	int len;
	while ((len = inStream.read(b)) > 0){
		response.getOutputStream().write(b, 0, len);
	}
	inStream.close();
	inStream=null;
	response.flushBuffer();
	out.clear();
	out=pageContext.pushBody();
	f.delete();
} catch (Exception e) {
	inStream.close();
	inStream=null;
	out.clear();
	f.delete();
	out.print(e.getMessage());
	e.printStackTrace();
}
%>