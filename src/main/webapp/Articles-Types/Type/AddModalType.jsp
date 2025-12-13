<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%
	Employe profil = (Employe) session.getAttribute("login_profil"); 
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
    	<link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
		<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalType.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
		<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<title>Création d'un nouveau type</title>
		<script type="text/javascript">
			document.addEventListener("pagehide", function() {
	            closeEmployeWebSocket();
	        });
    	</script>
	</head>
	<body>
		<header>
			<image src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
			<h1>Gestion de Stock IT - Ajouter un nouveau type d'article</h1>
			<a><%= profil.getNomPrenom() %></a>
		</header>
		<main>
			<form id="addForm_type" onsubmit="setForm(event, 'Ajout', 'Type')">
				<fieldset>
					<legend>Informations sur le nouveau type d'article</legend>
					<div>
						<div>
							<label>Nom: <input type="text" name="nom_type"></label>
							<span id="error_nom_type"></span>
						</div>
						<div>
							<label>Description: <textarea name="description_type"></textarea> </label>
							<span id="error_description_type"></span>
						</div>
					</div>
				</fieldset>
				<input type="submit" class="submit_type btn" value="Confirmer">
		   </form>
		</main>
		<footer>
			<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
		</footer>
	</body>
</html>