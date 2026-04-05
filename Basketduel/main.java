package main;

import javax.swing.SwingUtilities;
import Controlleur.ControleurMenu;
import Vue.MenuPrincipale;


public class main {
    public static void main (String[] args) {

        SwingUtilities.invokeLater(() -> {
            // Création du contrôleur
            ControleurMenu controleur = new ControleurMenu();

            // Affichage du menu principal
            new MenuPrincipale(controleur);
        });
    }
}
