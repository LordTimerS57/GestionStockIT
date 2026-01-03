package com.gestion_stock_it.ArtType.Article;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.gestion_stock_it.ArtType.Type.TypeArticle;

public class Article{

  private String tag_article;
  private final String nom_article;
  private final String description_article;
  private final TypeArticle type_article;
  private final long stock_article;
  
  private double cmd;
  private double delai_reappro_estime;
  private long seuil_critique_arrondi;
  
  private String situation_article;
  
  private long nombre_occurence_entrees_article = 0;
  private LocalDateTime date_derniere_entree;
  
  private long nombre_occurence_sorties_article = 0;
  private LocalDateTime date_derniere_sortie;
  
  public Article(String tag_article, String nom_article, String description_article, TypeArticle type_article, long stock_article) {
	  this.tag_article = tag_article;
	  this.nom_article = nom_article;
	  this.description_article = description_article;
	  this.type_article = type_article;
	  this.stock_article = stock_article;
  }

  public void setNombre_occurence_entrees_article(long occ) {this.nombre_occurence_entrees_article = occ;}
  public void setNombre_occurence_sorties_article(long occ) {this.nombre_occurence_sorties_article = occ;}
  public void setSituation_article(String status_alerte, long nombre_article) {
	  this.situation_article = ( (nombre_article == 0) ? "Rupture de stock" : status_alerte );
  }
  
  public void setCMD(double cmd) {this.cmd = cmd;}
  public void setDelai_reappro_estime(double delai) {this.delai_reappro_estime = delai;}
  public void setSeuil_critique_arrondi(long seuil) {this.seuil_critique_arrondi = seuil;}
  
  public void setDate_derniere_entree(LocalDateTime date) {this.date_derniere_entree = date;}
  public void setDate_derniere_sortie(LocalDateTime date) {this.date_derniere_sortie = date;}

  public String getTag_article() { return tag_article; }
  public String getNom_article() { return nom_article; }
  public String getDescription_article() { return description_article; }
  public TypeArticle getType_article() { return type_article; }
  public long getStock_article() { return stock_article; }
  public String getSituation_article() { return situation_article; }
  public long getNombre_occurence_entrees_article() {return nombre_occurence_entrees_article;}
  public long getNombre_occurence_sorties_article() {return nombre_occurence_sorties_article;}
  
  public String getCMD() { return ((cmd > 0 || delai_reappro_estime > 0 ) ? String.format("%.3f", cmd) + " unité" + (Math.floor(cmd) > 1 ? "s" : "") : "N/A" );}
  public String getDelai_reappro_estime() {return (delai_reappro_estime > 0 ? String.format("%.2f", delai_reappro_estime) + " jour" + (Math.floor(delai_reappro_estime) > 1 ? "s" : "") : "N/A" );}
  public long getSeuil_critique_arrondi() {return seuil_critique_arrondi;}
  
  public String getDate_derniere_entree() {
      return (date_derniere_entree != null) ? date_derniere_entree.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString() : "Jamais";
  }
  public String getDate_derniere_sortie() {
      return (date_derniere_sortie != null) ? date_derniere_sortie.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString() : "Jamais";
  }
  
  public void setTag_article(String tag_article) {
      long partInt = Long.parseLong(tag_article.trim().substring(4));
      partInt ++;
      this.tag_article = "ART00" + partInt;
  }
}
