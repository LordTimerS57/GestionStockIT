package com.gestion_stock_it.ArtType.Type;

import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet({"/UpdateTypeServlet", "/Types/Modification"})
public class UpdateTypeServlet extends HttpServlet {

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

        String tag_type = req.getParameter("tag_type");
        String nom = req.getParameter("nom_type");
        String description = req.getParameter("description_type");

        try{
            TypeArticleDataController.testError(nom, description);

            TypeArticle t = new TypeArticle
                    (
                            tag_type,
                            nom,
                            description
                    );

            TypeArticleDataController fn = new TypeArticleDataController();
            fn.updateTypeArticle(c, tag_type, t);

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
        String tag_type = (String) session.getAttribute("Tag_type");

        TypeArticleDataController fn = new TypeArticleDataController();
        TypeArticle type = null;

        try {
            type = fn.getTypeArticleByTag(c, tag_type);
        }
        catch (ServletException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("type", type);
        req.getRequestDispatcher("/Articles-Types/Type/ModifyModalType.jsp").forward(req, resp);
    }


    @Override
    public void destroy() {
        db.disconnect();
    }

}
