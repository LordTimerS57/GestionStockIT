package com.gestion_stock_it.Fournisseur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Fournisseurs")
public class ListFournisseurServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nom = request.getParameter("nom_fournisseur");

        FournisseurDataController dao = new FournisseurDataController();
        List<Fournisseur> fournisseurs = new ArrayList<>();

        try {
            fournisseurs = dao.getFournisseurList(null, nom, null, null);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        request.setAttribute("fournisseurs", fournisseurs);
        request.setAttribute("content", "/Fournisseur/AcceuilFournisseur.jsp");

        request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    }
}
