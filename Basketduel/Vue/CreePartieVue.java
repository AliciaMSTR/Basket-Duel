package Vue;

import javax.swing.*;
import Controlleur.ControleurMenu;

public class CreePartieVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public CreePartieVue(ControleurMenu controleur) {

        setTitle("Créer une Partie");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField pseudo = new JTextField();
        JTextField points = new JTextField("10");
        JComboBox<String> mode = new JComboBox<>(new String[]{"LOCAL", "IA"});

        JButton lancer = new JButton("Lancer la Partie");
        JButton retour = new JButton("Retour");

        lancer.addActionListener(e -> {
            try {
                int pts = Integer.parseInt(points.getText());

                boolean ok = controleur.validerCreationLocale(
                        pseudo.getText(),
                        pts,
                        (String) mode.getSelectedItem()
                );

                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Champs invalides !");
                    return;
                }

                dispose();
                controleur.lancerPartieLocale();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Points invalides !");
            }
        });

        retour.addActionListener(e -> {
            dispose();
            controleur.allerMenu();
        });

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Pseudo du joueur :"));
        add(pseudo);

        add(new JLabel("Points pour gagner :"));
        add(points);

        add(new JLabel("Mode :"));
        add(mode);

        add(lancer);
        add(retour);

        setVisible(true);
    }
}
