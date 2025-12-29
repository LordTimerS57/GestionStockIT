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
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    private ChatBotService chatbotService;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        try {
            this.chatbotService = new ChatBotService();
        } catch (Exception e) {
            System.err.println("Impossible de démarrer le service Chatbot.");
            throw new ServletException("Impossible de démarrer le service Chatbot.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String question = request.getParameter("question");
        Employe employe = (Employe) request.getSession().getAttribute("login_profil");

        List<Chat> history = (List<Chat>) request.getSession().getAttribute("chats");
        if (history == null) {
            history = new ArrayList<>();
            if (employe != null) {
				Chat welcomeChat = new Chat("Bot",  "Bonjour ! Je suis l'assistant AI. Posez-moi une question sur les stock, nos fournisseurs, les entrées ou sorties d'articles, les employés ou les articles.");
				history.add(welcomeChat);
			}
            request.getSession().setAttribute("chats", history);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
                
        int role = -1;

        Map<String, String> jsonResponse = new HashMap<>();

        if (employe != null){
            role = employe.getRoleInt();
        } else {
             jsonResponse.put("statut", "ERREUR");
             jsonResponse.put("reponse", "Veuillez vous connecter pour utiliser le chatbot.");
             response.getWriter().write(gson.toJson(jsonResponse));
             return;
        }

        if (question == null || question.trim().isEmpty()) {
            jsonResponse.put("statut", "ERREUR");
            jsonResponse.put("reponse", "Veuillez fournir une question valide.");
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        try {
            String botResponse = chatbotService.processQuestion(question, role);

            Chat responseChat = new Chat("Bot", botResponse);
            Chat questionChat = new Chat("User", question);
            
            history.add(questionChat);
            history.add(responseChat);
            
            request.getSession().setAttribute("chats", history);
            
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