package com.gestion_stock_it.ArtType.Article;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Articles")
public class ListArticleServlet extends HttpServlet {
	
	private ArticleDataController dao;
	
    @Override
    public void init() {
        dao = new ArticleDataController();
    }
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nomArticle = request.getParameter("nom_article");
        String nomType = request.getParameter("nom_type");

        System.out.println("Nom article: " + nomArticle + " Nom type: " + nomType);
        List<Article> articles = new ArrayList<>();

        try {
            articles = dao.getArticleList(nomArticle, nomType);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        request.setAttribute("articles", articles);
        request.setAttribute("content", "/Articles-Types/Article/AcceuilArticle.jsp");

        request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    }
    
}
