
-- Table Employe déjà créée, on insère les employés avec mot de passe haché
INSERT INTO Employe (
  Matricule, Nom, Prenom, Date_de_naissance, Email, Mot_de_passe, Adresse,
  Connecte, Role, Actif, Date_creation, Date_modification, Telephone
) VALUES
('EMP001', 'Rakoto', 'Jean', '1988-05-14', 'ainaridia3@gmail.com',
 crypt('pass123', gen_salt('bf')), 'Antananarivo', FALSE, 1, TRUE, NOW(), NOW(), '+261341234567'),
('EMP002', 'aina', 'Ridia', '1993-09-22', 'ainarala3@gmail.com',
 crypt('12345678', gen_salt('bf')), 'Toamasina', FALSE, 3, TRUE, NOW(), NOW(), '+261327890123'),
('EMP003', 'Ando', 'Mickael', '1995-01-03', 'mickael.ando@example.com',
 crypt('mickael95', gen_salt('bf')), 'Antsirabe', FALSE, 3, TRUE, NOW(), NOW(), '+261338765432');


INSERT INTO Type (Tag_type, Nom_type, Description_type) VALUES
('TYP001', 'Électronique', 'Appareils et composants électroniques'),
('TYP002', 'Mobilier', 'Meubles et accessoires de bureau'),
('TYP003', 'Papeterie', 'Articles de bureau et fournitures scolaires');

INSERT INTO Article (Tag_article, Tag_type, Nom_article, Description_article, Stock_article) VALUES
('ART001', 'TYP001', 'Ordinateur Portable', 'HP ProBook 450 G9 - i5 12e Gen', 0),
('ART002', 'TYP002', 'Chaise de Bureau', 'Chaise ergonomique réglable', 0),
('ART003', 'TYP003', 'Bloc-notes A5', 'Bloc-notes 200 pages lignées', 0);

INSERT INTO Fournisseur (Tag_fournisseur, Raison_sociale, Email_fournisseur, Telephone_fournisseur) VALUES
('FOU001', 'Tech Supplies SARL', 'contact@techsupplies.mg', '+261340001122'),
('FOU002', 'Mobilier Bureau SA', 'sales@mobilierbureau.mg', '+261340003344'),
('FOU003', 'Papeterie Express', 'info@papeterieexpress.mg', '+261340005566');

-- Entrées
INSERT INTO Entree (Tag_entree, Tag_article, Expediteur, Date_entree, Nombre_article_entree) VALUES
('ENT001', 'ART002', 'FOU002', NOW(), 20),
('ENT002', 'ART003', 'FOU003', NOW(), 50);

-- Sorties
INSERT INTO Sortie (Tag_sortie, Tag_article, Expediteur, Destinataire, Date_sortie, Nombre_article_sortie) VALUES
('SOR001', 'ART002', 'EMP002', 'EMP003', NOW(), 5);


-- Début de la période de test
-- Jour 1 : 10 Nov 2025
INSERT INTO Entree (Tag_entree, Tag_article, Expediteur, Date_entree, Nombre_article_entree) VALUES
('ENT003', 'ART001', 'FOU001', '2025-11-10 09:00:00', 10);
INSERT INTO Sortie (Tag_sortie, Tag_article, Expediteur, Destinataire, Date_sortie, Nombre_article_sortie) VALUES
('SOR002', 'ART001', 'EMP001', 'EMP002', '2025-11-10 10:00:00', 1);

-- Jour 3 : 12 Nov 2025
INSERT INTO Sortie (Tag_sortie, Tag_article, Expediteur, Destinataire, Date_sortie, Nombre_article_sortie) VALUES
('SOR003', 'ART001', 'EMP001', 'EMP003', '2025-11-12 11:00:00', 1);

-- Jour 7 : 17 Nov 2025 (Date de la deuxième Entrée)
INSERT INTO Entree (Tag_entree, Tag_article, Expediteur, Date_entree, Nombre_article_entree) VALUES
('ENT004', 'ART001', 'FOU001', '2025-11-17 09:00:00', 15);
INSERT INTO Sortie (Tag_sortie, Tag_article, Expediteur, Destinataire, Date_sortie, Nombre_article_sortie) VALUES
('SOR004', 'ART001', 'EMP002', 'EMP003', '2025-11-17 12:00:00', 2);

-- Jour 10 : 20 Nov 2025 (Fin de la période)
INSERT INTO Sortie (Tag_sortie, Tag_article, Expediteur, Destinataire, Date_sortie, Nombre_article_sortie) VALUES
('SOR005', 'ART001', 'EMP002', 'EMP001', '2025-11-20 09:00:00', 1);

-- Mise à jour des stocks
UPDATE Article a
SET Stock_article = COALESCE((
    SELECT SUM(e.Nombre_article_entree) 
    FROM Entree e 
    WHERE e.Tag_article = a.Tag_article
), 0)
- COALESCE((
    SELECT SUM(s.Nombre_article_sortie) 
    FROM Sortie s
    WHERE s.Tag_article = a.Tag_article
), 0);
