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
    
    <link rel="stylesheet" 
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" 
    integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" 
    crossorigin="anonymous" 
    referrerpolicy="no-referrer" />
    <link rel="icon" type="image/png" href="<%= ctx %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilArtType.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilEmploye.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilFlux.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/AcceuilFournisseur.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ChatBot.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/Dashboard.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/Profile.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    
    <script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%=System.currentTimeMillis()%>" defer></script>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const ctx = '<%= ctx %>';
            const currentMatricule = '<%= employe.getMatricule() %>';
            const currentRoleInt = '<%= employe.getRoleInt() %>';

            if (currentMatricule && currentRoleInt) {
                initEmployeWebSocket(ctx, currentMatricule, currentRoleInt);
            }
            
            // --- GESTION DE TOUS LES MENUS DÉROULANTS ---
            const dropdownMenus = document.querySelectorAll('.dropdown-menu');
            
            dropdownMenus.forEach(dropdownMenu => {
                const dropdownToggle = dropdownMenu.querySelector('.dropdown-toggle');
                
                if (dropdownToggle) {
                    dropdownToggle.addEventListener('click', function(e) {
                        e.preventDefault(); 
                        dropdownMenu.classList.toggle('open');
                    });
                    const activeSubLink = dropdownMenu.querySelector('.dropdown-content a.active-sub');
                    if (activeSubLink) {
                        dropdownMenu.classList.add('open');
                    }
                }
            });
        });
        document.addEventListener("pagehide", function() {
            closeEmployeWebSocket();
        });
    </script>
</head>

<body>

	<header>
		<image src="<%= ctx %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
	    <h1>Gestion Stock IT - Tableau de Bord</h1>
	
	    <div>
	    	<a href="<%= ctx %>/Profil" 
	           onclick="navigateTo(this.href); return false;"
	           class="<%= "/Profil".equals(uri) ? "active" : "" %>">
	           <%= employe.getNomPrenom() %>
	        </a>
	        <button
	                onclick="logOut('<%= ctx %>', '<%= employe.getMatricule() %>')"
	                data-matricule="<%=employe.getMatricule()%>"
	        >
	            Se déconnecter
	        </button>
	    </div>
	</header>
	
	<main>
	
	    <nav>
	    
		    <div class="dropdown-menu"> 
	            <a href="javascript:void(0)" class="dropdown-toggle <%= "/Articles".equals(uri) || "/Types".equals(uri) ? "active" : "" %>">
	                <i class="fas fa-box-open"></i> Articles et Types
	                <i class="fas fa-caret-down dropdown-icon"></i>
	            </a>
	            <ul class="dropdown-content">
	                <li>
	                    <a href="<%= ctx %>/Articles" 
	                       onclick="navigateTo(this.href); return false;"
	                       class="<%= "/Articles".equals(uri) ? "active-sub" : "" %>">
	                        <i class="fas fa-list-alt"></i> Voir les articles
	                    </a>
	                </li>
	                <li>
	                    <a href="<%= ctx %>/Types" 
	                       onclick="navigateTo(this.href); return false;"
	                       class="<%= "/Types".equals(uri) ? "active-sub" : "" %>">
	                        <i class="fas fa-sitemap"></i> Voir les types
	                    </a>
	                </li>
	            </ul>
	        </div>
		    
			<% if(Objects.equals(role, "Administrateur") || Objects.equals(role, "Super Administrateur")) { %>
		        <div class="dropdown-menu"> 
		            <a href="<%= ctx %>/Employes" 
	                   onclick="navigateTo(this.href); return false;"
	                   class="<%= "/Employes".equals(uri) ? "active" : "" %>">
		                <i class="fas fa-users"></i> Employés
		            </a>
		        </div>
		        <div class="dropdown-menu"> 
		            <a href="<%= ctx %>/Fournisseurs" 
	                   onclick="navigateTo(this.href); return false;"
	                   class="<%= "/Fournisseurs".equals(uri) ? "active" : "" %>">
		                <i class="fas fa-truck"></i> Fournisseurs
		            </a>
		        </div>
	        <% } %>	
	       
	       	<div class="dropdown-menu">
		       	<a href="javascript:void(0)" class="dropdown-toggle <%= "/Entrees".equals(uri) || "/Sorties".equals(uri) ? "active" : "" %>">
			            <i class="fas fa-exchange-alt"></i> Mouvements d'articles
		                <i class="fas fa-caret-down dropdown-icon"></i>
	        	</a>
	        	<ul class="dropdown-content">
	        		<li>
	                    <a href="<%= ctx %>/Entrees" 
	                       onclick="navigateTo(this.href); return false;"
	                       class="<%= "/Entrees".equals(uri) ? "active-sub" : "" %>">
	                        <i class="fas fa-arrow-down"></i> Voir les entrées
	                    </a>
	                </li>
	                <li>
	                    <a href="<%= ctx %>/Sorties" 
	                       onclick="navigateTo(this.href); return false;"
	                       class="<%= "/Sorties".equals(uri) ? "active-sub" : "" %>">
	                        <i class="fas fa-arrow-up"></i> Voir les sorties
	                    </a>
	                </li>
	        	</ul>
	       	</div>
	       
	        <div class="dropdown-menu">
		        <a href="<%= ctx %>/Chatbot" 
	               onclick="navigateTo(this.href); return false;"
	               class="<%= "/Chatbot".equals(uri) ? "active" : "" %>">
		            <i class="fas fa-robot"></i> Chatbot
		        </a>
	        </div>
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