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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/Login.css?v=<%= System.currentTimeMillis() %>"  type="text/css"/>
    <script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
    <title>Login</title>
</head>
<body>

    <form id="loginForm_employe" onsubmit="setForm(event, null, 'Employe', 'Login')">
        <fieldset>
            <legend><h1>Se connecter</h1></legend>
            <label for="email_matricule">
                <span>Matricule de l'utilisateur: </span><input type="text" id="matricule" name="matricule" value="<%= (e != null) ? e.getMatricule() : "" %>" >
                <span>Email de l'utilisateur: </span><input type="email" id="email" name="email" value="<%= (e != null) ? e.getEmail() : "" %>" >
                <span id="error_email_matricule_login"></span>
            </label>
            <label for="mot_de_passe">
                <span>Mot de passe: </span><input type="password" id="mot_de_passe" name="mot_de_passe" value="<%= (e != null) ? e.getMot_de_passe() : "" %>" required>
                <span id="error_mot_de_passe_login"></span>
            </label>
            <input type="submit" id="submit" class="submit_login" value="Se connecter" <%= !activite ? "disabled" : "" %> >
        </fieldset>
    </form>
</body>
</html>