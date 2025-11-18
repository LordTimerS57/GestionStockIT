<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page import="com.gestion_stock_it.Employe.EmployeDataController" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.ArtType.Article.Article" %>
<%@ page import="com.gestion_stock_it.ArtType.Article.ArticleDataController" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	List<Article> articles = (List<Article>) request.getAttribute("articles_recherches");
	List<Employe> destinataires = (List<Employe>) request.getAttribute("destinataires_recherches");
	Employe profil = (Employe) session.getAttribute("login_profil");
%>
<!DOCTYPE html>
<html>
<head>
	<title>Sortir un article</title>
	<meta charset="UTF-8">
	<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalFlux.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
	<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
</head>
<body>
<h1>Sortir un article</h1>
<form id="addForm_sortie" onsubmit="setForm(event, 'Ajout', 'Flux', 'Sortie');">
	<fieldset class="article-info">
		<legend>Informations sur l'article</legend>
			<div class="search-article">
				<label>
					Nom: <input type="search" id="nom_article" oninput="searchSortie('article')"> <input type="hidden" id="tag_article" name="tag_article">
				</label>
			</div>
			<div id="result_article">
				<% if (!articles.isEmpty()) { %>
				<table>
					<tbody>
					<%	for (Article article : articles) {%>
					<tr>
						<td>
							<button
									onclick="setDetails(event, 'Show', 'Article', this)"
									data-nom_article="<%=article.getNom_article()%>"
									data-type_article="<%=article.getType_article().getNom_type()%>"
									data-stock_article="<%=article.getNombre_article()%>"
									data-tag_article="<%=article.getTag_article()%>">
								Voir les détails : <%=article.getNom_article()%>
							</button>
						</td>
					</tr>
					<% } %>
					</tbody>
				</table>
				<dialog id="dialog_article">
					<fieldset>
						<legend>Details sur l'article <span id="dialog_nom_article"></span></legend>
						<p id="dialog_type_article"></p>
						<p id="dialog_nombre_article"></p>
						<input type="hidden" id="dialog_tag_article">
						<div style="margin-top:10px;">
							<button onclick="setDetails(event, 'Close', 'Article', null)">Fermer</button>
							<button
									onclick="setTag(event, 'Entree', 'Article', document.getElementById('dialog_tag_article').value);
																	 setDetails(event, 'Close', 'Article', null);">
								Choisir
							</button>
						</div>
					</fieldset>
				</dialog>
				<% } else { %>
				<p>Aucun article trouvé</p>
				<% } %>
				<span id="error_tag_article"></span>
			</div>
			<div class="number-article">
				<% if (!articles.isEmpty()) { %>
				<label>
					Nombre à mouvoir: <input type="number" id="nombre_article_deplace" name="nombre_article_deplace">
				</label>
				<span id="error_nombre_article"></span>
				<% } %>
			</div>
			<!--
			<div>
				<label>
					Spécifications:
					<textarea id="specifications" name="specifications"></textarea>
				</label>
			</div>
			-->
	</fieldset>
	<fieldset class="expediteur-info">
		<legend>Informations sur l'expéditeur</legend>
		<div>
			<input type="hidden" id="expediteur" name="expediteur" value="<%=profil.getMatricule()%>">

			<button onclick="setDetails(event, 'Show', 'Expediteur-Administrateur', null)"><%=profil.getNomPrenom()%></button>

			<dialog id="dialog_expediteur">
				<fieldset>
					<legend> Détails de l'employé <span id="dialog_expediteur_nom_complet"><%=profil.getNomPrenom()%></span></legend>
					<p id="dialog_expediteur_id">Matricule: <%=profil.getMatricule()%></p>
					<p id="dialog_expediteur_role">Role: <%=profil.getRole()%></p>
					<div style="margin-top:10px;">
						<button onclick="setDetails(event, 'Close', 'Expediteur-Administrateur', null)">Fermer</button>
					</div>
				</fieldset>
			</dialog>
			<span id="error_expediteur"></span>
		</div>
	</fieldset>
	<fieldset class="destinataire-info">
		<legend>Informations sur la destinataire</legend>
			<div class="search-employe">
				<label>
					Nom ou prénom ou matricule: <input type="search" id="destinataire_search" oninput="searchSortie('destinataire')"> <input type="hidden" id="destinataire"  name="destinataire">
				</label>
			</div>
			<div id="result_destinataire">
				<% if (!destinataires.isEmpty()) { %>
				<% for (Employe destinataire : destinataires) { %>
				<button
						onclick="setDetails(event, 'Show', 'Destinataire-Employe', this)"
						data-nom="<%=destinataire.getNom()%>"
						data-prenom="<%=destinataire.getPrenom()%>"
						data-matricule="<%=destinataire.getMatricule()%>"
						data-role="<%=destinataire.getRole()%>">
					<%=destinataire.getNomPrenom()%>
				</button>
				<% } %>

				<!-- Dialogue unique partagé pour tous les destinataires/fournisseurs -->
				<dialog id="dialog_destinataire">
					<fieldset>
						<legend> Détails de l'employé <span id="dialog_destinataire_nom_complet"></span></legend>
						<p id="dialog_destinataire_id"></p>
						<p id="dialog_destinataire_role"></p>
						<input type="hidden" id="dialog_destinataire_matricule">
						<div style="margin-top:10px;">
							<button onclick="setDetails(event, 'Close', 'Destinataire-Employe', null)">Fermer</button>
							<button onclick="setTag(event, 'Sortie', 'Destinataire', document.getElementById('dialog_destinataire_matricule').value);
													 setDetails(event, 'Close', 'Destinataire-Employe', null);">
								Choisir
							</button>
						</div>
					</fieldset>
				</dialog>

				<% } else { %>
				<p>Aucun destinataire trouvé</p>
				<% } %>
				<span id="error_destinataire"></span>
			</div>
	</fieldset>
	<input type="submit" class="submit_sortie btn" value="Confirmer">
</form>
</body>
</html>
