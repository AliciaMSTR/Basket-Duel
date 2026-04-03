package Controlleur;

import Modele.*;
import java.util.List;

public class ControleurJeu {

    private final Terrain terrain;
    private final Partie partie;
    private final ReseauManager reseau;

    private Panier panier;
    private List<Bonus> bonusList;

    private final int toursTotal;
    private final int pointsVictoire;
    private int tourCourant = 1;

    private boolean partieTerminee = false;

    public enum PhaseVisee { VISEE, PUISSANCE, TIR }
    private PhaseVisee phase = PhaseVisee.VISEE;

    // Angle et puissance oscillants pour la visée du joueur humain
    private double angle = 10;
    private double puissance = 0;
    private int sens = 1;

    private static final double VITESSE_ROT = 90.0;
    private static final double VITESSE_JAUGE = 0.7;
    private static final double P_MIN = 100;
    private static final double P_MAX = 1200;

    public ControleurJeu(int w, int h, int tours, int points, Partie partie, ReseauManager reseau) {
        this.terrain = new Terrain(w, h);
        this.partie = partie;
        this.reseau = reseau;
        this.toursTotal = tours;
        this.pointsVictoire = points;

        nouveauTour();
    }

    //on renvoie toujours le ballon du joueur dont c'est le tour, pas l'autre
    private Ballon getBallonActif() {
        return partie.getJoueurActif().getBallon();
    }

    //on met a jour le jeu a chaque frame : si le ballon est immobile on gere la visee, sinon on fait avancer le ballon
    public void mettreAJour(double dt) {
        if (partieTerminee) return;

        Joueur actif = partie.getJoueurActif();
        Ballon ballonActif = getBallonActif();

        //on verifie si le ballon est en train de voler ou en attente de tir
        if (!ballonActif.isEnMouvement()) {

            //si c'est une IA, elle calcule et tire toute seule sans passer par les phases
            if (actif instanceof JoueurIA) {
                ((JoueurIA) actif).preparerTirAuto(panier);
                partie.executerTir();
                phase = PhaseVisee.TIR;
                return;
            }

            //on fait osciller l'angle pendant la phase de visee
            if (phase == PhaseVisee.VISEE) {
                angle += VITESSE_ROT * sens * dt;
                if (angle >= 85) { angle = 85; sens = -1; }
                if (angle <= 5)  { angle = 5;  sens =  1; }
            }

            //on fait osciller la jauge de puissance pendant la phase de puissance
            if (phase == PhaseVisee.PUISSANCE) {
                puissance += VITESSE_JAUGE * sens * dt;
                if (puissance >= 1) { puissance = 1; sens = -1; }
                if (puissance <= 0) { puissance = 0; sens =  1; }
            }
            return;
        }

        // Le ballon est en vol, on met à jour sa position
        ballonActif.mettreAJour(dt);

        // Détection de collision avec le panier
        if (panier.estTouche(ballonActif.getX(), ballonActif.getY(), Ballon.RAYON)) {
            partie.getJoueurActif().marquerPanier(1);
            finTir();
            return;
        }

        // Détection de collision avec les bonus/malus
        for (Bonus b : bonusList) {
            if (b.isActif() && b.estTouche(ballonActif.getX(), ballonActif.getY(), Ballon.RAYON)) {
                appliquer(b);
                b.collecter();
            }
        }

        // Fin de tir si le ballon sort du terrain
        if (terrain.ballonAtteinSol(ballonActif) || terrain.ballonHorsLimites(ballonActif)) {
            finTir();
        }
    }

    //on reagit a l'appui sur Espace : on verrouille l'angle ou on verrouille la puissance et on tire
    public void appuyer() {
        if (getBallonActif().isEnMouvement() || partieTerminee) return;

        switch (phase) {
            case VISEE -> {
                //on fige l'angle choisi et on passe a la phase de puissance
                phase = PhaseVisee.PUISSANCE;
                puissance = 0;
                sens = 1;
            }
            case PUISSANCE -> {
                //on convertit la jauge (0 a 1) en vraie valeur de puissance et on lance le tir
                double p = P_MIN + puissance * (P_MAX - P_MIN);
                partie.getJoueurActif().setTir(angle, p);
                partie.executerTir();
                phase = PhaseVisee.TIR;
            }
        }
    }

    // Applique tous les effets d'un bonus/malus collecté
    private void appliquer(Bonus b) {
        // Modification du score si c'est un bonus de points
        int modScore = b.getModificateurScore();
        if (modScore != 0) {
            partie.getJoueurActif().marquerPanier(modScore);
        }

        // Modification de la vitesse du panier selon le type
        double facteurPanier = b.getFacteurVitessePanier();
        if (facteurPanier != 1.0) {
            // Le facteur s'applique au prochain déplacement du panier
            // (le panier est régénéré au prochain tour, donc on stocke l'info si besoin)
            System.out.println("Facteur vitesse panier : " + facteurPanier);
        }

        // Si c'est un malus de trajectoire, on perturbe le tir en cours
        double facteurTraj = b.getFacteurTrajectoire();
        if (facteurTraj != 1.0) {
            Ballon bal = getBallonActif();
            bal.setPosition(bal.getX(), bal.getY());
        }

        // Le MUR_OBSTACLE est un malus visuel, géré côté vue
        if (b.genereUnMur()) {
            System.out.println("Un mur obstacle est généré !");
        }
    }

    //on verifie si la partie est terminee, sinon on passe au joueur suivant et on genere un nouveau tour
    private void finTir() {
        if (partie.verifierGagnant(partie.getJ1(), pointsVictoire) ||
            partie.verifierGagnant(partie.getJ2(), pointsVictoire) ||
            tourCourant >= toursTotal) {
            partieTerminee = true;
            return;
        }

        //on reinitialise le ballon, on change de joueur et on incremente le compteur de tours
        partie.resetBallonPartie();
        partie.switchTour();
        tourCourant++;

        //on repart en phase de visee pour le nouveau joueur
        phase = PhaseVisee.VISEE;
        angle = 10;
        puissance = 0;
        sens = 1;

        nouveauTour();
    }

    //on genere un nouveau panier et de nouveaux bonus a chaque debut de tour
    private void nouveauTour() {
        panier = terrain.genererPanier();
        bonusList = terrain.genererBonus(panier, 3);
    }

    public Terrain getTerrain() { return terrain; }
    public Ballon getBallon() { return getBallonActif(); }
    public Panier getPanier() { return panier; }
    public List<Bonus> getBonusList() { return bonusList; }
    public int getTourCourant() { return tourCourant; }
    public int getToursTotal() { return toursTotal; }
    public boolean isPartieTerminee() { return partieTerminee; }
    public PhaseVisee getPhaseVisee() { return phase; }
    public double getAngleCourant() { return angle; }
    public double getJaugePuissance() { return puissance; }
    public Partie getPartie() { return partie; }
}
