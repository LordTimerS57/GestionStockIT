package com.gestion_stock_it.ArtType.Article;

import com.gestion_stock_it.ArtType.Type.TypeArticleDataController;
import com.gestion_stock_it.ArtType.Type.TypeArticle;
import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet({"/AddArticleServlet", "/Articles/Creation"})
public class AddArticleServlet extends HttpServlet {

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

        String tag_type = req.getParameter("type_article");
        String nom = req.getParameter("nom_article");
        String description = req.getParameter("description_article");
        try{
            ArticleDataController.testError(tag_type, nom, description);
            Article a = new Article
                    (
                            null,
                            nom,
                            description,
                            new TypeArticle
                                    (
                                            tag_type,
                                            null,
                                            null
                                    ),
                            0
                    );
            ArticleDataController fn = new ArticleDataController();
            a.setTag_article(fn.nextTagArticle(c));

            fn.addArticle(c, a);
            EmployeWebSocket.notifyAllEmployes("refresh_data", "Mise à jour des données");
            resp.setStatus(200);

        }
        catch (ErrorConfirmException errors) {
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
        TypeArticleDataController dao = new TypeArticleDataController();
        List<TypeArticle> types = null;
        try {
            types = dao.getTypeArticleList(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        req.setAttribute("types", types);
        req.getRequestDispatcher("/Articles-Types/Article/AddModalArticle.jsp").forward(req, resp);
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

}
