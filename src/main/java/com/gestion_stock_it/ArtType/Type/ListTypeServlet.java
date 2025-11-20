package com.gestion_stock_it.ArtType.Type;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/Types")
public class ListTypeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nomType = request.getParameter("nom_type");

        TypeArticleDataController dao = new TypeArticleDataController();
        List<TypeArticle> types = new ArrayList<>();

        try {
            // Si aucun filtre, récupère tous les articles
            types = dao.getTypeArticleList(nomType);
            
        } catch (Exception e) {
            throw new ServletException(e);
        }

        request.setAttribute("types", types);
        request.setAttribute("content", "/Articles-Types/Type/AcceuilType.jsp");

        request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    }
}
