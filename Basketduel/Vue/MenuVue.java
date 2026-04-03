package Vue;

import javax.swing.*;
import java.awt.*;
import Controlleur.ControleurMenu;

public class MenuVue extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton boutonRejoindre;
    private JButton boutonCreer;
    private JButton boutonQuitter;

    private final ControleurMenu controleur;

    public MenuVue(ControleurMenu controleur) {
        this.controleur = controleur;

        setTitle("Basket Duel - Menu Principal");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initialiserUI();
        ajouterListeners();

        setVisible(true);
    }

    private void initialiserUI() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 25));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel titre = new JLabel("Basket Duel");
        titre.setFont(new Font("Arial", Font.BOLD, 46));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        boutonCreer = bouton("Créer / Démarrer une Partie");
        boutonRejoindre = bouton("Rejoindre une Partie");
        boutonQuitter = bouton("Quitter");

        panel.add(titre);
        panel.add(Box.createVerticalStrut(60));
        panel.add(boutonCreer);
        panel.add(Box.createVerticalStrut(25));
        panel.add(boutonRejoindre);
        panel.add(Box.createVerticalStrut(25));
        panel.add(boutonQuitter);

        setContentPane(panel);
    }

    private JButton bouton(String texte) {
        JButton b = new JButton(texte);
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 22));
        b.setFocusPainted(false);
        b.setMaximumSize(new Dimension(300, 55));
        return b;
    }

    private void ajouterListeners() {

        // Ouvre Création de partie
        boutonCreer.addActionListener(e -> {
            dispose();
            controleur.allerCreerPartie();
        });

        // Ouvre rejoindre partie réseau
        boutonRejoindre.addActionListener(e -> {
            dispose();
            controleur.allerRejoindrePartie();
        });

        // Quitter le jeu
        boutonQuitter.addActionListener(e -> controleur.quitter());
    }
}
