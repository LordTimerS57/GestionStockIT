<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gestion_stock_it.ChatBot.Chat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<% 
    // Récupération de l'employé
    Employe employe = (Employe) session.getAttribute("login_profil");
    
    // Vérification de connexion
    if (employe == null) {
        response.sendRedirect("login.jsp"); 
        return;
    }
    
    // --- LOGIQUE DE PERSISTANCE ET DE CHARGEMENT ---
    // Récupère l'historique de la session HTTP.
    // L'historique persiste si la session est toujours valide.
    List<Chat> history = (List<Chat>) session.getAttribute("chats");
    
    // Détermine si c'est une nouvelle conversation
    boolean isNewSession = (history == null || history.isEmpty());
%>

<section class="chatbot-content">
  <form id="chatService" onsubmit="return submitQuestion('<%= request.getContextPath() %>')">
    <fieldset>
      <legend>Assistant IA IT</legend>      
      <div id="messages">
        <div id="message">
        <% 
            if (isNewSession) { 
                // Affichage du message de bienvenue si la liste est vide/null
        %>
            <p class="bot-msg">
                <b>Bot:</b> Bonjour ! Je suis l'assistant AI. Posez-moi une question sur les stock, nos fournisseurs, les entrées ou sorties d'articles, les employés ou les articles.
            </p>
        <% 
            } else { 
                // BOUCLE : Afficher tous les messages enregistrés dans la session Java
                for (Chat chat : history) {
                    // Correction de la casse pour distinguer l'utilisateur du bot
                    boolean isUser = chat.getType() != null && chat.getType().equals("User");
                    
                    String senderClass = isUser ? "user-msg" : "bot-msg";
                    String senderName = isUser ? "Vous" : "Bot";
                    
                    // Gestion des sauts de ligne
                    String content = chat.getContent().replace("\n", "<br>").replace("\r", "");
        %>
            <p class="<%= senderClass %>">
                <b><%= senderName %>:</b> <%= content %>
            </p>
        <%
                }
            }
        %>
        </div>
      </div>
      
      <label for="question-area">
        <textarea id="question-area" name="question" placeholder="Poser les questions que vous voulez..." rows="3" style="width: 90%;"></textarea>
      </label>
      
      <button type="submit" class="submit_chat btn">Soumettre</button>
    </fieldset>
  </form>
</section>

<script>
  // Global variable injection (Nécessaire pour le ChatServlet si l'ID utilisateur est requis)
  window.currentMatricule = '<%= employe.getMatricule() %>';

  document.addEventListener("DOMContentLoaded", function() {
    const messageDiv = document.getElementById('message');
    if (messageDiv) {
      // Défilement vers le bas pour voir le dernier message chargé par le JSP (ou le message de bienvenue)
      messageDiv.scrollTop = messageDiv.scrollHeight;
    }
  });

</script> 