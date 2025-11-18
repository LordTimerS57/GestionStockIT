<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/ModalType.css?v=<%=System.currentTimeMillis()%>" type="text/css"/>
		<script src="<%= request.getContextPath() %>/JS/Handle.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<script src="<%= request.getContextPath() %>/JS/HandleError.js?v=<%= System.currentTimeMillis() %>" defer></script>
		<title>Création d'un nouveau type</title>
	</head>
	<body>
		<h1>Ajouter un nouveau type d'article</h1>
		   <form id="addForm_type" onsubmit="setForm(event, 'Ajout', 'Type')">
			<fieldset>
				<legend>Informations sur le nouveau type d'article</legend>
				<div>
					<div>
						<label>Nom: <input type="text" name="nom_type"></label>
						<span id="error_nom_type"></span>
					</div>
					<div>
						<label>Description: <textarea name="description_type"></textarea> </label>
						<span id="error_description_type"></span>
					</div>
				</div>
			</fieldset>
			<input type="submit" class="submit_type btn" value="Confirmer">
		   </form>
	</body>
</html>