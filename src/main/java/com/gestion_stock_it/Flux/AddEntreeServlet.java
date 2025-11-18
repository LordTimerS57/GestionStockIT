package com.gestion_stock_it.Flux;

import com.gestion_stock_it.ArtType.Article.Article;
import com.gestion_stock_it.ArtType.Article.ArticleDataController;
import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.Employe.Employe;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import com.gestion_stock_it.Fournisseur.FournisseurDataController;
import com.gestion_stock_it.Fournisseur.Fournisseur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet({"/AddEntreeServlet", "/Mouvements/Entrees/Creation"})
public class AddEntreeServlet extends HttpServlet {

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
        FluxDataController.testError(tagArticle, destinataire, expediteur, articleDeplace, "Entree");

        Entree e = new Entree(
                null,
                new Employe(destinataire,null,null,null,null,null,null, null, -1, true, true, null, null),
                new Fournisseur(expediteur, null, null, null),
                new Article(tagArticle, null, null, null, 0),
                Long.parseLong(articleDeplace),
                LocalDateTime.now()
        );

        e.setTag_flux(FluxDataController.nextTagFlux(c, "Entree"));

        FluxDataController fn =  new FluxDataController();

            fn.addEntree(c, e);

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nomArticle = request.getParameter("nom_article");
        String raisonSociale = request.getParameter("raison_sociale");

        ArticleDataController dao1 = new ArticleDataController();
        FournisseurDataController dao2 = new FournisseurDataController();

        List<Article> articles = null;
        List<Fournisseur> fournisseurs = null;

        try {
            articles = dao1.getArticleList(nomArticle, null);
            fournisseurs = dao2.getFournisseurList(null, raisonSociale, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("articles_recherches", articles);
        request.setAttribute("fournisseurs_recherches", fournisseurs);
        request.getRequestDispatcher("/Flux/AddModalIn.jsp").forward(request, response);

    }

    @Override
    public void destroy() {
        db.disconnect();
    }

}
