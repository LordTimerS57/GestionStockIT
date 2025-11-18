package com.gestion_stock_it.ArtType.Article;

import com.gestion_stock_it.DatabaseConnection;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;

@WebServlet("/DeleteArticleServlet")
public class DeleteArticleServlet extends HttpServlet {

    private DatabaseConnection db;
    private Connection c;
    @Override
    public void init() {
        db = new DatabaseConnection();
        db.connect();
        c = db.getConnection();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        String tagArticle = req.getParameter("tag_article");

        ArticleDataController fn = new ArticleDataController();
        try {
            fn.deleteArticle(c, tagArticle);

            EmployeWebSocket.notifyAllEmployes("refresh_data", "Mise à jour des données");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void destroy() {
        db.disconnect();
    }
}
