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
      <p><span id="dialog_nom_type"></span></p>
      <p><span id="dialog_description_type"></span></p>
    </fieldset>
    <button onclick="setDetails(event, 'Close', 'Type', null)">Fermer</button>
  </dialog>
  
  <dialog id="dialog_info_article">
    <fieldset>
      <legend>Détails du stock sur l'article <span id="dialog_nom_article_info"></span> </legend>
      <p><span id="dialog_stock_actuel_article"></span></p>
      <p><span id="dialog_consommation_moyen_article"></span></p>
      <p><span id="dialog_delai_reapprovisionnement_article"></span></p>
      <p><span id="dialog_seuil_critique_article"></span></p>
      <p><span id="dialog_situation_article"></span></p>
      <p><span id="dialog_entree_article"></span></p>
      <p><span id="dialog_sortie_article"></span></p>
    </fieldset>
    <button onclick="setDetails(event, 'Close', 'Article-Info', null)">Fermer</button>
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
            <% if(a.getNombre_occurence_entrees_article() + a.getNombre_occurence_sorties_article() == 0) { %>
            <button class="action-supprimer" onclick="removeData('<%=a.getTag_article()%>', 'Article', null)">Supprimer</button>
            <% } %>
            <button class="action-details" 
            		onclick="setDetails(event, 'Show', 'Article-Info', this)"
            		data-nom_article="<%= a.getNom_article() %> "
            		data-stock_article="<%= a.getNombre_article() %>"
            		data-cmd_article="<%= a.getCMD() %>"
            		data-dirm_article="<%= a.getDelai_reappro_estime() %>"
            		data-seuil_stock_article="<%= (a.getSeuil_critique_arrondi() > 0) ? a.getSeuil_critique_arrondi() : "N/A" %>"
            		data-situation_article="<%= a.getSituation_article() %>"
            		data-derniere_entree_article="<%= a.getDate_derniere_entree() %>"
            		data-derniere_sortie_article="<%= a.getDate_derniere_sortie() %>"><i class="fa-solid fa-eye"></i>
      		</button>
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