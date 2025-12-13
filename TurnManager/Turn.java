package TurnManager;

import AbstractClass.Unit;
import Skills.PassiveSkill;
import Skills.Skill;
import UI.BattleConsoleUI;
import UI.GameUI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Turn {
    private final List<Unit> teamA;
    private final List<Unit> teamB;
    private final GameUI ui;
    private final ActionExecutor actionExecutor;
    private int turnCounter = 1;

    public Turn(List<Unit> teamA, List<Unit> teamB, GameUI ui) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.ui = ui;
        this.actionExecutor = new ActionExecutor(ui);
        
        setupUnitListeners(teamA);
        setupUnitListeners(teamB);
    }
    
    private void setupUnitListeners(List<Unit> team) {
        for (Unit u : team) {
            u.setListener(ui::logMessage);
        }
    }

    public void startBattle() {
        ui.updateTurn(turnCounter);
        ui.updateTeams(teamA, teamB);
        ui.pause(600);
        
        boolean running = true;
        while (running) {
            ui.updateTurn(turnCounter);
            ui.updateTeams(teamA, teamB);

            // Player team
            ui.setInstruction("-- IMMUNE TEAM TURN --");
            running = takeTurn(teamA, teamB, true);
            if (!running) break;

            ui.pause(800);

            // Enemy team
            ui.setInstruction("-- VIRUS TEAM TURN --");
            running = takeTurn(teamB, teamA, false);

            turnCounter++;
            ui.pause(1000);
        }

        boolean aliveA = teamA.stream().anyMatch(Unit::isAlive);
        ui.setInstruction(aliveA ? "===IMMUNE WINS!===" : "=== VIRUS WINS ===");
    }

    private boolean takeTurn(List<Unit> actingTeam, List<Unit> defendingTeam, boolean playerControlled) {
        for (Unit unit : actingTeam) {
            if (!unit.isAlive()) continue;

            ui.setInstruction(">>> " + unit.getName() + "'s turn");
            ui.pause(300);

            unit.processTurnStartStatuses();
            ui.updateTeams(teamA, teamB);
            ui.pause(200);
            if (!unit.isAlive()) continue;

            for (Skill s : unit.getSkills()) {
                if (s instanceof PassiveSkill passiveSkill) {
                    passiveSkill.onTurnStart(unit, actingTeam, defendingTeam);
                }
            }

            ui.updateTeams(teamA, teamB);
            ui.pause(150);

            if (playerControlled) {
                executePlayerAction(unit, actingTeam, defendingTeam);
            } else {
                executeAIAction(unit, actingTeam, defendingTeam);
            }

            ui.pause(250);
            
            unit.processTurnEndStatuses();
            unit.tickSkillCooldowns();
            ui.updateTeams(teamA, teamB);
            ui.pause(200);

            boolean anyA = teamA.stream().anyMatch(Unit::isAlive);
            boolean anyB = teamB.stream().anyMatch(Unit::isAlive);
            if (!anyA || !anyB) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void executePlayerAction(Unit unit, List<Unit> allies, List<Unit> enemies) {
        try {
            CompletableFuture<BattleConsoleUI.ActionChoice> actionFuture = ui.requestAction(unit);
            BattleConsoleUI.ActionChoice choice = actionFuture.get();

            if (choice.type == BattleConsoleUI.ActionType.BASIC_ATTACK) {
                CompletableFuture<Unit> targetFuture = ui.requestTarget(enemies, BattleConsoleUI.TargetType.ENEMY);
                Unit target = targetFuture.get();
                if (target != null) {
                    actionExecutor.executeBasicAttack(unit, target);
                }
            } else if (choice.type == BattleConsoleUI.ActionType.USE_SKILL) {
                // Pass NULL target to trigger UI target selection
                actionExecutor.executeSkill(unit, choice.skill, null, allies, enemies);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void executeAIAction(Unit unit, List<Unit> allies, List<Unit> enemies) {
        if (unit.getAIStrategy() != null) {
            ui.setInstruction(unit.getName() + " is thinking...");
            unit.getAIStrategy().executeTurn(unit, allies, enemies, actionExecutor);
        } else {
            // Fallback (Zombie AI)
            Unit target = enemies.stream().filter(Unit::isAlive).findFirst().orElse(null);
            if (target != null) {
                actionExecutor.executeBasicAttack(unit, target);
            }
        }
        ui.pause(300);
    }
}