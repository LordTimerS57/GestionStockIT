<%@ page import="com.gestion_stock_it.Flux.Entree" %>
<%@ page import="java.util.List" %>
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
    List<Entree> entrees = (List<Entree>) request.getAttribute("entrees");
    String role = (String) session.getAttribute("login_role");
    Boolean visibleRapportButton = (Boolean) request.getAttribute("rapport_button");
    boolean isAdminOrSuperAdmin = Objects.equals(role, "Administrateur") || Objects.equals(role, "Super Administrateur");
%>

<section class="content-flux-in">
    <% if(isAdminOrSuperAdmin) { %>
    <div class="content-flux-create">
        <a href="<%=request.getContextPath()%>/Entrees/Creation"><i class="fa-solid fa-box-archive"></i> Entrer un article au stock</a>
    </div>
    <% } %>
    <div class="search">
	    <fieldset id="date_param_search_container">
	        <legend class="collapsible" onclick="toggleFieldset(this,'date')">Recherche par date <i class="fas fa-caret-down"></i></legend>
	        
	        <div class="fieldset-content" style="display: none;">
	            <div>
		            <label id="date_type_select_label">
		                Chercher la date de déplacement suivant
		                <select name="date" onchange="updateSearchFlux('Date_flux')">
		                    <option value="date">la date</option>
		                    <option value="mois">le mois et l'année seulement</option>
		                </select>
		            </label>
	            </div>
	
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
	                        <input type="date" name="date_flux" id="date_flux" oninput="searchFlux('Entree')">
	                    </label>
	                </div>
	
	                <div id="date_search_2" style="display: none">
	                    <label>
	                        <select name="month_date_flux_1" id="month_flux" onchange="searchFlux('Entree')">
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
	                        <input type="search" name="month_date_flux_2" id="year_flux" pattern="^[0-9]+$" oninput="searchFlux('Entree')">
	                    </label>
	                </div>
	            </div>
	        </div>
	    </fieldset>
	    <label id="expediteur_search">
            <input type="search" name="expediteur" id="expediteur" placeholder="Rechercher l'expéditeur par sa raison sociale..." oninput="searchFlux('Entree')"><i class="fas fa-search"></i>
        </label>
	    <label id="article_search">
	        <input type="search" name="nom_article" id="nom_article" placeholder="Rechercher l'article souhaité ..." oninput="searchFlux('Entree')">
	        <i class="fas fa-search"></i>
	    </label>
	</div>
    <div id="result_entree">
        <% if (!entrees.isEmpty()) { %>
        <table>
        	<caption>Tous (<%= entrees.size() %>)</caption>
            <thead>
            <tr>
                <th>ID</th>
                <th>Article</th>
                <th>Quantité déplacée</th>
                <th>Date de déplacement</th>
                <th>Expéditeur</th>
                <!--
                    <th>Destinataire</th>
                    <th>Motif</th>
                -->
            <tr>
            </thead>
            <tbody>
                <%  for(Entree e : entrees) { %>
                <tr>
                    <td><%=e.getTag_flux()%></td>
                    <td><%=e.getArticle().getNom_article()%></td>
                    <td><%=e.getNombre_article_deplace()%></td>
                    <td><%=e.getDate_deplacement_formatter()%></td>
                    <td><%=e.getExpediteur().getRaison_sociale()%></td>
                    <!--
                        <td>...</td>
                        <td>...</td>
                    -->
                </tr>
                <%  } %>
            </tbody>
        </table>
        <% if(visibleRapportButton) { %>
        <div id="excel">
            <button onclick="setExcelTransform('Entree', this)"><i class="fa-solid fa-download"></i> Faire un rapport excel</button>
        </div>
        <% } %>
        <% } else { %>
        <p> Aucune entrée d'article trouvée</p>
        <% } %>
    </div>
</section>