package Vue;

import Controlleur.ControleurJeu;
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
    private Image imgTerrain;
    private Image imgBallon;

    private static final String MSG_VISEE = "ESPACE : verrouiller l'angle";
    private static final String MSG_PUISSANCE = "ESPACE : verrouiller la puissance";
    private static final String MSG_TIR = "Ballon en vol...";
    private static final String MSG_FIN = "FIN DE PARTIE";

    // Animation sinusoïdale des bonus
    private final Map<Bonus, Double> bonusPhase = new HashMap<>();
    private double anim = 0;

    public TerrainVue(ControleurJeu controleur) {
        this.controleur = controleur;

        setPreferredSize(new Dimension(
                controleur.getTerrain().getLargeur(),
                controleur.getTerrain().getHauteur()));
        setFocusable(true);

        imgTerrain = chargerImage("assets/terrain.png");
        imgBallon  = chargerImage("assets/ballon.png");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    controleur.appuyer();
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  RENDU PRINCIPAL
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Antialiasing pour des formes lisses
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        anim += 0.05;

        dessinerFond(g2);
        dessinerPanier(g2);
        dessinerBonus(g2);
        dessinerJoueur(g2);
        dessinerBallon(g2);

        if (!controleur.getBallon().isEnMouvement()) {
            dessinerFleche(g2);
        }

        dessinerHUD(g2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CHARGEMENT IMAGE
    // ─────────────────────────────────────────────────────────────────────────

    private Image chargerImage(String chemin) {
        String[] tentatives = {
            chemin,
            "Basketduel/" + chemin,
            "Basket-Duel-main/Basketduel/" + chemin
        };
        for (String c : tentatives) {
            java.io.File f = new java.io.File(c);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath()).getImage();
            }
        }
        System.err.println("Image introuvable : " + chemin);
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  FOND / TERRAIN
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerFond(Graphics2D g2) {
        if (imgTerrain != null) {
            g2.drawImage(imgTerrain, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Fallback dégradé si l'image est absente
            GradientPaint ciel = new GradientPaint(
                    0, 0, new Color(20, 60, 120),
                    0, getHeight() * 0.6f, new Color(40, 100, 160));
            g2.setPaint(ciel);
            g2.fillRect(0, 0, getWidth(), getHeight());

            Terrain t = controleur.getTerrain();
            GradientPaint sol = new GradientPaint(
                    0, t.getYSol(), new Color(120, 80, 30),
                    0, getHeight(), new Color(80, 50, 15));
            g2.setPaint(sol);
            g2.fillRect(0, t.getYSol(), t.getLargeur(), t.getHauteur());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  JOUEUR
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerJoueur(Graphics2D g2) {
        Partie p = controleur.getPartie();
        Joueur j = p.getJoueurActif();

        int x = (int) j.getX();
        int y = (int) j.getY();

        // Corps (dégradé bleu)
        GradientPaint corps = new GradientPaint(
                x - 18, y - 36, new Color(60, 160, 255),
                x + 18, y,      new Color(20, 80, 160));
        g2.setPaint(corps);
        g2.fillOval(x - 18, y - 36, 36, 36);

        // Contour blanc
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x - 18, y - 36, 36, 36);

        // Nom du joueur avec ombre portée
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        dessinerTexteOmbre(g2, j.getNom(), x - 20, y - 46, Color.WHITE, new Color(0, 0, 0, 150));

        g2.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BALLON
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerBallon(Graphics2D g2) {
        Ballon b = controleur.getBallon();
        int r = Ballon.RAYON;
        int diam = r * 2;
        int bx = (int) b.getX() - r;
        int by = (int) b.getY() - r;

        if (imgBallon != null) {
            // Légère ombre sous le ballon
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(bx + 4, by + 6, diam, diam);
            g2.drawImage(imgBallon, bx, by, diam, diam, null);
        } else {
            // Fallback : ballon dessiné avec dégradé
            GradientPaint gp = new GradientPaint(
                    bx, by, new Color(255, 160, 30),
                    bx + diam, by + diam, new Color(200, 80, 0));
            g2.setPaint(gp);
            g2.fillOval(bx, by, diam, diam);
            g2.setColor(new Color(140, 40, 0));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(bx, by, diam, diam);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PANIER (anneau + backboard)
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerPanier(Graphics2D g2) {
        Panier p = controleur.getPanier();
        int r = Panier.RAYON;

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval((int) p.getX() - r, (int) p.getY() - r, r * 2, r * 2);
        g2.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BONUS / MALUS
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerBonus(Graphics2D g2) {
        for (Bonus b : controleur.getBonusList()) {
            if (!b.isActif()) continue;

            bonusPhase.putIfAbsent(b, Math.random() * 6.28);
            double osc = Math.sin(anim + bonusPhase.get(b)) * 5;

            int x  = (int) b.getX();
            int y  = (int) (b.getY() + osc);
            int br = Bonus.RAYON;

            boolean malus = b.getType().estMalus();

            // Halo lumineux
            Color haloColor = malus
                    ? new Color(220, 60, 60, 60)
                    : new Color(60, 200, 220, 60);
            g2.setColor(haloColor);
            g2.fillOval(x - br - 5, y - br - 5, (br + 5) * 2, (br + 5) * 2);

            // Corps du bonus avec dégradé
            Color c1 = malus ? new Color(240, 80, 80)  : new Color(80, 220, 240);
            Color c2 = malus ? new Color(160, 20, 20)  : new Color(20, 120, 160);
            GradientPaint gp = new GradientPaint(
                    x - br, y - br, c1,
                    x + br, y + br, c2);
            g2.setPaint(gp);
            g2.fillOval(x - br, y - br, br * 2, br * 2);

            // Contour blanc
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(x - br, y - br, br * 2, br * 2);

            // Symbole  +  ou  −
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String sym = malus ? "−" : "+";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(sym, x - fm.stringWidth(sym) / 2, y + 5);

            g2.setStroke(new BasicStroke(1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  FLÈCHE DE VISÉE
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerFleche(Graphics2D g2) {
        Ballon b = controleur.getBallon();
        double rad = Math.toRadians(controleur.getAngleCourant());
        double jauge = controleur.getJaugePuissance();

        int ox = (int) b.getX();
        int oy = (int) b.getY();
        int longueur = 100;
        int tx = ox + (int) (longueur * Math.cos(rad));
        int ty = oy - (int) (longueur * Math.sin(rad));

        // Couleur principale selon la puissance
        Color couleur;
        if (controleur.getPhaseVisee() == ControleurJeu.PhaseVisee.VISEE) {
            couleur = Color.WHITE;
        } else {
            float hue = (float) ((1.0 - jauge) * 0.67);
            couleur = Color.getHSBColor(hue, 1.0f, 1.0f);
        }

        // Trait principal
        g2.setColor(couleur);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(ox, oy, tx, ty);

        // Pointe de la flèche
        int taillePointe = 14;
        double aL = rad + Math.toRadians(150);
        double aR = rad - Math.toRadians(150);
        int[] px = { tx, tx + (int)(taillePointe * Math.cos(aL)), tx + (int)(taillePointe * Math.cos(aR)) };
        int[] py = { ty, ty - (int)(taillePointe * Math.sin(aL)), ty - (int)(taillePointe * Math.sin(aR)) };
        g2.fillPolygon(px, py, 3);

        g2.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HUD (scores, tours, instructions)
    // ─────────────────────────────────────────────────────────────────────────

    private void dessinerHUD(Graphics2D g2) {
        Partie partie = controleur.getPartie();
        int s1 = partie.getJ1().getScore();
        int s2 = partie.getJ2().getScore();

        // ── Panneau score J1 (gauche)
        dessinerPanneauScore(g2, 10, 8,
                partie.getJ1().getNom(), s1,
                new Color(40, 120, 220, 200));

        // ── Panneau score J2 (droite)
        String lblJ2 = partie.getJ2().getNom() + " : " + s2;
        dessinerPanneauScore(g2, 200, 8,
                partie.getJ2().getNom(), s2,
                new Color(200, 60, 60, 200));

        // ── Compteur de tours (centré en haut)
        String lblTour = "Tour " + controleur.getTourCourant() + " / " + controleur.getToursTotal();
        dessinerPanneauCentre(g2, lblTour, getWidth() / 2, 10, new Color(0, 0, 0, 160));

        // ── Message de phase (bas de l'écran)
        String msg = switch (controleur.getPhaseVisee()) {
            case VISEE     -> MSG_VISEE;
            case PUISSANCE -> MSG_PUISSANCE;
            default        -> MSG_TIR;
        };
        dessinerBandeauBas(g2, msg);

        // ── FIN DE PARTIE
        if (controleur.isPartieTerminee()) {
            dessinerEcranFin(g2);
        }
    }

    /** Panneau score avec fond arrondi semi-transparent. */
    private void dessinerPanneauScore(Graphics2D g2, int x, int y,
                                      String nom, int score, Color fond) {
        String texte = nom + "  " + score + " pts";
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(texte) + 20;
        int h = 28;

        // Fond arrondi
        g2.setColor(fond);
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        // Texte avec ombre
        dessinerTexteOmbre(g2, texte, x + 10, y + h - 7, Color.WHITE, new Color(0, 0, 0, 120));
        g2.setStroke(new BasicStroke(1));
    }

    /** Petit bandeau centré (ex: compteur de tours). */
    private void dessinerPanneauCentre(Graphics2D g2, String texte, int cx, int y, Color fond) {
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(texte) + 24;
        int h = 24;
        int x = cx - w / 2;

        g2.setColor(fond);
        g2.fillRoundRect(x, y, w, h, 10, 10);
        dessinerTexteOmbre(g2, texte, x + 12, y + h - 5, Color.WHITE, new Color(0, 0, 0, 100));
    }

    /** Bandeau translucide en bas avec le message de phase. */
    private void dessinerBandeauBas(Graphics2D g2, String msg) {
        int mh = 36;
        int my = getHeight() - mh;

        // Fond
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, my, getWidth(), mh);

        // Ligne de séparation haute
        g2.setColor(new Color(255, 200, 50, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, my, getWidth(), my);

        // Texte centré
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fm = g2.getFontMetrics();
        int tx = getWidth() / 2 - fm.stringWidth(msg) / 2;
        dessinerTexteOmbre(g2, msg, tx, my + 23, new Color(255, 220, 60), new Color(0, 0, 0, 160));

        g2.setStroke(new BasicStroke(1));
    }

    /** Message plein écran de fin de partie avec overlay sombre. */
    private void dessinerEcranFin(Graphics2D g2) {
        // Overlay semi-transparent
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Titre
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String fin = MSG_FIN;
        FontMetrics fm = g2.getFontMetrics();
        int fx = getWidth() / 2 - fm.stringWidth(fin) / 2;
        dessinerTexteOmbre(g2, fin, fx, getHeight() / 2 - 20,
                new Color(255, 210, 50), new Color(0, 0, 0, 200));

        // Sous-titre
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String sous = "Appuyez sur une touche...";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.setColor(new Color(200, 200, 200, 200));
        g2.drawString(sous, getWidth() / 2 - fm2.stringWidth(sous) / 2, getHeight() / 2 + 30);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UTILITAIRE : texte avec ombre portée
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Dessine un texte avec une ombre portée décalée d'1px en bas à droite.
     * Technique simple et très lisible sur n'importe quel fond.
     */
    private void dessinerTexteOmbre(Graphics2D g2, String texte, int x, int y,
                                    Color couleur, Color ombre) {
        g2.setColor(ombre);
        g2.drawString(texte, x + 1, y + 1);
        g2.setColor(couleur);
        g2.drawString(texte, x, y);
    }
}
