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

@WebServlet({"/UpdateArticleServlet", "/Articles/Modification"})
public class UpdateArticleServlet extends HttpServlet {

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

        String tag_article = req.getParameter("tag_article");
        String tag_type = req.getParameter("type_article");
        String nom = req.getParameter("nom_article");
        String description = req.getParameter("description_article");
        try
        {
            ArticleDataController.testError(tag_type,nom,description);
            Article a = new Article
                    (
                            tag_article,
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
            fn.updateArticle(c, tag_article, a, null, null);

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
        String tag_article= (String) session.getAttribute("Tag_article");
        System.out.println("tag_article:"+tag_article);

        ArticleDataController fn = new ArticleDataController();

        TypeArticleDataController fn1 = new TypeArticleDataController();
        Article article = null;
        List<TypeArticle> types = null;

        try {
            article = fn.getArticleByTag(c,tag_article);
            types = fn1.getTypeArticleList(null);
        }
        catch (ServletException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("article", article);
        req.setAttribute("types", types);
        req.getRequestDispatcher("/Articles-Types/Article/ModifyModalArticle.jsp").forward(req, resp);

    }

    @Override
    public void destroy() {
        db.disconnect();
    }


}
