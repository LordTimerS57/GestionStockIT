package com.gestion_stock_it.Employe;

import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.websocket.Session;

import java.io.IOException;
import java.sql.Connection;

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

        String matricule = req.getParameter("matricule");
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("mot_de_passe");

        Employe e;

        EmployeDataController fn = new EmployeDataController();
        try {
            e = fn.testMotDePasse(matricule, motDePasse, email);
            switch (path) {
                case "/LogoutServlet": {
                    fn.connect(c,  e.getMatricule(), email, motDePasse, "non");

                    HttpSession session = SessionRegistryEmploye.getHttpSession(e.getMatricule());

                    if (session != null) {
                        session.invalidate();
                        SessionRegistryEmploye.remove(e.getMatricule());
                    } else {
                        System.out.println("Logout : session déjà inexistante pour " + e.getMatricule());
                    }
                    Session ws = EmployeWebSocket.getWebSocketSession(e.getMatricule());
                    if (ws != null && ws.isOpen()) {
                        try {
                            ws.close();
                            System.out.println("WebSocket fermé pour " + e.getMatricule());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Logout success");
                    break;
                }

                case "/LoginServlet": {
                    fn.connect(c,  e.getMatricule(), email, motDePasse, "oui");

                    HttpSession session = req.getSession();
                    session.setAttribute("login_role",e.getRole());
                    session.setAttribute("login_profil",e);

                    SessionRegistryEmploye.register(e.getMatricule(), session);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    break;
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
