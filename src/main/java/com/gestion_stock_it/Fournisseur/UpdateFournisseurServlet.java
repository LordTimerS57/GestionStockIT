package com.gestion_stock_it.Fournisseur;

import com.gestion_stock_it.DatabaseConnection;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

@WebServlet({"/UpdateFournisseurServlet", "/Fournisseurs/Modification"})
public class UpdateFournisseurServlet extends HttpServlet {

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

        String oldTag = req.getParameter("old_tag_fournisseur");
        String tag = req.getParameter("tag_fournisseur");
        String raisonSociale = req.getParameter("raison_sociale");
        String email = req.getParameter("email_fournisseur");
        String telephone = req.getParameter("telephone_fournisseur");

        Fournisseur f = new Fournisseur(tag.equals(oldTag) ? oldTag : tag, raisonSociale, email, telephone);

        FournisseurDataController fn = new FournisseurDataController();
        try {
            FournisseurDataController.testError(tag, raisonSociale, email, telephone);
            fn.updateFournisseur(c,oldTag,f);

            EmployeWebSocket.notifyAllEmployes("refresh_data", "Mise à jour des données");
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

        FournisseurDataController fn = new FournisseurDataController();
        Fournisseur fournisseur = null;

        try {
            fournisseur = fn.getFournisseurByTag(c, tag_fournisseur);
        }
        catch (ServletException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("fournisseur", fournisseur);
        req.getRequestDispatcher("/Fournisseur/ModifyModalFournisseur.jsp").forward(req, resp);

    }

    @Override
    public void destroy() {
        db.disconnect();
    }
}
