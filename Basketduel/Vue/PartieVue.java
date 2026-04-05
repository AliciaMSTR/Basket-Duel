package Vue;

import javax.swing.*;
import Controlleur.*;

public class PartieVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public PartieVue(ControleurMenu menu, ControleurJeu jeu) {

        setTitle("Basket Duel – Partie");
        setSize(900, 600);
        setMinimumSize(new java.awt.Dimension(600, 400));
        setLocationRelativeTo(null); // Place la fenêtre dynamiquement au centre de l'écran principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TerrainVue sera le canvas où tout se dessine
        TerrainVue terrain = new TerrainVue(jeu);
        add(terrain);
        Timer timer = new Timer(16, null);

        timer.addActionListener(e -> {
            jeu.mettreAJour(0.016);
            
            // Une fois que l'état mathématique des objets a évolué, on force le composant graphique à redessiner la scène
            terrain.repaint();

            // Condition d'arrêt de fin de jeu
            if (jeu.isPartieTerminee()) {
                 timer.stop();
                dispose(); // Libère les ressources système liées à la fenêtre actuelle
                new EcranFinVue(menu, jeu);
            }
        });

        // Activation de la boucle temporelle
        timer.start();

        setVisible(true);
        terrain.requestFocusInWindow();

        terrain.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    timer.stop();
                    
                    Object[] options = {"Reprendre", "Relancer", "Quitter au Menu"};
                    int choix = JOptionPane.showOptionDialog(
                            PartieVue.this,
                            "Jeu en pause. Que souhaitez-vous faire ?",
                            "Pause",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);
                    
                    if (choix == 2) { 
                        // Quitter la partie
                        dispose();
                        menu.allerMenu();
                    } else if (choix == 1) { 
                        // Relancer une nouvelle partie (reset)
                        dispose();
                        menu.relancerPartie();
                    } else { 
                        // Reprendre (ou si l'utilisateur annule/ferme avec la croix)
                        timer.start();
                    }
                }
            }
        });
    }
}
