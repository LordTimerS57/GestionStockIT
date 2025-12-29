package com.gestion_stock_it.Flux;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/Entrees", "/Sorties"})
public class ListFluxServlet extends HttpServlet {
	
	private FluxDataController dao;

	@Override
	public void init() {
		dao = new FluxDataController();
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String nomArticle = request.getParameter("article");
        String dateFlux = request.getParameter("date_flux");
        String dateParams = request.getParameter("date_params");

        switch (path){
            case "/Entrees": {
                List<Entree> entrees = new ArrayList<>();
                try {
                	String expediteur = request.getParameter("expediteur");
                    entrees = dao.getEntreeList(null, nomArticle, expediteur, dateFlux, dateParams);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if(entrees.isEmpty() || (nomArticle == null && dateFlux == null && dateParams == null)){
                    request.setAttribute("rapport_button", false);
                }
                else{
                    request.setAttribute("rapport_button", true);
                }
                request.setAttribute("entrees", entrees);
                request.setAttribute("content", "/Flux/AcceuilEntree.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;
            }
            case "/Sorties": {
                List<Sortie> sorties = new ArrayList<>();
                try {
                    String expediteur = request.getParameter("expediteur");
                    String destinataire = request.getParameter("destinataire");
                    
                    
                    sorties = dao.getSortieList(null, nomArticle, expediteur, destinataire, dateFlux, dateParams);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if(sorties.isEmpty() || (nomArticle == null && dateFlux == null && dateParams == null)){
                    request.setAttribute("rapport_button", false);
                }
                else {
                    assert nomArticle != null;
                    if (nomArticle.trim().isEmpty() && dateFlux.trim().isEmpty() && dateParams.trim().isEmpty()) {
                            request.setAttribute("rapport_button", false);
                    }
                    else {
                        request.setAttribute("rapport_button", true);
                    }
                }
                request.setAttribute("sorties", sorties);
                request.setAttribute("content", "/Flux/AcceuilSortie.jsp");
                request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
                break;
            }
            default: {
                break;
            }
        }

    }
}
