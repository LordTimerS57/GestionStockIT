package com.gestion_stock_it.ArtType.Article;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/Articles-Types/Articles")
public class ListArticleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nomArticle = request.getParameter("nom_article");
        String nomType = request.getParameter("nom_type");

        System.out.println("Nom article: " + nomArticle + " Nom type: " + nomType);

        ArticleDataController dao = new ArticleDataController();
        List<Article> articles;

        try {
            // Si aucun filtre, récupère tous les articles
            articles = dao.getArticleList(nomArticle, nomType);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        request.setAttribute("articles", articles);
        request.setAttribute("content", "/Articles-Types/Article/AcceuilArticle.jsp");

        request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    }
}
