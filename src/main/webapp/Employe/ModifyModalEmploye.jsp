<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%
	Employe emp = (Employe) session.getAttribute("login_profil");
	String section = (String) session.getAttribute("section");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalEmploye.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
	<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<title>Modification du compte</title>
	<script>
		document.addEventListener("DOMContentLoaded", () => {
			const form = document.getElementById("modifyForm_employe");
			if (!form) return;

			const section = form.querySelector('input[name="section"]').value;
			const submitButtons = form.querySelectorAll(".submit_employe");
			
			initEmployeWebSocket('<%= request.getContextPath() %>', null, null);

			submitButtons.forEach(btn => {
				btn.addEventListener("click", (e) => handleSubmitWithPasswordDialog(e, section));
			});
		});
		document.addEventListener("pagehide", function(event) {
	        const contextPath = '<%= request.getContextPath() %>';
	        if (event.persisted === false) {
	            closeEmployeWebSocket(true, contextPath);
	            console.log("Sortie définitive détectée par pagehide (persisted: false). SendBeacon envoyé.");
	        } else {
	            closeEmployeWebSocket(false, contextPath);
	            console.log("Navigation interne détectée par pagehide (persisted: true). Connexion maintenue.");
	        }
	    });
	</script>
</head>
<body>
	<header>
		<image src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
		<h1>Gestion de Stock IT - Modifier les informations de votre compte</h1>
		<a><%= emp.getNomPrenom() %></a>
	</header>
	<main>
		<form id="modifyForm_employe">
			<input type="hidden" name="matricule"  value="<%= emp.getMatricule() %>">
			<input type="hidden" name="section" value="<%= section %>">
			<div>
				<% if ("1".equals(section) || "2".equals(section)) { %>
				<dialog id="passwordDialog">
					<form method="dialog">
						<p>Veuillez confirmer votre mot de passe actuel :</p>
						<label>
							<input type="password" name="mot_de_passe" id="motDePasseDialog" required>
							<span id="error_mot_de_passe_login"></span>
						</label>
						<menu>
							<button id="confirmBtn" type="submit">Confirmer</button>
							<button id="cancelBtn">Annuler</button>
						</menu>
					</form>
				</dialog>
				<% } %>
				<% switch (section) {
					case "1": %>
				<fieldset class="personnel-info">
					<legend>Informations personnels</legend>
					<div>
						<div>
							<label>Nom: <input type="text" value="<%= emp.getNom() %>" name="nom"></label>
							<span id="error_nom"></span>
						</div>
						<div>
							<label>Prénoms: <input type="text"  value="<%= emp.getPrenom() %>" name="prenom"></label>
							<span id="error_prenom"></span>
						</div>
						<div>
							<label>Adresse: <textarea name="adresse"><%= emp.getAdresse() %></textarea></label>
							<span id="error_adresse"></span>
						</div>
						<div>
							<label>Date de naissance: <input type="date" value="<%= emp.getDate_de_naissance() %>" name="date_de_naissance"></label>
							<span id="error_date_naissance"></span>
						</div>
						<div>
							<label>Telephone: <input type="text" value="<%= emp.getTelephone() %>" name="telephone"> </label>
							<span id="error_tel"></span>
						</div>
						<div>
				        	<input type="submit" class="submit_employe btn" value="Confirmer l'action">
				        	<input type="button" class="cancel_employe btn" value="Retourner au profil" onClick="closeForm()">
				        </div>
					</div>
				</fieldset>
				<%
						break;
		
					case "2":
				%>
				<fieldset class="connection-info">
					<legend>Informations sur la connexion de votre compte</legend>
					<div>
						<div>
							<label>Email: <input type="hidden" value="<%= emp.getEmail() %>" name="email"><input type="email" value="<%= emp.getEmail() %>" name="nouvel_email"></label>
							<span id="error_email"></span>
						</div>
						<div>
				        	<input type="submit" class="submit_employe btn" value="Confirmer l'action">
				        	<input type="button" class="cancel_employe btn" value="Retourner au profil" onClick="closeForm()">
				        </div>
					</div>
				</fieldset>
				<%
						break;
		
					case "3":
				%>
				<fieldset class="password-info">
					<legend>Veuillez saisir votre nouveau mot de passe</legend>
					<div>
						<div>
							<label>Votre nouveau mot de passe <input type="password" id="nouveau_mot_de_passe" oninput="testMessage('mot_de_passe','Employe','Modification')"></label>
						</div>
						<div>
							<label>Confirmer votre mot de passe <input type="password" name="nouveau_mot_de_passe" id="nouveau_mot_de_passe confirm" oninput="testMessage('mot_de_passe','Employe','Modification')"></label>
						</div>
						<div>
							<span id="error_mot_de_passe"></span>
						</div>
						<div>
				        	<input type="submit" class="submit_employe btn" value="Confirmer l'action">
				        	<input type="button" class="cancel_employe btn" value="Retourner au profil" onClick="closeForm()">
				        </div>
					</div>
				</fieldset>
				<%
							break;
		
						default: break;
					} %>
			</div>
		</form>
	</main>
	<footer>
		<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
	</footer>
</body>
</html>