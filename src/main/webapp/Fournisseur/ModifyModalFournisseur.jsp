
<%@ page import="com.gestion_stock_it.Fournisseur.Fournisseur" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 29/10/2025
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Fournisseur f = (Fournisseur) request.getAttribute("fournisseur");
    Employe profil = (Employe) session.getAttribute("login_profil");
%>
<html>
<head>

    <meta charset="UTF-8">
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/IMAGES/logo_spat.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalFournisseur.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <title>Title</title>
    <script>
		document.addEventListener("pagehide", function() {
            closeEmployeWebSocket();
        });
	</script>
</head>
<body>
	<header>
		<image src="<%= request.getContextPath() %>/IMAGES/logo_spat.png" alt="Logo SPAT" class="logo"/>
		<h1>Gestion de Stock IT - Modifier les informations d'un fournisseur</h1>
		<a><%= profil.getNomPrenom() %></a>
	</header>
	<main>
		<form id="modifyForm_fournisseur" onsubmit="setForm(event, 'Modification', 'Fournisseur', null)">
		    <fieldset>
		        <legend>Modifier les informations du fournisseur</legend>
		        <label>
		            NIF: <input type="text" name="tag_fournisseur" value="<%=f.getTag_fournisseur()%>"> <input type="hidden" name="old_tag_fournisseur" value="<%=f.getTag_fournisseur()%>">
		            <span id="error_tag_fournisseur"></span>
		        </label>
		        <label>
		            Raison sociale (ou nom et prénoms): <textarea name="raison_sociale"><%=f.getRaison_sociale()%></textarea>
		            <span id="error_raison_sociale"></span>
		        </label>
		        <label>
		            Email: <input type="email" name="email_fournisseur" value="<%=f.getEmail_fournisseur()%>">
		            <span id="error_email_fournisseur"></span>
		        </label>
		        <label>
		            Téléphone: <input type="text" name="telephone_fournisseur" value="<%=f.getTelephone_fournisseur()%>">
		            <span id="error_tel_fournisseur"></span>
		        </label>
		    </fieldset>
		    <input type="submit" class="submit_fournisseur btn" value="Confirmer">
		</form>
	</main>
	<footer>
		<p>&copy; <%= java.time.Year.now() %> Gestion Stock IT. Tous droits réservés.</p>
	</footer>
</body>
</html>