<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
		<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalEmploye.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
		<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<script>
		document.addEventListener("DOMContentLoaded", () => {
			const form = document.getElementById("addForm_employe");
			if (!form) return;

	        const toggleButton1 = document.getElementById('btn_toggle_password1');
	        
	        const toggleButton2 = document.getElementById('btn_toggle_password2');
			
			function togglePasswordVisibility(e, input) {
	            e.preventDefault();
	            e.stopPropagation();
	            const iconElement = e.currentTarget.querySelector('i');
	            
	            if (input.type === "password") {
	                input.type = "text";
	                iconElement.classList.remove('fa-eye-slash');
	                iconElement.classList.add('fa-eye');
	            } else {
	                input.type = "password";
	                iconElement.classList.remove('fa-eye');
	                iconElement.classList.add('fa-eye-slash');
	            }
	        }
			
			if(toggleButton1) { toggleButton1.addEventListener('click', () => { togglePasswordVisibility(event, document.getElementById('mot_de_passe')) }); }

			if(toggleButton2) { toggleButton2.addEventListener('click', () => { togglePasswordVisibility(event, document.getElementById('mot_de_passe confirm')) }); }
			
			
		});
	</script>
		<title>Créer un compte</title>
	</head>
	<body>
		<header>
			<image src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
	    	<h1>Gestion Stock IT - Créer un compte</h1>
		</header>
		<main>
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
						
					</div>
				</fieldset>
				<fieldset class="connection-info">
					<legend>Information sur la connexion de votre compte (1re partie)</legend>
					<div>
						<label>Email: <input type="email" name="email"></label>
						<span id="error_email"></span>
					</div>
				</fieldset>
				<fieldset class="password-info">
					<legend>Information sur la connexion de votre compte (2ème partie)</legend>
					<div>
						<div>
							<label>
								Veuillez saisir votre mot de passe 
								<div class="password-wrapper"> 
									<input type="password" id="mot_de_passe" oninput="testMessage('mot_de_passe','Employe','Ajout')">
									<button type="button" id="btn_toggle_password1">
							            <i class="fa-solid fa-eye-slash" id="togglePassword"></i>
							        </button>
						        </div>
							</label>
						</div>
						<div>
							<label>
								Confirmer votre mot de passe 
								<div class="password-wrapper"> 
									<input type="password" name="mot_de_passe" id="mot_de_passe confirm" oninput="testMessage('mot_de_passe','Employe','Ajout')">
									<button type="button" id="btn_toggle_password2">
							            <i class="fa-solid fa-eye-slash" id="togglePassword"></i>
							        </button>
								</div>
							</label>
						</div>
						<div>
							<span id="error_mot_de_passe"></span>
						</div>
					</div>
					<div>
						<input type="submit" class="submit_employe btn" value="Créer">
						<input type="button" class="cancel_employe btn" value="Annuler" onClick="closeForm()">
					</div>
				</fieldset>
			</form>
		</main>
		<footer>
			<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
		</footer>
	</body>
</html>