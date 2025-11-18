<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 29/10/2025
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalFournisseur.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
    <script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
</head>
<body>
    <form id="addForm_fournisseur" onsubmit="setForm(event, 'Ajout', 'Fournisseur', null);">
        <fieldset>
            <legend>Nouveau Fournisseur</legend>
            <label>
                NIF: <input type="text" name="tag_fournisseur">
                <span id="error_tag_fournisseur"></span>
            </label>
            <label>
                Raison sociale (ou nom et prénoms): <textarea name="raison_sociale"></textarea>
                <span id="error_raison_social"></span>
            </label>
            <label>
                Email: <input type="email" name="email_fournisseur">
                <span id="error_email_fournisseur"></span>
            </label>
            <label>
                Téléphone: <input type="text" name="telephone_fournisseur">
                <span id="error_tel_fournisseur"></span>
            </label>
        </fieldset>
        <input type="submit" class="submit_fournisseur btn" value="Confirmer">
    </form>
</body>
</html>
