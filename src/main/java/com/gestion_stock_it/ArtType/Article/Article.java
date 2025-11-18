package com.gestion_stock_it.ArtType.Article;

import com.gestion_stock_it.ArtType.Type.TypeArticle;

public class Article{

  private String tag_article;
  private final String nom_article;
  private final String description_article;
  private final TypeArticle type_article;
  private final long nombre_article;
  private final String situation_article;

  private long nombre_occurence_entrees = 0;
  private long nombre_occurence_sorties = 0;
  	
  public Article(String tag_article, String nom_article, String description_article, TypeArticle type_article, long nombre_article) {
	  this.tag_article = tag_article;
	  this.nom_article = nom_article;
	  this.description_article = description_article;
	  this.type_article = type_article;
	  this.nombre_article = nombre_article;
	  this.situation_article = ( (nombre_article == 0) ? "Rupture de stock" : "En stock" );
  }

  public void setNombre_occurence_entrees(long nombre_occurence_entrees) {this.nombre_occurence_entrees = nombre_occurence_entrees;}
  public void setNombre_occurence_sorties(long nombre_occurence_sorties) {this.nombre_occurence_sorties = nombre_occurence_sorties;}

  public String getTag_article() { return tag_article; }
  public String getNom_article() { return nom_article; }
  public String getDescription_article() { return description_article; }
  public TypeArticle getType_article() { return type_article; }
  public long getNombre_article() { return nombre_article; }
  public String getSituation_article() { return situation_article; }
  public long getNombre_occurence_entrees() {return nombre_occurence_entrees;}
  public long getNombre_occurence_sorties() {return nombre_occurence_sorties;}

  public void setTag_article(String tag_article) {
      long partInt = Long.parseLong(tag_article.trim().substring(4));
      partInt ++;
      this.tag_article = "ART00" + partInt;
  }
}
