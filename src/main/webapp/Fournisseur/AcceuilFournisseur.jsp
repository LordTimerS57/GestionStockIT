<%@ page import="com.gestion_stock_it.Fournisseur.Fournisseur" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.Fournisseur.FournisseurDataController" %><%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 29/10/2025
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs");
%>
<section class="fournisseur-section">
  <label>
    <input type="search" name="nom_fournisseur" oninput="searchFournisseur()">
  </label>
  <div class="content-fournisseur-create">
    <a href="<%= request.getContextPath() %>/Fournisseurs/Creation"> Ajouter un fournisseur </a>
  </div>
  <% if(!fournisseurs.isEmpty()) { %>

  <dialog id="dialog_fournisseur">
    <fieldset>
      <legend>Détails sur le fournisseur <span id="dialog_fournisseur_raison_sociale"></span> </legend>
      <p id="dialog_telephone_fournisseur"></p>
      <p id="dialog_email_fournisseur"></p>
      <p id="dialog_tag_fournisseur"></p>
    </fieldset>
    <button onclick="setDetails(event, 'Close', 'Fournisseur', null)">Fermer</button>
  </dialog>

  <div id="result_fournisseur">
    <table>
      <tbody>
      <%  for (Fournisseur f : fournisseurs) { %>
      <tr>
        <td>
          <button onclick="setDetails(event, 'Show', 'Fournisseur', this)"
                  data-raison_sociale="<%=f.getRaison_sociale()%>"
                  data-tag_fournisseur="<%=f.getTag_fournisseur()%>"
                  data-email="<%=f.getEmail_fournisseur()%>"
                  data-telephone="<%=f.getTelephone_fournisseur()%>">
            <%= f.getRaison_sociale() %>
          </button>
        </td>
        <td>
            <button onclick="setUpdateFournisseur('<%=f.getTag_fournisseur()%>')">Modifier</button>
            <% if(f.getNombre_occurence_entree() == 0) { %>
            <button onclick="removeData('<%=f.getTag_fournisseur()%>', 'Fournisseur')">Supprimer</button>
            <% } %>
        <td>
      <tr>
      <% } %>
      </tbody>
    </table>
    <% } else { %>
    <p>Aucun fournisseur trouvé</p>
    <% } %>
  </div>
</section>
