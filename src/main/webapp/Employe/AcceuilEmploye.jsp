<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page import="java.util.Objects" %>

<%
    List<Employe> connectes, nonConnectes;
	connectes = (List<Employe>) request.getAttribute("connectes");
	nonConnectes = (List<Employe>) request.getAttribute("non_connectes");
%>

<section class="employe-section">
	<label>
		<input placeholder="Rechercher par le nom ou/et prénoms" type="search" name="nom_prenom" oninput="searchEmploye()"><i class="fas fa-search"></i>
	</label>
	<% if(!connectes.isEmpty() && !nonConnectes.isEmpty()) { %>
	<dialog id="dialog_employe">
		<div id="employe_details">
			<h1>Details sur l'employé <span id="dialog_employe_nom_complet"></span></h1>
			<fieldset>
				<legend>Informations personnelles</legend>
				<p id="dialog_employe_adresse"></p>
				<p>Email: <span id="dialog_employe_email"></span></p>
				<p id="dialog_employe_telephone"></p>
				<p id="dialog_employe_date_naissance"></p>
			</fieldset>
			<fieldset>
				<legend>Information sur le compte</legend>
				<p>Matricule: <span id="dialog_employe_matricule"></span></p>
				<p>Role: <span id="dialog_employe_role"></span></p>
				<p id="dialog_employe_date_creation"></p>
				<p id="dialog_employe_date_modification"></p>
			</fieldset>
			<button onclick="setDetails(event, 'Close', 'Employe', null)">Fermer</button>
			<button id="modify_role_btn" onclick="passModification()">Modifier le rôle</button>
		</div>
		<form id="modify_role" onsubmit="event.preventDefault(); setForm(event, 'Modification', 'Employe', 'Modification_role'); setDetails(event, 'Close', 'Employe', null);">
			<label>
				Rôle:
				<select id="emp_role" name="role">
					<option value="" disabled>Veuillez choisir le role</option>
					<option value="2">Administrateur</option>
					<option value="3">Employé Simple</option>
				</select>
			</label>
			<label>
				<input type="hidden" id="emp_matricule" name="matricule">
				<input type="hidden" id="emp_email" name="email">
			</label>
			<button class="submit_employe btn">Attribuer</button>
		</form>
	</dialog>

	<% } %>
	<% if(!connectes.isEmpty()) { %>
	<div id="result_employe_connected">
		<h3>Liste des employés connectés</h3>
		<table>
			<tbody>
			<%  for (Employe c : connectes) { %>
			<tr>
				<td>
					<button class="show-details-employe"
						onclick="setDetails(event, 'Show', 'Employe', this)"
						data-nom_prenom="<%= c.getNomPrenom() %>"
						data-email="<%= c.getEmail() %>"
						data-telephone="<%= c.getTelephone() %>"
						data-adresse="<%= c.getAdresse() %>"
						data-date_naissance="<%= c.getDate_de_naissance_formatter() %>"
						data-matricule="<%= c.getMatricule() %>"
						data-role="<%= Objects.equals(c.getRole(), "Super Administrateur") ? "Administrateur" : (Objects.equals(c.getRole(), "Administrateur") ? "Sous Admnistrateur" : c.getRole()) %>"
						data-activite="<%= c.getActivite() %>"
						data-date_creation="<%= c.getDate_creation_formatter() %>"
						data-date_modification="<%= c.getDate_modification_formatter() %>">
						<%= c.getNomPrenom() %>
					</button>
				</td>
				<% if(!Objects.equals(c.getRole(), "Super Administrateur")) { %>
				<td>
					<button class="activation-employe" onclick="removeData('<%=c.getMatricule()%>', 'Employe')"><%= c.getActivite() ? "Désactiver" : "Activer" %> le compte</button>
				</td>
				<% } %>
			<tr>
					<% } %>
			</tbody>
		</table>
	</div>
	<% } else { %>
	<p>Aucun employé en ligne trouvé.</p>
	<% } %>
	<% if(!nonConnectes.isEmpty()) { %>
	<div id="result_employe_not_connected">
		<h3>Liste des employés non connectés</h3>
		<table>
			<tbody>
			<%  for (Employe nc : nonConnectes) { %>
			<tr>
				<td>
					<button class="show-details-employe"
							onclick="setDetails(event, 'Show', 'Employe', this)"
							data-nom_prenom="<%= nc.getNomPrenom() %>"
							data-email="<%= nc.getEmail() %>"
							data-telephone="<%= nc.getTelephone() %>"
							data-adresse="<%= nc.getAdresse() %>"
							data-date_naissance="<%= nc.getDate_de_naissance_formatter() %>"
							data-matricule="<%= nc.getMatricule() %>"
							data-role="<%= Objects.equals(nc.getRole(), "Super Administrateur") ? "Administrateur" : (Objects.equals(nc.getRole(), "Administrateur") ? "Sous Admnistrateur" : nc.getRole()) %>"
							data-activite="<%= nc.getActivite() %>"
							data-date_creation="<%= nc.getDate_creation_formatter() %>"
							data-date_modification="<%= nc.getDate_modification_formatter() %>">
						<%= nc.getNomPrenom() %>
					</button>
				</td>
				<% if(!Objects.equals(nc.getRole(), "Super Administrateur")) { %>
				<td>
					<button class="activation-employe" onclick="removeData('<%=nc.getMatricule()%>', 'Employe')"><%= nc.getActivite() ? "Désactiver" : "Activer" %> le compte</button>
				</td>
				<% } %>
			<tr>
					<% } %>
			</tbody>
		</table>
	</div>
	<% } else { %>
	<p>Aucun employé hors ligne trouvé.</p>
	<% } %>
</section>