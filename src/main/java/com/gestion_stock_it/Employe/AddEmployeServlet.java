package com.gestion_stock_it.Employe;

import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            Employe e = null; 
            List<String> allErrors = new ArrayList<>();

            try { e = fn.testInfoPersonnel(nom, prenom, adresse, telephone, dateDeNaissance, null, "add"); } 
            catch (ErrorConfirmException ex) { allErrors.addAll(ex.getMessages()); }

            try { e = fn.testInfoMotDePasse(motDePasse, e, "add"); } 
            catch (ErrorConfirmException ex) { allErrors.addAll(ex.getMessages()); }

            try { e = fn.testEmail(email, null, e, "add"); } 
            catch (ErrorConfirmException ex) { allErrors.addAll(ex.getMessages()); }

            if (!allErrors.isEmpty()) {
                throw new ErrorConfirmException(allErrors);
            }

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
                	resp.setStatus(500);
                    System.out.println("⏱️ Aucun retour de l’administrateur (délai expiré).");
                	resp.getWriter().write("creation_invalid: L’administrateur n'a pas eu le temps de valider la création du compte.");
                    return;
                }
                if (response.contains("ACCEPTE")) {
                    fn.addEmploye(e);
                    System.out.println("✅ Compte créé avec succès après validation administrateur !");
                    resp.setStatus(200);
                } else {
                	resp.setStatus(500);
                    resp.getWriter().write("creation_invalid: L’administrateur a refusé la création du compte.");
                    System.out.println("❌ L’administrateur a refusé la création du compte.");
                }
            } else {
            	resp.setStatus(500);
                resp.getWriter().write("creation_invalid: L’administrateur s'est déconnecté et n'est pas en état de confirmer la création du compte.");
                fn.sendEmail(superAdmin, e,"Création d'un nouveau compte employé");
                System.out.println("❌ L’administrateur s'est déconnecté et n'est pas en état de confirmer la création du compte.");
            }
            EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);

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
