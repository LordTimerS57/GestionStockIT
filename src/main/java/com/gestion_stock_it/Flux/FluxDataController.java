package com.gestion_stock_it.Flux;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.gestion_stock_it.ArtType.Article.Article;
import com.gestion_stock_it.ArtType.Article.ArticleDataController;
import com.gestion_stock_it.ArtType.Type.TypeArticle;
import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.Employe.Employe;
import com.gestion_stock_it.ErrorConfirmException;
import com.gestion_stock_it.Fournisseur.Fournisseur;

public class FluxDataController {

	public List<Entree> getEntreeList(String Tag_flux, String Nom_article, String Nom_expediteur, String Date_flux, String Date_flux_parameters) throws Exception {
		List<Entree> listEntree = new ArrayList<>();

		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		StringBuilder subQuery = new StringBuilder();
		List<Object> params = new ArrayList<>();

		boolean hasTagFlux = Tag_flux != null && !Tag_flux.trim().isEmpty();
		boolean hasNomArticle = Nom_article != null && !Nom_article.trim().isEmpty();
		boolean hasNomExpediteur = Nom_expediteur != null && !Nom_expediteur.trim().isEmpty();
		boolean hasDateFlux = Date_flux != null && !Date_flux.trim().isEmpty();
		boolean hasDateFluxParam = Date_flux_parameters != null && !Date_flux_parameters.trim().isEmpty();


		if (hasTagFlux || hasNomArticle || hasNomExpediteur || (hasDateFlux && hasDateFluxParam) ) {
			subQuery.append(" WHERE ");

			List<String> conditions = new ArrayList<>();
			List<String> order = new ArrayList<>();

			if (hasTagFlux) {
				conditions.add("e.Tag_entree = ?");
				params.add(Tag_flux);
			}

			if (hasNomArticle) {
				conditions.add("art.Nom_article LIKE ? ");
				params.add("%" + Nom_article + "%");
				order.add("art.Nom_article ASC");
			}

			if (hasNomExpediteur) {
				conditions.add("exp.Raison_sociale LIKE ? ");
				params.add("%" + Nom_expediteur + "%");
				order.add("exp.Raison_sociale ASC");
				/*} else {
					conditions.add("exp.Nom LIKE ? OR exp.Prenom LIKE ? ");
					params.add("%" + Nom_expediteur + "%");
					params.add("%" + Nom_expediteur + "%");
					order.add("CONCAT(exp.Nom + \" \" + exp.Prenom) ASC");
				}*/
			}

			/*if (hasNomDestinataire && !fluxBoolean) {
				conditions.add("des.Nom LIKE ? OR des.Prenom LIKE ? ");
				params.add("%" + Nom_destinataire + "%");
				params.add("%" + Nom_destinataire + "%");
				order.add("CONCAT(des.Nom + \" \" + des.Prenom) ASC");
			}*/

			if (hasDateFlux && hasDateFluxParam) {
				order.add("e.Date_entree DESC");
				switch (Date_flux_parameters) {
					case "equals":
						conditions.add("TO_CHAR(e.Date_entree, 'YYYY-MM-DD') = ? ");
						params.add(Date_flux);
						break;

					case "before":
						conditions.add("e.Date_entree < TO_DATE(?, 'YYYY-MM-DD') ");
						params.add(Date_flux);
						break;

					case "after":
						conditions.add("e.Date_entree > TO_DATE(?, 'YYYY-MM-DD') ");
						params.add(Date_flux);
						break;

					case "month":
						conditions.add("TO_CHAR(e.Date_entree, 'MM/YYYY') = ? ");
						params.add(Date_flux);
						break;
				}
			} else if (hasDateFlux && !hasDateFluxParam) {
				conditions.add("TO_CHAR(e.Date_entree, 'MM/YYYY') = TO_CHAR(NOW(), 'MM/YYYY') ");
				order.add("e.Date_entree DESC");
			}

			subQuery.append(String.join(" AND ", conditions));

			if (hasNomExpediteur || hasNomArticle || (hasDateFlux && !hasDateFluxParam)) {
				subQuery.append(" ORDER BY ");
				subQuery.append(String.join(", ", order));
			}

		}

		String finalQuery = subQuery.toString();

		try(

				Connection conn = db.getConnection();
				PreparedStatement p_stmt = conn.prepareStatement(

						"	SELECT "

								+ " 	e.* ,"

								+ "		exp.Raison_sociale AS exp_Raison_sociale ,"
								+ "		exp.Email_fournisseur AS exp_Email,"
								+ "		exp.Telephone_fournisseur AS exp_Telephone,"

								+ "	art.Tag_article, art.Nom_article, art.Description_article,"

								+ "	n.Nombre, "

								+ "	t.Tag_type, t.Nom_type, t.Description_type	"

								+ "	FROM "
								+ "		Entree e "
								+ " JOIN Article art "
								+ "		ON art.Tag_article = e.Tag_article "
								+ " JOIN Nombre_article n "
								+ "		ON n.Tag_article = e.Tag_article "
								+ " JOIN Type t "
								+ "		ON t.Tag_Type = art.Tag_Type "
								+ " LEFT JOIN Fournisseur exp "
								+ "		ON exp.Tag_fournisseur = e.Expediteur "
								+ finalQuery);

		){

			for (int i = 0; i < params.size(); i++) {
				p_stmt.setObject(i + 1, params.get(i));
			}

			ResultSet res = p_stmt.executeQuery();

			while (res.next()) {
				/*Employe destinataire = new Employe(
						res.getString("Destinataire"),
						res.getString("des_Nom"),
						res.getString("des_Prenom"),
						res.getString("des_Email"),
						null,
						res.getString("des_Telephone"),
						res.getString("des_Adresse"),
						desDateNaissance != null ? desDateNaissance.toLocalDateTime().toLocalDate() : null,
						res.getInt("des_Role"),
						desDateCreation != null ? desDateCreation.toLocalDateTime() : null,
						desDateModification != null ? desDateModification.toLocalDateTime() : null
				);*/

				Fournisseur expediteur = new Fournisseur(
						res.getString("Expediteur"),
						res.getString("exp_Raison_sociale"),
						res.getString("exp_Email"),
						res.getString("exp_Telephone")
				);
				Article article = new Article(
						res.getString("Tag_article"),
						res.getString("Nom_article"),
						res.getString("Description_article"),
						new TypeArticle(
								res.getString("Tag_type"),
								res.getString("Nom_type"),
								res.getString("Description_type")
						),
						res.getLong("Nombre")
				);
				Entree f = new Entree(
						res.getString("Tag_entree"),
						null,
						expediteur,
						article,
						res.getLong("Nombre_article_entree"),
						res.getTimestamp("Date_entree").toLocalDateTime()
				);
				listEntree.add(f);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return listEntree;
	}

	public List<Sortie> getSortieList(String Tag_flux, String Nom_article, String Nom_expediteur, String Nom_destinataire, String Date_flux, String Date_flux_parameters) throws Exception {
		List<Sortie> listSortie = new ArrayList<>();

		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		StringBuilder subQuery = new StringBuilder();
		List<Object> params = new ArrayList<>();

		boolean hasTagFlux = Tag_flux != null && !Tag_flux.trim().isEmpty();
		boolean hasNomArticle = Nom_article != null && !Nom_article.trim().isEmpty();
		boolean hasNomExpediteur = Nom_expediteur != null && !Nom_expediteur.trim().isEmpty();
		boolean hasNomDestinataire = Nom_destinataire != null && !Nom_destinataire.trim().isEmpty();
		boolean hasDateFlux = Date_flux != null && !Date_flux.trim().isEmpty();
		boolean hasDateFluxParam = Date_flux_parameters != null && !Date_flux_parameters.trim().isEmpty();


		if (hasTagFlux || hasNomArticle || hasNomExpediteur || hasNomDestinataire ||(hasDateFlux && hasDateFluxParam) ) {
			subQuery.append(" WHERE ");

			List<String> conditions = new ArrayList<>();
			List<String> order = new ArrayList<>();

			if (hasTagFlux) {
				conditions.add("s.Tag_sortie = ?");
				params.add(Tag_flux);
			}

			if (hasNomArticle) {
				conditions.add("art.Nom_article LIKE ? ");
				params.add("%" + Nom_article + "%");
				order.add("art.Nom_article ASC");
			}

			if (hasNomExpediteur) {
				conditions.add("CONCAT(exp.Nom, ' ', exp.Prenom) LIKE ? ");
				params.add("%" + Nom_expediteur + "%");
				order.add("CONCAT(exp.Nom, ' ', exp.Prenom) ASC");
			}

			if(hasNomDestinataire) {
				conditions.add("CONCAT(des.Nom, ' ', des.Prenom)LIKE ? ");
				params.add("%" + Nom_destinataire + "%");
				order.add("CONCAT(des.Nom, ' ', des.Prenom) ASC");
			}

			if (hasDateFlux && hasDateFluxParam) {
				order.add("s.Date_sortie DESC");
				switch (Date_flux_parameters) {
					case "equals":
						conditions.add("TO_CHAR(s.Date_sortie, 'YYYY-MM-DD') = ? ");
						params.add(Date_flux);
						break;

					case "before":
						conditions.add("s.Date_sortie < TO_DATE(?, 'YYYY-MM-DD') ");
						params.add(Date_flux);
						break;

					case "after":
						conditions.add("s.Date_sortie > TO_DATE(?, 'YYYY-MM-DD') ");
						params.add(Date_flux);
						break;

					case "month":
						conditions.add("TO_CHAR(s.Date_sortie, 'MM/YYYY') = ? ");
						params.add(Date_flux);
						break;
				}
			} else if (hasDateFlux && !hasDateFluxParam) {
				conditions.add("TO_CHAR(s.Date_sortie, 'MM/YYYY') = TO_CHAR(NOW(), 'MM/YYYY') ");
				order.add("s.Date_sortie DESC");
			}

			subQuery.append(String.join(" AND ", conditions));

			if (hasNomExpediteur || hasNomDestinataire || hasNomArticle || (hasDateFlux && !hasDateFluxParam)) {
				subQuery.append(" ORDER BY ");
				subQuery.append(String.join(", ", order));
			}

		}

		String finalQuery = subQuery.toString();

		try(

				Connection conn = db.getConnection();
				PreparedStatement p_stmt = conn.prepareStatement(

						"	SELECT "

								+ " s.* ,"

								+ " exp.Nom AS exp_Nom,"
								+ " exp.Prenom AS exp_Prenom,"
								+ " exp.Date_de_naissance AS exp_Date_de_naissance,"
								+ " exp.Email AS exp_Email,"
								+ " exp.Telephone AS exp_Telephone,"
								+ " exp.Adresse AS exp_Adresse,"
								+ " exp.Role AS exp_Role,"
								+ " exp.Actif AS exp_Actif,"
								+ " exp.Date_creation AS exp_Date_creation,"
								+ " exp.Date_modification AS exp_Date_modification,"

								+ " des.Nom AS des_Nom,"
								+ " des.Prenom AS des_Prenom,"
								+ " des.Date_de_naissance AS des_Date_de_naissance,"
								+ " des.Email AS des_Email,"
								+ " des.Telephone AS des_Telephone,"
								+ " des.Adresse AS des_Adresse,"
								+ " des.Role AS des_Role,"
								+ " des.Actif AS des_Actif,"
								+ " des.Date_creation AS des_Date_creation,"
								+ " des.Date_modification AS des_Date_modification,"


								+ "	art.Tag_article, art.Nom_article, art.Description_article,"

								+ "	n.Nombre, "

								+ "	t.Tag_type, t.Nom_type, t.Description_type	"

								+ "	FROM "
								+ "		Sortie s "
								+ " JOIN Article art "
								+ "		ON art.Tag_article = s.Tag_article "
								+ " JOIN Nombre_article n "
								+ "		ON n.Tag_article = s.Tag_article "
								+ " JOIN Type t "
								+ "		ON t.Tag_Type = art.Tag_Type "
								+ " LEFT JOIN Employe des "
								+ "		ON des.Matricule = s.Destinataire "
								+ " LEFT JOIN Employe exp "
								+ "		ON exp.Matricule = s.Expediteur "
								+ finalQuery);

		){

			for (int i = 0; i < params.size(); i++) {
				p_stmt.setObject(i + 1, params.get(i));
			}

			ResultSet res = p_stmt.executeQuery();

			while (res.next()) {

				Timestamp expDateNaissance = res.getTimestamp("exp_Date_de_naissance");
				Timestamp expDateCreation = res.getTimestamp("exp_Date_creation");
				Timestamp expDateModification = res.getTimestamp("exp_Date_modification");

				Timestamp desDateNaissance = res.getTimestamp("des_Date_de_naissance");
				Timestamp desDateCreation = res.getTimestamp("des_Date_creation");
				Timestamp desDateModification = res.getTimestamp("des_Date_modification");

				Employe destinataire = new Employe(
						res.getString("Destinataire"),
						res.getString("des_Nom"),
						res.getString("des_Prenom"),
						res.getString("des_Email"),
						null,
						res.getString("des_Telephone"),
						res.getString("des_Adresse"),
						desDateNaissance != null ? desDateNaissance.toLocalDateTime().toLocalDate() : null,
						res.getInt("des_Role"),
						false,
						res.getBoolean("des_Actif"),
						desDateCreation != null ? desDateCreation.toLocalDateTime() : null,
						desDateModification != null ? desDateModification.toLocalDateTime() : null
				);
				Employe expediteur = new Employe(
						res.getString("Expediteur"),
						res.getString("exp_Nom"),
						res.getString("exp_Prenom"),
						res.getString("exp_Email"),
						null,
						res.getString("exp_Telephone"),
						res.getString("exp_Adresse"),
						expDateNaissance != null ? expDateNaissance.toLocalDateTime().toLocalDate() : null,
						res.getInt("exp_Role"),
						true,
						res.getBoolean("exp_Actif"),
						expDateCreation != null ? expDateCreation.toLocalDateTime() : null,
						expDateModification != null ? expDateModification.toLocalDateTime() : null
				);
				Article article = new Article(
						res.getString("Tag_article"),
						res.getString("Nom_article"),
						res.getString("Description_article"),
						new TypeArticle(
								res.getString("Tag_type"),
								res.getString("Nom_type"),
								res.getString("Description_type")
						),
						res.getLong("Nombre")
				);
				Sortie f = new Sortie(
						res.getString("Tag_sortie"),
						destinataire,
						expediteur,
						article,
						res.getLong("Nombre_article_sortie"),
						res.getTimestamp("Date_sortie").toLocalDateTime()
				);
				listSortie.add(f);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return listSortie;
	}


	public void addEntree(Connection c, Entree flux) throws SQLException {
		ArticleDataController fn = new ArticleDataController();
		try{
			c.setAutoCommit(false);

			try (PreparedStatement post_stmt1 = c.prepareStatement("SELECT COALESCE(Nombre,0) AS Stock_defaut FROM Nombre_article WHERE Tag_article = ?")) {

				post_stmt1.setString(1, flux.getArticle().getTag_article());
				ResultSet rs = post_stmt1.executeQuery();
				while (rs.next()) {
					if (rs.getLong("Stock_defaut") == 0) {

					}
				}
			}

            try(PreparedStatement p_stmt = c.prepareStatement("INSERT INTO Entree ( "
							+ "  Tag_entree, "
							+ "  Tag_article, "
							+ "  Expediteur, "
							+ "  Date_entree, "
							+ "  Nombre_article_entree "
							+ ") VALUES (?, ?, ?, ?, ?)")
			){
				p_stmt.setString(1, flux.getTag_flux());
				p_stmt.setString(2, flux.getArticle().getTag_article());
				p_stmt.setString(3, flux.getExpediteur().getTag_fournisseur());
				p_stmt.setTimestamp(4, Timestamp.valueOf(flux.getDate_deplacement()));
				p_stmt.setLong(5, flux.getNombre_article_deplace());
				p_stmt.executeUpdate();
				// set value
			}

			try {
				fn.updateArticle(c, flux.getArticle().getTag_article(), flux.getArticle(), "Entree", flux.getNombre_article_deplace());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		catch (Exception e) {
			try {
				c.rollback();
			} catch (SQLException ex) {
				System.err.println("Rollback failed: " + ex.getMessage());
			}
			if (e instanceof ErrorConfirmException) {
				throw ( ErrorConfirmException) e;
			} else if (e instanceof SQLException) {
				throw (SQLException) e;
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			try {
				c.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Erreur setAutoCommit(true): " + e.getMessage());
			}
		}
	}


	public void addSortie(Connection c, Sortie flux) throws SQLException {
		ArticleDataController fn = new ArticleDataController();
		try{
			c.setAutoCommit(false);
			try (PreparedStatement post_stmt1 = c.prepareStatement("SELECT COALESCE(Nombre,0) AS Stock_defaut FROM Nombre_article WHERE Tag_article = ?")) {
				post_stmt1.setString(1, flux.getArticle().getTag_article());
				ResultSet rs = post_stmt1.executeQuery();
				while (rs.next()) {
					long reste = rs.getLong("Stock_defaut") - flux.getNombre_article_deplace();
					if (reste < 0) {
                        throw new ErrorConfirmException("Vous ne pouvez pas sortir " + flux.getNombre_article_deplace() + "articles ayant le tag " + flux.getArticle().getTag_article() + " \n  RESTE à combler: " + Math.abs(reste));
					} else if (rs.getLong("Stock_defaut") - flux.getNombre_article_deplace() == 0){
						System.out.println("Stock_defaut ok");
					}
				}
			}

			try (PreparedStatement p_stmt = c.prepareStatement("INSERT INTO Sortie ("
						+ "Tag_sortie, "
						+ "Expediteur, "
						+ "Destinataire, "
						+ "Tag_article, "
						+ "Date_sortie, "
						+ "Nombre_article_sortie "
						+ ") VALUES (?, ?, ?, ?, ?, ?)")
			){
				p_stmt.setString(1, flux.getTag_flux());
				p_stmt.setString(2, flux.getExpediteur().getMatricule());
				p_stmt.setString(3, flux.getDestinataire().getMatricule());
				p_stmt.setString(4, flux.getArticle().getTag_article());
				p_stmt.setTimestamp(5, Timestamp.valueOf(flux.getDate_deplacement()));
				p_stmt.setLong(6, flux.getNombre_article_deplace());
				p_stmt.executeUpdate();
			}

			try {
				fn.updateArticle(c, flux.getArticle().getTag_article(), flux.getArticle(), "Sortie", flux.getNombre_article_deplace());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		catch (Exception e) {
			try {
				c.rollback();
			} catch (SQLException ex) {
				System.err.println("Rollback failed: " + ex.getMessage());
			}
			if (e instanceof ErrorConfirmException) {
				throw ( ErrorConfirmException) e;
			} else if (e instanceof SQLException) {
				throw (SQLException) e;
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			try {
				c.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Erreur setAutoCommit(true): " + e.getMessage());
			}
		}
	}

	public static String nextTagFlux(Connection c, String type) throws Exception {
		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		String query,  typeTag, searchTag;
		if (type.trim().equals("Entree")){
			query = """
						SELECT Tag_entree
						FROM Entree
						ORDER BY CAST(SUBSTRING(Tag_entree, 4) AS INT) DESC LIMIT 1;
					""" ;
			searchTag = "Tag_entree";
		}
		else {
			query =	"""
						SELECT Tag_sortie
						FROM Sortie
						ORDER BY CAST(SUBSTRING(Tag_sortie, 4) AS INT) DESC LIMIT 1;
					""" ;
			searchTag = "Tag_sortie";
		}
		String nextTag = null;

		try (Statement stmt = c.createStatement()) {

			try (ResultSet rs = stmt.executeQuery(query)) {
				if (rs.next()) {
					nextTag = rs.getString(searchTag);
				}
			}
		} catch (SQLException e) {
			throw new Exception("Erreur lors de la récupération du tag");
		}
		return nextTag;
	}

	public static void testError(String tagArticle, String destinataire, String expediteur, String nombreArticleDeplace, String type) {
		List<String> errors = new ArrayList<>();

		if (tagArticle == null || tagArticle.trim().isEmpty()) {
			errors.add("tag_article: Veuilez choisir un article valide !");
		}

		if(expediteur == null || expediteur.trim().isEmpty()) {
			errors.add("expediteur: Veuillez choisir un expéditeur valide !");
		}

		if(type.equals("Sortie")) {
			if(destinataire == null || destinataire.trim().isEmpty()) {
				errors.add("destinataire: Veuillez choisir un destinataire valide !");
			}
		}

		if(nombreArticleDeplace == null || nombreArticleDeplace.trim().isEmpty()) {
			errors.add("nombre_article: Veuillez entrer un nombre valide !");
		}else if (!nombreArticleDeplace.matches("^\\d+$") || Integer.parseInt(nombreArticleDeplace.trim()) <= 0) {
			errors.add("nombre_article: Le nombre d'article à mouvoir doit être un nombre entier strictement positif !");
		}

		if (!errors.isEmpty()) {
			throw new ErrorConfirmException(errors);
		}
	}
	
}
