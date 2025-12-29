package com.gestion_stock_it.Flux;

import com.gestion_stock_it.ArtType.Article.Article;
import com.gestion_stock_it.ArtType.Article.ArticleDataController;
import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.Employe.EmployeDataController;
import com.gestion_stock_it.Employe.Employe;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet({"/AddSortieServlet" , "/Sorties/Creation"})
public class AddSortieServlet extends HttpServlet {
    
	private FluxDataController fn;
    private ArticleDataController dao1;
    EmployeDataController dao2 = new EmployeDataController();
    
    @Override
    public void init() {
        fn = new FluxDataController();
        dao1 = new ArticleDataController();
		dao2 = new EmployeDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String destinataire = req.getParameter("destinataire");
        String expediteur = req.getParameter("expediteur");
        String tagArticle = req.getParameter("tag_article");
        String articleDeplace = req.getParameter("nombre_article_deplace");

        try {
        	Sortie s = (Sortie) fn.testError(tagArticle, destinataire, expediteur, articleDeplace, "Sortie");

            fn.addSortie(s);
            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2,3);
            resp.setStatus(200);
        }
        catch (ErrorConfirmException errors) {
            resp.setStatus(400);
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
        String nomArticle = req.getParameter("nom_article");
        String nomPrenomOuMatricule = req.getParameter("nom_prenom_ou_matricule");

        List<Article> articles = null;
        List<Employe> destinataires = null;

        try {
            articles = dao1.getArticleList(nomArticle, null);
            destinataires = dao2.getEmployeList(null, nomPrenomOuMatricule, -1, null,  null, "oui");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("articles_recherches", articles);
        req.setAttribute("destinataires_recherches", destinataires);
        req.getRequestDispatcher("/Flux/AddModalOut.jsp").forward(req, resp);
    }
}
