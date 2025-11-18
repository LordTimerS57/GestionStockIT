package com.gestion_stock_it.ArtType.Type;

public class TypeArticle {
  private String tag_type;
  private final String nom_type;
  private final String description_type;

  private long nombre_occurence_article = 0;

  public long getNombre_occurence_article() {return nombre_occurence_article;}
  public void setNombre_occurence_article(long Occurence_article) {this.nombre_occurence_article = Occurence_article;}

    public TypeArticle(String tag_type, String nom_type, String description_type) {
	this.tag_type = tag_type;
	this.nom_type = nom_type;
	this.description_type = description_type;
  }
  
  public String getTag_type() { return tag_type; }
  public String getNom_type() { return nom_type; }
  public String getDescription_type() { return description_type; }

    public void setTag_type(String tag_type) {
        long partInt = Long.parseLong(tag_type.trim().substring(4));
        partInt ++;
        this.tag_type = "TYP00" + partInt;
    }
 
}
