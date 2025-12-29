<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 29/10/2025
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%
    Employe profil = (Employe) session.getAttribute("login_profil");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Title</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalFournisseur.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
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
		<h1>Gestion de Stock IT - Ajouter un nouveau fournisseur</h1>
		<a><%= profil.getNomPrenom() %></a>
	</header>
	<main>
	    <form id="addForm_fournisseur" onsubmit="setForm(event, 'Ajout', 'Fournisseur', null);">
	        <fieldset>
	            <legend>Informations sur le nouveau Fournisseur</legend>
	            <div>
		            <div>
			            <label>
			                NIF: <input type="text" name="tag_fournisseur">
			                <span id="error_tag_fournisseur"></span>
			            </label>
		            </div>
		            <div>
			            <label>
			                Raison sociale (ou nom et prénoms): <textarea name="raison_sociale"></textarea>
			                <span id="error_raison_social"></span>
			            </label>
		            </div>
		            <div>
			            <label>
			                Email: <input type="email" name="email_fournisseur">
			                <span id="error_email_fournisseur"></span>
			            </label>
		            </div>
		            <div>
			            <label>
			                Téléphone: <input type="text" name="telephone_fournisseur">
			                <span id="error_tel_fournisseur"></span>
			            </label>
		            </div>
		            <div>
			        	<input type="submit" class="submit_fournisseur btn" value="Confirmer l'action">
			        	<input type="button" class="cancel_fournisseur btn" value="Retourner au tableau de bord" onClick="closeForm()">
			        </div>
	        	</div>
	        </fieldset>
	    </form>
    </main>
	<footer>
		<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
	</footer>
</body>
</html>
