package Vue;

import javax.swing.*;
import Controlleur.*;

public class EcranFinVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public EcranFinVue(ControleurMenu menu, ControleurJeu jeu) {

        setTitle("Fin de Partie");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int s1 = jeu.getPartie().getJoueurActif().getScore();
        int s2 = jeu.getPartie().getJoueurActif().getScore();

        JLabel titre = new JLabel("Partie terminée !", SwingConstants.CENTER);
        JLabel scores = new JLabel("Scores : J1 = " + s1 + " | J2 = " + s2, SwingConstants.CENTER);

        JButton rejouer = new JButton("Rejouer");
        JButton menuBtn = new JButton("Menu Principal");
        JButton quitter = new JButton("Quitter");

        rejouer.addActionListener(e -> {
            dispose();
            menu.relancerPartie();
        });

        menuBtn.addActionListener(e -> {
            dispose();
            menu.allerMenu();
        });

        quitter.addActionListener(e -> menu.quitter());

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(titre);
        add(scores);
        add(rejouer);
        add(menuBtn);
        add(quitter);

        setVisible(true);
    }
}
