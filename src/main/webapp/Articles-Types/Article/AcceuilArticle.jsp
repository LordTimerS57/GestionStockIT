<%@ page import="com.gestion_stock_it.ArtType.Article.Article" %>
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
  List<Article> articles = (List<Article>) request.getAttribute("articles");
%>
<section class="content-article">
  <% if(role.equals("Administrateur") || role.equals("Super Administrateur")) { %>
  <div class="content-article-create">
    <a href="<%= request.getContextPath() %>/Articles-Types/Articles/Creation">Un nouvel article</a>
  </div>
  <% } %>
  <div class="search">
    <label>
      <input type="search" name="nom_article" placeholder="Veuillez chercher l'article en question" oninput="searchArticle()">
    </label>
    <label>
      <input type="search" name="nom_type" placeholder="Veuillez chercher le type d'article en question" oninput="searchArticle()">
    </label>
  </div>
  <% if(!articles.isEmpty()) { %>

  <dialog id="dialog_type_article">
    <fieldset>
      <legend>Détails sur le type de l'article <span id="dialog_nom_article"></span> </legend>
      <p id="dialog_nom_type"></p>
      <p id="dialog_description_type"></p>
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
        <th>Quantité en stock</th>
        <th>Description</th>
      <tr>
      </thead>
      <tbody>
      <% for(Article a : articles) { %>
      <tr>

        <td><%= a.getTag_article() %></td>
        <td><%= a.getNom_article() %></td>
        <td>
          <button onclick="setDetails(event, 'Show', 'Type', this)"
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
          <div>
            <button onclick="setUpdateArticle('<%=a.getTag_article()%>')">Modifier</button>
            <% if(a.getNombre_occurence_entrees() + a.getNombre_occurence_sorties() == 0) { %>
            <button onclick="removeData('<%=a.getTag_article()%>', 'Article', null)">Supprimer</button>
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
  <p>Aucun article trouvé.</p>
  <% } %>
  </section>
