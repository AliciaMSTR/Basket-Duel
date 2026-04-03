package Vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;


public class StyleUI {

    // Palette 
    public static final Color FOND_SOMBRE   = new Color(12, 14, 22);
    public static final Color FOND_MOYEN    = new Color(20, 22, 35);
    public static final Color ORANGE_VIF    = new Color(255, 120, 0);
    public static final Color ORANGE_HOVER  = new Color(210, 85, 0);
    public static final Color BLEU_VIF      = new Color(30, 144, 255);
    public static final Color BLEU_HOVER    = new Color(20, 100, 200);
    public static final Color TEXTE_CLAIR   = new Color(210, 215, 235);
    public static final Color TEXTE_GRIS    = new Color(120, 128, 155);
    public static final Color VERT_BONUS    = new Color(60, 220, 100);
    public static final Color ROUGE_MALUS   = new Color(230, 55, 75);
    public static final Color FOND_PANNEAU  = new Color(25, 28, 44);

    // Polices
    public static final Font POLICE_TITRE    = new Font("Impact", Font.PLAIN, 64);
    public static final Font POLICE_SECTION  = new Font("Impact", Font.PLAIN, 36);
    public static final Font POLICE_BOUTON   = new Font("Arial",  Font.BOLD,  16);
    public static final Font POLICE_LABEL    = new Font("Arial",  Font.PLAIN, 14);
    public static final Font POLICE_LABEL_B  = new Font("Arial",  Font.BOLD,  14);
    public static final Font POLICE_SCORE    = new Font("Impact", Font.PLAIN, 38);
    public static final Font POLICE_MONO     = new Font("Monospaced", Font.BOLD, 28);
    public static final Font POLICE_CODE     = new Font("Impact", Font.PLAIN, 34);

    // Bouton principal 

    
    public static JButton boutonPrincipal(String texte, Color bg, Color hover) {
        JButton btn = new JButton(texte) {
            private static final long serialVersionUID = 1L;
			private boolean survol = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { survol = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { survol = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Halo au survol
                if (survol) {
                    Color c = hover;
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 45));
                    g2.fillRoundRect(-5, -5, getWidth() + 10, getHeight() + 10, 18, 18);
                }
                g2.setColor(survol ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(POLICE_BOUTON);
        btn.setPreferredSize(new Dimension(300, 52));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Bouton discret (secondaire / retour).
    public static JButton boutonSecondaire(String texte) {
        JButton btn = new JButton(texte) {
            private static final long serialVersionUID = 1L;
			private boolean survol = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { survol = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { survol = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(survol ? new Color(55, 58, 80) : new Color(38, 40, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(90, 95, 130));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(TEXTE_CLAIR);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Champ texte stylisé avec placeholder grisé. */
    public static JTextField champTexte(String placeholder) {
        JTextField tf = new JTextField(placeholder) {
            private static final long serialVersionUID = 1L;
			{
                setForeground(TEXTE_GRIS);
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (getText().equals(placeholder)) { setText(""); setForeground(TEXTE_CLAIR); }
                    }
                    public void focusLost(FocusEvent e) {
                        if (getText().trim().isEmpty()) { setText(placeholder); setForeground(TEXTE_GRIS); }
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOND_PANNEAU);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(70, 75, 105));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        tf.setFont(POLICE_LABEL);
        tf.setCaretColor(Color.WHITE);
        tf.setBackground(FOND_PANNEAU);
        tf.setBorder(new EmptyBorder(4, 10, 4, 10));
        tf.setOpaque(false);
        tf.setPreferredSize(new Dimension(220, 38));
        return tf;
    }

    // Label standard avec couleur personnalisée. 
    public static JLabel label(String texte, Font font, Color couleur) {
        JLabel l = new JLabel(texte);
        l.setFont(font);
        l.setForeground(couleur);
        return l;
    }


    public static void dessinerFond(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Dégradé de fond
        GradientPaint bg = new GradientPaint(0, 0, FOND_SOMBRE, 0, h, new Color(18, 8, 3));
        g2.setPaint(bg);
        g2.fillRect(0, 0, w, h);
        // Grille subtile
        g2.setColor(new Color(255, 120, 0, 10));
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x < w; x += 60) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 60) g2.drawLine(0, y, w, y);
    }
    
    public static void dessinerBallon(Graphics2D g2, int cx, int cy, int r, float alpha) {
        Composite ancienComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Corps (dégradé radial)
        java.awt.RadialGradientPaint rg = new java.awt.RadialGradientPaint(
            new java.awt.geom.Point2D.Float(cx - r * 0.3f, cy - r * 0.3f),
            r * 1.3f,
            new float[]{0f, 1f},
            new Color[]{new Color(255, 165, 40), new Color(185, 58, 0)}
        );
        g2.setPaint(rg);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Lignes du ballon
        g2.setColor(new Color(0, 0, 0, 170));
        g2.setStroke(new BasicStroke(r / 10f + 1f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        g2.drawLine(cx - r, cy, cx + r, cy);
        g2.drawLine(cx, cy - r, cx, cy + r);
        g2.drawArc(cx - r / 2, cy - r, r, r * 2, 0, 180);
        g2.drawArc(cx - r / 2, cy - r, r, r * 2, 180, 180);

        g2.setComposite(ancienComposite);
    }
}
