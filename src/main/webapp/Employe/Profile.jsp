<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page import="com.gestion_stock_it.Employe.EmployeDataController" %>
<%@ page import="java.util.Objects" %>

<%
    Employe profil = (Employe) session.getAttribute("login_profil");
%>

<section class="profile">
    <h4>Bienvenue sur le profil <%= profil.getRole().equalsIgnoreCase("super administrateur") ? "administrateur" : profil.getRole().equalsIgnoreCase("administrateur") ? "sous-administrateur" : "employé"  %> de <%= profil.getNomPrenom() %></h4>
    <fieldset id="details-personnels">
        <legend> Informations personnelles </legend>
        <div>
            <p><span>Nom:</span> <%= profil.getNom() %></p>
            <p><span>Prénoms:</span> <%= profil.getPrenom() %></p>
            <p><span>Adresse:</span> <%= profil.getAdresse() %></p>
            <p><span>Téléphone:</span> <%= profil.getTelephone() %></p>
            <p><span>Date de naissance:</span> <%= profil.getDate_de_naissance_formatter() %></p>
            <a href="<%= request.getContextPath() %>/Employe/SessionModifyEmploye.jsp?section=1"> Modifier vos informations personnelles </a>
        </div>
    </fieldset>
    <fieldset id="details-connexion">
        <legend> Information sur la connexion </legend>
        <div>
            <p><%= profil.getEmail() %></p>
            <ul>
                <li><a href="<%= request.getContextPath() %>/Employe/SessionModifyEmploye.jsp?section=2"> Changer votre email </a></li>
                <li><a href="<%= request.getContextPath() %>/Employe/SessionModifyEmploye.jsp?section=3"> Changer votre mot de passe </a></li>
            </ul>
        </div>
    </fieldset>
    <fieldset id="details-role">
        <legend> Rôle </legend>
        <div>
            <p><%= Objects.equals(profil.getRole(), "Super Administrateur") ? "Administrateur" : (Objects.equals(profil.getRole(), "Administrateur") ? "Sous Admnistrateur" : profil.getRole())  %></p>
        </div>
    </fieldset>
</section>