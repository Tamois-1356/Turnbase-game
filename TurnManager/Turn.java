package TurnManager;

import AbstractClass.Unit;
import Skills.PassiveSkill;
import Skills.Skill;
import UI.BattleConsoleUI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Turn {
    private final List<Unit> teamA;
    private final List<Unit> teamB;
    private final BattleConsoleUI ui;
    private final ActionExecutor actionExecutor;
    private int turnCounter = 1;

    public Turn(List<Unit> teamA, List<Unit> teamB, BattleConsoleUI ui) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.ui = ui;
        this.actionExecutor = new ActionExecutor(ui);
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

            // Process turn start
            unit.processTurnStartStatuses();
            ui.updateTeams(teamA, teamB);
            ui.pause(200);
            if (!unit.isAlive()) continue;

            // Passive skills
            for (Skill s : unit.getSkills()) {
                if (s instanceof PassiveSkill passiveSkill) {
                    passiveSkill.onTurnStart(unit, actingTeam, defendingTeam);
                }
            }

            ui.updateTeams(teamA, teamB);
            ui.pause(150);

            // Execute action
            if (playerControlled) {
                executePlayerAction(unit, actingTeam, defendingTeam);
            } else {
                executeAIAction(unit, actingTeam, defendingTeam);
            }

            ui.pause(250);
            
            // Process turn end
            unit.processTurnEndStatuses();
            unit.tickSkillCooldowns();
            ui.updateTeams(teamA, teamB);
            ui.pause(200);

            // Check victory
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
                actionExecutor.executeSkill(unit, choice.skill, allies, enemies);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void executeAIAction(Unit unit, List<Unit> allies, List<Unit> enemies) {
        Skill readySkill = unit.getSkills().stream()
            .filter(Skill::isReady)
            .filter(s -> !(s instanceof PassiveSkill))
            .findFirst()
            .orElse(null);

        Unit target = enemies.stream()
            .filter(Unit::isAlive)
            .findFirst()
            .orElse(null);

        if (target == null) return;

        if (readySkill != null) {
            ui.setInstruction(unit.getName() + " (AI) uses " + readySkill.getName());
            actionExecutor.executeSkill(unit, readySkill, allies, enemies);
        } else {
            ui.setInstruction(unit.getName() + " (AI) attacks " + target.getName());
            actionExecutor.executeBasicAttack(unit, target);
        }

        ui.pause(300);
    }
}