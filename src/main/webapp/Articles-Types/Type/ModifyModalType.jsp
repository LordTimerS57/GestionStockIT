<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="com.gestion_stock_it.ArtType.Article.ArticleDataController" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%
	TypeArticle t = (TypeArticle) request.getAttribute("type");
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
	<title>Modification d'un type d'article</title>
	<script type="text/javascript">
		document.addEventListener("DOMContentLoaded", () => {		
			initEmployeWebSocket('<%= request.getContextPath() %>', null, null);
		});
		document.addEventListener("pagehide", function() {
            closeEmployeWebSocket();
        });
   	</script>
</head>
<body>
	<header>
		<image src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
		<h1>Gestion de Stock IT - Modifier les informations d'un type d'article</h1>
		<a><%= profil.getNomPrenom() %></a>
	</header>
	<main>
		
		<form id="modifyForm_type" onsubmit="setForm(event, 'Modification', 'Type', null)">
	    	<fieldset>
				<legend>Informations sur le type d'article</legend>
				<div>
					<input type="hidden" value="<%= t.getTag_type() %>" name="tag_type">
					<div>
						<label>Nom: <input type="text" name="nom_type" value="<%= t.getNom_type() %>"></label>
						<span id="error_nom_type"></span>
					</div>
					<div>
						<label>Description: <textarea name="description_type"> <%= t.getDescription_type() %> </textarea></label>
						<span id="error_description_type"></span>
					</div>
					<div>
			        	<input type="submit" class="submit_type btn" value="Confirmer l'action">
			        	<input type="button" class="cancel_type btn" value="Retourner au tableau de bord" onClick="closeForm()">
			        </div>
				</div>
			</fieldset>
	    </form>
    </main>
    <footer>
		<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
	</footer>
</body>