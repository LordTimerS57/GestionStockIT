package com.gestion_stock_it.Employe;

import com.gestion_stock_it.ErrorConfirmException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;


@WebServlet({"/UpdateEmployeServlet", "/UpdateRoleEmployeServlet"})
public class UpdateEmployeServlet extends HttpServlet {
	
	private EmployeDataController fn;

    @Override
    public void init() {
        fn = new EmployeDataController();
    }
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getServletPath();
        String matricule = req.getParameter("matricule");
        String email;
        String role;

        try {
            Employe e = null;

            e = fn.getEmployeByMatricule(matricule);

            if (path.equals("/UpdateEmployeServlet")) {

                int section = Integer.parseInt(req.getParameter("section"));
                if(section < 3 )
                {
                    String motDePasse = req.getParameter("mot_de_passe");
                    if(motDePasse != null && !motDePasse.trim().isEmpty()) {

                        e = fn.testMotDePasse(matricule, motDePasse, null);

                    }
                }

                HttpSession session = req.getSession();

                if (section == 1) {
                    String nom = req.getParameter("nom");
                    String prenom = req.getParameter("prenom");
                    String adresse = req.getParameter("adresse");
                    String telephone = req.getParameter("telephone");
                    String dateDeNaissance = req.getParameter("date_de_naissance");

                    fn.testInfoPersonnel(nom, prenom, adresse, telephone, dateDeNaissance, e, "modify");

                }
                else if (section == 2) {
                    email = req.getParameter("email");
                    String nouvelEmail = req.getParameter("nouvel_email");

                    fn.testEmail(nouvelEmail, email, e, "modify");

                    Employe superAdmin = null;

                    superAdmin = fn.getEmployeList(null, null, 1, null, null, null).getFirst();
                    if (superAdmin != null) {
                        if(!email.equals(nouvelEmail) && !superAdmin.getMatricule().equals(matricule)) {
                            EmployeWebSocket.notifyAdmin("L'employé avec un id " + matricule + " change son email de " + email + " en " + nouvelEmail , matricule, "notify_info", null);
                        }
                        else if (!superAdmin.getConnection() && !email.equals(nouvelEmail)){
                            fn.sendEmail(superAdmin, e, "Changement d'adresse email");
                        }
                    }
                }
                else if (section == 3) {
                    fn.testInfoMotDePasse(req.getParameter("nouveau_mot_de_passe"), e, "modify");
                }

                fn.updateEmploye(matricule, e, section);
                session.removeAttribute("login_profil");
                session.setAttribute("login_profil", e);
                EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);
                resp.setStatus(200);

            }
            else
            {
                email = req.getParameter("email");
                role = req.getParameter("role");

                fn.testRole(role, e);
                Employe superAdmin = null;

                superAdmin = fn.getEmployeList(null, null, 1, null, null, null).getFirst();
                if(!email.trim().isEmpty()){
                    if (e != null) {
                        fn.updateEmploye(matricule, e, 4);
                    	EmployeWebSocket.changeRole(e, Integer.parseInt(role));
                    }
                    assert e != null;
                    fn.sendEmail(e, superAdmin, "Mise à niveau de votre rôle");
                }
                
                EmployeWebSocket.notifyEmployes("refresh_data", "Mise à jour des données", 1,2);

                resp.setStatus(200);
                System.out.println("role: " + role + ", matricule: " + matricule + ", email: " + email );
            }
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
