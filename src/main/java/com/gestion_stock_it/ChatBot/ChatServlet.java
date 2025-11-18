package com.gestion_stock_it.ChatBot;

import com.gestion_stock_it.Employe.Employe;
import com.gestion_stock_it.ErrorConfirmException;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    private ChatBotService chatbotService;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        try {
            // Initialisation du service (CohereClient)
            this.chatbotService = new ChatBotService();
        } catch (Exception e) {
            System.err.println("Impossible de démarrer le service Chatbot.");
            throw new ServletException("Impossible de démarrer le service Chatbot.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Récupération des paramètres (Question et Employe de la session)
        String question = request.getParameter("question");
        Employe employe = (Employe) request.getSession().getAttribute("login_profil");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int role = -1;

        Map<String, String> jsonResponse = new HashMap<>();

        if (employe != null){
            role = employe.getRoleInt();
        }

        if (question == null || question.trim().isEmpty()) {
            jsonResponse.put("statut", "ERREUR");
            jsonResponse.put("reponse", "Veuillez vous connecter et fournir une question valide.");
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        try {
            // 2. Traitement de la question (NL2SQL pur)
            // L'historique sera lu/mis à jour via le ChatWebSocket par le service
            String botResponse = chatbotService.processQuestion(question, role);

            // 3. Réponse réussie
            jsonResponse.put("statut", "OK");
            jsonResponse.put("reponse", botResponse);

        } catch (ErrorConfirmException e) {
            e.printStackTrace();
            jsonResponse.put("statut", "ERREUR_INVALIDE");
            jsonResponse.put("reponse",  e.getMessage().toLowerCase().matches(".*au chat.*") ? e.getMessage() : e.getMessage().toLowerCase() );

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("statut", "ERREUR");
            jsonResponse.put("reponse", "Désolé, une erreur interne est survenue : " + e.getMessage());
        } finally {
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }
}