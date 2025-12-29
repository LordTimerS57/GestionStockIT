package com.gestion_stock_it.ArtType.Type;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/AddTypeServlet")
public class AddTypeServlet extends HttpServlet {

   private TypeArticleDataController fn;
   
   @Override
    public void init() {
		fn = new TypeArticleDataController();
	}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String nom = req.getParameter("nom_type");
        String description = req.getParameter("description_type");
        try{
            TypeArticle t = fn.testError(nom, description, "add");

            fn.addTypeArticle(t);

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

}

