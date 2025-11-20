<%@ page import="com.gestion_stock_it.ArtType.Article.Article" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String role = (String) session.getAttribute("login_role");
  List<Article> articles = (List<Article>) request.getAttribute("articles");
%>
<section class="article-section">
  <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
  <div class="content-article-create">
    <a href="<%= request.getContextPath() %>/Articles/Creation">Ajouter un nouvel article</a>
  </div>
  <% } %>
  
  <div class="search-container">
    <label class="search-label">
      <input type="search" name="nom_article" placeholder="Rechercher par nom d'article" oninput="searchArticle()">
      <i class="fas fa-search"></i>
    </label>
    <label class="search-label">
      <input type="search" name="nom_type" placeholder="Rechercher par type d'article" oninput="searchArticle()">
      <i class="fas fa-search"></i>
    </label>
  </div>
  
  <% if(!articles.isEmpty()) { %>

  <dialog id="dialog_type_article">
    <fieldset>
      <legend>Détails sur le type de l'article <span id="dialog_nom_article"></span> </legend>
      <p>Nom du type: <span id="dialog_nom_type"></span></p>
      <p>Description: <span id="dialog_description_type"></span></p>
    </fieldset>
    <button onclick="setDetails(event, 'Close', 'Type', null)">Fermer</button>
  </dialog>

  <div id="result_article">
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>Nom</th>
        <th>Type</th>
        <th>Quantité</th>
        <th>Description</th>
        <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
        <th>Actions</th>
        <% } %>
      </tr>
      </thead>
      <tbody>
      <% for(Article a : articles) { %>
      <tr>
        <td><%= a.getTag_article() %></td>
        <td><%= a.getNom_article() %></td>
        <td>
          <button class="show-details-type"
                  onclick="setDetails(event, 'Show', 'Type', this)"
                  data-nom_article="<%=a.getNom_article()%>"
                  data-nom_type="<%=a.getType_article().getNom_type()%>"
                  data-description_type="<%=a.getType_article().getDescription_type()%>">
            <%= a.getType_article().getNom_type() %>
          </button>
        </td>
        <td><%= a.getNombre_article() %></td>
        <td><%= a.getDescription_article() %></td>
          <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
        <td>
          <div class="table-actions">
            <button class="action-modifier" onclick="setUpdateArticle('<%=a.getTag_article()%>')">Modifier</button>
            <% if(a.getNombre_occurence_entrees() + a.getNombre_occurence_sorties() == 0) { %>
            <button class="action-supprimer" onclick="removeData('<%=a.getTag_article()%>', 'Article', null)">Supprimer</button>
            <% } %>
          </div>
        </td>
          <% } %>
      </tr>
      <% } %>
      </tbody>
    </table>
  </div>
  <% } else { %>
  <p>Aucun article trouvé.</p>
  <% } %>
</section>