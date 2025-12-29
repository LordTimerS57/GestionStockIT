<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%
    Employe e = (Employe) session.getAttribute("employe_connection");
    Boolean activiteAttr = (Boolean) session.getAttribute("activite");
    boolean activite = (activiteAttr != null) ? activiteAttr : true; // false par défaut

%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" 
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" 
    integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" 
    crossorigin="anonymous" 
    referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/Login.css?v=<%= System.currentTimeMillis() %>"  type="text/css"/>
    <script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <title>Login</title>
</head>
<body>
	<div class="user-icon-container">
    	<img src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="user-icon"/>
    </div>
    <form id="loginForm_employe" onsubmit="setForm(event, null, 'Employe', 'Login')">
        <fieldset>
            <legend><h1>Se connecter</h1></legend>
            <label id="content_login" for="email_matricule">
                <label id="check_matricule">
                    <span id="name_matricule"></span><input type="hidden" id="matricule" name="matricule" value="<%= (e != null) ? e.getMatricule() : "" %>" >
                </label>
                <label id="check_email">
                    <span id="name_email">Email de l'utilisateur: </span><input type="email" id="email" name="email" value="<%= (e != null) ? e.getEmail() : "" %>" >
                </label>
                <span id="error_email_matricule_login"></span>
            </label>
            <label for="mot_de_passe" id="content_login">
                <span>Mot de passe: </span>
                <div class="password-wrapper"> 
			        <input type="password" id="mot_de_passe" name="mot_de_passe" value="<%= (e != null) ? e.getMot_de_passe() : "" %>" required>
			        <button type="button" id="btn_toggle_password">
			            <i class="fa-solid fa-eye-slash" id="togglePassword"></i>
			        </button>
			    </div>
                <span id="error_mot_de_passe_login"></span>
            </label>
            <button type="submit" id="submit" class="submit_login" <%= !activite ? "disabled" : "" %> ><i class="fa-solid fa-right-to-bracket"></i> Se connecter</button>

            <label for="check_matricule" id="check_matricule_label">
                <input type="checkbox" id="check" class="checkbox_login" checked>
                <span>J'ai oublié mon matricule</span>
            </label>
            
           	<label for="links_login" id="links_login">
               	<a href="<%= request.getContextPath() %>/" class="link_login">Retour à la page d'accueil</a>
               	<a href="<%= request.getContextPath() %>/CreationCompte" class="link_login">Créer un compte</a>
            </label>
        </fieldset>
    </form>
    
    <script>
		    document.addEventListener('DOMContentLoaded', function() {
		        const checkbox = document.getElementById('check');
		        
		        const matriculeInput = document.getElementById('matricule');
		        const matriculeLabel = document.getElementById('name_matricule');
		        
		        const emailInput = document.getElementById('email');
		        const emailLabel = document.getElementById('name_email');
		        
		        const passwordInput = document.getElementById('mot_de_passe');
		        const toggleButton = document.getElementById('btn_toggle_password');
		
		        // 1. Logique Matricule / Email
		        function updateFields() {
		            if (checkbox.checked) {
		                // Si coché (utiliser Email)
		                emailInput.type = "email";
		                emailLabel.textContent = 'Email de l\'utilisateur: ';
		                emailInput.required = true;
		                
		                matriculeInput.type = "hidden";
		                matriculeInput.required = false;
		                matriculeLabel.textContent = '';
		            } else {
		                // Si non coché (utiliser Matricule)
		                matriculeInput.type = "text";
		                matriculeInput.required = true;
		                matriculeLabel.textContent = 'Matricule de l\'utilisateur: ';
		                
		                emailInput.type = "hidden";
		                emailLabel.textContent = '';
		                emailInput.required = false;
		            }
		        }
		        
		        // 2. Logique Affichage Mot de Passe
		        function togglePasswordVisibility(e) {
		            e.preventDefault();
		            e.stopPropagation();
		            const iconElement = e.currentTarget.querySelector('i');
		            
		            if (passwordInput.type === "password") {
		                passwordInput.type = "text";
		                iconElement.classList.remove('fa-eye-slash');
		                iconElement.classList.add('fa-eye');
		            } else {
		                passwordInput.type = "password";
		                iconElement.classList.remove('fa-eye');
		                iconElement.classList.add('fa-eye-slash');
		            }
		        }
		
		        // Initialisation et Écouteurs d'événements
		        
		        // Corriger l'état initial
		        updateFields(); 
		        
		        // Écouter le changement de la case à cocher
		        checkbox.addEventListener('change', updateFields);
		        
		        // Écouter le clic sur le bouton de bascule du mot de passe
		        toggleButton.addEventListener('click', togglePasswordVisibility);
		    });
		</script>
</body>
</html>