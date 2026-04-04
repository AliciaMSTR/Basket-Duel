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

        // Champs de saisie
        JTextField pseudo = new JTextField();
        JTextField pseudo2 = new JTextField("Joueur 2");
        JTextField points = new JTextField("10");
        JTextField tours = new JTextField("10"); // Nouveau champ pour le nombre de tours
        
        JComboBox<String> mode = new JComboBox<>(new String[]{"LOCAL", "IA", "RESEAU"});

        // Gérer le cas du Joueur 2 inactif si IA ou Réseau est choisi
        mode.addActionListener(e -> {
            String selection = (String) mode.getSelectedItem();
            if ("IA".equals(selection) || "RESEAU".equals(selection)) {
                pseudo2.setEnabled(false);
                pseudo2.setText(selection.equals("IA") ? "IA" : "Client Reseau");
            } else {
                pseudo2.setEnabled(true);
                if (pseudo2.getText().equals("IA") || pseudo2.getText().equals("Client Reseau")) {
                    pseudo2.setText("Joueur 2");
                }
            }
        });

        JButton lancer = new JButton("Lancer la Partie");
        JButton retour = new JButton("Retour");

        lancer.addActionListener(e -> {
            try {
                int pts = Integer.parseInt(points.getText());
                int trs = Integer.parseInt(tours.getText());

                boolean ok = controleur.validerCreationLocale(
                        pseudo.getText(),
                        pseudo2.getText(),
                        pts,
                        trs,
                        (String) mode.getSelectedItem()
                );

                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Champs invalides ! Vérifiez qu'aucun nom n'est vide et que les bornes (points/tours) sont respectées.");
                    return;
                }

                // Démarrage spécifique selon que ce soit réseau ou local/IA
                String md = (String) mode.getSelectedItem();
                if ("RESEAU".equals(md)) {
                    // On ne dispose pas de la fenêtre tant qu'on ne sait pas si le port est libre
                    boolean hostOk = controleur.lancerServeur();
                    if (!hostOk) {
                        JOptionPane.showMessageDialog(this, "Le port " + controleur.getPort() + " n'est pas disponible pour héberger !");
                        return;
                    }
                } else {
                    controleur.lancerPartieLocale();
                }

                dispose(); // Fermer la création uniquement si c'est réussi

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides pour les Points et les Tours !");
            }
        });

        retour.addActionListener(e -> {
            dispose();
            controleur.allerMenu();
        });

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // On crée de petits panneaux (FlowLayout) pour chaque ligne pour faire plus propre.
        JPanel panP1 = new JPanel(); panP1.add(new JLabel("Pseudo J1 :")); panP1.add(pseudo); pseudo.setColumns(10);
        JPanel panP2 = new JPanel(); panP2.add(new JLabel("Pseudo J2 :")); panP2.add(pseudo2); pseudo2.setColumns(10);
        JPanel panPts = new JPanel(); panPts.add(new JLabel("Points pour gagner :")); panPts.add(points); points.setColumns(5);
        JPanel panTrs = new JPanel(); panTrs.add(new JLabel("Tours maximum :")); panTrs.add(tours); tours.setColumns(5);
        JPanel panMode = new JPanel(); panMode.add(new JLabel("Mode :")); panMode.add(mode);

        add(panP1);
        add(panP2);
        add(panPts);
        add(panTrs);
        add(panMode);

        JPanel panBtn = new JPanel();
        panBtn.add(lancer);
        panBtn.add(retour);
        add(panBtn);

        setVisible(true);
    }
}
