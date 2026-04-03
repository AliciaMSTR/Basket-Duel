package Vue;

import javax.swing.*;
import java.awt.*;
import Controlleur.ControleurMenu;

public class ParametresVue extends JFrame {

    private static final long serialVersionUID = 1L;

    public ParametresVue(ControleurMenu controleur) {

        setTitle("Paramètres du Jeu");
        setSize(450, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Composants
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titre = new JLabel("Paramètres");
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        titre.setFont(new Font("Arial", Font.BOLD, 30));
        titre.setForeground(Color.WHITE);

        // LANGUE
        JLabel labelLangue = createLabel("Langue :");
        JComboBox<String> langues = new JComboBox<>(
                new String[]{"Français", "Anglais", "Espagnol", "Allemand"}
        );
        langues.setMaximumSize(new Dimension(200, 30));

        // THEME
        JLabel labelTheme = createLabel("Thème visuel :");
        JComboBox<String> theme = new JComboBox<>(
                new String[]{"Sombre", "Clair"}
        );
        theme.setMaximumSize(new Dimension(200, 30));

        // VOLUME
        JLabel labelVolume = createLabel("Volume musique :");
        JSlider volume = new JSlider(0, 100, 70);
        volume.setMajorTickSpacing(20);
        volume.setPaintTicks(true);
        volume.setPaintLabels(true);
        volume.setBackground(new Color(30, 30, 30));
        volume.setForeground(Color.WHITE);

        // SON ON/OFF
        JCheckBox musique = new JCheckBox("Activer la musique");
        musique.setSelected(true);
        musique.setForeground(Color.WHITE);
        musique.setBackground(new Color(30, 30, 30));

        // Boutons
        JButton appliquer = createButton("Appliquer");
        JButton retour = createButton("Retour");

        appliquer.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Paramètres enregistrés !",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        retour.addActionListener(e -> {
            dispose();
            controleur.allerMenu();
        });

        //  Ajout dans la fenêtre
        panel.add(titre);
        panel.add(Box.createVerticalStrut(20));

        panel.add(labelLangue);
        panel.add(langues);
        panel.add(Box.createVerticalStrut(15));

        panel.add(labelTheme);
        panel.add(theme);
        panel.add(Box.createVerticalStrut(15));

        panel.add(labelVolume);
        panel.add(volume);
        panel.add(Box.createVerticalStrut(15));

        panel.add(musique);
        panel.add(Box.createVerticalStrut(25));

        panel.add(appliquer);
        panel.add(Box.createVerticalStrut(10));
        panel.add(retour);

        setContentPane(panel);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    private JButton createButton(String txt) {
        JButton b = new JButton(txt);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}
