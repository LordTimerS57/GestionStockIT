<%@ page import="java.util.Objects" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%
    String ctx = request.getContextPath();
    String uri = request.getServletPath();
    String contentPage = (String) request.getAttribute("content");
    String role = (String) session.getAttribute("login_role");
    Employe employe = (Employe) session.getAttribute("login_profil");
%>
<head>
    <meta charset="UTF-8">
    <title>Gestion de Stock IT (SPAT)</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilArtType.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilEmploye.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilFlux.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilFournisseur.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/Dashboard.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/Profile.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLMD/CDyS38ZiuI0gGllKwZtQc0/J37I/YmK0rD+M4P/5a+2sD08i8+D+d+g+" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%=System.currentTimeMillis()%>" defer></script>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const ctx = '<%= ctx %>'; // Récupère le chemin de contexte
            const currentMatricule = '<%= employe.getMatricule() %>';
            const currentRoleInt = '<%= employe.getRoleInt() %>'; // Utilisation du rôle entier pour le WS Employe

            if (currentMatricule && currentRoleInt) {
                initEmployeWebSocket(ctx, currentMatricule, currentRoleInt);
            }
        });
    </script>
</head>
<body>

<header>
    <h1>Gestion Stock IT - Tableau de Bord</h1>

    <div>
        <button
                onclick="logOut('<%= ctx %>',this)"
                data-email="<%=employe.getEmail()%>"
                data-mot_de_passe="<%=employe.getMot_de_passe()%>"
                data-matricule="<%=employe.getMatricule()%>"

        <%-- STYLE INLINE SUPPRIMÉ, GÉRÉ PAR dashboard.css --%>
        >
            Se déconnecter
        </button>
    </div>
</header>

<main>

    <nav>
        <a href="<%= ctx %>/Profil" class="<%= "/Profil".equals(uri) ? "active" : "" %>">
            <i class="fas fa-user"></i> Profil
        </a>
        <a href="<%= ctx %>/Articles-Types" class="<%= "/Articles-Types".equals(uri) ? "active" : "" %>">
            <i class="fas fa-box-open"></i> Articles et Types
        </a>

        <% if(Objects.equals(role, "Administrateur") || Objects.equals(role, "Super Administrateur")) { %>
        <a href="<%= ctx %>/Employes" class="<%= "/Employes".equals(uri) ? "active" : "" %>">
            <i class="fas fa-users"></i> Employés
        </a>
        <a href="<%= ctx %>/Fournisseurs" class="<%= "/Fournisseurs".equals(uri) ? "active" : "" %>">
            <i class="fas fa-truck"></i> Fournisseurs
        </a>
        <% } %>

        <a href="<%= ctx %>/Mouvements" class="<%= "/Mouvements".equals(uri) ? "active" : "" %>">
            <i class="fas fa-exchange-alt"></i> Mouvements d'articles
        </a>
        <a href="<%= ctx %>/Chatbot" class="<%= "/Chatbot".equals(uri) ? "active" : "" %>">
            <i class="fas fa-robot"></i> Chatbot
        </a>
    </nav>

    <div class="content">
        <jsp:include page="<%= contentPage %>" />
    </div>

</main>
<footer>
    <p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
</footer>
</body>
</html>
