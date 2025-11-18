package com.gestion_stock_it.ChatBot;

import com.cohere.api.Cohere;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.Message;
import com.cohere.api.types.NonStreamedChatResponse;
import com.gestion_stock_it.ErrorConfirmException;

import java.util.List;
import java.util.ArrayList;

public class CohereClient {
    private final Cohere cohere;
    private final List<Message> baseHistory;
    // Utilisation du modèle Command-R-Plus, plus puissant et supporté
    private static final String MODEL_NAME = "command-a-03-2025";

    public CohereClient(String apiKey, String fullSchema) {
        if (apiKey == null || fullSchema == null) {
            throw new IllegalArgumentException("API Key et Schéma ne peuvent pas être nuls.");
        }
        this.cohere = Cohere.builder().token(apiKey).build();
        this.baseHistory = setupBaseHistory(fullSchema);
    }

    private List<Message> setupBaseHistory(String schema) {
        String systemInstruction =
                "Tu es un traducteur expert en langage naturel vers SQL pour PostgreSQL. Ton unique réponse DOIT être la requête SQL valide, sans aucun autre texte, explication, ou bloc de code." +
                        "\n\nVoici le schéma de la base de données :\n<SCHEMA>\n" + schema + "\n</SCHEMA>\n";

        List<Message> history = new ArrayList<>();
        history.add(Message.system(ChatMessage.builder().message(systemInstruction).build()));
        return history;
    }

    /** ÉTAPE 1 : Traduit la question de l'utilisateur en une requête SQL. */
    public String generateSql(String question, int role) throws Exception {
        if(role != 1 && role != 2){
            if(!question.toLowerCase().matches(".*(sorti|entré).*")){
                if(!question.toLowerCase().matches("(?i).*(employ[ée]|employe).*(nom|prénom|adresse|email|téléphone).*|.*(nom|prénom|adresse|email|téléphone).*(employ[ée]|employe).*")){
                    throw new ErrorConfirmException("Vous ne pouvez questionner que les informations des noms, prénoms, adresse email et des numéros de télephones des employés. ");
                }
            }
        }
        else{
            if(question.toLowerCase().matches(".*date.*(naissance|modification|création).*")) throw new ErrorConfirmException("Je suis dans l'impossibilité de divulger ses informations");
            if(question.toLowerCase().matches(".*mot de passe.*")) throw new ErrorConfirmException("Je suis dans l'impossibilité de divulger le mot de passe.");
            if(question.toLowerCase().matches(".*ajout.*")) throw new ErrorConfirmException("Impossible de faire des requêtes de type \" INSERT \".");
            if(question.toLowerCase().matches(".*modifi.*")) throw new ErrorConfirmException("Impossible de faire des requêtes de type \" UPDATE \".");
        }
        if(question.toLowerCase().matches(".*(mode d'emploi|utilis(ation|er)|manuel).*")) specificQuestion(question, role);

        List<Message> currentHistory = new ArrayList<>(baseHistory);

        NonStreamedChatResponse response = cohere.chat(
                ChatRequest.builder()
                        .message(question)
                        .chatHistory(currentHistory)
                        .temperature(0.0F)
                        .model(MODEL_NAME) // Utilisation de command-r-plus
                        .build()
        );
        // CORRECTION : Utilisation de getText()
        return response.getText().trim();
    }

    /** ÉTAPE 3 : Reformule les résultats bruts (JSON) en une réponse conversationnelle. */
    public String reformulateResponse(String userQuestion, String jsonResults) throws Exception {

        String systemInstruction =
                "Tu es un assistant convivial et concis. Ta tâche est de reformuler les données brutes (format JSON) ci-dessous en une réponse directe à la question de l'utilisateur. Ne donne que la réponse reformulée, sans préambule ni explications.";

        String reformulationPrompt = String.format(
                "Question de l'utilisateur : %s\n\nRésultats de la base de données (JSON) : %s",
                userQuestion,
                jsonResults
        );

        NonStreamedChatResponse response = cohere.chat(
                ChatRequest.builder()
                        .message(reformulationPrompt)
                        .temperature(0.2F)
                        .chatHistory(List.of(Message.system(ChatMessage.builder().message(systemInstruction).build())))
                        .model(MODEL_NAME) // Utilisation de command-r-plus
                        .build()
        );
        // CORRECTION : Utilisation de getText()
        return response.getText().trim();
    }

    public void specificQuestion(String question, int role) throws Exception {
        if(question.toLowerCase().matches(".*(chat)?.*")){
            throw new ErrorConfirmException("L'utilisateur peut poser des questions au chat suivant tous les archétypes: articles, employé, types d'articles, entrées et sorties et fournisseurs");
        } else if(question.toLowerCase().matches(".*stock.*")){
            throw new ErrorConfirmException("L'utilisateur peut poser des questions la nature du stock (approfondi) d'un ou plusieurs articles au chat");
        } else if(question.toLowerCase().matches(".*employés.*")){
            throw new ErrorConfirmException("L'utilisateur peut poser des questions sur les informations des employés (sauf le mot de passe"+ ( (role > 2) ? " et les dates de modification, de création pour les employés simples": "") +") au chat");
        } else if(question.toLowerCase().matches(".*articles|types.*")){
            throw new ErrorConfirmException("L'utilisateur peut poser des questions sur les articles, types (description, nom, quantité en stock) au chat");
        } else if(question.toLowerCase().matches(".*entrée|sortie.*")){
            throw new ErrorConfirmException("L'utilisateur peut poser des questions sur les informations des entrées et/ou sorties au chat");
        } else if(question.toLowerCase().matches(".*fournisseur.*")){
            throw new ErrorConfirmException("L'utilisateur peut poser des questions sur les informations des fournisseurs au chat");
        }
    }

}