<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
    ATTENTION : Ce fichier ne doit contenir AUCUNE logique de récupération d'historique en Java (ChatWebSocket)
    car l'historique est maintenant géré par le sessionStorage du navigateur (Handle.js).
    Il dépend uniquement de la fonction loadChatHistoryFromSession() définie dans Handle.js.
--%>

<style>
  /* Style pour le chat (Idéalement à placer dans un fichier CSS externe) */
  #message {
    height: 300px;
    border: 1px solid #ccc;
    overflow-y: scroll;
    padding: 10px;
    margin-bottom: 10px;
  }
  .user-msg {
    text-align: right;
    color: #007bff; /* Bleu Bootstrap */
    background-color: #e9f0f9;
    padding: 5px;
    margin: 5px 0;
    border-radius: 10px 10px 0 10px;
    max-width: 80%;
    margin-left: auto;
  }
  .bot-msg {
    text-align: left;
    color: #28a745; /* Vert Success */
    background-color: #e6f7e8;
    padding: 5px;
    margin: 5px 0;
    border-radius: 10px 10px 10px 0;
    max-width: 80%;
    margin-right: auto;
  }
  .chatbot-container fieldset {
    border: 1px solid #dee2e6;
    padding: 15px;
    border-radius: 5px;
  }
</style>

<div class="chatbot-container">
  <%-- Le formulaire appelle submitQuestion, qui est dans Handle.js --%>
  <form id="ChatService" onsubmit="return submitQuestion('<%= request.getContextPath() %>')">
    <fieldset>
      <legend>Assistant IA IT</legend>

      <div id="message">
        <p class="bot-msg"><b>Bot:</b> Bonjour ! Je suis l\'assistant SQL. Posez-moi une question sur votre stock, vos employés ou vos articles.</p>
      </div>

      <label for="question-area">
        <textarea id="question-area" name="question" placeholder="Poser les questions que vous voulez..." rows="3" style="width: 90%;"></textarea>
      </label>
      <button type="submit" class="submit_chat btn">Soumettre</button>
    </fieldset>
  </form>
</div>

<script>
  document.addEventListener("DOMContentLoaded", function() {
    loadChatHistoryFromSession();
  });

  (function() {
    const messageDiv = document.getElementById('message');
    if (messageDiv) {
      messageDiv.scrollTop = messageDiv.scrollHeight;
    }
  })();
</script>