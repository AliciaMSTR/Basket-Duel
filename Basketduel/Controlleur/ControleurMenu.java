package Controlleur;
import Modele.Partie;
import Modele.Joueur;
import Modele.JoueurIA; // Import direct plus propre
import Modele.Partie;
import Vue.FenetrePrincipale;

import Modele.Joueur;
import Modele.Partie;
import Modele.JoueurIA;
import Vue.FenetrePrincipale;

public class ControleurMenu {

    private static final int PORT_DEFAUT   = 5000;
    private static final int TOURS_DEFAUT  = 10; 
    private final FenetrePrincipale fenetre;

    private String  pseudoJ1       = "Joueur 1";
    private String  pseudoJ2       = "Joueur 2";
    private int     pointsVictoire = 5;
    private String  modeAdversaire = "LOCAL"; 
    private int     niveauIA       = 1;
    private String  ipServeur      = "localhost";
    private int     portReseau     = PORT_DEFAUT;

    private Partie        partie;
    private ControleurJeu controleurJeu;
    private ReseauManager reseauManager;

    public ControleurMenu(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;
    }

    // --- Navigation ---
    public void allerMenu() { fenetre.afficherEcran(FenetrePrincipale.ECRAN_MENU); }
    public void allerCreerPartie() { fenetre.afficherEcran(FenetrePrincipale.ECRAN_CREER); }
    public void allerRejoindrePartie() { fenetre.afficherEcran(FenetrePrincipale.ECRAN_REJOINDRE); }

    public void quitter() {
        if (reseauManager != null) reseauManager.fermerConnexion(); // Nom de méthode corrigé selon ton ReseauManager
        System.exit(0);
    }

    public void validerParametres(String pseudo1, String pseudo2, int pointsVict, String mode, int niveauIA) {
        this.pseudoJ1       = (pseudo1 == null || pseudo1.isBlank()) ? "Joueur 1" : pseudo1.trim();
        this.pseudoJ2       = (pseudo2 == null || pseudo2.isBlank()) ? "Joueur 2" : pseudo2.trim();
        this.pointsVictoire = Math.max(1, pointsVict);
        this.modeAdversaire = mode;
        this.niveauIA       = Math.max(1, Math.min(3, niveauIA));
    }

    // --- Lancement des modes ---
    public void lancerPartieLocale() {
        creerPartieEtControleur();
        fenetre.setVisible(true); // Ou ta méthode spécifique pour changer de vue
    }

    private void creerPartieEtControleur() {
        // Utilisation du constructeur de Joueur(String) corrigé
        Joueur j1 = new Joueur(pseudoJ1);
        Joueur j2;

        if ("IA".equals(modeAdversaire)) {
            // Utilisation du constructeur de JoueurIA(String, double)
            // On convertit le niveau (1,2,3) en facteur d'erreur (0.1, 0.2, etc.)
            j2 = new JoueurIA(pseudoJ2, (double)niveauIA / 10.0);
        } else {
            j2 = new Joueur(pseudoJ2);
        }

        this.partie = new Partie(j1, j2);
        
        // Initialisation du contrôleur de jeu (assure-toi que le constructeur existe dans ControleurJeu)
        // Pour l'instant, on l'appelle simplement s'il est vide :
        this.controleurJeu = new ControleurJeu(largeur, hauteur, niveauIA, TOURS_DEFAUT, this.partie);

        // Liaison avec la vue
        // fenetre.setPartie(this.partie); // Décommente si ces méthodes existent dans FenetrePrincipale
    }

    // Getters / Setters restants...
}
