<%@ page import="com.gestion_stock_it.ArtType.Article.Article" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%
	Article a = (Article) request.getAttribute("article");
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
		<title>Modification d'article</title>
		<script type="text/javascript">
			document.addEventListener("pagehide", function() {
	            closeEmployeWebSocket();
	        });
    	</script>
	</head>
	<body>
		<header>
			<image src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
			<h1>Gestion Stock IT - Modifier les informations d'un article</h1>
			<a><%= profil.getNomPrenom() %></a>
		</header>
		<main>
			<form id="modifyForm_article" onsubmit="setForm(event, 'Modification', 'Article', null)">
				<fieldset>
					<legend>Informations sur l'article</legend>
					<div>
						<input type="hidden" name="tag_article" value="<%= a.getTag_article() %>">
						<div>
							<label>Nom: <input type="text" name="nom_article" value="<%= a.getNom_article() %>"> </label>
							<span id="error_nom_article"></span>
						</div>
						<div>
							<label>Description: <textarea name="description_article"><%= a.getDescription_article() %></textarea></label>
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
								<option value="" disabled> Veuiller choisir le type d'article </option>
								<% for (TypeArticle t : data ) { %>
								<option value="<%= t.getTag_type() %>" <%= (t.getTag_type().equals(a.getType_article().getTag_type())) ? "selected" : "" %>>
									<%= t.getNom_type() %>
								</option>
								<% } %>
							</select>
						</label>
						<% } else { %>
						<div>
							<p>Aucun type trouvé</p>
							<a href="<%=request.getContextPath()%>/Articles-Types/Articles-Types/Types/Creation">Veuiller créer un nouveau type d'article</a>
						</div>
						<% } %>
						<span id="error_type_article"></span>
					</div>
				</fieldset>
				<input type="submit" class="submit_article btn" value="Confirmer">
			</form>
		</main>
		<footer>
			<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
		</footer>
	</body>
</html>