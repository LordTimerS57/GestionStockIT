package com.gestion_stock_it.Employe;

import com.gestion_stock_it.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/DeleteEmployeServlet")
public class DeleteEmployeServlet extends HttpServlet {

    private DatabaseConnection db;
    private Connection c;

    @Override
    public void init() {
        db = new DatabaseConnection();
        db.connect();
        c = db.getConnection();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String matricule = request.getParameter("matricule");

        try{
            EmployeDataController fn = new EmployeDataController();
            Employe employe = fn.getEmployeByMatricule(matricule);
            Employe superAdmin = fn.getEmployeList(null, null, 1, null,  "oui", null).getFirst();


            // ⚡ Envoi instantané du logout via WebSocket
            if (employe.getConnection()) {
                EmployeWebSocket.forceLogout(matricule);
            }

            employe.changeActivite();
            fn.deleteEmploye(c, employe.getActivite(), matricule);

            if(employe.getActivite()){
                fn.sendEmail(employe, superAdmin, "Réactivation de votre compte");
            }
            else{
                fn.sendEmail(employe, superAdmin, "Désactivation de votre compte");
            }

            EmployeWebSocket.notifyAllEmployes("refresh_data", "Mise à jour des données");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        db.disconnect();
    }
}
