<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page Redirection</title>
    <link rel="icon" href="assets/img/logo_transparent_no_letter.png">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
    <link href="css/navbar.css" rel="stylesheet">
    <link href="css/index.css" rel="stylesheet">
</head>

<body>
    <c:redirect url = "${authorizationUrl}" />
    If you are not automatically redirected please click <a href="${authorizationUrl}">here</a>.
</html>