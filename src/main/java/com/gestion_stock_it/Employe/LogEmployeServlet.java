package com.gestion_stock_it.Employe;

import java.io.IOException;
import java.sql.Connection;

import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

@WebServlet({"/LoginServlet","/LogoutServlet"})
public class LogEmployeServlet extends HttpServlet {

    private DatabaseConnection db;
    private Connection c;
    @Override
    public void init() {
        db = new DatabaseConnection();
        db.connect();
        c = db.getConnection();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        System.out.println("Received " + path + " request");

        String matricule = req.getParameter("matricule");

        EmployeDataController fn = new EmployeDataController();
        try {
            switch (path) {
                case "/LogoutServlet": {
                    HttpSession session = SessionRegistryEmploye.getHttpSession(matricule);

                    if (session != null) {
                        session.invalidate();
                        SessionRegistryEmploye.remove(matricule);
                    } else {
                        System.out.println("Logout : session déjà inexistante pour " + matricule);
                    }
                    Session ws = EmployeWebSocket.getWebSocketSession(matricule);
                    if (ws != null && ws.isOpen()) {
                        try {
                            ws.close();
                            System.out.println("WebSocket fermé pour " + matricule);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    fn.connect(c,  matricule, null, null, "non");
                    
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Logout success");
                    break;
                }

                case "/LoginServlet": {

                    String email = req.getParameter("email");
                    String motDePasse = req.getParameter("mot_de_passe");
                    
                	Employe e = fn.testMotDePasse(matricule, motDePasse, email);
                	if(e != null) {
                		fn.connect(c,  e.getMatricule(), email, motDePasse, "oui");

                        HttpSession session = req.getSession();
                        session.setAttribute("login_role",e.getRole());
                        session.setAttribute("login_profil",e);

                        SessionRegistryEmploye.register(e.getMatricule(), session);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write("Login success");
                        break;		
                	}
                }
            }
            EmployeWebSocket.notifyAllEmployes("refresh_data", "Mise à jour des données");
        } catch (ErrorConfirmException errors) {
            resp.setStatus(400);
            resp.setContentType("text/plain;charset=UTF-8");

            for (String msg : errors.getMessages()) {
                System.out.println(msg);
                resp.getWriter().write(msg + "\n");
            }
        } catch (Exception ex) {
            resp.setStatus(500);
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if (path.equals("/ForceLogout")) {
            String activite = req.getParameter("activite");
            HttpSession session = req.getSession(false);
            if (session != null) {
                if (activite.equals("desactivate")) {
                    session.setAttribute("activite", false);
                } else {
                    session.setAttribute("activite", true);
                }
            }
            resp.setStatus(200);
        }
    }

    @Override
    public void destroy() {
        db.disconnect();
    }
}
