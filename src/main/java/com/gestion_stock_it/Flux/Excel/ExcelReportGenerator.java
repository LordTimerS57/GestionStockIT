package com.gestion_stock_it.Flux;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExcelReportGenerator {
    public static void generateReport(List<?> list, HttpServletResponse response, String typeFlux, String dateParams, String dateFlux, String nomArticle) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rapport " + typeFlux);
            workbook.getCreationHelper();

            // Style d’en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            StringBuilder subText = new StringBuilder();

            subText.append("Rapport_").append(typeFlux.equals("Entree") ? "entree_" : "sortie_");

            boolean hasNomArticle = nomArticle != null && !nomArticle.trim().isEmpty();
            boolean hasDateFlux = dateFlux != null && !dateFlux.trim().isEmpty();
            boolean hasDateFluxParam = dateParams != null && !dateParams.trim().isEmpty();

            if (hasDateFlux && hasDateFluxParam) {
                String formattedDateFlux = dateFlux;
                if (dateFlux.matches("\\d{4}-\\d{2}-\\d{2}")) { // vérifie le format
                    String[] parts = dateFlux.split("-");
                    formattedDateFlux = parts[2] + "-" + parts[1] + "-" + parts[0];
                }

                switch (dateParams) {
                    case "equals":
                        subText.append("du_");
                        break;
                    case "before":
                        subText.append("avant_");
                        break;
                    case "after":
                        subText.append("apres_");
                        break;
                    case "month":
                        subText.append("mois_");
                        break;
                }

                subText.append(formattedDateFlux);
            }

            if (hasNomArticle) {
                subText.append("article_").append(nomArticle);
            }

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

