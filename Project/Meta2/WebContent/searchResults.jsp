<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Search results</title>
        <link rel="icon" href="assets/img/logo_transparent_no_letter.png">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
        <link href="css/navbar.css" rel="stylesheet">
        <link href="css/searchResults.css" rel="stylesheet">
        <script type="text/javascript" src="js/materialize.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
        <script type="text/javascript">
            function traduzir(ev) {

                var num = ev.id.split("-")[1];

                var title = $("#title-" + num) .val();
                var text = $("#text-" + num) .val();

                $.ajax({
                    type: "POST",
                    url: "translateAction",
                    data: {title: title, text: text},
                    dataType: 'json',
                    success: function(result)
                    {
                        console.log(result)

                        var json = $.parseJSON(result)

                        var titleTranlated = json["title"]
                        var textTranslated = json["text"]

                        if(titleTranlated == "null"){
                            $.getScript('js/materialize.min.js', function()
                            {
                                $('.modal').modal({dismissible: false, onCloseEnd: ()=>$('.modal').modal('destroy')});
                                $('.modal').modal('open');
                            });
                        } else{
                            $("#title-" + num) .val(titleTranlated)
                        }

                        if(titleTranlated != "null" && textTranslated == "null"){
                            $.getScript('js/materialize.min.js', function()
                            {
                                $('.modal').modal({dismissible: false, onCloseEnd: ()=>$('.modal').modal('destroy')});
                                $('.modal').modal('open');
                            });
                        } else if(textTranslated != "null"){
                            $("#text-" + num) .val(textTranslated)
                        }

                    }
                });

            }
        </script>
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
                                    <li><a style="pointer-events: none;cursor: default">${session.username}</a></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a style="pointer-events: none;cursor: default">${session.name}</a></li>
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
                            <li><a href="<s:url action="linksPointingAction"/>">Links pointing</a></li>
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
                    <li><a href="<s:url action="linksPointingAction"/>">Links pointing</a></li>
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
                            <s:form action="searchAction" method="POST">
                                <div class="input-field row s12">
                                    <input value= "${searchTerms}" id="search" type="text" name="searchTerms" class="validate" autofocus>
                                    <c:choose>
                                        <c:when test="${empty session.username}">
                                            <label for="search">O que busca?</label>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${empty session.name}">
                                                    <label for="search">${session.username}, o que busca?</label>
                                                </c:when>
                                                <c:otherwise>
                                                    <label for="search">${session.name}, o que busca?</label>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="row s12 center-align">
                                    <button class="btn waves-effect waves-light blue" type="submit">
                                        Procurar
                                        <i class="material-icons right">search</i>
                                    </button>
                                </s:form>
                                    <c:choose>
                                        <c:when test="${not empty session.name}">
                                            <s:form action="facebookShare">
                                                <div class="col">
                                                    <button class="btn waves-effect waves-light blue" type="submit">
                                                        Share on Facebook
                                                    </button>
                                                </div>
                                            </s:form>
                                        </c:when>
                                    </c:choose>
                                </div>

                            <p>${uiMsg}</p>
                            <c:choose>
                                <c:when test="${fn:contains(uiMsg,'results')}">
                                    <!--- For each com os valores do arrayList--->
                                    <c:set var = "i" scope = "session" value = "${0}"/>
                                    <c:forEach items="${searchResults}" var="result">
                                            <div class="row">
                                                <div class="col s12">
                                                    <div class="card hoverable">
                                                        <div class="card-content">
                                                            <span class="card-title">
                                                                <span class="badge">${result.lang} - <button id = "btn-<c:out value = "${i}"/>" class="linkBtn" onclick="traduzir(this)">Traduzir</button></span>
                                                                <input id = "title-<c:out value = "${i}"/>" class = "nothing" type="text" name = "title" readonly value = "${result.title}">
                                                            </span>
                                                            <p><a href="${result.url}" target="_blank">${result.url}</a></p>
                                                            <input id = "text-<c:out value = "${i}"/>" class = "nothing" type="text" name="text" readonly value = "${result.text}">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        <c:set var="i" value="${i + 1}" scope="page"/>
                                    </c:forEach>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Structure -->
        <div class="modal">
            <div class="modal-content">
                <h4>Error</h4>
                <p>There was an error in translation</p>

            </div>
            <div class="modal-footer">
                <a class="modal-close waves-effect waves-green btn-flat">Ok</a>
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
