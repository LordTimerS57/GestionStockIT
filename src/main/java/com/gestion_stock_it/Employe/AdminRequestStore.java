package com.gestion_stock_it.Employe;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AdminRequestStore {

    private static class Request {
        String response;
        CountDownLatch latch = new CountDownLatch(1);
    }

    private static final ConcurrentHashMap<String, Request> pendingRequests = new ConcurrentHashMap<>();

    // 🔹 Appelé par AddEmployeServlet : créer une demande en attente
    public static boolean createRequest(String matricule) {
        Request newReq = new Request();
        Request existing = pendingRequests.putIfAbsent(matricule, newReq);
        return existing == null; // true si créé, false si déjà présent
    }


    // 🔹 Appelé par EmployeWebSocket : enregistrer la réponse de l'admin
    public static void saveResponse(String matricule, String response) {
        Request req = pendingRequests.get(matricule);
        if (req != null) {
            req.response = response;
            req.latch.countDown(); // libère le thread bloqué dans AddEmployeServlet
        }
    }

    // 🔹 Appelé par AddEmployeServlet : attendre la réponse
    public static String waitForResponse(String matricule, long timeoutSeconds) {
        Request req = pendingRequests.get(matricule);
        if (req == null) return null;
        try {
            boolean completed = req.latch.await(timeoutSeconds, TimeUnit.SECONDS);
            if (completed) {
                return req.response;
            } else {
                return "TIMEOUT";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "INTERRUPTED";
        } finally {
            pendingRequests.remove(matricule);
        }
    }
}
