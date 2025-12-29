package com.gestion_stock_it.ArtType.Type;

import com.gestion_stock_it.Employe.EmployeWebSocket;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteTypeServlet")
public class DeleteTypeServlet extends HttpServlet {

	private TypeArticleDataController fn;
	
    @Override
    public void init() {
    	fn = new TypeArticleDataController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        String tagType = req.getParameter("tag_type");
        
        try {
            fn.deleteTypeArticle(tagType);
            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2,3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
