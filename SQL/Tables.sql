CREATE TABLE Employe (
  Matricule         varchar(255) NOT NULL, 
  Nom               varchar(255) NOT NULL, 
  Prenom            varchar(255), 
  Date_de_naissance timestamp NOT NULL, 
  Email             varchar(255) NOT NULL, 
  Mot_de_passe      text NOT NULL, 
  Adresse           text NOT NULL, 
  Telephone         varchar(13) NOT NULL, 
  Role              int4 NOT NULL, 
  Connecte          bool NOT NULL,
  Actif             bool NOT NULL DEFAULT TRUE,
  Date_creation     timestamp NOT NULL, 
  Date_modification timestamp , 
  PRIMARY KEY (Matricule));
CREATE TABLE Article (
  Tag_article         varchar(255) NOT NULL, 
  Tag_type            varchar(255) NOT NULL, 
  Nom_article         varchar(255) NOT NULL, 
  Description_article text NOT NULL, 
  Stock_article       int8 DEFAULT 0 NOT NULL, 
  PRIMARY KEY (Tag_article));
CREATE TABLE Type (
  Tag_type         varchar(255) NOT NULL, 
  Nom_type         varchar(255) NOT NULL, 
  Description_type text NOT NULL, 
  PRIMARY KEY (Tag_type));
CREATE TABLE Sortie (
  Tag_sortie            varchar(255) NOT NULL, 
  Tag_article           varchar(255) NOT NULL, 
  Expediteur            varchar(255) NOT NULL, 
  Destinataire          varchar(255) NOT NULL, 
  Date_sortie           timestamp NOT NULL, 
  Nombre_article_sortie int8 NOT NULL, 
  PRIMARY KEY (Tag_sortie));
CREATE TABLE Entree (
  Tag_entree            varchar(255) NOT NULL, 
  Tag_article           varchar(255) NOT NULL, 
  Expediteur            varchar(255) NOT NULL, 
  Date_entree           timestamp NOT NULL, 
  Nombre_article_entree int8 NOT NULL, 
  PRIMARY KEY (Tag_entree));
CREATE TABLE Fournisseur (
  Tag_fournisseur       varchar(255) NOT NULL, 
  Raison_sociale        varchar(255), 
  Email_fournisseur     varchar(255), 
  Telephone_fournisseur varchar(13), 
  PRIMARY KEY (Tag_fournisseur));
ALTER TABLE Sortie ADD CONSTRAINT FKSortie222216 FOREIGN KEY (Tag_article) REFERENCES Article (Tag_article);
ALTER TABLE Article ADD CONSTRAINT FKArticle429601 FOREIGN KEY (Tag_type) REFERENCES Type (Tag_type);
ALTER TABLE Sortie ADD CONSTRAINT FKSortie774232 FOREIGN KEY (Expediteur) REFERENCES Employe (Matricule);
ALTER TABLE Entree ADD CONSTRAINT FKEntree67541 FOREIGN KEY (Expediteur) REFERENCES Fournisseur (Tag_fournisseur);
ALTER TABLE Entree ADD CONSTRAINT FKEntree896716 FOREIGN KEY (Tag_article) REFERENCES Article (Tag_article);
ALTER TABLE Sortie ADD CONSTRAINT FKSortie157801 FOREIGN KEY (Destinataire) REFERENCES Employe (Matricule);

CREATE OR REPLACE VIEW Nombre_article AS 
SELECT 
  a.Tag_article,
  COALESCE(SUM(e.Nombre_article_entree), 0) - COALESCE(SUM(s.Nombre_article_sortie), 0) AS Nombre
FROM
  Article a
LEFT JOIN  
  Entree e
ON
  e.Tag_article = a.Tag_article
LEFT JOIN  
  Sortie s
ON
  s.Tag_article = a.Tag_article
GROUP BY
  a.Tag_article;

CREATE OR REPLACE VIEW Seuil_critique_article AS
WITH
-- =================================================================================
-- 1. CALCUL DE LA CMD (Consommation Moyenne Journalière)
--    Mesure la moyenne des sorties sur la période historique enregistrée.
-- =================================================================================
CMD_Calculs AS (
    SELECT
        Tag_article,
        SUM(Nombre_article_sortie) AS Total_Sorties,
        -- Calcule la durée de l'historique en jours (conversion intervalle en jours)
        EXTRACT(EPOCH FROM (MAX(Date_sortie) - MIN(Date_sortie))) / 86400 AS Duree_Jours,
        
        -- CMD : Total_Sorties / Duree_Jours (Utilise Total_Sorties si la durée est <= 0 jour)
        CASE
            WHEN EXTRACT(EPOCH FROM (MAX(Date_sortie) - MIN(Date_sortie))) <= 0 THEN SUM(Nombre_article_sortie)
            ELSE SUM(Nombre_article_sortie) / (EXTRACT(EPOCH FROM (MAX(Date_sortie) - MIN(Date_sortie))) / 86400)
        END AS CMD_Journaliere
    FROM 
        Sortie
    GROUP BY 
        Tag_article
),

-- =================================================================================
-- 2. CALCUL DU DIRM (Délai Inter-Réception Moyen)
--    Mesure le temps moyen entre les réceptions successives (approximation du DR).
-- =================================================================================
DIRM_Intervalles AS (
    SELECT
        Tag_article,
        Date_entree,
        -- Fonction de fenêtrage : récupère la date d'entrée précédente
        LAG(Date_entree, 1) OVER (
            PARTITION BY Tag_article 
            ORDER BY Date_entree
        ) AS Date_entree_precedente
    FROM Entree
),
DIRM_Moyen AS (
    SELECT
        Tag_article,
        -- Calcule la moyenne de l'intervalle entre les réceptions en jours
        COALESCE(AVG(EXTRACT(EPOCH FROM (Date_entree - Date_entree_precedente)) / 86400), 0) AS DIRM_Jours
    FROM 
        DIRM_Intervalles
    WHERE 
        Date_entree_precedente IS NOT NULL
    GROUP BY 
        Tag_article
)

-- =================================================================================
-- 3. VUE FINALE : Assemblage et Détermination du Statut d'Alerte
-- =================================================================================
SELECT
    A.Tag_article,
    A.Nom_article,
    A.Stock_article AS Stock_Actuel,
    COALESCE(C.CMD_Journaliere, 0) AS CMD_Calculee,
    1 AS Stock_Securite, -- Fixé à 1 selon votre règle
    COALESCE(D.DIRM_Jours, 0) AS Delai_Reappro_Estime,
    
    -- Calcule le Seuil Critique : (CMD * DIRM) + SS(1)
    ROUND(
      ((COALESCE(C.CMD_Journaliere, 0) * COALESCE(D.DIRM_Jours, 0)) + 1 )
    ) AS Seuil_Critique_Arrondi,

    -- Détermine le Statut : si Stock Actuel <= Seuil Critique
    CASE
        WHEN A.Stock_article <= ROUND((COALESCE(C.CMD_Journaliere, 0) * COALESCE(D.DIRM_Jours, 0)) + 1)
        THEN 'COMMANDE URGENTE'
        ELSE 'OK'
    END AS Statut_Alerte
FROM 
    Article A
LEFT JOIN 
    CMD_Calculs C ON A.Tag_article = C.Tag_article
LEFT JOIN 
    DIRM_Moyen D ON A.Tag_article = D.Tag_article
ORDER BY
    A.Tag_article;

CREATE EXTENSION IF NOT EXISTS pgcrypto;