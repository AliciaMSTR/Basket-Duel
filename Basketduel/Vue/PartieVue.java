package Vue;

import javax.swing.*;
import Controlleur.*;

public class PartieVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public PartieVue(ControleurMenu menu, ControleurJeu jeu) {

        // On configure la fenêtre principale : sa taille nominale est fixée à 900x600 
        // (qui sert de référence logique), mais grâce au composant TerrainVue mis à jour, 
        // l'utilisateur peut étirer la fenêtre sans casser l'échelle.
        setTitle("Basket Duel – Partie");
        setSize(900, 600);
        setMinimumSize(new java.awt.Dimension(600, 400));
        setLocationRelativeTo(null); // Place la fenêtre dynamiquement au centre de l'écran principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TerrainVue sera le canvas où tout se dessine
        TerrainVue terrain = new TerrainVue(jeu);
        add(terrain);

        // On crée une boucle de jeu (Game Loop) simple avec un Timer Swing réglé sur 16 millisecondes.
        // On choisit 16 ms car cela donne environ ~60 itérations par seconde (1000 ms / 60 ≈ 16.6 ms),
        // ce qui est le framerate standard (60 FPS) pour des animations fluides sur écran.
        Timer timer = new Timer(16, null);

        timer.addActionListener(e -> {
            // À chaque tick d'environ 16 ms, on informe le modèle physique (jeu) 
            // d'avancer d'un delta-time logique fixe (ici 0.016 seconde)
            jeu.mettreAJour(0.016);
            
            // Une fois que l'état mathématique des objets a évolué, on force le composant graphique à redessiner la scène
            terrain.repaint();

            // Condition d'arrêt de fin de jeu
            if (jeu.isPartieTerminee()) {
                // On DOIT absolument arrêter le timer avant de fermer la fenêtre physique, 
                // sinon une boucle fantôme continuerait de consommer les ressources du processeur inutilement.
                timer.stop();
                dispose(); // Libère les ressources système liées à la fenêtre actuelle
                new EcranFinVue(menu, jeu);
            }
        });

        // Activation de la boucle temporelle
        timer.start();

        setVisible(true);
        // On force le JPanel à obtenir le focus clavier immédiatement, 
        // indispensable pour que les écoutes de touches (comme l'Espace) soient reçues
        terrain.requestFocusInWindow();

        // Ajout du listener pour le Menu Pause (Touche ÉCHAP)
        // L'écouteur est branché sur "terrain" car c'est lui qui possède le focus clavier.
        terrain.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    // On gèle la physique et l'animation purement et simplement
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
