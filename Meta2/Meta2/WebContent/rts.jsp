<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>RTS</title>
        <link rel="icon" href="assets/img/logo_transparent_no_letter.png">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
        <link href="css/navbar.css" rel="stylesheet">
        <link href="css/register.css" rel="stylesheet">
        <script type="text/javascript" src="js/materialize.min.js"></script>
        <!---<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>--->
    </head>

    <body>
        <noscript>JavaScript must be enabled for WebSockets to work.</noscript>
        <div>
            <div id="container"><div id="history"></div></div>
            <p><input type="text" placeholder="type to chat" id="chat"></p>
        </div>


        <script type="text/javascript" src="js/websockets.js"></script>
        <!---<script type="text/javascript" src="js/initializeSideBar.js"></script>--->
    </body>
</html>
