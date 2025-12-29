package com.gestion_stock_it.Employe;

import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@WebServlet("/AddEmployeServlet")
public class AddEmployeServlet extends HttpServlet {

	private EmployeDataController fn;

    @Override
    public void init() {
        fn = new EmployeDataController();
    }
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String adresse = req.getParameter("adresse");
        String telephone = req.getParameter("telephone");
        String dateDeNaissance = req.getParameter("date_de_naissance");
        String motDePasse = req.getParameter("mot_de_passe");

        try {
            Employe e = fn.testInfoPersonnel(nom, prenom, adresse, telephone, dateDeNaissance, null, "add");
            e = fn.testInfoMotDePasse(motDePasse, e, "add");
            e = fn.testEmail(email, email, e, "add");

            String matricule = fn.nextTag();

            Employe superAdmin = fn.getEmployeList(null, null, 1, null, null, null).getFirst();

            if (superAdmin != null && superAdmin.getRole().equals("Super Administrateur") && superAdmin.getConnection()) {

                AdminRequestStore.createRequest(matricule);

                EmployeWebSocket.notifyAdmin(
                        "L'employé " + nom + " " + prenom +
                                " demande la création d’un compte (" + matricule + ").",
                        matricule,
                        "notify_decision",
                        e
                );

                // 🔹 Attente de la réponse (60 secondes max)
                String response = AdminRequestStore.waitForResponse(matricule, 60);

                if (response == null || response.equals("TIMEOUT")) {
                    resp.getWriter().write("⏱️ Aucun retour de l’administrateur (délai expiré).");
                    return;
                }
                if (response.contains("ACCEPTE")) {
                    fn.addEmploye(e);
                    resp.getWriter().write("✅ Compte créé avec succès après validation administrateur !");
                    System.out.println("✅ Compte créé avec succès après validation administrateur !");
                } else {
                    resp.getWriter().write("❌ L’administrateur a refusé la création du compte.");
                    System.out.println("❌ L’administrateur a refusé la création du compte.");
                }
            } else {
                resp.getWriter().write("⚠️ Aucun super administrateur trouvé pour valider le compte.");
                fn.sendEmail(superAdmin, e,"Création d'un nouveau compte employé");
                System.out.println("❌ L’administrateur a refusé la création du compte.");
            }
            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);
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
    
}
