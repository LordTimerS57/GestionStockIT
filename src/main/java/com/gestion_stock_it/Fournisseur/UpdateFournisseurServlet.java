package com.gestion_stock_it.Fournisseur;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet({"/UpdateFournisseurServlet", "/Fournisseurs/Modification"})
public class UpdateFournisseurServlet extends HttpServlet {

	private FournisseurDataController fn; 
	
    @Override
    public void init() {
        fn = new FournisseurDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String oldTag = req.getParameter("old_tag_fournisseur");
        String tag = req.getParameter("tag_fournisseur");
        String raisonSociale = req.getParameter("raison_sociale");
        String email = req.getParameter("email_fournisseur");
        String telephone = req.getParameter("telephone_fournisseur");
        
        try {
        	Fournisseur f = fn.testError(tag, oldTag, raisonSociale, email, telephone);
            fn.updateFournisseur(oldTag,f);

            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);
            resp.setStatus(200);

        } catch (ErrorConfirmException errors) {
            resp.setStatus(500);
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
        HttpSession session = req.getSession();
        String tag_fournisseur = (String) session.getAttribute("Tag_fournisseur");

        Fournisseur fournisseur = null;

        try {
            fournisseur = fn.getFournisseurByTag(tag_fournisseur);
        }
        catch (ServletException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("fournisseur", fournisseur);
        req.getRequestDispatcher("/Fournisseur/ModifyModalFournisseur.jsp").forward(req, resp);

    }
}
