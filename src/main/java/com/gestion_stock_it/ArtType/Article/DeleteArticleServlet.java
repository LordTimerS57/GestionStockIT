package com.gestion_stock_it.ArtType.Article;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteArticleServlet")
public class DeleteArticleServlet extends HttpServlet {

	private ArticleDataController fn;
	
    @Override
    public void init() {
        fn = new ArticleDataController();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        String tagArticle = req.getParameter("tag_article");

        try {
            fn.deleteArticle(tagArticle);

            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2,3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
