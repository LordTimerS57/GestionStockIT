<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
		<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalEmploye.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
		<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<title>Créer un compte</title>
		<script>
			document.addEventListener("pagehide", function() {
	            closeEmployeWebSocket();
	        });
		</script>
	</head>
	<body>
		<h1>Ajouter un Employé</h1>
		<form id="addForm_employe" onsubmit="setForm(event, 'Ajout', 'Employe', 'Ajout')">
			<fieldset class="personnel-info">
				<legend>Informations personnels</legend>
				<div>
					<div>
						<label>Nom: <input type="text" name="nom"></label>
						<span id="error_nom"></span>
					</div>
					<div>
						<label>Prénoms: <input type="text" name="prenom"></label>
						<span id="error_prenom"></span>
					</div>
					<div>
						<label>Adresse: <textarea name="adresse"></textarea></label>
						<span id="error_adresse"></span>
					</div>
					<div>
						<label>Téléphone <input type="text" name="telephone" ></label>
						<span id="error_tel"></span>
					</div>
					<div>
						<label>Date de naissance: <input type="date" name="date_de_naissance"></label>
						<span id="error_date_naissance"></span>
					</div>
				</div>
				<div>
					<input type="button" value="Suivant">
				</div>
			</fieldset>
			<fieldset class="connection-info">
				<legend>Information sur la connexion de votre compte (1re partie)</legend>
				<div>
					<label>Email: <input type="email" name="email"></label>
					<span id="error_email"></span>
				</div>
				<div>
					<input type="button" value="Précédent">
					<input type="button" value="Suivant">
				</div>
			</fieldset>
			<fieldset class="password-info">
				<legend>Information sur la connexion de votre compte (2ème partie)</legend>
				<div>
					<div>
						<label>
							Veuillez saisir votre mot de passe <input type="password" id="mot_de_passe" oninput="testMessage('mot_de_passe','Employe','Ajout')">
						</label>
					</div>
					<div>
						<label>
							Confirmer votre mot de passe <input type="password" name="mot_de_passe" id="mot_de_passe confirm" oninput="testMessage('mot_de_passe','Employe','Ajout')">
						</label>
					</div>
					<div>
						<span id="error_mot_de_passe"></span>
					</div>
				</div>
				<div>
					<input type="button" value="Précédent">
					<input type="submit" class="submit_employe btn" value="Créer">
				</div>
			</fieldset>
		</form>
	</body>
</html>