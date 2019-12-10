<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Register</title>
		<link rel="icon" href="assets/img/logo_transparent_no_letter.png">
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
		<link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
		<link href="css/navbar.css" rel="stylesheet">
		<link href="css/register.css" rel="stylesheet">
		<script type="text/javascript" src="js/materialize.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
	</head>

	<body>
		<nav>
			<div class="nav-wrapper blue lighten-2">
				<ul class="left">
					<li><a href="<s:url action="indexView"/>"><img id="navImg" class="circle responsive-img" src="assets/img/logo_transparent_no_letter.png" alt="Logo"></a></li>
				</ul>
				<ul class="right">
					<li><a href="<s:url action="loginView"/>">Login</a></li>
					<li><a href="#">Registo</a></li>
				</ul>
			</div>
		</nav>

		<div class="valign-wrapper" style="width:100%;height:80%;position: absolute;">
			<div class="valign" style="width:100%;">
				<div class="container">
					<div class="row">
						<div class="col s12 m6 offset-m3">
							<div class="card">
								<div class="card-content">
									<span class="card-title black-text">Register</span>
									<s:form action="registerAction" method="POST">
										<div class="row">
											<div class="input-field col s12">
												<input id="username" type="text" class="validate" name="username">
												<label for="username" class="active">Username</label>
											</div>
										</div>
										<div class="row">
											<div class="input-field col s12">
												<input id="password" type="password" class="validate" name="password">
												<label for="password" class="active">Password</label>
											</div>
										</div>

										<div class="row card-action">
											<button class="btn waves-effect waves-light blue" type="submit">Registar</button>
										</div>
									</s:form>
								</div>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- Modal Structure -->
		<div class="modal">
			<div class="modal-content">
				<h4>Register</h4>
				<p>${uiMsg}</p>
			</div>
			<div class="modal-footer">
				<c:choose>
					<c:when test="${fn:contains(uiMsg,'successful')}">
						<a href="<s:url action="indexView"/>" class="modal-close waves-effect waves-green btn-flat">Ok</a>
					</c:when>
					<c:otherwise>
						<a href="<s:url action="registerView"/>" class="modal-close waves-effect waves-green btn-flat">Ok</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<c:choose>
			<c:when test="${uiMsg != null}">
				<script>
					$.getScript('js/materialize.min.js', function()
					{
						$('.modal').modal({dismissible: false, onCloseEnd: ()=>$('.modal').modal('destroy')});
						$('.modal').modal('open');
					});
				</script>
			</c:when>
		</c:choose>
	</body>
</html>