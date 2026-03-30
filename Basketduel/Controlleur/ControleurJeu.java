package Controlleur;

import Modele.Ballon;
import Modele.Bonus;
import Modele.IntelligenceArtificielle; // Méthodes statiques
import Modele.Panier;
import Modele.Terrain;
import Modele.Partie; // Ajout de la partie
import Modele.Joueur; // Ajout du joueur
import java.util.List;

public class ControleurJeu {
    private static final int nb_bonus_par_tour = 3;
    private static final double vitesse_panier_base = 3.0;
    private static final double puissance_max = 1200.0;
    private static final double puissance_min = 100.0;

    private final Terrain terrain;
    private final Ballon ballon;
    private Panier panier;
    private List<Bonus> bonusList;

    private final Partie partie; // On utilise l'objet Partie centralisé
    private int tourCourant;
    private final int toursTotal;

    private boolean partieTerminee;
    private double facteurTrajectoireProchainTir;
    private double facteurVitessePanier;

    public enum PhaseVisee {
        VISEE, PUISSANCE, TIR
    }

    private PhaseVisee phaseVisee;
    private double angleCourant;
    private static final double VITESSE_ROTATION = 90.0;
    private double jaugePuissance;
    private static final double VITESSE_JAUGE = 0.7;
    private int sensOscillation;
    private final int niveauIA; // On stocke juste le niveau en int

    public ControleurJeu(int largeurFenetre, int hauteurFenetre, int niveauIA, int toursTotal, Partie partie) {
        this.terrain = new Terrain(largeurFenetre, hauteurFenetre);
        
        // On récupère le joueur actif et son ballon depuis la partie
        this.partie = partie;
        this.ballon = partie.getJoueurActif().getBallon();
        this.niveauIA = niveauIA;

        this.tourCourant = 1;
        this.toursTotal = toursTotal;
        this.partieTerminee = false;

        this.facteurTrajectoireProchainTir = 1.0;
        this.facteurVitessePanier = 1.0;

        this.phaseVisee = PhaseVisee.VISEE;
        this.angleCourant = 10.0;
        this.jaugePuissance = 0.0;
        this.sensOscillation = 1;

        demarrerNouveauTour();
    }

    public void mettreAJour(double dt) {
        if (partieTerminee) return;

        if (!ballon.isEnMouvement()) {
            switch (phaseVisee) {
                case VISEE -> {
                    angleCourant += VITESSE_ROTATION * sensOscillation * dt;
                    if (angleCourant >= 85.0) { angleCourant = 85.0; sensOscillation = -1; }
                    if (angleCourant <= 5.0) { angleCourant = 5.0; sensOscillation = 1; }
                }
                case PUISSANCE -> {
                    jaugePuissance += VITESSE_JAUGE * sensOscillation * dt;
                    if (jaugePuissance >= 1.0) { jaugePuissance = 1.0; sensOscillation = -1; }
                    if (jaugePuissance <= 0.0) { jaugePuissance = 0.0; sensOscillation = 1; }
                }
                case TIR -> {}
            }
            return;
        }

        ballon.mettreAJour(dt);

        if (ballon.isEnMouvement()) {
            // Collision panier -> On passe par l'objet Joueur pour le score
            if (panier.estTouche(ballon.getX(), ballon.getY(), Ballon.RAYON)) {
                partie.getJoueurActif().marquerPanier(1); 
                finDeTir();
                return;
            }

            // Collision bonus
            for (Bonus b : bonusList) {
                if (b.isActif() && b.estTouche(ballon.getX(), ballon.getY(), Ballon.RAYON)) {
                    appliquerBonus(b);
                    b.collecter();
                }
            }

            // Ballon hors terrain
            if (terrain.ballonAtteinSol(ballon) || terrain.ballonHorsLimites(ballon)) {
                finDeTir();
                return;
            }

            deplacerPanierIA(dt);
        }
    }

    public void appuyer() {
        if (ballon.isEnMouvement() || partieTerminee) return;

        switch (phaseVisee) {
            case VISEE -> {
                phaseVisee = PhaseVisee.PUISSANCE;
                jaugePuissance = 0.0;
                sensOscillation = 1;
            }
            case PUISSANCE -> {
                double puissanceChoisie = puissance_min + jaugePuissance * (puissance_max - puissance_min);
                double puissanceFinale = puissanceChoisie * facteurTrajectoireProchainTir;
                puissanceFinale = Math.max(puissance_min, Math.min(puissance_max, puissanceFinale));

                // On sauvegarde le tir dans le joueur et on l'exécute via la partie
                partie.getJoueurActif().setTir(angleCourant, puissanceFinale);
                partie.executerTir();
                
                facteurTrajectoireProchainTir = 1.0;
                phaseVisee = PhaseVisee.TIR;
            }
            case TIR -> {}
        }
    }

    private void deplacerPanierIA(double dt) {
        double vitesse = vitesse_panier_base * facteurVitessePanier;

        double bx = ballon.getX();
        double by = ballon.getY();
        double px = panier.getX();
        double py = panier.getY();

        // CORRECTION : Le panier cherche à intercepter le ballon (va vers lui)
        if (bx > px) { panier.deplacerDroite(vitesse); } else { panier.deplacerGauche(vitesse); }
        if (by > py) { panier.deplacerBas(vitesse); } else { panier.deplacerHaut(vitesse); }
    }

    private void appliquerBonus(Bonus b) {
        // Le score est appliqué directement au joueur actif
        partie.getJoueurActif().marquerPanier(b.getModificateurScore());

        facteurTrajectoireProchainTir *= b.getFacteurTrajectoire();
        facteurVitessePanier *= b.getFacteurVitessePanier();
    }

    private void finDeTir() {
        // Utilise la méthode qu'on a créée ensemble dans Partie.java !
        partie.resetBallonPartie(); 
        facteurVitessePanier = 1.0;

        phaseVisee = PhaseVisee.VISEE;
        angleCourant = 10.0;
        jaugePuissance = 0.0;
        sensOscillation = 1;

        if (tourCourant >= toursTotal) {
            partieTerminee = true;
        } else {
            tourCourant++;
            partie.switchTour(); // Alterne les joueurs
            demarrerNouveauTour();
        }
    }

    private void demarrerNouveauTour() {
        this.panier = terrain.genererPanier();
        this.bonusList = terrain.genererBonus(panier, nb_bonus_par_tour);
    }

    // Getters (pour la Vue)

    public Terrain getTerrain() {
        return terrain;
    }

    public Ballon getBallon() {
        return ballon;
    }

    public Panier getPanier() {
        return panier;
    }

    public List<Bonus> getBonusList() {
        return bonusList;
    }

    public int getScoreJoueur() {
        return scoreJoueur;
    }

    public int getScoreIA() {
        return scoreIA;
    }

    public int getTourCourant() {
        return tourCourant;
    }

    public int getToursTotal() {
        return toursTotal;
    }

    public boolean isPartieTerminee() {
        return partieTerminee;
    }

    public double getFacteurTrajectoireProchainTir() {
        return facteurTrajectoireProchainTir;
    }

    // --- Getters système de visée (utilisés par TerrainVue) ---

    public PhaseVisee getPhaseVisee() {
        return phaseVisee;
    }

    public double getAngleCourant() {
        return angleCourant;
    }

    public double getJaugePuissance() {
        return jaugePuissance;
    }
}
