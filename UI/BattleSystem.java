package UI;

import AbstractClass.Unit;
import Factory.UnitFactory;
import TurnManager.Turn;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class BattleSystem {
    private List<Unit> teamA;
    private List<Unit> teamB;
    private BattleConsoleUI ui;
    private Turn turnManager;
    
    //Start the battle system
    public void start() {
        initialize();
        runBattle();
    }
    
    //Initialize all battle components
    private void initialize() {
        System.out.println("=== Initializing Battle System ===");
        
        // Create UI
        ui = new BattleConsoleUI();
        
        // Create teams
        teamA = createImmuneTeam();
        teamB = createVirusTeam();
        
        // Create turn manager
        turnManager = new Turn(teamA, teamB, ui);
        
        System.out.println("Battle System initialized successfully");
    }
    
    //Create the player's immune team
    private List<Unit> createImmuneTeam() {
        List<Unit> team = new ArrayList<>();
        team.add(UnitFactory.createUnit("tcell", "T-Killer"));
        team.add(UnitFactory.createUnit("bcell", "B-Helper"));
        team.add(UnitFactory.createUnit("tcell", "T-Defender"));
        
        System.out.println("Immune Team created: " + team.size() + " units");
        return team;
    }
    
    //Create the enemy virus team
    private List<Unit> createVirusTeam() {
        List<Unit> team = new ArrayList<>();
        team.add(UnitFactory.createUnit("tcell", "Virus-Alpha"));
        team.add(UnitFactory.createUnit("bcell", "Virus-Beta"));
        team.add(UnitFactory.createUnit("tcell", "Virus-Gamma"));
        
        System.out.println("Virus Team created: " + team.size() + " units");
        return team;
    }
    
    //Run the battle in a separate thread
    private void runBattle() {
        new Thread(() -> {
            try {
                // Give UI time to fully load
                Thread.sleep(1000);
                
                System.out.println("=== BATTLE START ===");
                turnManager.startBattle();
                System.out.println("=== BATTLE END ===");
                
                // Show final result
                showBattleResult();
                
            } catch (InterruptedException e) {
                handleBattleError(e);
            }
        }).start();
    }
    
    //Show the battle result and ask if player wants to play again
    private void showBattleResult() {
        boolean immuneWins = teamA.stream().anyMatch(Unit::isAlive);
        
        SwingUtilities.invokeLater(() -> {
            String title = immuneWins ? "Victory!" : "Defeat";
            String message = immuneWins 
                ? "The Immune System has defeated the virus!\n\nYour cells have successfully protected the body!" 
                : "The virus has overwhelmed the immune system\n\nBetter luck next time!";
            
            int messageType = immuneWins 
                ? JOptionPane.INFORMATION_MESSAGE 
                : JOptionPane.WARNING_MESSAGE;
            
            JOptionPane.showMessageDialog(ui, message, title, messageType);
            
            // Ask if they want to play again
            promptPlayAgain();
        });
    }
    
    //Ask the player if they want to play again
    private void promptPlayAgain() {
        int choice = JOptionPane.showConfirmDialog(ui, 
            "Would you like to battle again?", 
            "Play Again?", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            restartBattle();
        } else {
            exitGame();
        }
    }
    
    //Restart the battle with a new instance
    private void restartBattle() {
        ui.dispose();
        SwingUtilities.invokeLater(() -> {
            BattleSystem newBattle = new BattleSystem();
            newBattle.start();
        });
    }
    
    //Exit the game
    private void exitGame() {
        System.out.println("Thanks for playing!");
        System.exit(0);
    }
    
    //Handle battle errors
    @SuppressWarnings("CallToPrintStackTrace")
    private void handleBattleError(Exception e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(ui, 
                "An error occurred during battle:\n" + e.getMessage(), 
                "Battle Error", 
                JOptionPane.ERROR_MESSAGE);
            exitGame();
        });
    }
}