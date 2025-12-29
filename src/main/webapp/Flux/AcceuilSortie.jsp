<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.Flux.Sortie" %>
<%@ page import="java.util.Objects" %>
<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 29/10/2025
  Time: 08:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<Sortie> sorties = (List<Sortie>) request.getAttribute("sorties");
    String role = (String) session.getAttribute("login_role");
    Boolean visibleRapportButton = (Boolean) request.getAttribute("rapport_button");
    boolean isAdminOrSuperAdmin = Objects.equals(role, "Administrateur") || Objects.equals(role, "Super Administrateur");
%>

<section class="content-flux-out">
    <% if(isAdminOrSuperAdmin) { %>
    <div class="content-flux-create">
        <a href="<%=request.getContextPath()%>/Sorties/Creation"> <i class="fa-solid fa-cash-register"></i> Sortir un article</a>
    </div>
    <% } %>
	<div class="search">
	    <fieldset id="date_param_search_container">
	        <legend class="collapsible" onclick="toggleFieldset(this,'date')">Recherche par date <i class="fas fa-caret-down"></i></legend>
	        
	        <div class="fieldset-content" style="display: none;">
	            <label id="date_type_select_label">
	                Chercher la date de déplacement suivant
	                <select name="date" onchange="updateSearchFlux('Date_flux')">
	                    <option value="date">la date</option>
	                    <option value="mois">le mois et l'année seulement</option>
	                </select>
	            </label>
	
	            <div id="date_input_fields">
	
	                <div id="date_search_1">
	                    <label>
	                        <select name="precision_date" id="precision_date">
	                            <option value="">...</option>
	                            <option value="before">Avant</option>
	                            <option value="after">Après</option>
	                            <option value="equals">Durant</option>
	                        </select>
	                        le
	                        <input type="date" name="date_flux" id="date_flux" oninput="searchFlux('Sortie')">
	                    </label>
	                </div>
	
	                <div id="date_search_2" style="display: none">
	                    <label>
	                        <select name="month_date_flux_1" id="month_flux" onchange="searchFlux('Sortie')">
	                            <option value="01">Janvier</option>
	                            <option value="02">Février</option>
	                            <option value="03">Mars</option>
	                            <option value="04">Avril</option>
	                            <option value="05">Mai</option>
	                            <option value="06">Juin</option>
	                            <option value="07">Juillet</option>
	                            <option value="08">Août</option>
	                            <option value="09">Septembre</option>
	                            <option value="10">Octobre</option>
	                            <option value="11">Novembre</option>
	                            <option value="12">Décembre</option>
	                        </select> -
	                        <input type="search" name="month_date_flux_2" id="year_flux" pattern="^[0-9]+$" oninput="searchFlux('Sortie')">
	                    </label>
	                </div>
	            </div>
	        </div>
	    </fieldset>
	    
	    <fieldset id="employe_param_search_container">
	        <legend class="collapsible" onclick="toggleFieldset(this,'employe')">Recherche par employé <i class="fas fa-caret-down"></i></legend>
	        
	        <div id="employe_search_input" class="fieldset-content" style="display: none;">
	            <label id="expediteur_search">
	                <input type="search" name="expediteur" id="expediteur" placeholder="Rechercher l'expéditeur par son nom et/ou prénoms..." oninput="searchFlux('Sortie')"><i class="fas fa-search"></i>
	            </label>
	            <label id="destinataire_search">
	                <input type="search" name="destinataire" id="destinataire" placeholder="Rechercher la destinataire par son nom et/ou prénoms..." oninput="searchFlux('Sortie')"><i class="fas fa-search"></i>
	            </label>
	        </div>
	    </fieldset>
	
	    <label id="article_search">
	        <input type="search" name="nom_article" id="nom_article" placeholder="Rechercher l'article souhaité ..." oninput="searchFlux('Sortie')">
	        <i class="fas fa-search"></i>
	    </label>
	</div>
    <div id="result_sortie">
        <% if (!sorties.isEmpty()) { %>
        <dialog id="dialog_employe">
            <h1>Details sur l'employé <span id="dialog_employe_nom_complet"></span></h1>
            <fieldset>
                <legend>Informations personnelles</legend>
                <p id="dialog_employe_adresse"></p>
                <p id="dialog_employe_email"></p>
                <p id="dialog_employe_telephone"></p>
                <p id="dialog_employe_date_naissance"></p>
            </fieldset>
            <fieldset>
                <legend>Information sur le compte</legend>
                <p id="dialog_employe_matricule"></p>
                <p id="dialog_employe_role"></p>
                <p id="dialog_employe_date_creation"></p>
                <p id="dialog_employe_date_modification"></p>
            </fieldset>
            <button onclick="setDetails(event, 'Close', 'Expediteur-Destinataire', null)">Fermer</button>
        </dialog>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Article</th>
                    <th>Quantité déplacée</th>
                    <th>Date de déplacement</th>
                    <th>Expéditeur</th>
                    <th>Destinataire</th>
                    <!--
                        <th>Motif</th>
                    -->
                <tr>
            </thead>
            <tbody>
                <% for(Sortie s : sorties) {%>
                <tr>
                    <td><%=s.getTag_flux()%></td>
                    <td><%=s.getArticle().getNom_article()%></td>
                    <td><%=s.getNombre_article_deplace()%></td>
                    <td><%=s.getDate_deplacement_formatter()%></td>
                    <td>
                        <button onclick="setDetails(event, 'Show', 'Expediteur-Destinataire', this)"
                                data-nom_prenom="<%= s.getExpediteur().getNomPrenom() %>"
                                data-email="<%= s.getExpediteur().getEmail() %>"
                                data-telephone="<%= s.getExpediteur().getTelephone() %>"
                                data-adresse="<%= s.getExpediteur().getAdresse() %>"
                                data-date_naissance="<%= s.getExpediteur().getDate_de_naissance() %>"
                                data-matricule="<%= s.getExpediteur().getMatricule() %>"
                                data-role="<%= s.getExpediteur().getRole() %>"
                                data-activite="<%= s.getExpediteur().getActivite() %>"
                                data-date_creation="<%= s.getExpediteur().getDate_creation() %>"
                                data-date_modification="<%= s.getExpediteur().getDate_modification() %>">
                            <%= s.getExpediteur().getNomPrenom() %>
                        </button>
                    </td>
                    <td>
                        <button onclick="setDetails(event, 'Show', 'Expediteur-Destinataire', this)"
                                data-nom_prenom="<%= s.getDestinataire().getNomPrenom() %>"
                                data-email="<%= s.getDestinataire().getEmail() %>"
                                data-telephone="<%= s.getDestinataire().getTelephone() %>"
                                data-adresse="<%= s.getDestinataire().getAdresse() %>"
                                data-date_naissance="<%= s.getDestinataire().getDate_de_naissance() %>"
                                data-matricule="<%= s.getDestinataire().getMatricule() %>"
                                data-role="<%= s.getDestinataire().getRole() %>"
                                data-activite="<%= s.getDestinataire().getActivite() %>"
                                data-date_creation="<%= s.getDestinataire().getDate_creation() %>"
                                data-date_modification="<%= s.getDestinataire().getDate_modification() %>">
                            <%= s.getDestinataire().getNomPrenom() %>
                        </button>
                    </td>
                    <!--
                        <td>...</td>
                    -->
                </tr>
                <%  } %>
            </tbody>
        </table>
        <% if(visibleRapportButton) { %>
        <div id="excel">
            <button onclick="setExcelTransform('Sortie')"><i class="fa-solid fa-download"></i> Faire un rapport excel</button>
        </div>
        <% } %>
        <% } else { %>
        <p> Aucune sortie d'article trouvée</p>
        <% } %>
    </div>
</section>