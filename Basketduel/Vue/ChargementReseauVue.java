package Vue;

import javax.swing.*;
import Controlleur.ControleurMenu;

public class ChargementReseauVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public ChargementReseauVue(ControleurMenu controleur) {

        setTitle("Connexion au serveur…");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Connexion en cours…", SwingConstants.CENTER);
        add(label);

        setVisible(true);

        new Thread(() -> {
            boolean ok = controleur.rejoindreServeur();

            SwingUtilities.invokeLater(() -> {
                dispose();
                if (!ok) {
                    JOptionPane.showMessageDialog(null, "Connexion échouée !");
                    controleur.allerMenu();
                }
            });

        }).start();
    }
}
