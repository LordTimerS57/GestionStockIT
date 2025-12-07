package com.gestion_stock_it.Fournisseur;

public class Fournisseur {
    private final String tag_fournisseur;
    private final String raison_sociale;
    private final String email_fournisseur;
    private final String telephone_fournisseur;

    private long nombre_occurence_entree_fournisseur = 0;

    public long getNombre_occurence_entree_fournisseur() {return nombre_occurence_entree_fournisseur;}
    public void setNombre_occurence_entree_fournisseur(long occ) {this.nombre_occurence_entree_fournisseur = occ;}

    public Fournisseur(String Tag_fournisseur, String Raison_Sociale, String Email_fournisseur, String Telephone_fournisseur) {
        this.tag_fournisseur = Tag_fournisseur;
        this.raison_sociale = Raison_Sociale;
        this.email_fournisseur = Email_fournisseur;
        this.telephone_fournisseur = Telephone_fournisseur;
    }

    public String getTag_fournisseur() {return tag_fournisseur;}
    public String getRaison_sociale() { return raison_sociale; }
    public String getEmail_fournisseur() { return email_fournisseur; }
    public String getTelephone_fournisseur() { return telephone_fournisseur; }

}
