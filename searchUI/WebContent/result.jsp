<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style type="text/css">
p.quest {
	font:18px arial,sans-serif;	
	color: black;
}
p.ans {
	font:14px Courier New,sans-serif;
	background-color: #F0F0F0; color: #282828;
}
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Search Diabetes</title>
</head>
<body style="height: 69px; ">
<h1>Search Result</h1>
<p>Please enter some keywords in the textbox.</p>
<form action="question_serv">
		<input name="query_str" style="width: 538px; " value="${param.query_str}">
		<input type="submit" value="Search" style="width: 90px; ">
</form>
<p>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table style="width: 650px; " >
<table style="width: 645px; ">
	<c:forEach var="element" items="${data}">
        <tr>
            <p class="quest"> ${element.question} </p>
            <p class="ans"> ${element.ans} </p>
            <p> ${element.url} </p>
        </tr> 
    </c:forEach>
</table>
</table>
<p>
<table></table>	
</body>
</html>