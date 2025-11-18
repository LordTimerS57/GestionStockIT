package com.gestion_stock_it.ArtType.Type;

import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/AddTypeServlet")
public class AddTypeServlet extends HttpServlet {

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

        String nom = req.getParameter("nom_type");
        String description = req.getParameter("description_type");
        try{
            TypeArticleDataController.testError(nom, description);
            TypeArticle t = new TypeArticle
                    (
                            null,
                            nom,
                            description
                    );

            TypeArticleDataController fn = new TypeArticleDataController();
            t.setTag_type(fn.nextTagTypeArticle(c));

            fn.addTypeArticle(c, t);

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

    @Override
    public void destroy() {
        db.disconnect();
    }

}

