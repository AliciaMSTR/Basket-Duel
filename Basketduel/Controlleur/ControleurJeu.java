package Controlleur;

import Modele.*;
import java.util.List;
import java.awt.geom.Rectangle2D;

public class ControleurJeu {

    private final Terrain terrain;
    private final Partie partie;
    private final ReseauManager reseau;

    private Panier panier;
    private List<Bonus> bonusList;
    private Rectangle2D.Double murObstacle;

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

    // Cette méthode est appelée 60 fois par seconde (boucle de jeu).
    // C'est le "Moteur Physique et Logique" principal de toute la partie.
    // Selon l'état du ballon (tenu en main ou en l'air), il permet de viser ou de calculer la trajectoire.
    public void mettreAJour(double dt) {
        if (partieTerminee) return; // Sécurité : on gèle le jeu à la fin.

        Joueur actif = partie.getJoueurActif();
        Ballon ballonActif = getBallonActif();

        // PHASE 1 : Le joueur a la balle en main (visée en cours ou joueur inactif)
        if (!ballonActif.isEnMouvement()) {

            // Intelligence artificielle : l'IA ne passe pas par les phases visuelles "visée/puissance". 
            // Elle calcule ses paramètres de façon instantanée et tire le ballon directement pour ne pas faire attendre le vrai joueur.
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

        // PHASE 2 : Le ballon vole dans l'air (Physique pure)
        // On demande au ballon d'avancer selon la gravité et sa vélocité sur le "Delta Time" de temps.
        ballonActif.mettreAJour(dt);

        // On vérifie immédiatement la "Hitbox" (zone de collision) : le ballon rentre-t-il dans le panier ?
        if (panier.estTouche(ballonActif.getX(), ballonActif.getY(), Ballon.RAYON)) {
            partie.getJoueurActif().marquerPanier(1);
            finTir();
            return;
        }

        // On scanne les bonus présents à l'écran pour voir si le ballon croise l'un d'eux.
        // C'est un test "O(N)" (linéaire), car on parcourt toute la liste active des objets interactifs.
        for (Bonus b : bonusList) {
            if (b.isActif() && b.estTouche(ballonActif.getX(), ballonActif.getY(), Ballon.RAYON)) {
                appliquer(b);
                b.collecter(); // On désactive le bonus visuellement et physiquement.
            }
        }

        // Vérification de collision avec le mur obstacle (s'il existe)
        if (murObstacle != null) {
            // Création d'une hitbox simple pour le ballon (Rectangle) 
            Rectangle2D.Double hitboxBallon = new Rectangle2D.Double(
                ballonActif.getX() - Ballon.RAYON, 
                ballonActif.getY() - Ballon.RAYON, 
                Ballon.RAYON * 2, Ballon.RAYON * 2
            );
            if (murObstacle.intersects(hitboxBallon)) {
                // S'écrase sur le mur = raté !
                finTir();
                return;
            }
        }

        // Vérification de "Out of Bounds" : Si le ballon touche le sol ou quitte complètement l'écran logique (gauche/droite/haut), on compte cela comme un tir raté et fini.
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

        // Agrandissement du panier (Bonus)
        double facteurRayon = b.getFacteurRayonPanier();
        if (facteurRayon != 1.0) {
            panier.setRayon((int) (Panier.RAYON_DEFAUT * facteurRayon));
        }

        // Téléportation du panier (Malus)
        if (b.teleportePanier()) {
            // Le "téléport" ingénieux du panier !
            // On regénère aléatoirement sa position avec le terrain
            panier = terrain.genererPanier();
        }

        // Si c'est un malus de trajectoire, on perturbe le tir en cours
        double facteurTraj = b.getFacteurTrajectoire();
        if (facteurTraj != 1.0) {
            Ballon bal = getBallonActif();
            bal.setPosition(bal.getX(), bal.getY()); // Dans l'idéal il faudrait perturber le vx/vy, mais on peut juste décaler le ballon si besoin
        }

        // Le MUR_OBSTACLE est un malus : on génère un bouclier rectangulaire géant devant le panier.
        if (b.genereUnMur()) {
            murObstacle = new Rectangle2D.Double(panier.getX() - 100, panier.getY() - 150, 30, 200);
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
        murObstacle = null;
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
    public ReseauManager getReseau() { return reseau; }
    public Rectangle2D.Double getMurObstacle() { return murObstacle; }
}
