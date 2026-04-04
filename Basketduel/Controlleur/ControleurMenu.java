package Controlleur;

import Modele.Partie;
import Modele.Joueur;
import Modele.JoueurIA;
import Vue.*;

public class ControleurMenu {

    private static final int PORT_DEFAUT = 5000;
    private static final int TOURS_DEFAUT = 10;

    private String pseudoJoueur = "";
    private String pseudoJoueur2 = "Adversaire"; // Nouveau paramètre pour nommer le J2 (Local)
    private int pointsVictoire = 10;
    private int toursTotalMax = TOURS_DEFAUT; // Nouveau paramètre pour la limite des tours

    // Mode de jeu possible : LOCAL, IA ou RESEAU
    private String mode = "LOCAL";

    private String ipServeur = "localhost";
    private int port = PORT_DEFAUT;

    private Partie partie;
    private ControleurJeu controleurJeu;
    private ReseauManager reseau;

    // Constructeur principal du Menu.
    // L'instanciation de ce contrôleur est la première chose que fait le programme.
    // Il crée immédiatement la fenêtre de base "MenuPrincipale" en se passant lui-même (this) en paramètre
    // pour que la vue puiss l'invoquer lors d'un clic sur un bouton.
    public ControleurMenu() {
        new MenuPrincipale(this);
    }

    // Ouvre (ou retourne sur) le menu de navigation principal (esthétique)
    public void allerMenu() {
        new MenuPrincipale(this);
    }

    //on ouvre la fenêtre de création de partie (choix du pseudo, mode IA ou local)
    public void allerCreerPartie() {
        new CreePartieVue(this);
    }

    //on ouvre la fenêtre pour rejoindre une partie en réseau via une IP et un port
    public void allerRejoindrePartie() {
        new RejoindrePartieVue(this);
    }

    //on ferme proprement la connexion réseau si elle est active, puis on quitte le programme
    public void quitter() {
        if (reseau != null) reseau.fermerTout();
        System.exit(0);
    }

    //on vérifie que le pseudo et les limites (score/tours) sont valides avant de mémoriser les paramètres de la partie
    public boolean validerCreationLocale(String pseudo, String pseudo2, int points, int tours, String modeChoisi) {
        if (pseudo == null || pseudo.isBlank()) return false;
        if (modeChoisi.equals("LOCAL") && (pseudo2 == null || pseudo2.isBlank())) return false;
        if (points < 1 || points > 200) return false;
        if (tours < 1 || tours > 100) return false;

        this.pseudoJoueur = pseudo.trim();
        if (modeChoisi.equals("LOCAL")) {
            this.pseudoJoueur2 = pseudo2.trim();
        } else {
            this.pseudoJoueur2 = "IA";
        }
        
        this.pointsVictoire = points;
        this.toursTotalMax = tours;
        this.mode = modeChoisi;

        return true;
    }

    //on vérifie que l'adresse IP et le port sont valides avant de les enregistrer pour la connexion réseau
    public boolean validerRejoindrePartie(String pseudo, String ip, int port) {
        if (pseudo == null || pseudo.isBlank()) return false;
        if (ip == null || ip.isBlank()) return false;
        if (port < 1 || port > 65535) return false;

        this.pseudoJoueur = pseudo.trim();
        this.ipServeur = ip.trim();
        this.port = port;
        this.mode = "RESEAU";
        return true;
    }

    // Méthode cruciale qui instancie véritablement le monde physique et lance le Timer visuel.
    // Elle génère les entités du Modèle (les "Joueurs" et la "Partie") avant d'ouvrir la fenêtre de Jeu.
    private void creerPartie() {
        // Le joueur 1 (placé à gauche de l'écran, X=100) est à la hauteur Y=450 qui correspond au sol logique.
        Joueur j1 = new Joueur(100, 450);
        j1.setNom(pseudoJoueur);

        Joueur j2;

        //on crée un JoueurIA si le mode est IA, sinon un second joueur humain
        if (mode.equals("IA")) {
            j2 = new JoueurIA(this.pseudoJoueur2, 1.0, 100, 450);
        } else {
            j2 = new Joueur(100, 450);
            j2.setNom(this.pseudoJoueur2);
        }

        this.partie = new Partie(j1, j2);

        // Création du "ControleurJeu", cerveau qui gèrera les collisions et la gravité tout au long de cette partie.
        // On lui injecte une taille logique fixe de 900x600. C'est grâce à ça que la physique reste identique 
        // peu importe si la fenêtre est redimensionnée plus tard (voir TerrainVue).
        this.controleurJeu = new ControleurJeu(
                900, 600,
                toursTotalMax,
                pointsVictoire,
                this.partie,
                this.reseau
        );

        // Une fois l'envers du décor prêt, on allume l'écran de jeu final ("PartieVue").
        new PartieVue(this, controleurJeu);
    }

    //on démarre une partie en local sans connexion réseau
    public void lancerPartieLocale() {
        reseau = null;
        creerPartie();
    }

    //on héberge une partie réseau sur le port configuré, puis on lance le jeu si la connexion réussit
    public boolean lancerServeur() {
        reseau = new ReseauManager();
        boolean ok = reseau.hebergerActif(port);
        if (ok) creerPartie();
        return ok;
    }

    //on tente de rejoindre un serveur existant via l'IP et le port, puis on lance le jeu si ça réussit
    public boolean rejoindreServeur() {
        reseau = new ReseauManager();
        boolean ok = reseau.rejoindrePartie(ipServeur, port);
        if (ok) creerPartie();
        return ok;
    }

    //on réinitialise la partie en cours sans repasser par le menu (relance rapide)
    public void relancerPartie() {
        if (reseau != null) reseau.fermerTout();
        creerPartie();
    }

    public String getPseudoJoueur() { return pseudoJoueur; }
    public int getPointsVictoire() { return pointsVictoire; }
    public String getIpServeur() { return ipServeur; }
    public int getPort() { return port; }
}
