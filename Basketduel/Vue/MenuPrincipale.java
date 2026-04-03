package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Controlleur.ControleurMenu;

public class MenuPrincipale extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton boutonLocal;
    private JButton boutonIA;
    private JButton boutonReseau;
    private JButton boutonQuitter;

    private final ControleurMenu controleur;

    public MenuPrincipale(ControleurMenu controleur) {
        this.controleur = controleur;

        setTitle("Basket Duel - Menu Principal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initialiserComposants();
        ajouterListeners();
        setVisible(true);
    }

    private void initialiserComposants() {

        // Panel principal avec paintComponent personnalisé (dégradé de fond)
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé vertical sombre → bleu nuit
                GradientPaint fond = new GradientPaint(
                        0, 0, new Color(10, 10, 30),
                        0, getHeight(), new Color(30, 60, 110));
                g2.setPaint(fond);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Ligne décorative orange en haut
                g2.setColor(new Color(255, 160, 30));
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(80, 100, getWidth() - 80, 100);
                g2.setStroke(new BasicStroke(1));
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // ── Titre principal
        JLabel titre = new JLabel("BASKET DUEL");
        titre.setFont(new Font("Arial", Font.BOLD, 48));
        titre.setForeground(new Color(255, 200, 50));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Sous-titre
        JLabel sousTitre = new JLabel("Choisissez votre mode de jeu");
        sousTitre.setFont(new Font("Arial", Font.ITALIC, 16));
        sousTitre.setForeground(new Color(180, 180, 200));
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Boutons
        boutonLocal = creerBouton("Jouer en Local", new Color(40, 120, 210));
        boutonIA = creerBouton("Jouer contre l'IA", new Color(60, 150, 80));
        boutonReseau = creerBouton("Jouer en Réseau", new Color(130, 70, 180));
        boutonQuitter = creerBouton("Quitter", new Color(180, 50, 50));

        panel.add(Box.createVerticalStrut(40));
        panel.add(titre);
        panel.add(Box.createVerticalStrut(8));
        panel.add(sousTitre);
        panel.add(Box.createVerticalStrut(60));
        panel.add(boutonLocal);
        panel.add(Box.createVerticalStrut(18));
        panel.add(boutonIA);
        panel.add(Box.createVerticalStrut(18));
        panel.add(boutonReseau);
        panel.add(Box.createVerticalStrut(18));
        panel.add(boutonQuitter);

        // Fond global de la fenêtre
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint fond = new GradientPaint(
                        0, 0, new Color(10, 10, 30),
                        0, getHeight(), new Color(30, 60, 110));
                g2.setPaint(fond);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.add(panel, BorderLayout.CENTER);
        setContentPane(root);
    }

    /**
     * Crée un bouton stylisé avec dégradé et effet hover.
     */
    private JButton creerBouton(String texte, Color couleurBase) {
        JButton btn = new JButton(texte) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c1 = getModel().isRollover()
                        ? couleurBase.brighter()
                        : couleurBase;
                Color c2 = c1.darker();

                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Contour lumineux
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);

                // Texte
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(texte)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                // Ombre
                g2.setColor(new Color(0, 0, 0, 100));
                g2.drawString(texte, tx + 1, ty + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(texte, tx, ty);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(280, 55));
        btn.setMaximumSize(new Dimension(280, 55));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Repaint au survol pour l'effet hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }
        });

        return btn;
    }

    private void ajouterListeners() {
        boutonLocal.addActionListener(e -> {
            dispose();
            controleur.validerCreationLocale("Joueur", 10, "LOCAL");
            controleur.lancerPartieLocale();
        });

        boutonIA.addActionListener(e -> {
            dispose();
            controleur.allerCreerPartie();
        });

        boutonReseau.addActionListener(e -> {
            dispose();
            controleur.allerRejoindrePartie();
        });

        boutonQuitter.addActionListener(e -> controleur.quitter());
    }
}
