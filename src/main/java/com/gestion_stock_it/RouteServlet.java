package com.gestion_stock_it;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {
        "/Profil",
        "/Articles-Types",
        "/CreationCompte", "/Profil/Modification",
        "/Fournisseurs/Creation",
        "/Mouvements",
        "/Articles-Types/Types/Creation",
        "/Chatbot",
        "/Connexion"
})
public class RouteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/Connexion":
                request.getRequestDispatcher("/Employe/Login.jsp").forward(request, response);
                break;

            // ---- EMPLOYE ----
            case "/CreationCompte":
                request.getRequestDispatcher("/Employe/AddModalEmploye.jsp").forward(request, response);
                break;

            case "/Profil":
                request.setAttribute("content", "/Employe/Profile.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;

            case "/Profil/Modification":
                request.getRequestDispatcher("/Employe/ModifyModalEmploye.jsp").forward(request, response);
                break;

            // ---- FOURNISSEUR ----
            case "/Fournisseurs/Creation":
                request.setAttribute("content", "/Fournisseur/AddModalFournisseur.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;

            case "/Fournisseurs/Modification":
                request.setAttribute("content", "/Fournisseur/ModifyModalFournisseur.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;

            // ---- MOUVEMENTS D'ARTICLES ----
            case "/Mouvements":
                request.setAttribute("content", "/Flux/AcceuilFlux.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;

            // ---- TYPE ARTICLE ----
            case "/Articles-Types/Types/Creation":
                request.getRequestDispatcher("/Articles-Types/Type/AddModalType.jsp").forward(request, response);
                break;

            // ---- ARTICLES - TYPES ----
            case "/Articles-Types":
                request.setAttribute("content", "/Articles-Types/AcceuilArtType.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;



            // ---- CHATBOT ----
            case "/Chatbot":
                request.setAttribute("content", "/IA/Chatbot.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;

            // ---- ERREUR ----
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
