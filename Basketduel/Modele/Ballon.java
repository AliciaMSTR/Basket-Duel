package Modele;

/*
 * Modèle mathématique :
 *   vx = v0 * cos(angle)
 *   vy = v0 * sin(angle)
 *   x(t) = x0 + vx * t
 *   y(t) = y0 + vy * t - 0.5 * g * t²
 */
public class Ballon {

    // On choisit une gravité de 980 car cette dernière agit par tick de 16ms (environ 60 FPS). 
    // Si on avait gardé 9.81 (la vraie gravité terrestre en m/s²), on n'aurait pas pu voir la parabole se former,
    // le ballon aurait flotté extrêmement lentement (les distances étant en pixels et non en mètres).
    // 980 pixels/s² donne une redescente réaliste à l'écran.
    public static final double GRAVITE = 980.0;

    // Rayon du ballon, utilisé pour la détection de collision.
    public static final int RAYON = 20;

    // Position X,Y courante du centre du ballon.
    private double x;
    private double y;

    private double xInitial;
    private double yInitial;

    // Composante horizontale de la vitesse: vx = v0 * cos(angle).
    private double vx;

    // Composante verticale de la vitesse: vy = v0 * sin(angle).
    // Négatif pour aller "vers le haut" (convention écran Java).
    private double vy;

    // Temps écoulé depuis le début du tir .
    private double t;

    private boolean enMouvement;

    // Constructeur
    public Ballon(double xSpawn, double ySpawn) {
        this.x = xSpawn;
        this.y = ySpawn;
        this.xInitial = xSpawn;
        this.yInitial = ySpawn;
        this.vx = 0;
        this.vy = 0;
        this.t = 0;
        this.enMouvement = false;
    }

    // Méthode pour initialiser un tir
    // Déclenchement du tir. On utilise la trigonométrie pour décomposer le vecteur de puissance (v0).
    // vx = v0 * cos(angle) nous donne la vitesse horizontale.
    // vy = -v0 * sin(angle) car dans Swing/AWT, l'axe Y est inversé (0 est en haut de l'écran, 600 est en bas).
    // Si on omettait le signe négatif, le ballon serait tiré vers le sol et non vers le ciel !
    public void tirer(double angleDegres, double puissance) {
        double angleRad = Math.toRadians(angleDegres); // Les fonctions Math de Java requièrent des radians
        this.xInitial = this.x;
        this.yInitial = this.y;
        this.vx = puissance * Math.cos(angleRad);
        this.vy = -puissance * Math.sin(angleRad);
        // On remet le chrono (t) à zéro pour commencer la courbe balistique.
        this.t = 0;
        this.enMouvement = true;
    }

    // Méthode pour mettre à jour la position du ballon
    // Cette fonction est appelée très fréquemment (60 fois par seconde) par le Timer principal (PartieVue).
    // Elle déplace le ballon le long de sa courbe parabolique parfaite grâce aux mathématiques.
    public void mettreAJour(double dt) {
        if (!enMouvement)
            return; // Si le ballon est à l'arrêt ou dans les mains, la physique ne l'affecte pas.

        // L'accumulation de "dt" (Delta Time, généralement de 0.016s) construit le temps "t" de vol réel.
        // Cela permet que la trajectoire soit exactement la même, même si l'ordinateur ralentit (Lag-independent).
        t += dt;

        // Équation horaire : la composante X évolue de manière linéaire (la vitesse horizontale ne change pas).
        x = xInitial + vx * t;
        // Équation parabolique : la gravité affecte Y en un carré du temps, créant la redescente.
        y = yInitial + vy * t + 0.5 * GRAVITE * t * t;
    }

    // Méthode pour calculer la distance entre le ballon et une cible
    /**
     * Calcule la distance euclidienne entre le centre du ballon et un point cible
     * (centre du panier ou d'un bonus).
     *
     * d = sqrt( (x2-x1)² + (y2-y1)² )
     *
     * xCible: Coordonnée X du centre de la cible.
     * yCible: Coordonnée Y du centre de la cible.
     * On retourne la distance en pixels entre les deux centres.
     */
    //on calcule la distance entre le centre du ballon et une cible pour savoir si une collision est proche
    public double distanceA(double xCible, double yCible) {
        double dx = xCible - this.x;
        double dy = yCible - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Getters et Setters */
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }



    public boolean isEnMouvement() {
        return enMouvement;
    }

    //Force l'arrêt du mouvement du ballon (ex : collision avec le sol).
    public void setEnMouvement(boolean enMouvement) {
        this.enMouvement = enMouvement;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //Sert a debuger et a tracer le comportement du code
    @Override
    public String toString() {
        return String.format("Ballon[x=%.1f, y=%.1f, vx=%.2f, vy=%.2f, t=%.3f, enMouvement=%b]",
                x, y, vx, vy, t, enMouvement);
    }
}
