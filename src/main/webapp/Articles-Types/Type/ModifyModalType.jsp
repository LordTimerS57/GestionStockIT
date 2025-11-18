<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="com.gestion_stock_it.ArtType.Article.ArticleDataController" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%
	TypeArticle t = (TypeArticle) request.getAttribute("type");
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalType.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
	<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
	<title>Modification d'un type d'article</title>
</head>
<body>
	<h1>Modifier les informations du type d'article ... </h1>
	<form id="modifyForm_type" onsubmit="setForm(event, 'Modification', 'Type', null)">
    	<fieldset>
			<legend>Informations sur le type d'article</legend>
			<div>
				<input type="hidden" value="<%= t.getTag_type() %>" name="tag_type">
				<div>
					<label>Nom: <input type="text" name="nom_type" value="<%= t.getNom_type() %>"></label>
					<span id="error_nom_type"></span>
				</div>
				<div>
					<label>Description: <textarea name="description_type"> <%= t.getDescription_type() %> </textarea></label>
					<span id="error_description_type"></span>
				</div>
			</div>
		</fieldset>
		<input type="submit" class="submit_type btn" value="Confirmer">
    </form>
</body>