# Basket Duel

> **Jeu de sport compétitif en 2D au tour par tour.**
> *Projet Universitaire - L3 MIAGE - 2025-2026*

## 1- Description du projet
**Basket Duel** est une simulation de tir au panier développée en **Java**. L'objectif est d'affronter un adversaire (humain ou IA) sur un terrain vu de profil, en marquant des paniers grâce à une gestion précise de la physique.

Le projet met l'accent sur une architecture **MVC (Modèle-Vue-Contrôleur)** solide et un moteur physique réaliste. Nous nous sommes focalisés sur la stabilité du coeur de jeu pour garantir une expérience fluide et fonctionnelle.

### 2- Fonctionnalités principales
*   **Modes de jeu opérationnels :**
    *   **Solo vs IA :** Affrontez une intelligence artificielle capable de calculer ses trajectoires balistiques avec différents niveaux de difficulté.
    *   **Local (1v1) :** Affrontez un ami sur la même machine.
*   **Moteur Physique :** Simulation précise de la gravité (980 px/s²) et trajectoires paraboliques. Les tirs en cloche ne sont plus limités par le haut de l'écran.
*   **Système d'items Dynamiques :**
    *   **Bonus :** Agrandissement du panier, trajectoire aidée, points supplémentaires.
    *   **Malus :** Téléportation surprise du panier, apparition de murs d'obstacles, points retirés.
*   **Évolutions Futures :**
    *   **Mode Réseau :** L'architecture technique (Sockets) est déjà présente dans le code mais sera activée ultérieurement pour permettre le multijoueur distant.

## 2- Membres du groupe
*   **TER Ilyas** (20233771)
*   **BENAMMAR Ahmed** (20233849)
*   **CIVANESWARAN Rathissan** (20230223)
*   **MESTOUR Alicia** (20245443)

## 3- Stack Technique
*   **Langage :** Java (JDK 17+)
*   **Interface :** Swing / AWT avec rendu Graphics2D avancé (Scaling dynamique, Antialiasing).
*   **Conception :** Diagrammes UML (Classes, Séquence, Activité).
*   **Versionning :** Git & GitHub.

## 4- Structure du projet
*   `Basketduel/` : Racine du code source.
    *   `Controlleur/` : Gestion de la logique de jeu, des tours et des collisions.
    *   `Modele/` : Définition des entités (Ballon, Panier, Terrain, IA) et de la physique.
    *   `Vue/` : Interface graphique (StyleUI, TerrainVue) et fenêtres de menu.
    *   `assets/` : Ressources graphiques (Sprites, fonds).

## 5- Lancement du projet
Pour compiler et lancer le jeu :
1. Compiler toutes les classes depuis la racine :
   ```bash
   javac -d bin Basketduel/*.java Basketduel/Controlleur/*.java Basketduel/Modele/*.java Basketduel/Vue/*.java
   ```
2. Lancer la classe principale :
   ```bash
   java -cp bin Basketduel.main
   ```

## 6- État d'avancement (Roadmap)

*   [x] **Phase 1 : Conception** (UML, Architecture MVC)
*   [x] **Phase 2 : Moteur Physique** (Gravité, Paraboles, Collisions)
*   [x] **Phase 3 : Gameplay & IA** (Tours de jeu, Algorithme IA balistique)
*   [x] **Phase 4 : Système d'Items** (Bonus, Malus, Murs obstacles)
*   [x] **Phase 5 : Interface Graphique** (Design moderne, Scaling dynamique)
*   [ ] **Phase 6 : Réseau** (Finalisation de la synchronisation Sockets)

---
*Projet réalisé dans le cadre du module de Projet Informatique.*
