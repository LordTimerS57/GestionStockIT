package com.gestion_stock_it.Employe;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class FilterController implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String ctx = request.getContextPath();
        boolean isPublic = isIsPublic(request, ctx);

        if (isPublic) {
            chain.doFilter(req, res);
            return;
        }

        // 🔒 Auth requise pour toutes les autres pages
        if (session == null || session.getAttribute("login_profil") == null) {
            response.sendRedirect(ctx + "/Connexion");
            return;
        }

        Employe employe = (Employe) session.getAttribute("login_profil");
        String matricule = employe.getMatricule();

        // 🔄 Vérifier 'Actif' à chaque requête (option : limiter à toutes les X minutes)
        try {
            boolean actif = new EmployeDataController().getEmployeActivite(matricule);
            if (!actif) {
                session.invalidate();
                response.sendRedirect(ctx + "/Connexion");
                return;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
            return;
        }

        chain.doFilter(req, res);
    }

    private static boolean isIsPublic(HttpServletRequest request, String ctx) {
        String path = request.getRequestURI();
        String normalizedPath = path.substring(ctx.length());

        boolean isPublic =
                normalizedPath.startsWith("/STATIC/")
                        || normalizedPath.startsWith("/CSS/")
                        || normalizedPath.startsWith("/JS/")
                        || normalizedPath.startsWith("/IMAGES/")
                        || normalizedPath.equals("/Connexion")
                        || normalizedPath.equals("/LoginServlet")
                        || normalizedPath.equals("/")
                        || normalizedPath.isEmpty();
        return isPublic;
    }
}