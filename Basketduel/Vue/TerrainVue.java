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
    // La méthode paintComponent est le cœur du composant graphique. 
    // Swing appelle cette méthode de lui-même (via repaint()) à chaque fois qu'un rafraîchissement visuel est nécessaire.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Cast en Graphics2D pour de meilleures options graphiques (rotation, scaling, antialiasing)
        Graphics2D g2 = (Graphics2D) g.create();

        // Antialiasing : permet de lisser les courbes (très important pour que le ballon et le panneau soient propres, sans effet 'pixel' en escalier)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // --- GESTION DU REDIMENSIONNEMENT (SCALING) ---
        // Notre physique de jeu (modèle) fonctionne sur un repère abstrait fixe de 900x600 pixels.
        // Si l'utilisateur passe en plein écran, on ne modifie PAS les coordonnées de la physique
        // (qui modifierait le temps de trajet du ballon), mais on applique un "zoom" sur la peinture de la vue.
        double echelleX = (double) getWidth() / 900.0;
        double echelleY = (double) getHeight() / 600.0;
        
        // On conserve le ratio minimum pour ne pas déformer ou étirer l'image, 
        // ce qui va créer des bandes noires compensatoires si le format de la fenêtre n'est pas 3:2.
        double echelle = Math.min(echelleX, echelleY);
        
        // Calcul du décalage (translation) pour centrer le terrain sur les axes X ou Y
        double decX = (getWidth() - (900 * echelle)) / 2.0;
        double decY = (getHeight() - (600 * echelle)) / 2.0;
        
        // On applique les transformations (d'abord déplacer, puis zoomer). Le code de rendu en dessous
        // pourra continuer à dessiner sur la base d'une fenêtre 900x600 pure, Java fera la conversion au pixel près.
        g2.translate(decX, decY);
        g2.scale(echelle, echelle);
        // ----------------------------------------------

        // "anim" est une variable de temps qui tourne en boucle de façon continue en tâche de fond. 
        // On l'ajoute légèrement à chaque tick pour faire osciller les bonus plus bas.
        anim += 0.05;

        // Le dessin se fait par "couches superposées" (Painter's Algorithm).
        // Le fond doit absolument être peint en premier, puis on superpose l'IHM et les objets par ordre d'importance visuelle.
        dessinerFond(g2);
        dessinerPanier(g2);
        dessinerBonus(g2);
        dessinerMurObstacle(g2);
        dessinerJoueur(g2);
        dessinerBallon(g2);

        // La flèche est une "aide visuelle", on la masque dès que le ballon quitte les mains du joueur pour épurer l'écran.
        if (!controleur.getBallon().isEnMouvement()) {
            dessinerFleche(g2);
        }

        // Le HUD recrache toutes les informations utiles au-dessus des sprites physiques
        dessinerHUD(g2);

        g2.dispose(); // Bonne pratique : désalloue le contexte cloné (g.create()) de la mémoire
    }
    //Chargement image du terrain et du ballon
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
    // On dessine l'arrière-plan du jeu. Soit une image complète, soit un dégradé de couleur en cas d'erreur de chargement.
    private void dessinerFond(Graphics2D g2) {
        if (imgTerrain != null) {
            // L'image de fond s'étire sur les dimensions logiques pleines du terrain (900x600).
            // Le "Scaling" mis en place dans paintComponent() se chargera de l'agrandir à l'écran.
            g2.drawImage(imgTerrain, 0, 0, 900, 600, null);
        } else {
            // Fallback : On crée un dégradé (GradientPaint) pour le ciel, simulant la profondeur atmosphérique 
            // avec un bleu foncé en haut et un peu plus clair vers l'horizon.
            GradientPaint ciel = new GradientPaint(
                    0, 0, new Color(20, 60, 120),
                    0, 600 * 0.6f, new Color(40, 100, 160));
            g2.setPaint(ciel);
            g2.fillRect(0, 0, 900, 600);

            // On utilise les dimensions de la physique (t.getLargeur()) pour dessiner un "sol".
            Terrain t = controleur.getTerrain();
            GradientPaint sol = new GradientPaint(
                    0, t.getYSol(), new Color(120, 80, 30),
                    0, 600, new Color(80, 50, 15));
            g2.setPaint(sol);
            g2.fillRect(0, t.getYSol(), t.getLargeur(), t.getHauteur());
        }
    }
    // On dessine ici l'avatar du joueur (le "corps" ou lanceur de la balle).
    // On lui applique un dégradé dynamique du haut vers le bas pour donner un léger effet de volume 3D
    // et on le contourne en blanc pour le faire ressortir visuellement.
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
    //attribution de l'image du ballon
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
    //dessiner le panier
    private void dessinerPanier(Graphics2D g2) {
        Panier p = controleur.getPanier();
        int r = p.getRayon();

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval((int) p.getX() - r, (int) p.getY() - r, r * 2, r * 2);
        g2.setStroke(new BasicStroke(1));
    }
    //dessiner le bonus et le malus
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

    // Dessin du mur obstacle
    private void dessinerMurObstacle(Graphics2D g2) {
        java.awt.geom.Rectangle2D.Double mur = controleur.getMurObstacle();
        if (mur != null) {
            // Effet brique transparent
            g2.setColor(new Color(180, 50, 30, 200));
            g2.fill(mur);
            
            // Contour strié rouge vif
            g2.setColor(new Color(255, 80, 50));
            g2.setStroke(new BasicStroke(3));
            g2.draw(mur);
            
            g2.setStroke(new BasicStroke(1));
        }
    }
    //dessiner la fleche de visée
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
    // Fonction chapeau qui orchestre le dessin de toute l'interface graphique (Heads Up Display).
    // On affiche des éléments fixes par-dessus l'animation : scores, compteur de tours et instructions de la visée.
    private void dessinerHUD(Graphics2D g2) {
        Partie partie = controleur.getPartie();
        int s1 = partie.getJ1().getScore();
        int s2 = partie.getJ2().getScore();

        // On crée un panneau informatif en haut à gauche pour le premier joueur.
        dessinerPanneauScore(g2, 10, 8,
                partie.getJ1().getNom(), s1,
                new Color(40, 120, 220, 200));

        // Panneau pour le Joueur 2. 
        // L'utilisation d'une couleur d'arrière-plan semi-transparente (le "200" dans new Color) 
        // permet de continuer à voir le décor du jeu en filigrane sous le texte.
        String lblJ2 = partie.getJ2().getNom() + " : " + s2;
        dessinerPanneauScore(g2, 200, 8,
                partie.getJ2().getNom(), s2,
                new Color(200, 60, 60, 200));

        // Le compteur de tours est placé dynamiquement au milieu exact de l'écran logique (900/2 pixels).
        String lblTour = "Tour " + controleur.getTourCourant() + " / " + controleur.getToursTotal();
        dessinerPanneauCentre(g2, lblTour, 900 / 2, 10, new Color(0, 0, 0, 160));

        // Le texte du bas aide le joueur à comprendre dans quelle "phase" de lancer il est.
        // Utilisation d'un 'switch' simplifié (introduit dans Java 14) pour raccourcir le code.
        String msg = switch (controleur.getPhaseVisee()) {
            case VISEE     -> MSG_VISEE;
            case PUISSANCE -> MSG_PUISSANCE;
            default        -> MSG_TIR;
        };
        dessinerBandeauBas(g2, msg);

        //FIN DE PARTIE
        if (controleur.isPartieTerminee()) {
            dessinerEcranFin(g2);
        }
    }

    //Panneau score avec fond arrondi semi-transparent.
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

    //Petit bandeau centré (ex: compteur de tours).
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

    // Bandeau translucide s'affichant en bas de l'écran pour dicter les instructions (Verrouiller Angle/Verrouiller Puissance).
    private void dessinerBandeauBas(Graphics2D g2, String msg) {
        int mh = 36;
        // On aligne ce bandeau parfaitement contre la bordure inférieure logique (600 pixels) du jeu.
        int my = 600 - mh;

        // Le fond noir avec une opacité de 140/255 donne un bel effet 'overlay'.
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, my, 900, mh);

        // Cette fine ligne jaune sert de distinction claire entre l'environnement en 3D et l'interface plate (2D).
        g2.setColor(new Color(255, 200, 50, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, my, 900, my);

        // Affichage du message centré
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fm = g2.getFontMetrics();
        int tx = 900 / 2 - fm.stringWidth(msg) / 2;
        dessinerTexteOmbre(g2, msg, tx, my + 23, new Color(255, 220, 60), new Color(0, 0, 0, 160));

        g2.setStroke(new BasicStroke(1));
    }

    // Affiche un écran sombre bloquant tout en pleine fin de partie, 
    // l'assombrissement force l'œil de l'utilisateur à se focaliser au milieu sur le statut de fin.
    private void dessinerEcranFin(Graphics2D g2) {
        // Overlay couvrant mathématiquement tout le terrain
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, 900, 600);

        // Titre massif et centré
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String fin = MSG_FIN;
        FontMetrics fm = g2.getFontMetrics();
        int fx = 900 / 2 - fm.stringWidth(fin) / 2;
        dessinerTexteOmbre(g2, fin, fx, 600 / 2 - 20,
                new Color(255, 210, 50), new Color(0, 0, 0, 200));

        // Le sous-titre donne la marche à suivre. C'est une bonne pratique UI d'expliquer comment sortir de l'état bloquant.
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String sous = "Appuyez sur une touche...";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.setColor(new Color(200, 200, 200, 200));
        g2.drawString(sous, 900 / 2 - fm2.stringWidth(sous) / 2, 600 / 2 + 30);
    }

    //dessiner un texte avec une ombre portée décalée d'1px en bas à droite.
    private void dessinerTexteOmbre(Graphics2D g2, String texte, int x, int y,
                                    Color couleur, Color ombre) {
        g2.setColor(ombre);
        g2.drawString(texte, x + 1, y + 1);
        g2.setColor(couleur);
        g2.drawString(texte, x, y);
    }
}
