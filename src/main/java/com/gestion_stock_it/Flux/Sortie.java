package com.gestion_stock_it.Flux;

import com.gestion_stock_it.Employe.Employe;

import java.time.LocalDateTime;

import com.gestion_stock_it.ArtType.Article.Article;

public class Sortie extends Flux{
	  private Employe destinataire;
	  private Employe expediteur;

	  public Sortie(String tag_sortie, Employe destinataire, Employe expediteur, Article article, long nbr_article_deplace, LocalDateTime date_flux) {
		super(tag_sortie, article, nbr_article_deplace, date_flux);
	    this.destinataire = destinataire;
	    this.expediteur = expediteur;
	  }

	  public Employe getDestinataire() { return destinataire; }
	  public Employe getExpediteur() { return expediteur; }

}
