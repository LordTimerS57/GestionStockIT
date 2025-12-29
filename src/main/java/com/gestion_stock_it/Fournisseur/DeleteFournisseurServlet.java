package com.gestion_stock_it.Fournisseur;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteFournisseurServlet")
public class DeleteFournisseurServlet extends HttpServlet {

	private FournisseurDataController fn;
	
    @Override
    public void init() {
    	fn = new FournisseurDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        String tagFournisseur = req.getParameter("tag_fournisseur");
        System.out.println("tagFournisseur: " + tagFournisseur);

        FournisseurDataController fn = new FournisseurDataController();
        try {
            fn.deleteFournisseur(tagFournisseur);
            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
