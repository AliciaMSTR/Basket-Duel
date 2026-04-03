package Vue;

import javax.swing.*;
import Controlleur.*;

public class PartieVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public PartieVue(ControleurMenu menu, ControleurJeu jeu) {

        setTitle("Basket Duel – Partie");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TerrainVue terrain = new TerrainVue(jeu);
        add(terrain);

        Timer timer = new Timer(16, e -> {
            jeu.mettreAJour(0.016);
            terrain.repaint();

            if (jeu.isPartieTerminee()) {
                dispose();
                new EcranFinVue(menu, jeu);
            }
        });

        timer.start();

        setVisible(true);
        terrain.requestFocusInWindow();
    }
}
