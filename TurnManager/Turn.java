package TurnManager;

import AbstractClass.Unit;
import Skills.PassiveSkill;
import Skills.Skill;
import UI.BattleConsoleUI;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Turn manager for console flow. Alternating team turns, player controls teamA.
 */
public class Turn {
    private final List<Unit> teamA;
    private final List<Unit> teamB;
    private final BattleConsoleUI ui;
    private int turnCounter = 1;

    public Turn(List<Unit> teamA, List<Unit> teamB, BattleConsoleUI ui) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.ui = ui;
    }

    public void startBattle() {
        ui.clearScreen();
        ui.log("=== BATTLE START ===");
        ui.pause(600);
        boolean running = true;
        while (running) {
            ui.displayHeader(turnCounter);
            ui.displayTeams(teamA, teamB);

            // Player team
            ui.log("\n-- IMMUNE TEAM --");
            running = takeTurn(teamA, teamB, true);
            if (!running) break;

            // Enemy team
            ui.log("\n-- VIRUS TEAM --");
            running = takeTurn(teamB, teamA, false);

            turnCounter++;
        }

        ui.log("\n=== BATTLE END ===");
        boolean aliveA = teamA.stream().anyMatch(Unit::isAlive);
        ui.log(aliveA ? "IMMUNE WINS!" : "VIRUS WINS!");
    }

    private boolean takeTurn(List<Unit> actingTeam, List<Unit> defendingTeam, boolean playerControlled) {
        for (Unit unit : actingTeam) {
            if (!unit.isAlive()) continue;

            ui.log("\n>>> " + unit.getName() + " turn");
            ui.pause(300);

            // start statuses
            unit.processTurnStartStatuses();
            ui.displayTeams(teamA, teamB);
            ui.pause(200);
            if (!unit.isAlive()) continue;

            // passive skills
            for (Skill s : unit.getSkills()) {
                if (s instanceof PassiveSkill) {
                    ((PassiveSkill) s).onTurnStart(unit, actingTeam, defendingTeam);
                }
            }

            ui.displayTeams(teamA, teamB);
            ui.pause(150);

            if (playerControlled) {
                // player choices
                int action = ui.askAction(unit);
                if (action == 1) { // basic attack
                    Unit target = ui.askTarget(defendingTeam);
                    if (target == null) return false;
                    unit.basicAttack(target);
                } else {
                    Skill skill = ui.askSkill(unit);
                    if (skill == null) { ui.log("No skill selected or skill on cooldown."); continue; }
                    // for ally-targeting skills, ui.askSkill could permit selecting ally; we'll pass both lists
                    Unit primary = ui.askTarget(defendingTeam); // simple: ask enemy target
                    if (primary == null) return false;
                    skill.use(unit, primary, actingTeam, defendingTeam);
                }
            } else {
                // AI: pick a ready skill or basic attack
                Skill s = unit.getSkills().stream().filter(Skill::isReady).filter(sk -> !(sk instanceof PassiveSkill)).findFirst().orElse(null);
                Unit target = defendingTeam.stream().filter(Unit::isAlive).findAny().orElse(null);
                if (target == null) return false;
                if (s != null) {
                    ui.log(unit.getName() + " (AI) uses " + s.getName());
                    s.use(unit, target, actingTeam, defendingTeam);
                } else {
                    ui.log(unit.getName() + " (AI) basic attacks " + target.getName());
                    unit.basicAttack(target);
                }
            }

            ui.pause(250);
            unit.processTurnEndStatuses();
            unit.tickSkillCooldowns();
            ui.displayTeams(teamA, teamB);
            ui.pause(200);

            // check victory
            boolean anyA = teamA.stream().anyMatch(Unit::isAlive);
            boolean anyB = teamB.stream().anyMatch(Unit::isAlive);
            if (!anyA || !anyB) {
                return false;
            }
        }
        return true;
    }
}
