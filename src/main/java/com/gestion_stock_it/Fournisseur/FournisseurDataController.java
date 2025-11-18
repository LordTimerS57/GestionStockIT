package com.gestion_stock_it.Fournisseur;

import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDataController {
    public List<Fournisseur> getFournisseurList(String Tag_fournisseur, String Raison_sociale, String Telephone, String Email) throws Exception {
        List<Fournisseur> listFournisseur = new ArrayList<>();

        DatabaseConnection db = new DatabaseConnection();
        db.connect();

        StringBuilder subQuery = new StringBuilder();
        List<Object> params = new ArrayList<>();

        boolean hasRaisonSociale = Raison_sociale != null && !Raison_sociale.trim().isEmpty();
        boolean hasTagFournisseur = Tag_fournisseur != null && !Tag_fournisseur.trim().isEmpty();
        boolean hasEmail = Email != null && !Email.trim().isEmpty();
        boolean hasTelephone = Telephone != null && !Telephone.trim().isEmpty();

        if (hasRaisonSociale  || hasTagFournisseur || (hasEmail && hasTelephone) ) {
            subQuery.append(" WHERE ");
            List<String> conditions = new ArrayList<>();

            if (hasRaisonSociale) {
                conditions.add("Raison_sociale LIKE ?");
                params.add("%" + Raison_sociale + "%");
            }

            if (hasTagFournisseur) {
                conditions.add("Tag_fournisseur = ?");
                params.add(Tag_fournisseur);
            }
            if (hasEmail && hasTelephone) {
                conditions.add("Email_fournisseur = ? AND Telephone_fournisseur = ?");
                params.add(Email);
                params.add(Telephone);
            }

            subQuery.append(String.join(" AND ", conditions));
        }

        String finalQuery = subQuery.toString();


        try(Connection conn = db.getConnection();
            PreparedStatement p_stmt = conn.prepareStatement(
                    "SELECT "
                        + "f.*, "
                        + "e.nombre_fournisseurs AS Occurence_fournisseur "
                        + "FROM Fournisseur AS f "
                        + "LEFT JOIN ("
                        + "		SELECT Expediteur, COUNT(*) AS nombre_fournisseurs "
                        + "			FROM Entree "
                        + "			GROUP BY Expediteur "
                        + ") e ON f.Tag_fournisseur = e.Expediteur "
                        + finalQuery);
        )
        {

            for (int i = 0; i < params.size(); i++) {
                p_stmt.setObject(i + 1, params.get(i));
            }

            ResultSet res = p_stmt.executeQuery();

            while (res.next()) {
                Fournisseur fournisseur = new Fournisseur(
                        res.getString("Tag_fournisseur"),
                        res.getString("Raison_sociale"),
                        res.getString("Email_fournisseur"),
                        res.getString("Telephone_fournisseur")
                );

                fournisseur.setNombre_occurence_entree(res.getLong("Occurence_fournisseur"));

                listFournisseur.add(fournisseur);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return listFournisseur;
    }

    public Fournisseur getFournisseurByTag(Connection c, String tagFournisseur) throws Exception {
        if (tagFournisseur == null || tagFournisseur.trim().isEmpty()) {
            return null; // Rien à chercher si le tag est vide
        }

        String query = "SELECT * FROM Fournisseur WHERE Tag_fournisseur = ?";

        try (PreparedStatement stmt = c.prepareStatement(query)) {

            stmt.setString(1, tagFournisseur);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Fournisseur(
                            rs.getString("Tag_fournisseur"),
                            rs.getString("Raison_sociale"),
                            rs.getString("Email_fournisseur"),
                            rs.getString("Telephone_fournisseur")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération du fournisseur par tag", e);
        }

        return null;
    }

    public void addFournisseur(Connection c, Fournisseur fournisseur) throws Exception {
        try(
                PreparedStatement p_stmt = c.prepareStatement("INSERT INTO Fournisseur ("
                        + "Tag_fournisseur, "
                        + "Raison_sociale, "
                        + "Email_fournisseur, "
                        + "Telephone_fournisseur ) " +
                        "VALUES (?, ?, ?, ?)")
        )
        {
            p_stmt.setString(1, fournisseur.getTag_fournisseur());
            p_stmt.setString(2, fournisseur.getRaison_sociale());
            p_stmt.setString(3, fournisseur.getEmail_fournisseur());
            p_stmt.setString(4, fournisseur.getTelephone_fournisseur());

            p_stmt.executeUpdate();
            // set value
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFournisseur(Connection c, String tag_fournisseur, Fournisseur fournisseur) throws Exception {
        try(
                PreparedStatement p_stmt = c.prepareStatement("UPDATE Fournisseur SET "
                        + "Tag_fournisseur = ?, "
                        + "Raison_sociale = ?, "
                        + "Email_fournisseur = ?, "
                        + "Telephone_fournisseur = ? "
                        + "WHERE Tag_fournisseur = ?")
        ){
            p_stmt.setString(1, fournisseur.getTag_fournisseur());
            p_stmt.setString(2, fournisseur.getRaison_sociale());
            p_stmt.setString(3, fournisseur.getEmail_fournisseur());
            p_stmt.setString(4, fournisseur.getTelephone_fournisseur());
            p_stmt.setString(5, tag_fournisseur);
            p_stmt.executeUpdate();
            System.out.println("Ok Update Fournisseur");
            // set value
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFournisseur(Connection c, String tag_fournisseur) {
        try(
                PreparedStatement p_stmt = c.prepareStatement("DELETE FROM Fournisseur WHERE Tag_fournisseur = ?")
        ){
            p_stmt.setString(1, tag_fournisseur);
            p_stmt.executeUpdate();
            // set value
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void testError(String tag, String nom, String email, String telephone) throws Exception {
        List<String> errors = new ArrayList<>();

        DatabaseConnection db = new DatabaseConnection();
        db.connect();
        Connection c = db.getConnection();

        Fournisseur f = new FournisseurDataController().getFournisseurByTag(c, tag);

        if(tag == null || tag.trim().isEmpty()) {
            errors.add("tag_fournisseur: Veuillez entrer un numéro d'identification valide !");
        } else if (f != null){
            errors.add("tag_fournisseur: Le tag que vous avez édité est déjà présente chez un autre fournisseur !");
        } else if (!tag.matches("^\\d{13}$")) {
            errors.add("tag_fournisseur: Le numéro d'identification doit contenir exactement 13 chiffres !");
        }

        if (nom == null || nom.trim().isEmpty()) {
            errors.add("raison_sociale: Veuillez entrer un nom valide !");
        }

        if(email == null || email.trim().isEmpty()) {
            errors.add("email_fournisseur: Veuillez entrer une adresse valide !");
        }

        if(telephone == null || telephone.trim().isEmpty()) {
            errors.add("tel_fournisseur: Veuillez entrer un numéro de téléphone valide !");
        } else if (telephone.length() != 10) {
            errors.add("tel_fournisseur: Veuillez entrer exactement 10 chiffres !");
        } else if (!telephone.matches("^\\d{10}$")) {
            errors.add("tel_fournisseur: Le numéro de téléphone doit contenir exactement 10 chiffres !");
        }

        db.disconnect();

        if (!errors.isEmpty()) {
            throw new ErrorConfirmException(errors);
        }
    }
}
