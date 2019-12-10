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
		<nav>
			<div class="nav-wrapper blue lighten-2">
				<ul class="left">
					<li><a href="<s:url action="indexView"/>"><img id="navImg" class="circle responsive-img" src="assets/img/logo_transparent_no_letter.png" alt="Logo"></a></li>
				</ul>
				<ul class="right">
					<li><a href="#">Login</a></li>
					<li><a href="<s:url action="registerView"/>">Registo</a></li>
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
									<s:form action="loginAction" method="POST">
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
											<button class="btn waves-effect waves-light blue" type="submit">Login</button>
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
		<div id="modal1" class="modal">
			<div class="modal-content">
				<h4>Modal Header</h4>
				<p>A bunch of text</p>
			</div>
			<div class="modal-footer">
				<a href="#!" class="modal-close waves-effect waves-green btn-flat">Agree</a>
			</div>
		</div>

		<script type="text/javascript" src="js/materialize.min.js"></script>
	</body>
</html>