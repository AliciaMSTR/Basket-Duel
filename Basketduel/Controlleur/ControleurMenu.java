package Controlleur;

import Modele.Partie;
import Modele.Joueur;
import Modele.JoueurIA;
import Vue.*;

public class ControleurMenu {

    private static final int PORT_DEFAUT = 5000;
    private static final int TOURS_DEFAUT = 10;

    private String pseudoJoueur = "";
    private int pointsVictoire = 10;

    // Mode de jeu possible : LOCAL, IA ou RESEAU
    private String mode = "LOCAL";

    private String ipServeur = "localhost";
    private int port = PORT_DEFAUT;

    private Partie partie;
    private ControleurJeu controleurJeu;
    private ReseauManager reseau;

    //on lance l'application en affichant le menu principal dès que le contrôleur est créé
    public ControleurMenu() {
        new MenuPrincipale(this);
    }

    //on ouvre le menu de navigation secondaire (créer ou rejoindre une partie)
    public void allerMenu() {
        new MenuVue(this);
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

    //on vérifie que le pseudo et le score limite sont valides avant de mémoriser les paramètres de la partie
    public boolean validerCreationLocale(String pseudo, int points, String modeChoisi) {
        if (pseudo == null || pseudo.isBlank()) return false;
        if (points < 1 || points > 200) return false;

        this.pseudoJoueur = pseudo.trim();
        this.pointsVictoire = points;
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

    //on construit les deux joueurs, on crée la Partie et le ControleurJeu, puis on ouvre la fenêtre de jeu
    private void creerPartie() {
        //on place J1 à gauche du terrain à une hauteur correspondant au sol
        Joueur j1 = new Joueur(100, 450);
        j1.setNom(pseudoJoueur);

        Joueur j2;

        //on crée un JoueurIA si le mode est IA, sinon un second joueur humain
        if (mode.equals("IA")) {
            j2 = new JoueurIA("IA", 1.0, 100, 450);
        } else {
            j2 = new Joueur(100, 450);
            j2.setNom("Adversaire");
        }

        this.partie = new Partie(j1, j2);

        //on initialise le contrôleur de jeu avec les dimensions du terrain et les paramètres de la partie
        this.controleurJeu = new ControleurJeu(
                900, 600,
                TOURS_DEFAUT,
                pointsVictoire,
                this.partie,
                this.reseau
        );

        //on affiche la fenêtre de jeu
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
