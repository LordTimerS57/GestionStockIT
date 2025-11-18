<%@ page import="com.gestion_stock_it.ArtType.Type.TypeArticle" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 29/10/2025
  Time: 09:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String role = (String) session.getAttribute("login_role");
  List<TypeArticle> typeArticles = (List<TypeArticle>) request.getAttribute("types");
%>

<section class="content-type">
    <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
    <div class="contenttype-create">
        <a href="<%= request.getContextPath() %>/Articles-Types/Types/Creation">Un nouveau type d'article</a>
    </div>
    <% } %>
    <label>
      <input type="search" name="nom_type" placeholder="Veuillez chercher le type d'article en question" oninput="searchType()">
    </label>
    <% if(!typeArticles.isEmpty()) { %>
    <div id="result_type">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Nom</th>
          <th>Description</th>
        <tr>
        </thead>
        <tbody>
        <% for(TypeArticle t : typeArticles) { %>
        <tr>
          <td><%= t.getTag_type() %></td>
          <td><%= t.getNom_type() %></td>
          <td><%= t.getDescription_type() %></td>
            <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
          <td>
            <div>
              <button onclick="setUpdateType('<%=t.getTag_type()%>')">Modifier</button>
              <% if(t.getNombre_occurence_article() == 0) { %>
              <button onclick="removeData('<%=t.getTag_type()%>', 'Type')">Supprimer</button>
              <% } %>
            </div>
          </td>
            <% } %>
        <tr>
            <% } %>
        </tbody>
      </table>
    </div>
    <% } else { %>
    <p> Aucun type d'article trouvé </p>
    <% } %>
</section>