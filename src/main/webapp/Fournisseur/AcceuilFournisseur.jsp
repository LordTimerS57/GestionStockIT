<%@ page import="com.gestion_stock_it.Fournisseur.Fournisseur" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.Fournisseur.FournisseurDataController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs");
%>
<section class="fournisseur-section">
  <div class="content-fournisseur-create">
    <a href="<%= request.getContextPath() %>/Fournisseurs/Creation"><i class="fas fa-plus"></i> Ajouter un fournisseur </a>
  </div>
  
  <label class="search-label">
    <input placeholder="Rechercher par raison sociale" type="search" name="nom_fournisseur" oninput="searchFournisseur()">
    <i class="fas fa-search"></i>
  </label>
  
  
  <% if(!fournisseurs.isEmpty()) { %>
  <dialog id="dialog_fournisseur">
    <div id="fournisseur_details">
	    <fieldset>
	      <legend>Détails sur le fournisseur <span id="dialog_fournisseur_raison_sociale"></span> </legend>
	      <p><span id="dialog_telephone_fournisseur"></span></p>
	      <p><span id="dialog_email_fournisseur"></span></p>
	      <p><span id="dialog_tag_fournisseur"></span></p>
	    </fieldset>
	    <div id="fournisseur_action_button">
	    	<button onclick="setDetails(event, 'Close', 'Fournisseur', null)">Fermer</button>
	    </div>
    </div>
  </dialog>

  <div id="result_fournisseur">
    <table>
      <caption>Tous (<%= fournisseurs.size() %>)</caption>
      <thead>
        <tr>
          <th>Raison Sociale</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
      <%  for (Fournisseur f : fournisseurs) { %>
      <tr>
        <td>
          <button class="show-details-fournisseur"
                  onclick="setDetails(event, 'Show', 'Fournisseur', this)"
                  data-raison_sociale="<%=f.getRaison_sociale()%>"
                  data-tag_fournisseur="<%=f.getTag_fournisseur()%>"
                  data-email="<%=f.getEmail_fournisseur()%>"
                  data-telephone="<%=f.getTelephone_fournisseur()%>">
            <%= f.getRaison_sociale() %>
          </button>
        </td>
        <td>
            <div class="table-actions">
              <button class="action-modifier" onclick="setUpdateFournisseur('<%=f.getTag_fournisseur()%>')"><i class="fas fa-pencil-alt"></i></button>
              <% if(f.getNombre_occurence_entrees_fournisseur() == 0) { %>
              <button class="action-supprimer" onclick="removeData('<%=f.getTag_fournisseur()%>', 'Fournisseur', this)"><i class="fas fa-trash"></i></button>
              <% } %>
            </div>
        </td>
      </tr>
      <% } %>
      </tbody>
    </table>
  </div>
  <% } else { %>
  <p>Aucun fournisseur trouvé</p>
  <% } %>
</section>