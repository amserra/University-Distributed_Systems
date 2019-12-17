<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Erro</title>
		<link rel="icon" href="assets/img/logo_transparent_no_letter.png">
		<link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
		<link href="https://fonts.googleapis.com/css?family=Montserrat:200,400,700" rel="stylesheet">
		<link href="css/error.css" rel="stylesheet">
	</head>

	<body>

	<div id="notfound">
		<div class="notfound">
			<div class="notfound-404">
				<h1>Oops!</h1>
				<h2>404 - The Page can't be reached</h2>
			</div>
			<a href="<s:url action="indexView"/>">Go TO Homepage</a>
		</div>
	</div>
	<p><s:property value="exceptionStack" /></p>

	<script type="text/javascript" src="js/materialize.min.js"></script>
	</body>
</html>