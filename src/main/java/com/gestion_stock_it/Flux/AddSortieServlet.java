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

        String destinataire = req.getParameter("destinataire");
        String expediteur = req.getParameter("expediteur");
        String tagArticle = req.getParameter("tag_article");
        String articleDeplace = req.getParameter("nombre_article_deplace");

        try {
            FluxDataController.testError(tagArticle, destinataire, expediteur, articleDeplace, "Sortie");

            Sortie s = new Sortie(
                    null,
                    new Employe(destinataire,null,null,null,null,null,null, null, -1, true, true, null, null),
                    new Employe(expediteur,null,null,null,null,null,null, null, -1, true, true, null, null),
                    new Article(tagArticle, null, null, null, 0),
                    Long.parseLong(articleDeplace),
                    LocalDateTime.now()
            );

            s.setTag_flux(FluxDataController.nextTagFlux(c, "Sortie"));

            FluxDataController fn =  new FluxDataController();

            fn.addSortie(c, s);
            EmployeWebSocket.notifyAllEmployes("refresh_data", "Mise à jour des données");
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

        ArticleDataController dao1 = new ArticleDataController();
        EmployeDataController dao2 = new EmployeDataController();

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

    @Override
    public void destroy() {
        db.disconnect();
    }
}
