package com.gestion_stock_it.ArtType.Article;

import com.gestion_stock_it.ArtType.Type.TypeArticleDataController;
import com.gestion_stock_it.ArtType.Type.TypeArticle;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet({"/UpdateArticleServlet", "/Articles/Modification"})
public class UpdateArticleServlet extends HttpServlet {

	private TypeArticleDataController dao;
	private ArticleDataController fn;
	
    @Override
    public void init() {
        dao = new TypeArticleDataController();
        fn = new ArticleDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String tag_article = req.getParameter("tag_article");
        String tag_type = req.getParameter("type_article");
        String nom = req.getParameter("nom_article");
        String description = req.getParameter("description_article");
        try
        {
            Article a = fn.testError(tag_type,nom,description,"modify");
            fn.updateArticle(tag_article, a, null, null);

            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2,3);
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

        Article article = null;
        List<TypeArticle> types = null;

        try {
            article = fn.getArticleByTag(tag_article);
            types = dao.getTypeArticleList(null);
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

}
