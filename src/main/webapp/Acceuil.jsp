<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Objects" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<% Employe employe = (Employe) session.getAttribute("login_profil"); %>
<section class="welcome-section">
	<h2 class="welcome-content">Bienvenue <%= employe.getNomPrenom() %> </h2>
</section>