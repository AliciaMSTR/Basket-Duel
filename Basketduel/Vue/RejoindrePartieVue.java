package Vue;

import javax.swing.*;
import Controlleur.ControleurMenu;

public class RejoindrePartieVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public RejoindrePartieVue(ControleurMenu controleur) {

        setTitle("Rejoindre une Partie Réseau");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField pseudo = new JTextField();
        JTextField ip = new JTextField("localhost");
        JTextField port = new JTextField("5000");

        JButton rejoindre = new JButton("Rejoindre");
        JButton retour = new JButton("Retour");

        rejoindre.addActionListener(e -> {
            try {
                int p = Integer.parseInt(port.getText());

                boolean ok = controleur.validerRejoindrePartie(
                        pseudo.getText(),
                        ip.getText(),
                        p
                );

                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Champs invalides !");
                    return;
                }

                dispose();
                new ChargementReseauVue(controleur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Port invalide !");
            }
        });

        retour.addActionListener(e -> {
            dispose();
            controleur.allerMenu();
        });

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Pseudo :"));
        add(pseudo);

        add(new JLabel("Adresse IP :"));
        add(ip);

        add(new JLabel("Port :"));
        add(port);

        add(rejoindre);
        add(retour);

        setVisible(true);
    }
}
