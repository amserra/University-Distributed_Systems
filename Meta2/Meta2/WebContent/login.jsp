<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html, charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Login</title>
		<link rel="icon" href="assets/img/logo_transparent_no_letter.png">
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
		<link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection">
		<link href="css/navbar.css" rel="stylesheet">
		<link href="css/register.css" rel="stylesheet">
	</head>

	<body>
	<div id="fb-root"></div>

		<nav>
			<div class="nav-wrapper blue lighten-2">
				<ul class="left">
					<li><a href="<s:url action="index"/>"><img id="navImg" class="circle responsive-img" src="assets/img/logo_transparent_no_letter.png" alt="Logo"></a></li>
				</ul>
				<ul class="right">
					<li><a href="#">Login</a></li>
					<li><a href="<s:url action="register"/>">Registo</a></li>
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
									<span class="card-title black-text">Login</span>
									<s:form action="login" method="POST">
										<div class="row">
											<div class="input-field col s12">
												<input id="firstname" type="text" class="validate">
												<label for="firstname" class="active">Username</label>
											</div>
										</div>
										<div class="row">
											<div class="input-field col s12">
												<input id="lastname" type="password" class="validate">
												<label for="lastname" class="active">Password</label>
											</div>
										</div>

										<div class="row card-action">
											<div class="col">
												<a class="btn waves-effect waves-light blue">Login</a>
											</div>
											<div class="col">
												<a class="btn waves-effect waves-light blue lighten-2" href="<s:url action="register"/>">Registo</a>
											</div>
											<div class="col">
												<a class="btn waves-effect waves-light blue" href="<s:url action="loginFacebook"/>">Login with Facebook</a>
											</div>
										</div>
									</s:form>
								</div>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

	</body>
</html>