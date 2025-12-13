package UI;

import AbstractClass.Unit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for Game UI.
 * The TurnManager/Logic only talks to this, adhering to Dependency Inversion (DIP).
 */
public interface GameUI {
    // Core Game Flow Updates
    void updateTurn(int turn);
    void setInstruction(String text);
    void updateTeams(List<Unit> teamA, List<Unit> teamB);
    void pause(int ms);
    
    // User Interaction
    CompletableFuture<BattleConsoleUI.ActionChoice> requestAction(Unit unit);
    CompletableFuture<Unit> requestTarget(List<Unit> validTargets, BattleConsoleUI.TargetType type);
    
    // Visual Effects
    void showDamage(Unit target, int damage);
    void showHealing(Unit target, int healing);
    void showShield(Unit target, int shield);
    
    // Logging (New method to handle the text output from Units)
    void logMessage(String message);
}