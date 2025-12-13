package com.gestion_stock_it;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {
		"/Acceuil",
        "/Profil",
        "/CreationCompte", "/Profil/Modification",
        "/Fournisseurs/Creation",
        "/Mouvements",
        "/Types/Creation",
        "/Chatbot",
        "/Connexion"
})
public class RouteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	try {
    		String path = request.getServletPath();

            switch (path) {
            	case "/Acceuil":
    				request.setAttribute("content", "/Acceuil.jsp");
    				request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    				break;
    				
    			// ---- AUTHENTIFICATION ----
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
                    request.getRequestDispatcher("/Fournisseur/AddModalFournisseur.jsp").forward(request, response);
                    break;

                // ---- TYPE ARTICLE ----
                case "/Types/Creation":
                    request.getRequestDispatcher("/Articles-Types/Type/AddModalType.jsp").forward(request, response);
                    break;

                // ---- CHATBOT ----
                case "/Chatbot":
                    request.setAttribute("content", "/ChatBot/Chatbot.jsp");
                    request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                    break;

                // ---- ERREUR ----
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
    	}
    	catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
