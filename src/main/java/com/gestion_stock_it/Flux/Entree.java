package com.gestion_stock_it.Flux;

import com.gestion_stock_it.Employe.Employe;

import java.time.LocalDateTime;

import com.gestion_stock_it.ArtType.Article.Article;
import com.gestion_stock_it.Fournisseur.Fournisseur;

public class Entree extends Flux{
    private Employe destinataire;
    private Fournisseur expediteur;

    public Entree(String tag_entree, Employe destinataire, Fournisseur expediteur, Article article, long nbr_article_deplace, LocalDateTime date_flux) {
        super(tag_entree, article, nbr_article_deplace, date_flux);
        this.destinataire = destinataire;
        this.expediteur = expediteur;
    }

    public Employe getDestinataire() {return destinataire;}
    public Fournisseur getExpediteur() {return expediteur;}

}

