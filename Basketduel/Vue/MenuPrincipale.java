package Vue;

import javax.swing.*;
import java.awt.*;
import Controlleur.ControleurMenu;

public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton boutonLocal;
    private JButton boutonIA;
    private JButton boutonReseau;
    private JButton boutonQuitter;

    private final ControleurMenu controleur;

    public MenuPrincipal(ControleurMenu controleur) {
        this.controleur = controleur;

        setTitle("Basket Duel - Menu Principal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initialiserComposants();
        ajouterListeners();

        setVisible(true);
    }

    private void initialiserComposants() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 25));

        JLabel titre = new JLabel("Basket Duel");
        titre.setFont(new Font("Arial", Font.BOLD, 42));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        boutonLocal   = new JButton("Jouer en Local");
        boutonIA      = new JButton("Jouer contre l'IA");
        boutonReseau  = new JButton("Jouer en Réseau");
        boutonQuitter = new JButton("Quitter");

        Dimension tailleBouton = new Dimension(260, 55);

        for (JButton b : new JButton[]{boutonLocal, boutonIA, boutonReseau, boutonQuitter}) {
            b.setMaximumSize(tailleBouton);
            styliserBouton(b);
        }

        panel.add(Box.createVerticalStrut(80));
        panel.add(titre);
        panel.add(Box.createVerticalStrut(70));

        panel.add(boutonLocal);
        panel.add(Box.createVerticalStrut(25));

        panel.add(boutonIA);
        panel.add(Box.createVerticalStrut(25));

        panel.add(boutonReseau);
        panel.add(Box.createVerticalStrut(25));

        panel.add(boutonQuitter);

        setContentPane(panel);
    }

    private void styliserBouton(JButton bouton) {
        bouton.setFocusPainted(false);
        bouton.setFont(new Font("Arial", Font.BOLD, 18));
        bouton.setBackground(new Color(70, 130, 180));
        bouton.setForeground(Color.WHITE);
    }

    private void ajouterListeners() {

        boutonLocal.addActionListener(e -> {
            dispose();
            controleur.allerCreerPartie();
        });

        boutonIA.addActionListener(e -> {
            dispose();
            controleur.allerCreerPartie();
        });

        boutonReseau.addActionListener(e -> {
            dispose();
            controleur.allerRejoindrePartie();
        });

        boutonQuitter.addActionListener(e -> controleur.quitter());
    }
}
