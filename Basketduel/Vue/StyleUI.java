package Vue;

import javax.swing.*;
import java.awt.*;


public class StyleUI {

    // Palette 
    public static final Color FOND_SOMBRE = new Color(12, 14, 22);
    public static final Color FOND_MOYEN = new Color(20, 22, 35);
    public static final Color ORANGE_VIF = new Color(255, 120, 0);
    public static final Color ORANGE_HOVER = new Color(210, 85, 0);
    public static final Color BLEU_VIF = new Color(30, 144, 255);
    public static final Color BLEU_HOVER = new Color(20, 100, 200);
    public static final Color TEXTE_CLAIR = new Color(210, 215, 235);
    public static final Color TEXTE_GRIS = new Color(120, 128, 155);
    public static final Color VERT_BONUS = new Color(60, 220, 100);
    public static final Color ROUGE_MALUS = new Color(230, 55, 75);
    public static final Color FOND_PANNEAU = new Color(25, 28, 44);

    // Polices
    public static final Font POLICE_TITRE = new Font("Impact", Font.PLAIN, 64);
    public static final Font POLICE_SECTION = new Font("Impact", Font.PLAIN, 36);
    public static final Font POLICE_BOUTON = new Font("Arial", Font.BOLD, 16);
    public static final Font POLICE_LABEL = new Font("Arial", Font.PLAIN, 14);
    public static final Font POLICE_LABEL_B = new Font("Arial", Font.BOLD, 14);
    public static final Font POLICE_SCORE = new Font("Impact", Font.PLAIN, 38);
    public static final Font POLICE_MONO = new Font("Monospaced", Font.BOLD, 28);
    public static final Font POLICE_CODE = new Font("Impact", Font.PLAIN, 34);

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
