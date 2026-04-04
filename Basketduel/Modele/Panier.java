package Modele;

//Détection de collision : utilise la distance euclidienne entre le centre du ballon et le centre du panier.

public class Panier {

    // Rayon par défaut
    public static final int RAYON_DEFAUT = 30;
    
    // Rayon de la hitbox (dynamique)
    private int rayon = RAYON_DEFAUT;

    // Position
    private double x;
    private double y;

    // Constructeur
    public Panier(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Cette méthode utilise le théorème de Pythagore pour calculer la distance entre deux centres (Ballon vs Panier).
    // Si la distance calculée est mathématiquement inférieure ou égale à la somme de leurs deux rayons, on valide qu'il y a eu "touche" ou panier.
    public boolean estTouche(double xBallon, double yBallon, int rayonBallon) {
        double dx = xBallon - this.x;
        double dy = yBallon - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (rayonBallon + rayon);
    }

    // Getters / Setters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }



    @Override
    public String toString() {
        return String.format("Panier[x=%.1f, y=%.1f, rayon=%d]", x, y, rayon);
    }
}
