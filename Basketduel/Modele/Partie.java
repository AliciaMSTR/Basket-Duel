package Modele;

public class Partie {
    private Joueur j1;
    private Joueur j2;

    public Partie(Joueur j1, Joueur j2) {
        this.j1 = j1;
        this.j2 = j2;
        // J1 commence toujours en premier
        this.j1.setActif(true);
        this.j2.setActif(false);
    }

    //on alterne le joueur actif : le joueur qui vient de tirer passe en inactif et l'autre prend la main
    public void switchTour() {
        if (j1.isActif()) {
            j1.setActif(false);
            j2.setActif(true);
        } else {
            j1.setActif(true);
            j2.setActif(false);
        }
    }

    //on remet le ballon a la position du joueur puis on le lance avec l'angle et la puissance qu'il a choisi
    public void executerTir() {
        Joueur actuel = getJoueurActif();
        actuel.getBallon().setPosition(actuel.getX(), actuel.getY());
        actuel.getBallon().tirer(actuel.getAngle(), actuel.getPuissance());
        System.out.println(actuel.getNom() + " effectue son tir !");
    }

    //on remet le ballon a l'arrêt à la position de départ du joueur actif, prêt pour le prochain tour
    public void resetBallonPartie() {
        Joueur actuel = getJoueurActif();
        actuel.getBallon().setPosition(actuel.getX(), actuel.getY());
        actuel.getBallon().setEnMouvement(false);
    }

    public Joueur getJoueurActif() {
        return j1.isActif() ? j1 : j2;
    }

    // Getters nécessaires pour afficher les scores des deux joueurs
    public Joueur getJ1() {
        return j1;
    }

    public Joueur getJ2() {
        return j2;
    }

    //on compare le score du joueur au score maximum : si atteint, la partie s'arrête
    public boolean verifierGagnant(Joueur j, int scoreMax) {
        return j.getScore() >= scoreMax;
    }

    public void setpseudoJoueur(String pseudo) {
        j1.setNom(pseudo);
    }
}
