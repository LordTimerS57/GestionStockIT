<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%
	List<TypeArticle> data = (List<TypeArticle>) request.getAttribute("types");
	Employe profil = (Employe) session.getAttribute("login_profil"); 
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
    	<link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
		<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalArticle.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
		<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<title>Création d'un nouvel article</title>
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
			<h1>Gestion de Stock IT - Ajouter un nouvel article</h1>
			<a><%= profil.getNomPrenom() %></a>
		</header>
		<main>
		   <form id="addForm_article" onsubmit="setForm(event, 'Ajout', 'Article', null)">
			<fieldset>
				<legend>Informations sur le nouvel article</legend>
				<div>
					<div>
						<label>Nom: <input type="text" name="nom_article"></label>
						<span id="error_nom_article"></span>
					</div>
					<div>
						<label>
							Description: <textarea name="description_article"></textarea>
						</label>
						<span id="error_description_article"></span>
					</div>
				</div>
			</fieldset>
			<fieldset>
				<legend>Information sur son type</legend>
				<div>
					<% if(!data.isEmpty()) { %>
					<label>
						Type
						<select name="type_article">
								<option value="" selected disabled> Veuiller choisir le type d'article </option>
							<% for (TypeArticle t : data ) { %>
								<option value="<%= t.getTag_type() %>">
									<%= t.getNom_type() %>
								</option>
							<% } %>
						</select>
					</label>
					<% } else { %>
					<div>
						<p>Aucun type trouvé</p>
						<a href="<%=request.getContextPath()%>/Types/Creation">Veuiller créer un nouveau type d'article</a>
					</div>
					<% } %>
					<span id="error_type_article"></span>
				</div>
			</fieldset>
			<div>
            	<input type="submit" class="submit_article btn" value="Confirmer l'action">
            	<input type="button" class="cancel_article btn" value="Retourner au tableau de bord" onClick="closeForm()">
            </div>
		   </form>
	   	</main>
   		<footer>
		    <p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
		</footer>
	</body>
</html>