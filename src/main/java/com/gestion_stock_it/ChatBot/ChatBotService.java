package com.gestion_stock_it.ChatBot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ChatBotService {

    private final CohereClient cohereClient;
    private final ObjectMapper objectMapper;

    private static final String FULL_SCHEMA = System.getenv("BD_SCHEMA");
    private static final String COHERE_API_KEY = System.getenv("COHERE_API_KEY");

    public ChatBotService() throws Exception {
        if (COHERE_API_KEY == null || FULL_SCHEMA == null) {
            throw new IllegalArgumentException("Les variables d'environnement (COHERE_API_KEY, DB_SCHEMA) doivent être définies.");
        }
        this.cohereClient = new CohereClient(COHERE_API_KEY, FULL_SCHEMA);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Exécute le flux Text-to-SQL complet.
     * @param question La question de l'utilisateur.s
     */
    public String processQuestion(String question, int role) throws Exception {

        DatabaseConnection db = new DatabaseConnection();
        DatabaseExecutor dbExecutor = new DatabaseExecutor(db);

        try {
            db.connect();

            // --- ÉTAPE 1 : Traduction (CohereClient) ---
            // L'historique complet est passé pour maintenir le contexte
            String generatedSql = cohereClient.generateSql(question, role);
            System.out.println("DEBUG SQL: " + generatedSql);

            // --- ÉTAPE 2 : Exécution (DatabaseExecutor/JDBC) ---
            List<Map<String, Object>> results = dbExecutor.executeSelect(generatedSql);

            String finalBotResponse;
            if (results.isEmpty()) {
                finalBotResponse = "Je n'ai trouvé aucune donnée correspondant à votre requête.";
            } else {
                String jsonResults = objectMapper.writeValueAsString(results);

                // --- ÉTAPE 3 : Reformulation (CohereClient) ---
                finalBotResponse = cohereClient.reformulateResponse(question, jsonResults);
            }
            return finalBotResponse;

        } catch (ErrorConfirmException e){
          e.printStackTrace();
          throw e;
        } catch (SQLException e) {
            System.err.println("Erreur SQL pour la requête: " + question);
            e.printStackTrace();
            return "Désolé, une erreur technique est survenue lors de l'accès aux données. (SQL)";
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + question);
            e.printStackTrace();
            return "Désolé, une erreur inattendue est survenue lors du traitement.";
        } finally {
            db.disconnect();
        }
    }
}