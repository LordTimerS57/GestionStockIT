package com.gestion_stock_it.ArtType.Type;

import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeArticleDataController {
	
	private static final DatabaseConnection DB_CONNECTION = DatabaseConnection.getInstance();
	
    public List<TypeArticle> getTypeArticleList(String Nom_type) throws Exception {
        List<TypeArticle> listType = new ArrayList<>();

        StringBuilder subQuery = new StringBuilder();
        List<Object> params = new ArrayList<>();

        boolean hasNomType = Nom_type != null && !Nom_type.trim().isEmpty();

        if (hasNomType) {
            subQuery.append(" WHERE Nom_type LIKE ?");
            params.add("%" + Nom_type + "%");
        }

        subQuery.append(" ORDER BY Nom_Type");

        String finalQuery = subQuery.toString();

        try(	Connection c = DB_CONNECTION.getConnection();
        		PreparedStatement p_stmt = c.prepareStatement(
                        "SELECT "
                                + "t.*, "
                                + "a.nombre_articles AS Occurence_article "
                                + "FROM Type AS t "
                                + "LEFT JOIN ("
                                + "		SELECT Tag_type, COUNT(*) AS nombre_articles "
                                + "			FROM Article "
                                + "			GROUP BY Tag_type "
                                + ") a ON t.Tag_type = a.Tag_type "
                                + finalQuery )
        ){

            for (int i = 0; i < params.size(); i++) {
                p_stmt.setObject(i + 1, params.get(i));
            }

            ResultSet res = p_stmt.executeQuery();

            while (res.next()) {
                TypeArticle type = new TypeArticle(
                        res.getString("Tag_type"),
                        res.getString("Nom_type"),
                        res.getString("Description_type")
                );
                type.setNombre_occurence_article(res.getLong("Occurence_article"));
                listType.add(type);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return listType;
    }

    public TypeArticle getTypeArticleByTag(String tagType) throws Exception {

        if (tagType == null || tagType.trim().isEmpty()) {
            return null;
        }

        String query = "SELECT * FROM Type WHERE Tag_type = ?";

        try (	Connection c = DB_CONNECTION.getConnection();
        		PreparedStatement stmt = c.prepareStatement(query)) {

            stmt.setString(1, tagType);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TypeArticle(
                            rs.getString("Tag_type"),
                            rs.getString("Nom_type"),
                            rs.getString("Description_type")
                    );
                }
            }

        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération du type d'article par tag", e);
        }

        return null;
    }


    public void addTypeArticle(TypeArticle typeArticle) {
        try(	Connection c = DB_CONNECTION.getConnection();
                PreparedStatement p_stmt = c.prepareStatement("INSERT INTO Type "
                        + "(Tag_type, Nom_type, Description_type ) "
                        + "VALUES "
                        + "(?, ?, ?)")
        ){
            p_stmt.setString(1, typeArticle.getTag_type()); // set value
            p_stmt.setString(2, typeArticle.getNom_type());
            p_stmt.setString(3, typeArticle.getDescription_type());
            p_stmt.executeUpdate();
            // set value
        }
        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void updateTypeArticle(String tag_type, TypeArticle typeArticle) {
        try(	Connection c = DB_CONNECTION.getConnection();
                PreparedStatement p_stmt = c.prepareStatement("UPDATE Type SET "
                        + "Nom_type = ?, "
                        + "Description_type = ? "
                        + "WHERE Tag_type = ?")
        ){
            p_stmt.setString(1, typeArticle.getNom_type()); // set value
            p_stmt.setString(2, typeArticle.getDescription_type());
            p_stmt.setString(3, tag_type);
            p_stmt.executeUpdate();
            // set value

            System.out.println("Ok Update TypeArticle");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTypeArticle(String tag_type) {
        try(	Connection c = DB_CONNECTION.getConnection();
                PreparedStatement p_stmt = c.prepareStatement("DELETE FROM Type WHERE Tag_type = ?")
        ){
            p_stmt.setString(1, tag_type);
            p_stmt.executeUpdate();
            // set value
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String nextTagTypeArticle() throws Exception {

        String query = """
        SELECT Tag_type
        FROM Type
        ORDER BY CAST(SUBSTRING(Tag_type, 4) AS INT) DESC LIMIT 1;
        """;

        String nextTagType = null;

        try (	Connection c = DB_CONNECTION.getConnection();
        		Statement stmt = c.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    nextTagType = rs.getString("Tag_type");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération du tag");
        }
        return nextTagType;
    }

    public TypeArticle testError(String nom, String description, String type) {
        List<String> errors = new ArrayList<>();

        if (nom == null || nom.trim().isEmpty()) {
            errors.add("nom_type: Veuillez entrer un nom valide !");
        } else if (!nom.matches("^[A-Za-zÀ-ÿ0-9\\s'-]+$")) {
            errors.add("nom_type: Le nom d'un type d'article est invalide !");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.add("description_type: Veuillez entrer une description valide !");
        } else if (!description.matches("^[A-Za-zÀ-ÿ0-9\\s'-]+$")) {
            errors.add("description_type: La description d'un type d'article ne doit contenir que des lettres !");
        }

        if (!errors.isEmpty()) {
            throw new ErrorConfirmException(errors);
        } else {
        	TypeArticle t = new TypeArticle
		                    (
	                            null,
	                            nom,
	                            description
		                    );
        	if(type != null && type.trim() == "add") {
				try {
					t.setTag_type(nextTagTypeArticle());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return t;
        	
        }
    }

}
