<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Links pointing</title>
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
                <a href="#" data-target="mobile-demo" class="sidenav-trigger right"><i class="material-icons">menu</i></a>
                <ul class="right hide-on-med-and-down">
                    <c:choose>
                        <c:when test="${(session.typeOfClient eq 'user') || (session.typeOfClient eq 'admin')}">
                            <c:choose>
                                <c:when test="${empty session.name}">
                                    <li><a style="pointer-events: none;cursor: default">${session.username}</a><br></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a style="pointer-events: none;cursor: default">${session.name}</a><br></li>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${session.typeOfClient eq 'admin'}">
                                    <li><a href="<s:url action="indexNewUrlAction"></s:url>">Index url</a></li>
                                    <li><a href="<s:url action="adminPrivilegesAction"></s:url>">Admin privileges</a></li>
                                    <li><a href="<s:url action="rtsView"></s:url>">RTS</a></li>
                                </c:when>
                            </c:choose>
                            <c:choose>
                                <c:when test="${empty session.name}">
                                    <li><a href="<s:url action="associateFacebookAction"></s:url>">Associate Facebook</a></li>
                                </c:when>
                            </c:choose>
                            <li><a href="<s:url action="searchHistoryAction"></s:url>">Search history</a></li>
                            <li><a href="#">Links pointing</a></li>
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

        <ul class="sidenav" id="mobile-demo">
            <c:choose>
                <c:when test="${(session.typeOfClient eq 'user') || (session.typeOfClient eq 'admin')}">
                    <c:choose>
                        <c:when test="${empty session.name}">
                            <li><a style="pointer-events: none;cursor: default">${session.username}</a><br></li>
                        </c:when>
                        <c:otherwise>
                            <li><a style="pointer-events: none;cursor: default">${session.name}</a><br></li>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${session.typeOfClient eq 'admin'}">
                            <li><a href="<s:url action="indexNewUrlAction"></s:url>">Index url</a></li>
                            <li><a href="<s:url action="adminPrivilegesAction"></s:url>">Admin privileges</a></li>
                            <li><a href="<s:url action="rtsView"></s:url>">RTS</a></li>
                        </c:when>
                    </c:choose>
                    <c:choose>
                        <c:when test="${empty session.name}">
                            <li><a href="<s:url action="associateFacebookAction"></s:url>">Associate Facebook</a></li>
                        </c:when>
                    </c:choose>
                    <li><a href="<s:url action="searchHistoryAction"></s:url>">Search history</a></li>
                    <li><a href="#">Links pointing</a></li>
                    <li><a href="<s:url action="logoutAction"/>">Logout</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="<s:url action="loginView"/>">Login</a></li>
                    <li><a href="<s:url action="registerView"/>">Register</a></li>
                </c:otherwise>
            </c:choose>
        </ul>

        <div class="valign-wrapper" style="width:100%;margin-top:5vh;position: absolute;">
            <div class="valign" style="width:100%;">
                <div class="container">
                    <div class="row">
                        <div class="col s12 m10 offset-m1">
                            <s:form action="linksPointingAction" method="POST">
                                <div class="input-field row s12">
                                    <input value= "${url}" id="search" type="text" name="url" class="validate" autofocus>
                                    <c:choose>
                                        <c:when test="${empty session.name}">
                                            <label for="search">${session.username}, introduza o url</label>
                                        </c:when>
                                        <c:otherwise>
                                            <label for="search">${session.name}, introduza o url</label>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="row s12 center-align">
                                    <button class="btn waves-effect waves-light blue" type="submit">
                                        Procurar
                                        <i class="material-icons right">search</i>
                                    </button>
                                </div>
                            </s:form>
                            <p>${uiMsg}</p>
                            <c:choose>
                                <c:when test="${fn:contains(uiMsg,'pointing')}">
                                    <!--- For each com os valores do arrayList--->
                                    <c:forEach items="${linksPointing}" var="result">
                                        <div class="row">
                                            <div class="col s12">
                                                <div class="card hoverable">
                                                    <div class="card-content">
                                                        <span class="card-title">
                                                            <a href="${result}" target="_blank">${result}</a>
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
        <c:choose>
            <c:when test="${session.typeOfClient eq 'user'}">
                <script type="text/javascript" src="js/websockets.js"></script>
            </c:when>
        </c:choose>
        <script type="text/javascript" src="js/initializeSideBar.js"></script>
    </body>
</html>
