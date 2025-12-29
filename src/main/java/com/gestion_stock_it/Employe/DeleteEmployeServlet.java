package com.gestion_stock_it.Employe;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/DeleteEmployeServlet")
public class DeleteEmployeServlet extends HttpServlet {

    private EmployeDataController fn;

    @Override
    public void init() {
        fn = new EmployeDataController();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String matricule = request.getParameter("matricule");

        try{
            Employe employe = fn.getEmployeByMatricule(matricule);
            Employe superAdmin = fn.getEmployeList(null, null, 1, null,  "oui", null).getFirst();

            // ⚡ Envoi instantané du logout via WebSocket
            if (employe.getConnection()) {
                EmployeWebSocket.forceLogout(matricule);
            }

            employe.changeActivite();
            fn.deleteEmploye(employe.getActivite(), matricule);

            if(employe.getActivite()){
                fn.sendEmail(employe, superAdmin, "Réactivation de votre compte");
            }
            else{
                fn.sendEmail(employe, superAdmin, "Désactivation de votre compte");
            }

            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    } 
}
