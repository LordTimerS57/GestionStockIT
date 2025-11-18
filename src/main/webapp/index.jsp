<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Page d'accueil</title>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/WelcomePage.css?v=<%= System.currentTimeMillis() %>"  type="text/css"/>
	<script src="JS/Handle.js?v=<%=System.currentTimeMillis()%>" defer></script>
</head>
<body>

<header>
	<h1>Bienvenue sur la page d'accueil de l'application sur la gestion du stock IT du SPAT</h1>
</header>

<main>

	<section>
		<h2>Présentation et Fonctionnalités Clés</h2>
		<p>
			Cette plateforme est dédiée à la **gestion centralisée du stock informatique** du SPAT.
			Elle vous permet de suivre l'état, l'affectation et le cycle de vie complet de tous les équipements IT.
			Voici un aperçu des fonctionnalités principales que vous pourrez gérer :
		</p>

		<ul>
			<li>**Les Employés** : Suivi des comptes employés et des employés acteurs aux mouvements des stocks.</li>
			<li>**Les Articles** : Catalogue détaillé de tous les équipements en stock.</li>
			<li>**Les Types d'Articles** : Classification et catégorisation précises des articles (ex. : ordinateurs portables, moniteurs, serveurs).</li>
			<li>**Les Entrées / Sorties (Stock)** : Enregistrement, recherche, historique, rapports sur Excel.</li>
			<li>**Les Fournisseurs** : Suivi </li>
			<!--
			<li>**Chatbot** : Assistant virtuel pour des requêtes rapides et de l'aide contextuelle.</li>
			-->
		</ul>

	</section>

	<section>
		<h2>Pour continuer vous pouvez choisir entre</h2>
		<div class="actions">
			<a href="<%= request.getContextPath() %>/Connexion">Se connecter</a>
			<a href="<%= request.getContextPath() %>/CreationCompte">Créer un compte</a>
		</div>
	</section>

</main>

<footer>
	<p>&copy; 2025 Gestion de Stock IT. Tous droits réservés.</p>
</footer>

</body>
</html>