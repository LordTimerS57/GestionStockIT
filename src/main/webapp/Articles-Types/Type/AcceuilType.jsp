<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Objects" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String role = (String) session.getAttribute("login_role");
  List<TypeArticle> typeArticles = (List<TypeArticle>) request.getAttribute("types");
  boolean isAdminOrSuperAdmin = Objects.equals(role, "Administrateur") || Objects.equals(role, "Super Administrateur");
%>

<section class="type-section">
    <% if(isAdminOrSuperAdmin) { %>
    <div class="content-type-create">
        <a href="<%= request.getContextPath() %>/Types/Creation"><i class="fas fa-plus"></i> Ajouter un nouveau type d'article</a>
    </div>
    <% } %>
    
    <label class="search-label">
      <input type="search" name="nom_type" placeholder="Rechercher par le nom du type d'article" oninput="searchType()">
      <i class="fas fa-search"></i>
    </label>
    
    <% if(!typeArticles.isEmpty()) { %>
    <div id="result_type">
      <table>
      	<caption>Tous (<%= typeArticles.size() %>)</caption>
        <thead>
        <tr>
          <th>ID</th>
          <th>Nom</th>
          <th>Description</th>
          <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
          <th>Actions</th>
          <% } %>
        </tr>
        </thead>
        <tbody>
        <% for(TypeArticle t : typeArticles) { %>
        <tr>
          <td><%= t.getTag_type() %></td>
          <td><%= t.getNom_type() %></td>
          <td><%= t.getDescription_type() %></td>
            <% if(isAdminOrSuperAdmin) { %>
          <td>
            <div class="table-actions">
              <button class="action-modifier" onclick="setUpdateType('<%=t.getTag_type()%>')"><i class="fas fa-pencil-alt"></i></button>
              <% if(t.getNombre_occurence_article() == 0) { %>
              <button class="action-supprimer" onclick="removeData('<%=t.getTag_type()%>', 'Type', this)"><i class="fas fa-trash"></i></button>
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
    <p> Aucun type d'article trouvé </p>
    <% } %>
</section>