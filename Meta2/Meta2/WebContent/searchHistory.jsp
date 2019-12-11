<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ucBusca</title>
    <link rel="icon" href="assets/img/logo_transparent_no_letter.png">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
    <link href="css/navbar.css" rel="stylesheet">
    <link href="css/searchResults.css" rel="stylesheet">
    <script type="text/javascript" src="js/materialize.min.js"></script>
</head>

<body>
<nav>
    <div class="nav-wrapper blue lighten-2">
        <ul class="left">
            <li><a href="<s:url action="indexView"/>"><img id="navImg" class="circle responsive-img" src="assets/img/logo_transparent_no_letter.png" alt="Logo"></a></li>
        </ul>
        <ul class="right">
            <c:choose>
                <c:when test="${(session.typeOfClient eq 'user') || (session.typeOfClient eq 'admin')}">
                    <c:choose>
                        <c:when test="${empty session.name}">
                            <li><a style="pointer-events: none;cursor: default">${session.username}</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a style="pointer-events: none;cursor: default">${session.name}</a></li>
                        </c:otherwise>
                    </c:choose>
                    <li><a href="#">Search history</a></li>
                    <li><a href="<s:url action="linksPointingView"/>">Links pointing</a></li>
                    <li><a href="<s:url action="logoutAction"/>">Logout</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="<s:url action="loginView"/>">Login</a></li>
                    <li><a href="<s:url action="registerView"/>">Register</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>

<div class="valign-wrapper" style="width:100%;margin-top:5vh;position: absolute;">
    <div class="valign" style="width:100%;">
        <div class="container">
            <div class="row">
                <div class="col s12 m10 offset-m1">
                    <p>${uiMsg}</p>
                    <c:choose>
                        <c:when test="${fn:contains(uiMsg,'Showing')}">
                            <!--- For each com os valores do arrayList--->
                            <c:forEach items="${searchHistory}" var="result">
                                <div class="row">
                                    <div class="col s12">
                                        <div class="card hoverable">
                                            <div class="card-content">
                                                <span class="card-title">
                                                    ${result}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
