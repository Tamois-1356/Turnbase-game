import UI.BattleSystem;
import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        // Initialize on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            BattleSystem battleSystem = new BattleSystem();
            battleSystem.start();
        });
    }
}