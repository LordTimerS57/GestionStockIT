package com.gestion_stock_it.Employe;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Employe {
	
  private String matricule;
  private String nom;
  private String prenom;
  private String email;
  private String mot_de_passe;
  private String telephone;
  private String adresse;
  private LocalDate date_naissance;
  private String role;
  private boolean connecte;
  private boolean actif;
  private LocalDateTime date_creation;
  private LocalDateTime date_modification;

  public Employe() {}

  public Employe(String matricule, String nom, String prenom, String email, String mot_de_passe, String telephone, String adresse, LocalDate date_naissance, int role, boolean connecte, boolean actif, LocalDateTime date_creation, LocalDateTime date_modification) {
	  this.matricule = matricule;
	  this.nom = nom;
	  this.prenom = prenom;
	  this.email = email;
	  this.mot_de_passe = mot_de_passe;
	  this.telephone = telephone;
	  this.adresse = adresse;
	  this.date_naissance = date_naissance;
	  switch(role) {
	  	case 1:
	  		this.role = "Super Administrateur";
	  		break;
        case 2:
            this.role = "Administrateur";
            break;
	  	case 3:
	  		this.role = "Employe Simple";
	  		break;
	  	default:
	  		this.role = "Employe";
	  		break;
	  }
      this.actif = actif;
      this.connecte = connecte;
	  this.date_creation = date_creation;
	  this.date_modification = date_modification;
  }

  public String getMatricule() { return matricule; }
  public String getNom() { return nom; }
  public String getPrenom() { return prenom; }
  public String getEmail() { return email; }
  public String getMot_de_passe() { return mot_de_passe; }
  public String getTelephone() { return telephone; }
  public String getAdresse() { return adresse; }
  public LocalDate getDate_naissance() { return date_naissance; }
  public String getRole() { return role; }
  public boolean getActivite() {return actif;}
  public boolean getConnection() { return connecte; }
  public LocalDateTime getDate_creation() { return date_creation; }
  public LocalDateTime getDate_modification() { return date_modification; }

  public String getDate_creation_formatter() {
      return date_creation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSSSSS "));
  }
  public String getDate_modification_formatter() {
      return date_modification.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSSSSS "));
  }
  public String getDate_de_naissance_formatter() {
      return date_naissance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }
  public String getNomPrenom() { return nom + " " + prenom; }

  public int getRoleInt(){
      int parse_role = -1;
      switch(role) {
          case "Super Administrateur":
              parse_role = 1;
              break;

          case "Administrateur":
              parse_role = 2;
              break;

          case "Employe Simple":
              parse_role = 3;
              break;

          default: break;
      }
      return parse_role;
  }

  public boolean changeActivite() {
      this.actif = !this.actif;
      return this.actif;
  }

  public void setMatricule(String matricule) {this.matricule = matricule;}
  public void setNom(String nom) {this.nom = nom;}
  public void setPrenom(String prenom) {this.prenom = prenom;}
  public void setAdresse(String adresse) {this.adresse = adresse;}
  public void setRoleInt(int role) {
      switch(role) {
          case 1:
              this.role = "Super Administrateur";
              break;
          case 2:
              this.role = "Administrateur";
              break;
          case 3:
              this.role = "Employe Simple";
              break;
          default:
              this.role = "Employe";
              break;
      }
  }
  public void setDate_modification(LocalDateTime date_modification) {this.date_modification = date_modification;}
  public void setMot_de_passe(String mot_de_passe){this.mot_de_passe = mot_de_passe;}
  public void setDate_naissance(LocalDate date_naissance) {this.date_naissance = date_naissance;}
  public void setTelephone(String telephone) {this.telephone = telephone;}
  public void setEmail(String email) {this.email = email;}
  public void setDateCreation(LocalDateTime now) {this.date_creation = now;}

  public void setMatriculeSpecific(String matricule) {
      if(matricule == null || matricule.isEmpty()){
          this.matricule = "EMP001";
      }
      else{
          long partInt = Long.parseLong(matricule.trim().substring(4));
          partInt++;
          this.matricule = "EMP00" + partInt;
      }
  }
}
