package com.gestion_stock_it.ArtType.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.gestion_stock_it.ArtType.Type.TypeArticle;
import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;

public class ArticleDataController {

	public List<Article> getArticleList(String Nom_article, String Nom_type) throws Exception {
		List<Article> listArticle = new ArrayList<>();
		
		DatabaseConnection db = new DatabaseConnection();
		db.connect();
		
		StringBuilder subQuery = new StringBuilder();
		List<Object> params = new ArrayList<>();

		boolean hasNomArticle = Nom_article != null && !Nom_article.trim().isEmpty();
		boolean hasNomType = Nom_type != null && !Nom_type.trim().isEmpty();
		
		if (hasNomArticle || hasNomType) {
		    subQuery.append(" WHERE ");
		    List<String> conditions = new ArrayList<>();

		    if (hasNomArticle) {
		        conditions.add("a.Nom_article LIKE ?");
		        params.add("%" + Nom_article + "%");
		    }
		    
		    if (hasNomType) {
		        conditions.add("t.Nom_type LIKE ?");
		        params.add("%" + Nom_type + "%");
		    }

		    subQuery.append(String.join(" AND ", conditions));
		}
		subQuery.append(" ORDER BY a.Nom_article ");
		
		String finalQuery = subQuery.toString();
		
		try(	
				Connection conn = db.getConnection();
				PreparedStatement p_stmt = conn.prepareStatement(
						"""
						SELECT 
							a.*, 
							t.Nom_type AS TypeNom, 
							t.Description_type AS TypeDescription, 
							e.nombre_entrees AS Occurence_entree, 
							s.nombre_sorties AS Occurence_sortie 
						FROM Article a 
							JOIN Type t ON a.Tag_Type = t.Tag_type 
						LEFT JOIN (
								SELECT Tag_article, COUNT(*) AS nombre_entrees 
									FROM Entree 
									GROUP BY Tag_article 
								) e ON a.Tag_article = e.Tag_article 
						LEFT JOIN (
								SELECT Tag_article, COUNT(*) AS nombre_sorties 
									FROM Sortie 
									GROUP BY Tag_article 
								) s ON a.Tag_article = s.Tag_article 
						"""
						+ finalQuery
						)
			){
			
			for (int i = 0; i < params.size(); i++) {
				p_stmt.setObject(i + 1, params.get(i));
			}
			
			ResultSet res = p_stmt.executeQuery();
			
			while (res.next()) {
				// long stock = 
				TypeArticle type = new TypeArticle(
						res.getString("Tag_type"),
						res.getString("TypeNom"),
						res.getString("TypeDescription")
						);
				Article art = new Article(
						res.getString("Tag_article"),
						res.getString("Nom_article"),
						res.getString("Description_article"),
						type,
						res.getLong("Stock_article")
						);

				art.setNombre_occurence_entrees(res.getLong("Occurence_entree"));
				art.setNombre_occurence_sorties(res.getLong("Occurence_sortie"));

				listArticle.add(art);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return listArticle;
	}

	public Article getArticleByTag(Connection c, String tagArticle) throws Exception {
		DatabaseConnection db = new DatabaseConnection();
		db.connect();
		if (tagArticle == null || tagArticle.trim().isEmpty()) {
			return null; // Rien à chercher si le tag est vide
		}

		String query = """
        SELECT Tag_article, Nom_article, Description_article, Tag_type, Stock_article
        FROM Article
        WHERE Tag_article = ?
        """;

		try (PreparedStatement stmt = c.prepareStatement(query)) {

			stmt.setString(1, tagArticle);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					TypeArticle type = new TypeArticle(
							rs.getString("Tag_type"),
							null,
							null
					);

					return new Article(
							rs.getString("Tag_article"),
							rs.getString("Nom_article"),
							rs.getString("Description_article"),
							type,
							rs.getLong("Stock_article")
					);
				}
			}

		} catch (SQLException e) {
			throw new Exception("Erreur lors de la récupération de l'article par tag", e);
		}

		return null;
	}

	public void addArticle(Connection c, Article article) {
		try(
				PreparedStatement p_stmt = c.prepareStatement("INSERT INTO Article "
						+ "(Tag_article, Tag_Type, Nom_article, Description_article,  Stock_article ) "
						+ "VALUES "
						+ "(?, ?, ?, ?, ?)")
				){
			p_stmt.setString(1, article.getTag_article()); // set value
			p_stmt.setString(2, article.getType_article().getTag_type());
			p_stmt.setString(3, article.getNom_article());
			p_stmt.setString(4, article.getDescription_article());
			p_stmt.setLong(5, article.getNombre_article());
			p_stmt.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());

			e.printStackTrace();
		}
	}
	
	public void updateArticle(Connection c, String tag_article, Article article, String type_flux, Long nombre_article_deplace) {

		StringBuilder subQuery = new StringBuilder();
		List<Object> params = new ArrayList<>();

		boolean hasTypeFlux = ( type_flux != null && !type_flux.trim().isEmpty() ) && ( nombre_article_deplace != null );

		subQuery.append(" UPDATE Article SET ");

		if (hasTypeFlux) {
			subQuery.append(" Stock_article = Stock_article " + (type_flux.equals("Entree") ? "+ ? " : "- ? " ) );
			params.add(nombre_article_deplace);
		}
		else{
			subQuery.append(" Tag_type = ? , Nom_article = ? , Description_article = ? ");
			params.add(article.getType_article().getTag_type());
			params.add(article.getNom_article());
			params.add(article.getDescription_article());
		}

		subQuery.append("WHERE Tag_article = ? ");
		params.add(tag_article);

		String finalQuery = subQuery.toString();


		try(
				PreparedStatement p_stmt = c.prepareStatement(finalQuery)
		){
			for (int i = 0; i < params.size(); i++) {
				p_stmt.setObject(i + 1, params.get(i));
			}
			p_stmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteArticle(Connection c, String tag_article) {
		try(
				PreparedStatement p_stmt = c.prepareStatement("DELETE FROM Article WHERE Tag_article = ?")
				){
			p_stmt.setString(1, tag_article);
			p_stmt.executeUpdate();
			// set value
		}
		catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public String nextTagArticle(Connection c) throws Exception {
		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		String query = """
        SELECT Tag_article
        FROM Article
        ORDER BY CAST(SUBSTRING(Tag_article, 4) AS INT) DESC
	 	LIMIT 1;
        """;

		String nextTagArticle = null;

		try (Statement stmt = c.createStatement()) {

			try (ResultSet rs = stmt.executeQuery(query)) {
				if (rs.next()) {
					nextTagArticle = rs.getString("Tag_article");
				}
			}
		} catch (SQLException e) {
			throw new Exception("Erreur lors de la récupération du tag");
		}
		return nextTagArticle;
	}

	public static void testError(String tag, String nom, String description) {
		List<String> errors = new ArrayList<>();
		if (tag == null || tag.trim().isEmpty()) {
			errors.add("type_article: Veuillez choisir un type d'article !");
		}
		if (nom == null || nom.trim().isEmpty()) {
			errors.add("nom_article: Veuillez entrer un nom valide !");
		} else if (!nom.matches("^[A-Za-zÀ-ÿ0-9\\s'-]+$")) {
			errors.add("nom_article: Le nom d'un article est invalide !");
		}
		if (description == null || description.trim().isEmpty()) {
			errors.add("description_article: Veuillez entrer une description valide !");
		} else if (!description.matches("^[A-Za-zÀ-ÿ0-9\\s'-]+$")) {
			errors.add("description_article: La description d'un article est invalide !");
		}

		if (!errors.isEmpty()) {
			throw new ErrorConfirmException(errors);
		}
	}

}

