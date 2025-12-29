package com.gestion_stock_it.Flux.Excel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import com.gestion_stock_it.Flux.Entree;
import com.gestion_stock_it.Flux.FluxDataController;
import com.gestion_stock_it.Flux.Sortie;

@WebServlet({"/RapportExcel/Entrees","/RapportExcel/Sorties"})
public class ExcelReportServlet extends HttpServlet {
	
	private FluxDataController dao;
	
	@Override
	public void init() {
		dao = new FluxDataController();
	}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String nomArticle = request.getParameter("article");
        String dateFlux = request.getParameter("date_flux");
        String dateParams = request.getParameter("date_params");
        
        System.out.println("Generating Excel report for path: " + path + ", nomArticle: " + nomArticle 
				+ ", dateFlux: " + dateFlux + ", dateParams: " + dateParams);

        try {
            if (path.equals("/RapportExcel/Entrees")) {
            	String expediteur = request.getParameter("expediteur");
                List<Entree> entrees = dao.getEntreeList(null, nomArticle, expediteur, dateFlux, dateParams);
                ExcelReportGenerator.generateReport(entrees, response, "Entree", dateParams, dateFlux, nomArticle, expediteur, null);
            } else if (path.equals("/RapportExcel/Sorties")) {
            	String expediteur = request.getParameter("expediteur");
            	String destinataire = request.getParameter("destinataire");
                List<Sortie> sorties = dao.getSortieList(null, nomArticle, expediteur, destinataire, dateFlux, dateParams);
                ExcelReportGenerator.generateReport(sorties, response, "Sortie", dateParams, dateFlux, nomArticle, expediteur, destinataire);
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la génération du rapport Excel", e);
        }
    }
    
}

