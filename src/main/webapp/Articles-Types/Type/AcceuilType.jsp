<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String role = (String) session.getAttribute("login_role");
  List<TypeArticle> typeArticles = (List<TypeArticle>) request.getAttribute("types");
%>

<section class="type-section">
    <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
    <div class="content-type-create">
        <a href="<%= request.getContextPath() %>/Types/Creation"><i class="fas fa-plus"></i> Ajouter un nouveau type d'article</a>
    </div>
    <% } %>
    
    <label class="search-label">
      <input type="search" name="nom_type" placeholder="Veuillez chercher le type d'article en question" oninput="searchType()">
      <i class="fas fa-search"></i>
    </label>
    
    <% if(!typeArticles.isEmpty()) { %>
    <div id="result_type">
      <table>
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
            <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
          <td>
            <div class="table-actions">
              <button class="action-modifier" onclick="setUpdateType('<%=t.getTag_type()%>')"><i class="fas fa-pencil-alt"></i></button>
              <% if(t.getNombre_occurence_article() == 0) { %>
              <button class="action-supprimer" onclick="removeData('<%=t.getTag_type()%>', 'Type')"><i class="fas fa-trash"></i></button>
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