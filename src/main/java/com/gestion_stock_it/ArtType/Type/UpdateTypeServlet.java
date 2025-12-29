package com.gestion_stock_it.ArtType.Type;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet({"/UpdateTypeServlet", "/Types/Modification"})
public class UpdateTypeServlet extends HttpServlet {

    private TypeArticleDataController fn;
    @Override
    public void init() {
        fn = new TypeArticleDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String tag_type = req.getParameter("tag_type");
        String nom = req.getParameter("nom_type");
        String description = req.getParameter("description_type");

        try{
            TypeArticle t = fn.testError(nom, description, "modify");

            fn.updateTypeArticle(tag_type, t);

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
        String tag_type = (String) session.getAttribute("Tag_type");

        TypeArticle type = null;

        try {
            type = fn.getTypeArticleByTag(tag_type);
        }
        catch (ServletException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("type", type);
        req.getRequestDispatcher("/Articles-Types/Type/ModifyModalType.jsp").forward(req, resp);
    }

}
