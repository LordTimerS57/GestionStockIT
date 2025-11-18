package com.gestion_stock_it.Flux;

import com.gestion_stock_it.ArtType.Article.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flux {
    private String tag_flux;
    private Article article;
    private long nombre_article_deplace;
    private LocalDateTime date_deplacement;

    public Flux(String tag_flux, Article article, long nombre_article_deplace, LocalDateTime date_deplacement) {
        this.tag_flux = tag_flux;
        this.article = article;
        this.nombre_article_deplace = nombre_article_deplace;
        this.date_deplacement = date_deplacement;
    }
    public String getTag_flux() {
        return tag_flux;
    }
    public Article getArticle() { return article; }
    public long getNombre_article_deplace() { return nombre_article_deplace; }
    public LocalDateTime getDate_deplacement() { return date_deplacement; }

    public String getDate_deplacement_formatter() {
        return date_deplacement.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSSSSS "));
    }

    public void setTag_flux(String tag_flux) {
        String partTag = tag_flux.trim().substring(0,3);
        long partInt = Long.parseLong(tag_flux.trim().substring(4));
        partInt ++;

        this.tag_flux = partTag + "00" + partInt;
    }
}
