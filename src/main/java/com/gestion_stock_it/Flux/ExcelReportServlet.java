package com.gestion_stock_it.Flux;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet({"/RapportExcel/Entrees","/RapportExcel/Sorties"})
public class ExcelReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String nomArticle = request.getParameter("nom_article");
        String dateFlux = request.getParameter("date_flux");
        String dateParams = request.getParameter("date_params");

        FluxDataController dao = new FluxDataController();

        try {
            if (path.equals("/RapportExcel/Entrees")) {
                List<Entree> entrees = dao.getEntreeList(null, nomArticle, null, dateFlux, dateParams);
                ExcelReportGenerator.generateReport(entrees, response, "Entree", dateParams, dateFlux, nomArticle);
            } else if (path.equals("/RapportExcel/Sorties")) {
                List<Sortie> sorties = dao.getSortieList(null, nomArticle, null, null, dateFlux, dateParams);
                ExcelReportGenerator.generateReport(sorties, response, "Sortie", dateParams, dateFlux, nomArticle);
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la génération du rapport Excel", e);
        }
    }
}

