<%@ page import="com.gestion_stock_it.Employe.Employe" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="chatbot-content">
  <% Employe employe = (Employe) session.getAttribute("login_profil");%>
  <form id="chatService" onsubmit="return submitQuestion('<%= request.getContextPath() %>')">
    <fieldset>
      <legend>Assistant IA IT</legend>
      <div>
        <p> Bonjour ! Je suis l'assistant AI. Posez-moi une question sur les stock, nos fournisseurs, les entrées ou sorties d'articles, les employés ou les articles.</p>
        <div id="message">
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
  document.addEventListener("DOMContentLoaded", function() {
    window.currentMatricule = '<%= employe.getMatricule() %>';
    loadChatHistoryFromSession();
  });

  (function() {
    const messageDiv = document.getElementById('message');
    if (messageDiv) {
      messageDiv.scrollTop = messageDiv.scrollHeight;
    }
  })();
</script>