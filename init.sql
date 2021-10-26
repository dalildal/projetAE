
/* GROUP 8 : Projet AE */
/* PROJET BINV2090 */

--CREATE SCHEMA
DROP SCHEMA IF EXISTS antiquapp CASCADE;
CREATE SCHEMA antiquapp;

--CREATE TABLE
CREATE TABLE antiquapp.utilisateurs
(
	id_utilisateur SERIAL PRIMARY KEY,
	pseudo VARCHAR(15) NOT NULL,
	mot_de_passe VARCHAR(200) NOT NULL,
	nom VARCHAR(50) NOT NULL,
	prenom VARCHAR(50) NOT NULL,
	rue VARCHAR(50) NOT NULL,
	numero VARCHAR(50) NOT NULL,
	boite VARCHAR(15) NULL,
	code_postal INTEGER NOT NULL,
	commune VARCHAR(50) NOT NULL,
	pays VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	date_inscription TIMESTAMP NOT NULL,
	type VARCHAR(10) DEFAULT 'client',
	etat INTEGER DEFAULT 0
);

CREATE TABLE antiquapp.types
(
	id_type SERIAL PRIMARY KEY,
	libelle VARCHAR(100) NOT NULL
);

CREATE TABLE antiquapp.meubles
(
	id_meuble SERIAL PRIMARY KEY,
	id_type INTEGER REFERENCES antiquapp.types (id_type) NOT NULL,
	description VARCHAR(200) NULL,
	prix_achat DOUBLE PRECISION NULL,
	prix_vente DOUBLE PRECISION NULL,
	prix_antiquaire DOUBLE PRECISION NULL,
	date_recuperation TIMESTAMP NULL,
	date_depot TIMESTAMP NULL,
	date_retrait TIMESTAMP NULL,
	date_livraison TIMESTAMP NULL,
	photo_prefere INTEGER NULL,
	etat VARCHAR(50)
);

CREATE TABLE antiquapp.photos
(
	id_photo SERIAL PRIMARY KEY,
	id_meuble INTEGER REFERENCES antiquapp.meubles(id_meuble) NOT NULL,
	lien VARCHAR NOT NULL,
	visibilite INTEGER NOT NULL
);

CREATE TABLE antiquapp.visites
(
	id_visite SERIAL PRIMARY KEY,
	id_utilisateur INTEGER REFERENCES antiquapp.utilisateurs(id_utilisateur) NOT NULL,
	date_creation TIMESTAMP NOT NULL,
	date_visite TIMESTAMP NULL,
	plage_horaire VARCHAR(50) NOT NULL,
	rue VARCHAR(50) NOT NULL,
	numero VARCHAR(50) NOT NULL,
	boite VARCHAR(15) NULL,
	code_postal INTEGER NOT NULL,
	commune VARCHAR(50) NOT NULL,
	pays VARCHAR(50) NOT NULL,
	etat VARCHAR(50) NOT NULL,
	raison_annulation VARCHAR(250) NULL
);

CREATE TABLE antiquapp.meubles_visites
(
	id_visite INTEGER REFERENCES antiquapp.visites(id_visite) NOT NULL,
	id_meuble INTEGER REFERENCES antiquapp.meubles(id_meuble) NOT NULL,
	PRIMARY KEY(id_visite,id_meuble)
);

CREATE TABLE antiquapp.options
(
	id_utilisateur INTEGER REFERENCES antiquapp.utilisateurs(id_utilisateur) NOT NULL,
	id_meuble INTEGER REFERENCES antiquapp.meubles(id_meuble) NOT NULL,
	duree INTEGER NOT NULL,
	date_option TIMESTAMP NOT NULL,
	PRIMARY KEY(id_utilisateur,id_meuble)
);

CREATE TABLE antiquapp.ventes
(
	id_vente SERIAL PRIMARY KEY,
	id_utilisateur INTEGER REFERENCES antiquapp.utilisateurs(id_utilisateur) NULL,
	id_meuble INTEGER REFERENCES antiquapp.meubles(id_meuble) NOT NULL,
	date_vente TIMESTAMP NOT NULL
);

-- CREATE TABLE antiquapp.adresses ?????????

--INSERT

--USERS

INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'bert', '$2a$10$KcRBqweIRdzQj9bHvxlf4uy3r0DxfUtrbC2DwTsZKQmFhqCR5eUYu'
										   , 'Satcho', 'Albert', 'sente des artistes', '1bis', NULL, 4800, 'Verviers', 'Belgique', 'bert.satcho@gmail.be', '2021-03-22', 'admin', 1);
INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'lau', '$2a$10$n53xc08FtN1GO9XJijwNfeXJemRWexQk/S9j5lghsgIhsVGgalkEW'
										   , 'Satcho', 'Laurent', 'sente des artistes', '18', NULL, 4800, 'Verviers', 'Belgique', 'laurent.satcho@gmail.be', '2021-03-22', 'admin', 1);
INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'Caro', '$2a$10$jSo5tL6uOd4843jG8JaJNuYjLRl3nDA99r1MFu2AlUrfaak3xTp9a'
										   , 'Line', 'Caroline', 'Rue de l’Eglise', '11', 'B1', 4987, 'Stoumont', 'Belgique', 'caro.line@hotmail.com', '2021-03-23', 'antiquaire', 1);
INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'achil', '$2a$10$jSo5tL6uOd4843jG8JaJNuYjLRl3nDA99r1MFu2AlUrfaak3xTp9a'
										   , 'Ile', 'Achille', 'Rue de Renkin', '7', NULL, 4800, 'Verviers', 'Belgique', 'ach.ile@gmail.com', '2021-03-23', 'client', 1);
INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'bazz', '$2a$10$jSo5tL6uOd4843jG8JaJNuYjLRl3nDA99r1MFu2AlUrfaak3xTp9a'
										   , 'Ile', 'Basile', 'Lammerskreuzstrasse', '6', NULL, 52159, 'Roetgen', 'Allemagne', 'bas.ile@gmail.be', '2021-03-23', 'client', 1);
										   
INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'Theo', '$2a$10$3Z8smITym.fT6kMTE6jAv.hHKAFT1H5R6KNF2wjoDxe.DAUxmeApO', 'Ile', 'Théophile', 'Rue de Renkin', '7', NULL, 4800, 'Verviers', 'Belgique', 'theo.phile@proximus.be', '2021-03-30', 'antiquaire', 1);

INSERT INTO antiquapp.utilisateurs
VALUES
	(DEFAULT, 'charline', '$2a$10$YW.ln3L641sSapqIhBomSu2dPO.a3ltVO6OwYmtcLQTpZiS.PnyM2', 'Line', 'Charles', 'Rue des Minières', '45', 'Ter', 4800, 'Verviers', 'Belgique', 'charline@proximus.be', '2021-04-22', 'client', 1);


--TYPES

INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'armoire');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'bahut');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'bibliotheque');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'bonnetiere');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'buffet');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'bureau');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'chaise');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'chiffonnier');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'coffre');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'coiffeuse');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'commode');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'confident');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'console');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'dresse');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'fauteuil');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'gueridon');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'lingere');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'lit');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'penderie');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'secretaire');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'table');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'tabouret');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'vaisselier');
INSERT INTO antiquapp.types
VALUES
	(DEFAULT, 'valet muet');


--VIVISTES
						   
INSERT INTO antiquapp.visites
VALUES
	(DEFAULT, 4, '2021-03-24', '2021-03-29 20:00:00', 'lundi de 18h à 22h', 'Rue de Renkin', '7', NULL, 4800, 'Verviers', 'Belgique', 'confirmee', NULL);
	
INSERT INTO antiquapp.visites
VALUES
	(DEFAULT, 4, '2021-03-25', NULL, 'lundi de 18h à 22h', 'Rue de Renkin', '7', NULL, 4800, 'Verviers', 'Belgique', 'annulee', 'meuble trop récent');
	
INSERT INTO antiquapp.visites
VALUES
	(DEFAULT, 5, '2021-03-25', '2021-03-29 15:00:00', 'tous les jours de 15h à 18h', 'Lammerskreuzstrasse', '6', NULL, 52159, 'Roetgen', 'Allemagne', 'confirmee', NULL);

INSERT INTO antiquapp.visites
VALUES
	(DEFAULT, 6, '2021-04-21', NULL, 'tous les matins de 9h à 13h', 'Rue Victor Bouillenne', '9', '4C', 4800, 'Verviers', 'Belgique', 'attente', NULL);
	
INSERT INTO antiquapp.visites
VALUES
	(DEFAULT, 7, '2021-04-22', '2021-04-26 18:00:00', 'tous les jours de 16h a 19h', 'Rue des Minières', '45', 'Ter', 4800, 'Verviers', 'Belgique', 'confirmee', NULL);
	
--FURNITURES

INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 2, 'Bahut profond d’une largeur de 112 cm et d’une hauteur de 147 cm.', 200, 0, 0, '2021-03-30', null, null, null, null, 'achete');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 6, 'Large bureau 1m87 cm, deux colonnes de tiroirs', 159, 299, 0, '2021-03-30', null, null, null, null, 'achete');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 21, 'Table jardin en bois brut', 0, 0, 0, '2021-02-25', null, null, null, null, 'annule');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 21, 'Table en chêne, pieds en fer forgé', 140, 459, 0, '2021-03-29', null, null, null, null, 'inadequat');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 20, 'Secrétaire en acajou, marqueterie', 90, 0, 0, '2021-03-29', '2021-03-29', null, null, null, 'disponnible');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 18, 'Lit à baldaquin en acajou', 0, 0, 0, null, null, null, null, null, 'demande');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 6, 'Bureau en bois ciré', 220, 0, 0, '2021-04-27', null, null, null, null, 'a restaurer');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 6, 'Bureau en chêne massif, sous-main intégré', 325, 378, 0, '2021-04-27', '2021-04-27', null, null, null, 'a vendre');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 6, 'Magnifique bureau en acajou', 180, 239, 0, '2021-04-27', '2021-04-27', null, null, null, 'a vendre');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 10, 'Splendide coiffeuse aux reliefs travaillés', 150, 199, 0, '2021-04-27', '2021-04-27', null, null, null, 'a vendre');
INSERT INTO antiquapp.meubles
VALUES
	(DEFAULT, 10, 'Coiffeuse marqueterie', 145, 199, 0, '2021-04-27', '2021-04-27', null, null, null, 'a vendre');

--FURNITURES_VISITES

INSERT INTO antiquapp.meubles_visites
VALUES (1,1);

INSERT INTO antiquapp.meubles_visites
VALUES (1,2);

INSERT INTO antiquapp.meubles_visites
VALUES (2,3);

INSERT INTO antiquapp.meubles_visites
VALUES (3,4);

INSERT INTO antiquapp.meubles_visites
VALUES (3,5);

INSERT INTO antiquapp.meubles_visites
VALUES (4,6);

INSERT INTO antiquapp.meubles_visites
VALUES (5,7);

INSERT INTO antiquapp.meubles_visites
VALUES (5,8);

INSERT INTO antiquapp.meubles_visites
VALUES (5,9);

INSERT INTO antiquapp.meubles_visites
VALUES (5,10);

INSERT INTO antiquapp.meubles_visites
VALUES (5,11);
