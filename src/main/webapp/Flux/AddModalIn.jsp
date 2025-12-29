<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.ArtType.Article.Article" %>
<%@ page import="com.gestion_stock_it.Fournisseur.Fournisseur" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	List<Article> articles = (List<Article>) request.getAttribute("articles_recherches");
	List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs_recherches");
	Employe profil = (Employe) session.getAttribute("login_profil");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Entrer un article</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalFlux.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
	<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<script>
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
    	<h1>Gestion Stock IT - Entrer un article au stock</h1>
    	<a><%= profil.getNomPrenom() %></a>
	</header>
	<main>
		<form id="addForm_entree" onsubmit="setForm(event, 'Ajout', 'Flux', 'Entree');">
			<fieldset class="article-info">
				<legend>Informations sur l'article</legend>
					<div class="search-article">
						<label>
							Nom: <input type="search" id="nom_article" oninput="searchEntree('article')"> <input type="hidden" id="tag_article" name="tag_article">
						</label>
					</div>
					<div id="result_article">
						<% if (!articles.isEmpty()) { %>
						<button id="choice_article" onclick="setDetails(event, 'Show', 'List-Articles', null)">Veuillez choisir un article parmi <%=  (articles.size() <= 1 ? "le résultat trouvé" : "les "+ articles.size() + " résultats trouvés" ) %> </button>
						<dialog id="dialog_list_articles">
							<fieldset>
								<legend>Veuillez choisir l'article en question</legend>
								<table>
									<tbody>
									<%	for (Article article : articles) {%>
									<tr>
										<td>
											<button
													class="article_info"
													onclick="setDetails(event, 'Show', 'Article', this);"
													data-nom_article="<%=article.getNom_article()%>"
													data-type_article="<%=article.getType_article().getNom_type()%>"
													data-stock_article="<%=article.getStock_article()%>"
													data-tag_article="<%=article.getTag_article()%>">
												Voir les détails : <%=article.getNom_article()%>
											</button>
										</td>
									</tr>
									<% } %>
									</tbody>
								</table>
								<div>
									<button id="cancel_selection_btn" onclick="setDetails(event, 'Close', 'List-Articles', null)">Annuler</button>
								</div>
							</fieldset>
						</dialog>
						<dialog id="dialog_article">
							<fieldset>
								<legend>Details sur l'article <span id="dialog_nom_article"></span></legend>
								<p id="dialog_type_article"></p>
								<p id="dialog_nombre_article"></p>
								<input type="hidden" id="dialog_tag_article">
								<div>
									<button id="select_article_btn" 
											onclick="setTag(event, 'Entree', 'Article', document.getElementById('dialog_tag_article').value, document.getElementById('dialog_nom_article').textContent); 
													 setDetails(event, 'Close', 'Article', null);
										 			 setDetails(event, 'Close', 'List-Articles', null);">
										Choisir
									</button>
									<button id="cancel_selection_btn" onclick="setDetails(event, 'Close', 'Article', null)">Fermer</button>
								</div>
							</fieldset>
						</dialog>
						<span id="selected_article_tag" style="display: none"></span>
						<% } else { %>
						<p>Aucun article trouvé</p>
						<% } %>
						<span id="error_tag_article"></span>
					</div>
					<% if(!articles.isEmpty()){%>
					<div class="number-article">
						<label>
							Nombre à mouvoir: <input type="number" id="nombre_article_deplace" name="nombre_article_deplace">
						</label>
						<span id="error_nombre_article"></span>
					</div>
					<% } %>
			</fieldset>
			<fieldset class="expediteur-info">
				<legend>Informations sur l'expéditeur</legend>
					<div class="search-fournisseur">
						<label>
							Nom: <input type="search" id="raison_sociale" oninput="searchEntree('fournisseur')"> <input type="hidden" id="tag_fournisseur"  name="expediteur">
						</label>
					</div>
					<div id="result_expediteur">
						<% if (!fournisseurs.isEmpty()) { %>
						<button id="choice_expediteur fournisseur" onclick="setDetails(event, 'Show', 'List-Expediteurs', null)">Veuillez choisir l'expéditeur de l'article parmi <%=  (fournisseurs.size() <= 1 ? "le résultat trouvé" : "les "+ fournisseurs.size() + " résultats trouvés" ) %> </button>
						<dialog id="dialog_list_expediteurs">
							<fieldset>
								<legend>Veuillez choisir l'expéditeur en question</legend>
								<table>
									<tbody>
									<%	for (Fournisseur expediteur : fournisseurs) { %>
									<tr>
										<td>
											<button class="expediteur_info"
													onclick="setDetails(event,'Show','Expediteur-Fournisseur', this)"
													data-raison_sociale="<%=expediteur.getRaison_sociale()%>"
													data-email_fournisseur="<%=expediteur.getEmail_fournisseur()%>"
													data-telephone_fournisseur="<%=expediteur.getTelephone_fournisseur()%>"
													data-tag_fournisseur="<%=expediteur.getTag_fournisseur()%>">
												<%=expediteur.getRaison_sociale()%>
											</button>
										</td>
									</tr>
									<% } %>
									</tbody>
								</table>
							</fieldset>
							<div>
								<button id="cancel_selection_btn" onclick="setDetails(event, 'Close', 'List-Expediteurs', null)">Annuler</button>
							</div>
						</dialog>
						<dialog id="dialog_expediteur">
							<fieldset>
								<legend>Détails du fournisseur <span id="dialog_expediteur_raison_sociale"></span></legend>
								<p id="dialog_expediteur_raison_sociale"></p>
								<p id="dialog_expediteur_email"></p>
								<p id="dialog_expediteur_telephone"></p>
								<input type="hidden" id="dialog_expediteur_tag_fournisseur">
							</fieldset>
							<div>
								<button id="select_expediteur_btn"
										onclick="setTag(event,'Entree','Fournisseur', document.getElementById('dialog_expediteur_tag_fournisseur').value, document.getElementById('dialog_expediteur_raison_sociale').textContent);
												 setDetails(event,'Close','Expediteur-Fournisseur', null);
												 setDetails(event, 'Close', 'List-Expediteurs', null);
									">
									Choisir
								</button>
								<button id="cancel_selection_btn" onclick="setDetails(event, 'Close','Expediteur-Fournisseur', null)">Fermer</button>
							</div>
						</dialog>
						<span id="selected_expediteur_tag" style="display: none"></span>
						<% } else { %>
						<p>Aucun fournisseur trouvé</p>
						<% } %>
						<span id="error_expediteur"></span>
					</div>
			</fieldset>
			<div>
			<% if( !fournisseurs.isEmpty() && !articles.isEmpty()) { %>
				<input type="submit" class="submit_entree btn" value="Confirmer">
			<% } %>
			</div>
		</form>
	</main>
	<footer>
	    <p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
    </footer>
</body>
</html>