package com.gestion_stock_it.Employe;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/Employes")
public class ListEmployeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nomPrenoms = request.getParameter("nom_prenom");

        EmployeDataController dao = new EmployeDataController();
        List<Employe> employesConnectes, employesNonConnectes;

        try {
            employesConnectes = dao.getEmployeList(null, nomPrenoms, -1, null,  "oui", null);
            employesNonConnectes = dao.getEmployeList(null, nomPrenoms, -1, null,  "non", null);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        request.setAttribute("connectes", employesConnectes);
        request.setAttribute("non_connectes", employesNonConnectes);
        request.setAttribute("content", "/Employe/AcceuilEmploye.jsp");

        request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    }
}
