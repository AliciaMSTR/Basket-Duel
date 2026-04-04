package Modele;

public class Joueur {
    private String nom;
    private int score;
    private boolean estActif;
    private final double x;
    private final double y;

    private Ballon ballon;
    private double angle;
    private double puissance;

    public Joueur(double x, double y) {
        this.nom = "joueur";
        this.score = 0;
        this.estActif = false;
        this.x = x;
        this.y = y;
        this.ballon = new Ballon(this.x, this.y);
    }

    public void setActif(boolean actif) {
        this.estActif = actif;
    }

    public void marquerPanier(int points) {
        this.score += points;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getScore() {
        return score;
    }

    public boolean isActif() {
        return estActif;
    }

    public void setTir(double angle, double puissance) {
        this.angle = angle;
        this.puissance = puissance;
    }

    public String getNom() {
        return nom;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getPuissance() {
        return puissance;
    }

    public Ballon getBallon() {
        return ballon;
    }
}
