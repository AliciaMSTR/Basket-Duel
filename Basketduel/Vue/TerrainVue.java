package Vue;

import Controlleur.ControleurJeu;
import Controlleur.ControleurJeu.PhaseVisee;
import Modele.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TerrainVue extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ControleurJeu controleur;
    private final Image background;

    private static final String MSG_VISEE = "ESPACE : verrouiller l'angle";
    private static final String MSG_PUISSANCE = "ESPACE : verrouiller la puissance";
    private static final String MSG_TIR = "Ballon en vol...";
    private static final String MSG_FIN = "FIN DE PARTIE";
    private static final int OFFSET_FIN_X = 150;

    // Bonus flottants
    private final Map<Bonus, Double> bonusPhase = new HashMap<>();
    private double anim = 0;

    public TerrainVue(ControleurJeu controleur) {
        this.controleur = controleur;

        setPreferredSize(new Dimension(
                controleur.getTerrain().getLargeur(),
                controleur.getTerrain().getHauteur()
        ));
        setFocusable(true);

       
        background = new ImageIcon("src/assets/gradins.png").getImage();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    controleur.appuyer();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        anim += 0.05;

        dessinerFond(g2);
        dessinerSol(g2);
        dessinerJoueur(g2);
        dessinerPanier(g2);
        dessinerBonus(g2);
        dessinerBallon(g2);

        if (!controleur.getBallon().isEnMouvement()) {
            dessinerFleche(g2);
        }

        dessinerHUD(g2);
    }



    private void dessinerFond(Graphics2D g2) {
        g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }

    private void dessinerSol(Graphics2D g2) {
        Terrain t = controleur.getTerrain();
        g2.setColor(new Color(90, 60, 30));
        g2.fillRect(0, t.getYSol(), t.getLargeur(), t.getHauteur());
    }

    private void dessinerJoueur(Graphics2D g2) {
        Partie p = controleur.getPartie();
        Joueur j = p.getJoueurActif();

        int x = (int) j.getX();
        int y = (int) j.getY();

        g2.setColor(new Color(40, 140, 220));
        g2.fillOval(x - 18, y - 36, 36, 36);

        g2.setColor(Color.WHITE);
        g2.drawString(j.getNom(), x - 20, y - 45);
    }

    private void dessinerBallon(Graphics2D g2) {
        Ballon b = controleur.getBallon();
        int r = Ballon.RAYON;

        g2.setColor(Color.ORANGE);
        g2.fillOval((int) b.getX() - r, (int) b.getY() - r, r * 2, r * 2);
    }

    private void dessinerPanier(Graphics2D g2) {
        Panier p = controleur.getPanier();
        int r = Panier.RAYON;

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval((int) p.getX() - r, (int) p.getY() - r, r * 2, r * 2);
    }

    private void dessinerBonus(Graphics2D g2) {
        for (Bonus b : controleur.getBonusList()) {
            if (!b.isActif()) continue;

            bonusPhase.putIfAbsent(b, Math.random() * 6.28);
            double osc = Math.sin(anim + bonusPhase.get(b)) * 6;

            int x = (int) b.getX();
            int y = (int) (b.getY() + osc);

            Color couleur = b.getType().estMalus()
                    ? new Color(230, 60, 60)
                    : new Color(60, 200, 220);

            g2.setColor(couleur);
            g2.fillOval(x - Bonus.RAYON, y - Bonus.RAYON,
                    Bonus.RAYON * 2,
                    Bonus.RAYON * 2);
        }
    }

    private void dessinerFleche(Graphics2D g2) {
        Ballon b = controleur.getBallon();
        double rad = Math.toRadians(controleur.getAngleCourant());

        int ox = (int) b.getX();
        int oy = (int) b.getY();
        int tx = ox + (int) (80 * Math.cos(rad));
        int ty = oy - (int) (80 * Math.sin(rad));

        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(ox, oy, tx, ty);
    }

    private void dessinerHUD(Graphics2D g2) {

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        Partie partie = controleur.getPartie();

        int s1 = partie.getJoueurActif().getScore();
        int s2 = partie.getJoueurActif().getScore();

        g2.drawString("J1 : " + s1, 10, 20);
        g2.drawString("J2 : " + s2, 100, 20);
        g2.drawString("Tour : " + controleur.getTourCourant() +
                "/" + controleur.getToursTotal(), 200, 20);

        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        String msg = switch (controleur.getPhaseVisee()) {
            case MSG_VISEE -> MSG_VISEE;
            case MSG_PUISSANCE -> MSG_PUISSANCE;
            default -> MSG_TIR;
        };

        g2.drawString(msg,
                getWidth() / 2 - 150,
                getHeight() - 20);

        if (controleur.isPartieTerminee()) {
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.drawString(MSG_FIN,
                    getWidth() / 2 - OFFSET_FIN_X,
                    getHeight() / 2);
        }
    }
}
