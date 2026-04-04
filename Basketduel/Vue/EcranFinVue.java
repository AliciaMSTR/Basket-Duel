package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Controlleur.*;

public class EcranFinVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public EcranFinVue(ControleurMenu menu, ControleurJeu jeu) {

        setTitle("Fin de Partie");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        int s1 = jeu.getPartie().getJ1().getScore();
        int s2 = jeu.getPartie().getJ2().getScore();
        String nomJ1 = jeu.getPartie().getJ1().getNom();
        String nomJ2 = jeu.getPartie().getJ2().getNom();

        // Déterminer le gagnant
        String msgGagnant;
        Color couleurGagnant;
        if (s1 > s2) {
            msgGagnant = "🏆 " + nomJ1 + " remporte la partie !";
            couleurGagnant = new Color(255, 210, 50);
        } else if (s2 > s1) {
            msgGagnant = "🏆 " + nomJ2 + " remporte la partie !";
            couleurGagnant = new Color(255, 210, 50);
        } else {
            msgGagnant = "🤝 Égalité parfaite !";
            couleurGagnant = new Color(180, 220, 255);
        }

        // Panel principal avec fond dégradé
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint fond = new GradientPaint(
                        0, 0, new Color(10, 10, 30),
                        0, getHeight(), new Color(30, 60, 110));
                g2.setPaint(fond);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // ── Titre "FIN DE PARTIE"
        JLabel titre = new JLabel("FIN DE PARTIE", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 32));
        titre.setForeground(new Color(255, 200, 50));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Séparateur décoratif
        JPanel ligne = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 160, 30));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(20, getHeight() / 2, getWidth() - 20, getHeight() / 2);
            }
        };
        ligne.setOpaque(false);
        ligne.setMaximumSize(new Dimension(400, 12));

        // ── Gagnant
        JLabel lblGagnant = new JLabel(msgGagnant, SwingConstants.CENTER);
        lblGagnant.setFont(new Font("Arial", Font.BOLD, 20));
        lblGagnant.setForeground(couleurGagnant);
        lblGagnant.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Scores
        String scoreTxt = nomJ1 + " :  " + s1 + " pts       " + nomJ2 + " :  " + s2 + " pts";
        JLabel lblScores = new JLabel(scoreTxt, SwingConstants.CENTER);
        lblScores.setFont(new Font("Arial", Font.PLAIN, 16));
        lblScores.setForeground(new Color(200, 200, 220));
        lblScores.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Boutons
        JButton rejouer = creerBoutonFin("Rejouer", new Color(40, 120, 210));
        JButton menuBtn = creerBoutonFin("Menu Principal", new Color(80, 100, 160));
        JButton quitter = creerBoutonFin("Quitter", new Color(180, 50, 50));

        rejouer.addActionListener(e -> { dispose(); menu.relancerPartie(); });
        menuBtn.addActionListener(e -> { dispose(); menu.allerMenu(); });
        quitter.addActionListener(e ->  menu.quitter());

        panel.add(Box.createVerticalStrut(30));
        panel.add(titre);
        panel.add(Box.createVerticalStrut(6));
        panel.add(ligne);
        panel.add(Box.createVerticalStrut(14));
        panel.add(lblGagnant);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblScores);
        panel.add(Box.createVerticalStrut(30));
        panel.add(rejouer);
        panel.add(Box.createVerticalStrut(10));
        panel.add(menuBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(quitter);

        setContentPane(panel);
        setVisible(true);
    }

    private JButton creerBoutonFin(String texte, Color couleur) {
        JButton btn = new JButton(texte) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c1 = getModel().isRollover() ? couleur.brighter() : couleur;
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c1.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);

                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(texte)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(texte, tx + 1, ty + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(texte, tx, ty);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(240, 44));
        btn.setMaximumSize(new Dimension(240, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });
        return btn;
    }
}
