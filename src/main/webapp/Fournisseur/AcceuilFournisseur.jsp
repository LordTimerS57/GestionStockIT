<%@ page import="com.gestion_stock_it.Fournisseur.Fournisseur" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.Fournisseur.FournisseurDataController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs");
%>
<section class="fournisseur-section">
  <label class="search-label">
    <input placeholder="Rechercher par raison sociale" type="search" name="nom_fournisseur" oninput="searchFournisseur()">
    <i class="fas fa-search"></i>
  </label>
  
  <div class="content-fournisseur-create">
    <a href="<%= request.getContextPath() %>/Fournisseurs/Creation"> Ajouter un fournisseur </a>
  </div>
  
  <% if(!fournisseurs.isEmpty()) { %>
  <dialog id="dialog_fournisseur">
    <fieldset>
      <legend>Détails sur le fournisseur <span id="dialog_fournisseur_raison_sociale"></span> </legend>
      <p><span id="dialog_telephone_fournisseur"></span></p>
      <p><span id="dialog_email_fournisseur"></span></p>
      <p><span id="dialog_tag_fournisseur"></span></p>
    </fieldset>
    <button onclick="setDetails(event, 'Close', 'Fournisseur', null)">Fermer</button>
  </dialog>

  <div id="result_fournisseur">
    <table>
      <caption>Liste des fournisseurs</caption>
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
              <button class="action-modifier" onclick="setUpdateFournisseur('<%=f.getTag_fournisseur()%>')">Modifier</button>
              <% if(f.getNombre_occurence_entree() == 0) { %>
              <button class="action-supprimer" onclick="removeData('<%=f.getTag_fournisseur()%>', 'Fournisseur')">Supprimer</button>
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