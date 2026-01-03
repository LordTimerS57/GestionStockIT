<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Page d'accueil - Gestion Stock IT SPAT</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/WelcomePage.css?v=<%= System.currentTimeMillis() %>" type="text/css"/>
    <script src="JS/Handle.js?v=<%=System.currentTimeMillis()%>" defer></script>
</head>
<body>

<img src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="" class="logo-background">

<header>
    <h1>Bienvenue sur la gestion du stock informatique du SPAT</h1>
</header>

<main>
    <section class="presentation">
        <h2>Présentation et Fonctionnalités Clés</h2>
        <p>
            Cette plateforme est dédiée à la <span>gestion centralisée du stock informatique</span> du SPAT. 
            Cliquez sur les rubriques ci-dessous pour découvrir les détails :
        </p>

        <div class="feature-accordion">
            <details>
                <summary><span>Les Employés</span></summary>
                <p>Suivi complet des comptes utilisateurs.</p>
            </details>

            <details>
                <summary><span>Les Articles</span></summary>
                <p>Catalogue détaillé de l'inventaire physique.</p>
            </details>

            <details>
                <summary><span>Les Types d'Articles</span></summary>
                <p>Classification intelligente par catégories : ordinateurs, moniteurs, serveurs et consommables.</p>
            </details>

            <details>
                <summary><span>Les Entrées / Sorties</span></summary>
                <p>Enregistrement des flux, historique des mouvements et rapports automatisés vers Excel.</p>
            </details>

            <details>
                <summary><span>Les Fournisseurs</span></summary>
                <p>Répertoire des partenaires commerciaux.</p>
            </details>

            <details>
                <summary><span>Chatbot Assistant</span></summary>
                <p>Assistant virtuel intelligent pour répondre à vos questions sur les stocks en temps réel.</p>
            </details>
        </div>
    </section>

    <section class="auth-box">
        <h2>Accès au portail</h2>
        <p>Veuillez vous identifier pour accéder à l'interface de gestion.</p>
        <div class="actions">
            <a href="<%= request.getContextPath() %>/Connexion" class="btn-login">Se connecter</a>
            <a href="<%= request.getContextPath() %>/CreationCompte" class="btn-register">Créer un compte</a>
        </div>
    </section>
</main>

<footer>
    <p>&copy; <%= java.time.Year.now() %> SPAT - Gestion de Stock IT. Tous droits réservés.</p>
</footer>

</body>
</html>