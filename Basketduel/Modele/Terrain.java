package Modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Terrain {

    // Dimensions du terrain
    private final int largeur;
    private final int hauteur;

    // Limites physiques
    private final int ySol;
    private final int yPlafond;
    private final int xMurGauche;
    private final int xMurDroit;

    // Le point de spawn (apparition) du ballon est fixe pour garantir qu'à chaque tour, 
    // le joueur lance depuis le même endroit, permettant l'apprentissage de la visée et la mémoire musculaire au fil des essais.
    private final int xSpawnBallon;
    private final int ySpawnBallon;

    // Zone réservée pour la génération procédurale du panier.
    // En le contraignant mathématiquement dans la moitié droite de l'écran (xMinPanier), 
    // on s'assure que la cible n'est jamais générée derrière le tireur ni hors d'atteinte physique.
    private final int xMinPanier;

    // Marge de sécurité entre entités
    private static final int MARGE_SECURITE = 60;

    // Générateur aléatoire
    private final Random random;

    // Constructeur
    public Terrain(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.yPlafond = 0;
        this.ySol = hauteur - 10; // 10px de marge en bas
        this.xMurGauche = 0;
        this.xMurDroit = largeur;
        this.xSpawnBallon = largeur / 8; // approximativement 12% depuis la gauche
        this.ySpawnBallon = hauteur / 2; // mi-hauteur
        this.xMinPanier = largeur / 2; // moitié droite uniquement
        this.random = new Random();
    }

    // Cette méthode génère un panier de manière semi-aléatoire.
    // L'aléatoire seul est risqué car il pourrait placer le panier contre un mur, le rendant impénétrable.
    // On calcule donc des marges (le rayon du panier + espace vital) auxquelles on contraint (Math.random)
    // les coordonnées X et Y pour que le tir reste toujours net.
    public Panier genererPanier() {
        int marge = Panier.RAYON_DEFAUT + 10;
        int xMin = xMinPanier + marge;
        int xMax = xMurDroit - marge;
        int yMin = yPlafond + marge + 40;
        int yMax = ySol - marge - 60; // On garde 60 pixels de marge pour ne pas créer un panier au ras du sol

        double x = xMin + random.nextInt(xMax - xMin);
        double y = yMin + random.nextInt(yMax - yMin);

        // On fournit les coordonnées générées.
        return new Panier(x, y);
    }

    //on place les bonus dans l'espace entre le joueur et le panier en evitant les chevauchements
    public List<Bonus> genererBonus(Panier panier, int nombreMax) {
        List<Bonus> liste = new ArrayList<>();

        int xMin = xSpawnBallon + 50;
        int xMax = (int) panier.getX() - panier.getRayon() - MARGE_SECURITE;
        int yMin = yPlafond + Bonus.RAYON + 20;
        int yMax = ySol - Bonus.RAYON - 20;

        if (xMax <= xMin || yMax <= yMin)
            return liste;

        int tentativesMax = nombreMax * 10;
        int tentatives = 0;

        //on essaie de placer chaque bonus jusqu'a trouver une position valide ou atteindre le max de tentatives
        while (liste.size() < nombreMax && tentatives < tentativesMax) {
            tentatives++;

            double bx = xMin + random.nextInt(xMax - xMin);
            double by = yMin + random.nextInt(yMax - yMin);

            //on verifie que le bonus n'est pas trop pres du panier
            double dx = bx - panier.getX();
            double dy = by - panier.getY();
            if (Math.sqrt(dx * dx + dy * dy) < MARGE_SECURITE)
                continue;

            //on verifie que le bonus n'empiete pas sur un autre bonus deja place
            boolean chevauchement = false;
            for (Bonus b : liste) {
                double ddx = bx - b.getX();
                double ddy = by - b.getY();
                if (Math.sqrt(ddx * ddx + ddy * ddy) < MARGE_SECURITE) {
                    chevauchement = true;
                    break;
                }
            }
            if (chevauchement)
                continue;

            //on choisit aleatoirement si c'est un bonus ou un malus (50/50)
            Bonus.TypeBonus type = (random.nextInt(4) < 2)
                    ? Bonus.TypeBonus.values()[random.nextInt(Bonus.TypeBonus.nbrBonus())]
                    : Bonus.TypeBonus.values()[Bonus.TypeBonus.nbrBonus() + random.nextInt(Bonus.TypeBonus.nbrMalus())];

            liste.add(new Bonus(bx, by, type));
        }

        return liste;
    }

    //on verifie si la partie basse du ballon a touche le sol, ce qui signifie que le tir est rate
    public boolean ballonAtteinSol(Ballon ballon) {
        return (ballon.getY() + Ballon.RAYON) >= ySol;
    }

    //on verifie si le ballon est sorti du terrain par la gauche ou la droite
    public boolean ballonHorsLimites(Ballon ballon) {
        return ballon.getX() - Ballon.RAYON < xMurGauche
                || ballon.getX() + Ballon.RAYON > xMurDroit;
    }

    // Getters
    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public int getYSol() {
        return ySol;
    }



    @Override
    public String toString() {
        return String.format("Terrain[%dx%d | sol=%d | spawn=(%d,%d)]",
                largeur, hauteur, ySol, xSpawnBallon, ySpawnBallon);
    }
}
