package Controlleur;

import Modele.Partie;
import Modele.Joueur;
import Modele.JoueurIA;
import Reseau.ReseauManager;
import Vue.*;

public class ControleurMenu {

    private static final int PORT_DEFAUT = 5000;
    private static final int TOURS_DEFAUT = 10;

    private String pseudoJoueur = "";
    private int pointsVictoire = 10;

    private String mode = "LOCAL"; // LOCAL | IA | RESEAU

    private String ipServeur = "localhost";
    private int port = PORT_DEFAUT;

    private Partie partie;
    private ControleurJeu controleurJeu;
    private ReseauManager reseau;

    public ControleurMenu() {
        new MenuPrincipal(this);
    }


    public void allerMenu() {
        new MenuVue(this);
    }

    public void allerCreerPartie() {
        new CreerPartieVue(this);
    }

    public void allerRejoindrePartie() {
        new RejoindrePartieVue(this);
    }

    public void quitter() {
        if (reseau != null) reseau.fermerTout();
        System.exit(0);
    }

    public boolean validerCreationLocale(String pseudo, int points, String modeChoisi) {
        if (pseudo == null || pseudo.isBlank()) return false;
        if (points < 1 || points > 200) return false;

        this.pseudoJoueur = pseudo.trim();
        this.pointsVictoire = points;
        this.mode = modeChoisi;

        return true;
    }


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

    private void creerPartie() {

        Joueur j1 = new Joueur();
        j1.setNom(pseudoJoueur);

        Joueur j2;

        if (mode.equals("IA")) {
            // IA automatique 
            j2 = new JoueurIA("IA", 1);
        } else {
            j2 = new Joueur();
            j2.setNom("Adversaire");
        }

        this.partie = new Partie(j1, j2);

        this.controleurJeu = new ControleurJeu(
                900, 600,
                TOURS_DEFAUT,
                pointsVictoire,
                this.partie,
                this.reseau
        );

        new PartieVue(this, controleurJeu);
    }

  
    public void lancerPartieLocale() {
        reseau = null;
        creerPartie();
    }

    public boolean lancerServeur() {
        reseau = new ReseauManager();
        boolean ok = reseau.hebergerActif(port);
        if (ok) creerPartie();
        return ok;
    }

    public boolean rejoindreServeur() {
        reseau = new ReseauManager();
        boolean ok = reseau.rejoindrePartie(ipServeur, port);
        if (ok) creerPartie();
        return ok;
    }

    public void relancerPartie() {
        if (reseau != null) reseau.fermerTout();
        creerPartie();
    }

    public String getPseudoJoueur() { 
        return pseudoJoueur; }
    public int getPointsVictoire() { 
        return pointsVictoire; }
    public String getIpServeur() { 
        return ipServeur; }
    public int getPort() { return port; }
}
