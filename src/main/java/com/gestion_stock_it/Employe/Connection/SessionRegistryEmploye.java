package com.gestion_stock_it.Employe.Connection;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionRegistryEmploye {

    // Stocke la correspondance matricule -> HttpSession
    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    public static void register(String matricule, HttpSession session) {
        sessions.put(matricule, session);
        System.out.println("Session HTTP enregistrée pour " + matricule);
    }

    public static HttpSession getHttpSession(String matricule) {
        return sessions.get(matricule);
    }

    public static void remove(String matricule) {
        sessions.remove(matricule);
        System.out.println("Session HTTP supprimée pour " + matricule);
    }
}
