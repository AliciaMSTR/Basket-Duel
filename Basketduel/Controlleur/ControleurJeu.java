package Controlleur;

import Modele.*;
import java.util.List;

public class ControleurJeu {

    private final Terrain terrain;
    private final Partie partie;
    private final Ballon ballon;
    private final ReseauManager reseau;

    private Panier panier;
    private List<Bonus> bonusList;

    private final int toursTotal;
    private final int pointsVictoire;
    private int tourCourant = 1;

    private boolean partieTerminee = false;

    public enum PhaseVisee { VISEE, PUISSANCE, TIR }
    private PhaseVisee phase = PhaseVisee.VISEE;

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
        this.ballon = partie.getJoueurActif().getBallon();
        this.reseau = reseau;
        this.toursTotal = tours;
        this.pointsVictoire = points;

        nouveauTour();
    }


    public void mettreAJour(double dt) {
        if (partieTerminee) return;

        if (!ballon.isEnMouvement()) {
            if (phase == PhaseVisee.VISEE) {
                angle += VITESSE_ROT * sens * dt;
                if (angle >= 85) { angle = 85; sens = -1; }
                if (angle <= 5) { angle = 5; sens = 1; }
            }
            if (phase == PhaseVisee.PUISSANCE) {
                puissance += VITESSE_JAUGE * sens * dt;
                if (puissance >= 1) { puissance = 1; sens = -1; }
                if (puissance <= 0) { puissance = 0; sens = 1; }
            }
            return;
        }

        ballon.mettreAJour(dt);

        if (panier.estTouche(ballon.getX(), ballon.getY(), Ballon.RAYON)) {
            partie.getJoueurActif().marquerPanier(1);
            finTir();
            return;
        }

        for (Bonus b : bonusList) {
            if (b.isActif() && b.estTouche(ballon.getX(), ballon.getY(), Ballon.RAYON)) {
                appliquer(b);
                b.collecter();
            }
        }

        if (terrain.ballonAtteinSol(ballon) || terrain.ballonHorsLimites(ballon)) {
            finTir();
        }
    }

    public void appuyer() {
        if (ballon.isEnMouvement() || partieTerminee) return;

        switch (phase) {
            case VISEE -> {
                phase = PhaseVisee.PUISSANCE;
                puissance = 0;
            }
            case PUISSANCE -> {
                double p = P_MIN + puissance * (P_MAX - P_MIN);
                partie.getJoueurActif().setTir(angle, p);
                partie.executerTir();
                phase = PhaseVisee.TIR;
            }
        }
    }

    private void appliquer(Bonus b) {
        partie.getJoueurActif().marquerPanier(b.getModificateurScore());
    }

    private void finTir() {

        if (partie.getJoueurActif().getScore() >= pointsVictoire ||
            tourCourant >= toursTotal) {
            partieTerminee = true;
            return;
        }

        partie.resetBallonPartie();
        partie.switchTour();
        tourCourant++;
        phase = PhaseVisee.VISEE;
        angle = 10;
        puissance = 0;

        nouveauTour();
    }

    private void nouveauTour() {
        panier = terrain.genererPanier();
        bonusList = terrain.genererBonus(panier, 3);
    }
    public Terrain getTerrain() { 
        return terrain; }
    public Ballon getBallon() { 
        return ballon; }
    public Panier getPanier() { 
        return panier; }
    public List<Bonus> getBonusList() {0
        return bonusList; }
    public int getTourCourant() {
        return tourCourant; }
    public int getToursTotal() { 
        return toursTotal; }
    public boolean isPartieTerminee() { 
        return partieTerminee; }
    public PhaseVisee getPhaseVisee() { 
        return phase; }
    public double getAngleCourant() { 
        return angle; }
    public double getJaugePuissance() { 
        return puissance; }
    public Partie getPartie() { return partie; }
}
