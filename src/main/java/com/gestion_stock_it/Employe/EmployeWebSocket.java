package com.gestion_stock_it.Employe;

import com.gestion_stock_it.Employe.Connection.SessionRegistryEmploye;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/EmployeLog/{matricule}/{role}")
public class EmployeWebSocket {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<Integer, Set<Session>> sessionsRole = new ConcurrentHashMap<>();

    private static final Gson gson = new Gson();

    // ✅ Lorsqu’une connexion WebSocket s’ouvre
    @OnOpen
    public void onOpen(Session session, @PathParam("matricule") String matricule, @PathParam("role") int role) {
        sessions.put(matricule, session);
        sessionsRole.computeIfAbsent(role, k -> ConcurrentHashMap.newKeySet()).add(session);
        System.out.println("WebSocket ouvert pour " + matricule + " avec rôle " + role);
    }

    // ✅ Lorsqu’une connexion WebSocket se ferme
    @OnClose
    public void onClose(Session session, @PathParam("matricule") String matricule) {
        removeFromSessionsRole(session);
        
        sessions.remove(matricule);
        System.out.println("WebSocket fermé pour : " + matricule);
    }

    // ✅ En cas d’erreur
    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("matricule") String matricule) {
        sessions.remove(matricule);
        removeFromSessionsRole(session);
        throwable.printStackTrace();
    }

    // ✅ Lorsqu’un message arrive depuis le client
    @OnMessage
    public void onMessage(String message, @PathParam("matricule") String matricule) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.has("type") ? json.get("type").getAsString() : "";

            if ("passage_logout".equals(type)) {
                System.out.println("Destruction de la session HTTP pour : " + matricule);

                HttpSession httpSession = getHttpSessionFromMatricule(matricule);
                if (httpSession != null) {
                    httpSession.removeAttribute("login_role");
                    httpSession.removeAttribute("login_profil");
                    httpSession.removeAttribute("chats");
                }

                SessionRegistryEmploye.remove(matricule);
                Session ws = sessions.remove(matricule);
                removeFromSessionsRole(ws);
                if (ws != null && ws.isOpen()) ws.close();

                notifyAdmin("info", matricule + " s'est déconnecté.", "logout", null);

            } else if ("change_role".equals(type)) {
                
            } else {
                String receptMessage = json.has("message") ? json.get("message").getAsString() : "";
                String receptMatricule = json.has("matricule") ? json.get("matricule").getAsString() : "";

                if ("accept_admin".equals(type)) {
                    System.out.println("✅ Admin a accepté : " + receptMatricule);
                    AdminRequestStore.saveResponse(receptMatricule, "ACCEPTE : " + receptMessage);

                } else if ("deny_admin".equals(type)) {
                    System.out.println("❌ Admin a refusé : " + receptMatricule);
                    AdminRequestStore.saveResponse(receptMatricule, "REFUSE : " + receptMessage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Forcer la déconnexion d’un employé
    public static void forceLogout(String matricule) throws Exception {
        Session session = sessions.get(matricule);
        if (session != null && session.isOpen()) {
            try {
                EmployeDataController fn = new EmployeDataController();
                
                Employe e = fn.getEmployeByMatricule(matricule);
            	if(e != null && e.getConnection()) {
            		fn.connect(matricule, null, null, "non");
            	}
            	
            	String jsonMessage = gson.toJson(Map.of(
                        "type", "force_logout",
                        "message", "Votre compte a été désactivé."
                ));
                
                session.getBasicRemote().sendText(jsonMessage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void changeRole(Employe e, int role) throws Exception {
        if (e != null) { 
            Session session = sessions.get(e.getMatricule()); 
            if (session != null && session.isOpen()) {
                
                Employe employeUpdated = null;
                EmployeDataController fn = new EmployeDataController();

                try {
                	employeUpdated = fn.getEmployeByMatricule(e.getMatricule()); 
                } catch (Exception ex) {
                    // Gérer les erreurs de connexion BDD si getEmployeByMatricule les lance
                    ex.printStackTrace();
                }

                if (employeUpdated != null && employeUpdated.getRoleInt() == role) {
                    
                    // Mise à jour des sessions WebSocket
                    removeFromSessionsRole(session);
                    sessionsRole.computeIfAbsent(role, k -> ConcurrentHashMap.newKeySet()).add(session);

                    // Mise à jour de la session HTTP
                    HttpSession httpSession = getHttpSessionFromMatricule(e.getMatricule());
                    if (httpSession != null) {
                    	
                        httpSession.setAttribute("login_role", employeUpdated.getRole());
                        httpSession.setAttribute("login_profil", employeUpdated);
                        
                        System.out.println("Rôle de " + e.getMatricule() + " mis à jour dans la session HTTP.");
                    }

                    // Notification
                    notifyEmploye(e.getMatricule(), role); 
                } else {
                    System.err.println("Échec de relecture de l'employé après le changement de rôle : " + e.getMatricule());
                }
            }
        }
    }

    // ✅ Notifier les administrateurs avec un objet Java
    public static void notifyAdmin(String message, String matricule, String type, Employe employe) {
        int adminRoleId = 1;
        Set<Session> admins = sessionsRole.get(adminRoleId);
        if (admins != null) {
            admins.forEach(ws -> {
                if (ws != null && ws.isOpen()) {
                    try {
                        if(type.equals("notify_decision") && employe != null) {
                            String jsonMessage = gson.toJson(Map.of(
                                    "type", type,
                                    "message", message,
                                    "matricule", matricule,
                                    "nom_prenom_by_matricule", employe.getNomPrenom(),
                                    "email_by_matricule", employe.getEmail(),
                                    "telephone_by_matricule", employe.getTelephone(),
                                    "adresse_by_matricule", employe.getAdresse(),
                                    "date_naissance_by_matricule", employe.getDate_de_naissance_formatter()
                            ));
                            ws.getBasicRemote().sendText(jsonMessage);
                        }
                        else {
                            String jsonMessage = gson.toJson(Map.of(
                                    "type", type,
                                    "message", message,
                                    "matricule", matricule
                            ));
                            ws.getBasicRemote().sendText(jsonMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void notifyEmployes(String type, String message, int... targetRoles) {
        String jsonMessage = gson.toJson(Map.of(
                "type", type,
                "message", message
        ));
        for (int roleId : targetRoles) {
            
            Set<Session> sessionsForRole = sessionsRole.get(roleId);

            if (sessionsForRole != null) {
                for (Session s : sessionsForRole) {
                    if (s != null && s.isOpen()) {
                        try {
                            s.getBasicRemote().sendText(jsonMessage);
                        } catch (IOException e) {
                            System.err.println("Erreur envoi au rôle " + roleId + " : " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    
    public static void notifyEmploye(String matricule, int role) {
        Session session = sessions.get(matricule);
        if (session != null && session.isOpen()) {
            try {
                String roleName = (role == 1) ? "Administrateur" : (role == 2) ? "Sous Administrateur" : "Employé";
                String roleMessage = "Vous "+  (role == 1 || role == 2 ? "avez été attribué de" : "êtes destitué de vos") +" fonctions d'administrateur.";
                
                String jsonMessage = gson.toJson(Map.of(
                    "type", "role_updated",
                    "role_int", role,
                    "role_name", roleName,
                    "message", roleMessage
                ));
                
                session.getBasicRemote().sendText(jsonMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 🔧 Récupérer la session HTTP associée à un matricule
    private static HttpSession getHttpSessionFromMatricule(String matricule) {
        return SessionRegistryEmploye.getHttpSession(matricule);
    }

    // 🔧 Supprimer une session de tous les rôles
    private static void removeFromSessionsRole(Session session) {
        if (session != null) {
            sessionsRole.values().forEach(set -> set.remove(session));
        }
    }

    // 🔧 Obtenir la session WebSocket d’un employé
    public static Session getWebSocketSession(String matricule) {
        return sessions.get(matricule);
    }
}
