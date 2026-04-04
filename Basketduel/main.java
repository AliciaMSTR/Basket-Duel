import javax.swing.SwingUtilities;
import Controlleur.ControleurMenu;

public class main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater est crucial, il garantit que l'interface graphique (UI) est créée depuis un thread dédié (l'Event Dispatch Thread). 
        // Si on crééait l'UI sans ça, on risquerait des problèmes de blocage ou d'affichage simultanés.
        SwingUtilities.invokeLater(() -> {
            new ControleurMenu();
        });
    }
}
