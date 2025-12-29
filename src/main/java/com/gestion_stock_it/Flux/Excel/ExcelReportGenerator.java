package com.gestion_stock_it.Flux.Excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gestion_stock_it.Flux.Entree;
import com.gestion_stock_it.Flux.Sortie;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExcelReportGenerator {
    public static void generateReport(List<?> list, HttpServletResponse response, String typeFlux, String dateParams, String dateFlux, String nomArticle, String expediteur, String destinataire ) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(createTitle(typeFlux, dateParams, dateFlux, nomArticle, expediteur, destinataire));
            workbook.getCreationHelper();

            // Style d’en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            StringBuilder subText = new StringBuilder();

            subText.append(createTitleXLSX(typeFlux, dateParams, dateFlux, nomArticle, expediteur, destinataire).replace(" ", "_"));
            
            
            subText.append(".xlsx");
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);

            if (typeFlux.equalsIgnoreCase("Entree")) {
                String[] headers = {"ID", "Article", "Quantité", "Date", "Heure", "Fournisseur"};
                createHeader(headerRow, headers, headerStyle);
                fillEntreeRows(sheet, list, rowNum);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(String.valueOf(subText), StandardCharsets.UTF_8) + "\"");
            }
            else if (typeFlux.equalsIgnoreCase("Sortie")) {
                String[] headers = {"ID", "Article", "Quantité", "Date", "Heure", "Expéditeur", "Destinataire"};
                createHeader(headerRow, headers, headerStyle);
                fillSortieRows(sheet, list, rowNum);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(String.valueOf(subText), StandardCharsets.UTF_8) + "\"");
            }

            // Auto-size des colonnes
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(response.getOutputStream());
        }
    }
    
    public static String createTitle(String typeFlux, String dateParams, String dateFlux, String nomArticle, String expediteur, String destinataire) {
		
    	boolean hasNomArticle = nomArticle != null && !nomArticle.trim().isEmpty();
		boolean hasDateFlux = dateFlux != null && !dateFlux.trim().isEmpty();
		boolean hasDateFluxParam = dateParams != null && !dateParams.trim().isEmpty();
		boolean hasExpediteur = expediteur != null && !expediteur.trim().isEmpty();
		boolean hasDestinataire = destinataire != null && !destinataire.trim().isEmpty();
    	
    	StringBuilder title = new StringBuilder(typeFlux.equals("Entree") ? "Entrées" : "Sorties");

		if (hasDateFlux && hasDateFluxParam) {
			String formattedDateFlux = dateFlux;
			if (dateFlux.matches("\\d{4}-\\d{2}-\\d{2}")) { // vérifie le format
				String[] parts = dateFlux.split("-");
				formattedDateFlux = parts[2] + "-" + parts[1] + "-" + parts[0];
			}

			switch (dateParams) {
				case "equals":
					title.append(" du ");
					break;
				case "before":
					title.append(" avant ");
					break;
				case "after":
					title.append(" après ");
					break;
				case "month":
					title.append(" Mois ");
					break;
			}

			title.append(formattedDateFlux);
		}
		
		if (hasExpediteur) {
			title.append(expediteur);
		}
		if(hasExpediteur && hasDestinataire) {
			title.append(" ");
		}
		if (hasDestinataire) {
			title.append(destinataire);
		}

		if (hasNomArticle) {
			title.append(" Article ").append(nomArticle);
		}
		
		return title.toString();
	}
    
    public static String createTitleXLSX(String typeFlux, String dateParams, String dateFlux, String nomArticle, String expediteur, String destinataire) {
		
    	boolean hasNomArticle = nomArticle != null && !nomArticle.trim().isEmpty();
		boolean hasDateFlux = dateFlux != null && !dateFlux.trim().isEmpty();
		boolean hasDateFluxParam = dateParams != null && !dateParams.trim().isEmpty();
		boolean hasExpediteur = expediteur != null && !expediteur.trim().isEmpty();
		boolean hasDestinataire = destinataire != null && !destinataire.trim().isEmpty();
    	
    	StringBuilder title = new StringBuilder("Rapport ");
		
		if (!hasNomArticle || !(hasDateFlux && hasDateFluxParam)) {
			title.append("Géneral ");
		}
		
		title.append(typeFlux.equals("Entree") ? "d'Entrées" : "de Sorties");
		
		if (hasDateFlux && hasDateFluxParam) {
			String formattedDateFlux = dateFlux;
			if (dateFlux.matches("\\d{4}-\\d{2}-\\d{2}")) { // vérifie le format
				String[] parts = dateFlux.split("-");
				formattedDateFlux = parts[2] + "-" + parts[1] + "-" + parts[0];
			}

			switch (dateParams) {
				case "equals":
					title.append(" du ");
					break;
				case "before":
					title.append(" avant le ");
					break;
				case "after":
					title.append(" après le ");
					break;
				case "month":
					title.append(" du mois de ");
					break;
			}

			title.append(formattedDateFlux);
		}
		
		if (hasExpediteur) {
			title.append(" par ").append(expediteur);
		}
		if(hasExpediteur && hasDestinataire) {
			title.append(" et ");
		}
		if (hasDestinataire) {
			title.append(" destiné à ").append(destinataire);
		}

		if (hasNomArticle) {
			title.append(" pour l'article ").append(nomArticle);
		}
		
		return title.toString();
	}

    /** Crée la ligne d’en-tête */
    private static void createHeader(Row row, String[] headers, CellStyle style) {
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    /** Remplissage des lignes pour les entrées */
    @SuppressWarnings("unchecked")
    private static void fillEntreeRows(Sheet sheet, List<?> list, int startRow) {
        List<Entree> entrees = (List<Entree>) list;

        for (Entree e : entrees) {
            Row row = sheet.createRow(startRow++);
            row.createCell(0).setCellValue(e.getTag_flux());
            row.createCell(1).setCellValue(e.getArticle().getNom_article());
            row.createCell(2).setCellValue(e.getNombre_article_deplace());
            row.createCell(3).setCellValue(e.getDate_deplacement_formatter().split(" ")[0]);
            row.createCell(4).setCellValue(e.getDate_deplacement_formatter().split(" ")[1]);
            row.createCell(5).setCellValue(e.getExpediteur().getRaison_sociale());
        }
    }

    /** Remplissage des lignes pour les sorties */
    @SuppressWarnings("unchecked")
    private static void fillSortieRows(Sheet sheet, List<?> list, int startRow) {
        List<Sortie> sorties = (List<Sortie>) list;
        for (Sortie s : sorties) {
            Row row = sheet.createRow(startRow++);
            row.createCell(0).setCellValue(s.getTag_flux());
            row.createCell(1).setCellValue(s.getArticle().getNom_article());
            row.createCell(2).setCellValue(s.getNombre_article_deplace());
            row.createCell(3).setCellValue(s.getDate_deplacement_formatter());
            row.createCell(4).setCellValue(s.getDate_deplacement_formatter().split(" ")[1]);
            row.createCell(5).setCellValue(s.getExpediteur().getNomPrenom());
            row.createCell(6).setCellValue(s.getDestinataire().getNomPrenom());
        }
    }
}

