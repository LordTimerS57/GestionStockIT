package com.gestion_stock_it.ArtType.Article;

import com.gestion_stock_it.ArtType.Type.TypeArticle;
import com.gestion_stock_it.ArtType.Type.TypeArticleDataController;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet({"/AddArticleServlet", "/Articles/Creation"})
public class AddArticleServlet extends HttpServlet {

	private TypeArticleDataController dao;
	private ArticleDataController fn;
	
    @Override
    public void init() {
        dao = new TypeArticleDataController();
        fn = new ArticleDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String tag_type = req.getParameter("type_article");
        String nom = req.getParameter("nom_article");
        String description = req.getParameter("description_article");
        try{
            Article a = fn.testError(tag_type, nom, description, "add");
            
            fn.addArticle(a);
            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2,3);
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

}
