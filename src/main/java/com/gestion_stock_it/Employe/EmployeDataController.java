package com.gestion_stock_it.Employe;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.gestion_stock_it.DatabaseConnection;
import com.gestion_stock_it.ErrorConfirmException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.mindrot.jbcrypt.BCrypt;

public class EmployeDataController {
	private final String EMAIL_SENDER = System.getenv("MAIL_HOST");
	private final String EMAIL_PASSWORD = System.getenv("MAIL_PASSWORD");

	public List<Employe> getEmployeList(String Matricule, String Nom_prenom, int Role, String Email, String Connecte, String Actif) throws Exception {
		List<Employe> listEmploye = new ArrayList<>();

		DatabaseConnection db = new DatabaseConnection();
		db.connect();


		StringBuilder subQuery = new StringBuilder();
		List<Object> params = new ArrayList<>();

		boolean hasNomPrenom = Nom_prenom != null && !Nom_prenom.trim().isEmpty();
		boolean hasRole = Role >= 0;
		boolean hasMatricule = Matricule != null && !Matricule.trim().isEmpty();
		boolean hasEmail = Email != null && !Email.trim().isEmpty();
		boolean hasConnection = Connecte != null && !Connecte.trim().isEmpty();
		boolean hasActif = Actif != null && !Actif.trim().isEmpty();

		if (hasNomPrenom || hasRole || hasMatricule || hasEmail  || hasConnection || hasActif ) {
			subQuery.append(" WHERE ");
			List<String> conditions = new ArrayList<>();

			if(hasActif) {
				conditions.add("Actif = ?");
				params.add(Actif.equals("oui"));
			}

			if(hasConnection){
				conditions.add("Connecte = ?");
				params.add(Connecte.equals("oui"));
			}

			if (hasNomPrenom) {
				conditions.add("(CONCAT(Nom, ' ', Prenom) LIKE ?)");
				params.add("%" + Nom_prenom + "%");
			}

			if (hasRole) {
				conditions.add("Role = ?");
				params.add(Role);
			}

			if (hasMatricule) {
				conditions.add("Matricule = ?");
				params.add(Matricule);
			}
			if (hasEmail) {
				conditions.add("Email = ?");
				params.add(Email);
			}

			subQuery.append(String.join(" AND ", conditions));
		}

		String finalQuery = subQuery.toString();


		try(Connection conn = db.getConnection();
			PreparedStatement p_stmt = conn.prepareStatement("SELECT * FROM Employe " + finalQuery);
		)
		{

			for (int i = 0; i < params.size(); i++) {
				p_stmt.setObject(i + 1, params.get(i));
			}

			ResultSet res = p_stmt.executeQuery();

			while (res.next()) {
				Employe emp = new Employe(
						res.getString("Matricule"),
						res.getString("Nom"),
						res.getString("Prenom"),
						res.getString("Email"),
						res.getString("Mot_de_passe"),
						res.getString("Telephone"),
						res.getString("Adresse"),
						res.getTimestamp("Date_de_naissance").toLocalDateTime().toLocalDate(),
						res.getInt("Role"),
						res.getBoolean("Connecte"),
						res.getBoolean("Actif"),
						res.getTimestamp("Date_creation").toLocalDateTime(),
						res.getTimestamp("Date_modification").toLocalDateTime()
				);
				listEmploye.add(emp);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return listEmploye;
	}

	public Employe testMotDePasse(String matricule, String motDePasse, String email) throws ErrorConfirmException {
		if ((matricule == null || matricule.trim().isEmpty()) &&
				(email == null || email.trim().isEmpty())) {
			throw new ErrorConfirmException("email_matricule_login: Veuillez saisir le matricule ou l'adresse email !");
		}

		if (motDePasse == null || motDePasse.trim().isEmpty()) {
			throw new ErrorConfirmException("mot_de_passe_login: Veuillez saisir le mot de passe !");
		}

		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		StringBuilder query = new StringBuilder("SELECT * FROM Employe WHERE ");
		List<Object> params = new ArrayList<>();
		List<String> conditions = new ArrayList<>();

		if (matricule != null && !matricule.trim().isEmpty()) {
			conditions.add(" Matricule = ? ");
			params.add(matricule);
		}
		if (email != null && !email.trim().isEmpty()) {
			conditions.add(" Email = ? ");
			params.add(email);
		}
		query.append(String.join(" AND ", conditions));


		try (Connection conn = db.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query.toString())) {

			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				// Sécurisé : ne précise pas si identifiant existe ou pas
				throw new ErrorConfirmException("email_matricule_login: Veuillez verifier si l'adresse email / matricule ou le mot de passe sont corrects!");
			}

			String hashedPwd = rs.getString("Mot_de_passe");
			System.out.println(BCrypt.checkpw(motDePasse, hashedPwd));
			if (!BCrypt.checkpw(motDePasse, hashedPwd)) {
				throw new ErrorConfirmException("mot_de_passe_login: Veuillez vérifier votre mot de passe !");
			}

			return new Employe(
					rs.getString("Matricule"),
					rs.getString("Nom"),
					rs.getString("Prenom"),
					rs.getString("Email"),
					rs.getString("Mot_de_passe"),
					rs.getString("Telephone"),
					rs.getString("Adresse"),
					rs.getTimestamp("Date_de_naissance").toLocalDateTime().toLocalDate(),
					rs.getInt("Role"),
					rs.getBoolean("Connecte"),
					rs.getBoolean("Actif"),
					rs.getTimestamp("Date_creation").toLocalDateTime(),
					rs.getTimestamp("Date_modification") != null ? rs.getTimestamp("Date_modification").toLocalDateTime() : null
			);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ErrorConfirmException("Erreur interne lors de la connexion.");
		}
	}

	public Employe getEmployeByMatricule(String matricule) throws Exception {
		Employe employe = null;
		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		StringBuilder query = new StringBuilder("SELECT * FROM Employe");
		List<Object> params = new ArrayList<>();

		if (matricule != null && !matricule.trim().isEmpty()) {
			query.append(" WHERE Matricule = ?");
			params.add(matricule);
		}

		try (Connection conn = db.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query.toString())) {

			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			ResultSet res = stmt.executeQuery();

			if (res.next()) {
				employe = new Employe(
						res.getString("Matricule"),
						res.getString("Nom"),
						res.getString("Prenom"),
						res.getString("Email"),
						res.getString("Mot_de_passe"),
						res.getString("Telephone"),
						res.getString("Adresse"),
						res.getTimestamp("Date_de_naissance").toLocalDateTime().toLocalDate(),
						res.getInt("Role"),
						res.getBoolean("Connecte"),
						res.getBoolean("Actif"),
						res.getTimestamp("Date_creation").toLocalDateTime(),
						res.getTimestamp("Date_modification").toLocalDateTime()
				);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return employe;
	}

	public boolean getEmployeActivite(String matricule) throws Exception {

		if (matricule == null || matricule.trim().isEmpty()) {
			return false; // Rien à faire si le tag est vide
		}

		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		String query = "SELECT Actif FROM Employe WHERE Matricule = ?";

		try (Connection conn = db.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, matricule);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getBoolean("Actif");
				}
			}

		} catch (SQLException e) {
			throw new Exception("Erreur lors de la récupération de l'activité par tag", e);
		}

		return false;
	}

	public void addEmploye(Connection c, Employe employe) {
		try(
				PreparedStatement p_stmt = c.prepareStatement("INSERT INTO Employe ( "
						+ "  Matricule, "
						+ "  Nom, "
						+ "  Prenom, "
						+ "  Date_de_naissance, "
						+ "  Email, "
						+ "  Mot_de_passe, "
						+ "  Adresse, "
						+ "  Connecte, "
						+ "  Role, "
						+ "  Date_creation, "
						+ "  Date_modification, "
						+ "  Telephone "
						+ " ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
		){
			p_stmt.setString(1, employe.getMatricule()); // set value
			p_stmt.setString(2, employe.getNom()); // set value
			p_stmt.setString(3, employe.getPrenom());
			p_stmt.setTimestamp(4, Timestamp.valueOf(employe.getDate_naissance().atStartOfDay()));
			p_stmt.setString(5, employe.getEmail());
			p_stmt.setString(6, employe.getMot_de_passe());
			p_stmt.setString(7, employe.getAdresse());
			p_stmt.setBoolean(8, employe.getConnection());
			p_stmt.setInt(9, employe.getRole().equals("Administrateur") ? 2 : employe.getRole().equals("Employe Simple") ? 3 : 1);
			p_stmt.setTimestamp(10, Timestamp.valueOf(employe.getDate_creation()));
			p_stmt.setTimestamp(11, null);
			p_stmt.setString(12, employe.getTelephone());
			p_stmt.executeUpdate();
			// set value
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateEmploye(Connection c, String matricule, Employe employe, int section) {
		StringBuilder query = new StringBuilder("UPDATE Employe SET ");
		List<Object> params = new ArrayList<>();

		if (section == 1) {
			query.append("Nom = ?, Prenom = ?, Adresse = ?, Telephone = ?, Date_de_naissance = ?, ");
			params.add(employe.getNom());
			params.add(employe.getPrenom());
			params.add(employe.getAdresse());
			params.add(employe.getTelephone());
			params.add(Timestamp.valueOf(employe.getDate_naissance().atStartOfDay()));
		}

		else if (section == 2) {
			query.append("Email = ?, ");

			params.add(employe.getEmail());
		}

		else if (section == 3) {
			query.append("Mot_de_passe = ?, ");
			params.add(employe.getMot_de_passe());
		}

		else if (section == 4) {
			query.append("Role = ?, ");
			params.add(
					employe.getRole().equals("Administrateur") ? 2 :
							employe.getRole().equals("Employe Simple") ? 3 : 1
			);
		}

		query.append("Date_modification = ? WHERE Matricule = ?");
		params.add(Timestamp.valueOf(employe.getDate_modification()));
		params.add(matricule);

		try (PreparedStatement p_stmt = c.prepareStatement(query.toString())) {
			for (int i = 0; i < params.size(); i++) {
				p_stmt.setObject(i + 1, params.get(i));
			}

			int rows = p_stmt.executeUpdate();
			System.out.println("Employé " + matricule + " mis à jour (" + rows + " ligne(s)).");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void connect(Connection c, String matricule, String Email, String Mot_de_passe, String connection) {
		boolean connected = connection != null && !connection.trim().isEmpty();
		System.out.println("Connection value: " + connection + ", connected: " + connected);
		try {
			StringBuilder updateQuery = new StringBuilder("UPDATE Employe SET Connecte = ?, Date_modification = NOW() WHERE Matricule = ?");
			try (PreparedStatement p_stmt = c.prepareStatement(updateQuery.toString())) {
				p_stmt.setBoolean(1, connected && connection.equals("oui"));
				p_stmt.setString(2, matricule);

				int rows = p_stmt.executeUpdate();
				if (rows > 0) {
					System.out.println( (connected && connection.equals("oui")) ? "Employé connecté avec succès : " + Email : "Employé déconnecté avec succès : " + matricule);	
				} else {
					System.out.println( (connected && connection.equals("oui")) ? "Échec de la mise à jour de la connexion pour : " + Email : "Échec de la mise à jour de la déconnexion pour : " + matricule);
				}
			}

		} catch (ErrorConfirmException | SQLException e) {
			e.printStackTrace();
		}
	}


	public void deleteEmploye(Connection c, boolean activite, String matricule) {
		try(
				PreparedStatement p_stmt = c.prepareStatement("UPDATE Employe SET Date_modification = NOW(), Actif = ? WHERE Matricule = ?")
		){
			p_stmt.setBoolean(1, activite);
			p_stmt.setString(2, matricule);
			p_stmt.executeUpdate();
			System.out.println(matricule + " " + activite);
			System.out.println("Axe blabla");
			// set value
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(Employe destinataire, Employe concerne, String sujet) throws Exception {

		// Construire le corps du message
		StringBuilder corpsEmail = new StringBuilder();

		corpsEmail.append("Cher ")
				.append((destinataire.getRoleInt() == 1) ? "Administrateur" : "Employé")
				.append(",\n\n");

		switch (sujet) {

			case "Changement d'adresse email" -> {
				corpsEmail.append("L'employé ")
						.append(concerne.getNom()).append(" ").append(concerne.getPrenom())
						.append(" a changé son adresse email en : ").append(concerne.getEmail()).append(".\n")
						.append("Merci de prendre note de cette modification pour le contacter correctement.\n");
			}

			case "Création d'un nouveau compte employé" -> {
				corpsEmail.append("Un employé tente de créer un nouveau compte, mais il n'a pas les permissions nécessaires.\n")
						.append("Veuillez vérifier cette demande et la valider si nécessaire.\n")
						.append("Email du demandeur : ").append(concerne.getEmail()).append("\n");
			}

			case "Désactivation de votre compte" -> {
				corpsEmail.append("Votre compte a été désactivé par un administrateur.\n")
						.append("Vous ne pouvez plus vous connecter pour le moment.\n")
						.append("Veuillez contacter l'administration pour plus d'informations.\n");
			}

			case "Mise à niveau de votre rôle" -> {
				corpsEmail.append("Votre rôle a été mis à jour par un administrateur.\n")
						.append("Veuillez consulter votre profil pour voir votre nouveau rôle.\n");
			}

			case "Réactivation de votre compte" -> {
				corpsEmail.append("Votre compte a été réactivé !\n")
						.append("Vous pouvez désormais vous reconnecter normalement.\n");
			}

			default -> corpsEmail.append("Vous avez reçu une notification concernant votre compte.\n")
					.append("Veuillez contacter l'administration pour plus d'informations.\n");
		}

		// Configuration SMTP
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EMAIL_SENDER, EMAIL_PASSWORD);
			}
		});

		// Création du message
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(EMAIL_SENDER));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire.getEmail()));
		message.setSubject(sujet);
		message.setText(corpsEmail + "\n\nMerci de votre compréhension,\nLe service client");

		// Envoi
		try {
			Transport.send(message);
			System.out.println("Email envoyé avec succès à " + destinataire.getEmail());
		} catch (MessagingException ex) {
			ex.printStackTrace();
			System.err.println("Échec de l'envoi : " + ex.getMessage());
		}

	}

	public String nextTag(Connection c) throws Exception {
		DatabaseConnection db = new DatabaseConnection();
		db.connect();

		String query = """
        SELECT Matricule
        FROM Employe
		ORDER BY CAST(SUBSTRING(Matricule, 4) AS INT) DESC LIMIT 1;
        """;

		String nextTag = null;

		try (Statement stmt = c.createStatement()) {

			try (ResultSet rs = stmt.executeQuery(query)) {
				if (rs.next()) {
					nextTag = rs.getString("Matricule");
				}
			}
		} catch (SQLException e) {
			throw new Exception("Erreur lors de la récupération du tag");
		}
		return nextTag;
	}

	public static void testInfoPersonnel(String nom, String prenom, String adresse, String telephone, String dateNaissance) {
		List<String> errors = new ArrayList<>();

		if (nom == null || nom.trim().isEmpty()) {
			errors.add("nom: Veuillez entrer un nom valide !");
		} else if (!nom.matches("^[A-Za-zÀ-ÿ\\s'-]+$")) {
			errors.add("nom: Le nom ne doit contenir que des lettres !");
		}
		if (!prenom.matches("^[A-Za-zÀ-ÿ\\s'-]+$")) {
			errors.add("prenom: Le prénom ne doit contenir que des lettres !");
		}

		if(adresse == null || adresse.trim().isEmpty()) {
			errors.add("adresse: Veuillez entrer une adresse valide !");
		}

		if(telephone == null || telephone.trim().isEmpty()) {
			errors.add("tel: Veuillez entrer un numéro de téléphone valide !");
		}else if (!telephone.matches("^(?:\\+261|0)\\d{9}$")) {
			errors.add("tel: Le numéro de téléphone doit contenir exactement 10 chiffres !");
		}

		if(dateNaissance == null || dateNaissance.trim().isEmpty()) {
			errors.add("date_naissance: Veuillez entrer une date de naissance valide !");
		}
		else{
			LocalDate aujourdHui = LocalDate.now();
			LocalDate laDate = LocalDate.parse(dateNaissance);

			if (laDate.isAfter(aujourdHui)) {
				errors.add("date_naissance: La date de naissance ne peut pas être une date future !");
			} else if (laDate.isEqual(aujourdHui)) {
				errors.add("date_naissance: La date de naissance ne peut pas être la date d'aujourd'hui !");
			} else {
				int age = Period.between(laDate, aujourdHui).getYears();
				if (age < 16) {
					errors.add("date_naissance: L'employé doit avoir au moins 16 ans !");
				}
				if (age > 65){
					errors.add("date_naissance: L'employé ne devrait pas avoir pas plus de 65 ans");
				}
			}
		}


		if (!errors.isEmpty()) {
			throw new ErrorConfirmException(errors);
		}
	}

	public static void testRole(String role) {
		List<String> errors = new ArrayList<>();

		if (role == null || role.trim().isEmpty()) {
			errors.add("role: Veuillez selectionner un rôle valide !");
		}

		if (!errors.isEmpty()) {
			throw new ErrorConfirmException(errors);
		}
	}

	public static void testEmail(Connection c, String email) {
		List<String> errors = new ArrayList<>();

		if (email == null || email.trim().isEmpty()) {
			errors.add("email: Veuillez entrer un adresse email valide !");
		}
		else
		{
			String query = """
				SELECT Matricule
				FROM Employe
				WHERE Email = ?;
			""";

			int count = 0;

			try (PreparedStatement stmt = c.prepareStatement(query)) {
				stmt.setString(1, email);
				ResultSet rs = stmt.executeQuery();

				while (rs.next()) {
					count++;
				}

				if (count != 0) {
					throw new ErrorConfirmException("email: Veuillez saisir un autre adresse email valide, l'adresse mail "+ email +"n'est plus valide !");
				}

			} catch (SQLException e) {
				throw new ErrorConfirmException("Erreur lors du comptage");
			}
		}

		if (!errors.isEmpty()) {
			throw new ErrorConfirmException(errors);
		}
	}

	public static void testInfoMotDePasse(String motDePasse) {
		List<String> errors = new ArrayList<>();

		if(motDePasse == null || motDePasse.trim().isEmpty()) {
			errors.add("mot_de_passe: Veuillez saisir un mot de passe valide !");
		}


		if (!errors.isEmpty()) {
			throw new ErrorConfirmException(errors);
		}
	}
}
