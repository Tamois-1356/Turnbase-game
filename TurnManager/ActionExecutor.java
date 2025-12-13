package TurnManager;

import AbstractClass.Unit;
import Skills.ActiveSkill;
import Skills.Skill;
import UI.BattleConsoleUI;
import UI.GameUI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ActionExecutor {
    private final GameUI ui;

    public ActionExecutor(GameUI ui) {
        this.ui = ui;
    }

    public void executeBasicAttack(Unit attacker, Unit target) {
        ui.setInstruction(attacker.getName() + " attacks " + target.getName());
        int oldHp = target.getHp();
        attacker.basicAttack(target);
        int damage = oldHp - target.getHp();
        if (damage > 0) {
            ui.showDamage(target, damage);
        }
        ui.pause(300);
    }

    /**
     * Executes a skill.
     * @param explicitTarget If NOT null, this target is used (AI mode). If null, UI prompts user (Player mode).
     */
    public void executeSkill(Unit caster, Skill skill, Unit explicitTarget, List<Unit> allies, List<Unit> enemies) {
        if (!(skill instanceof ActiveSkill activeSkill)) {
            ui.logMessage("Cannot activate passive skill: " + skill.getName());
            return;
        }

        ui.setInstruction(caster.getName() + " uses " + skill.getName());
        
        SkillTargetType targetType = determineSkillTargetType(skill);
        
        try {
            Unit primaryTarget = explicitTarget;
            
            // If we don't have a target yet (Player Mode), ask the UI
            if (primaryTarget == null) {
                if (null != targetType) switch (targetType) {
                    case SINGLE_ENEMY -> {
                        CompletableFuture<Unit> targetFuture = ui.requestTarget(enemies, BattleConsoleUI.TargetType.ENEMY);
                        primaryTarget = targetFuture.get();
                        }
                    case SINGLE_ALLY -> {
                        CompletableFuture<Unit> targetFuture = ui.requestTarget(allies, BattleConsoleUI.TargetType.ALLY);
                        primaryTarget = targetFuture.get();
                        }
                    case ALL_ALLIES -> 
                        primaryTarget = allies.isEmpty() ? null : allies.get(0);
                    case ALL_ENEMIES -> 
                        primaryTarget = enemies.isEmpty() ? null : enemies.get(0);
                    default -> {
                    }
                }
            } else {
                // AI Mode or Explicit Target: Verify the target is valid for AoE logic fallback
                // (e.g., if AI passed a target but skill is AoE, we usually just accept it or ignore it)
            }
            
            // If AOE, we might ignore the specific target in the skill logic, but we need a placeholder to start
            if (primaryTarget == null && (targetType == SkillTargetType.ALL_ALLIES || targetType == SkillTargetType.ALL_ENEMIES)) {
                 if (targetType == SkillTargetType.ALL_ALLIES && !allies.isEmpty()) primaryTarget = allies.get(0);
                 if (targetType == SkillTargetType.ALL_ENEMIES && !enemies.isEmpty()) primaryTarget = enemies.get(0);
            }

            if (primaryTarget == null) return; // Cancelled or no targets
            
            List<StateSnapshot> snapshots = captureStates(allies, enemies);
            
            activeSkill.use(caster, primaryTarget, allies, enemies);
            ui.pause(200);
            
            showEffectsFromChanges(snapshots, allies, enemies);
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private SkillTargetType determineSkillTargetType(Skill skill) {
        String skillName = skill.getName().toLowerCase();
        
        if (skillName.contains("shield") || skillName.contains("heal") || skillName.contains("buff")) {
            if (skillName.contains("antibody") || skillName.contains("all")) {
                return SkillTargetType.ALL_ALLIES;
            }
            return SkillTargetType.SINGLE_ALLY;
        }
        
        if (skillName.contains("rapid") || skillName.contains("multi") || skillName.contains("aoe")) {
            return SkillTargetType.ALL_ENEMIES;
        }
        
        return SkillTargetType.SINGLE_ENEMY;
    }

    private List<StateSnapshot> captureStates(List<Unit> allies, List<Unit> enemies) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        allies.forEach(u -> snapshots.add(new StateSnapshot(u, u.getHp(), u.getShield())));
        enemies.forEach(u -> snapshots.add(new StateSnapshot(u, u.getHp(), u.getShield())));
        return snapshots;
    }

    private void showEffectsFromChanges(List<StateSnapshot> snapshots, List<Unit> allies, List<Unit> enemies) {
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(allies);
        allUnits.addAll(enemies);
        
        for (int i = 0; i < Math.min(snapshots.size(), allUnits.size()); i++) {
            StateSnapshot snapshot = snapshots.get(i);
            Unit unit = allUnits.get(i);
            if (snapshot.unit != unit) continue; 
            
            int hpChange = unit.getHp() - snapshot.oldHp;
            int shieldChange = unit.getShield() - snapshot.oldShield;
            
            if (hpChange > 0) ui.showHealing(unit, hpChange);
            else if (hpChange < 0) ui.showDamage(unit, -hpChange);
            
            if (shieldChange > 0) ui.showShield(unit, shieldChange);
        }
    }

    private enum SkillTargetType { SINGLE_ENEMY, SINGLE_ALLY, ALL_ENEMIES, ALL_ALLIES }

    private static class StateSnapshot {
        final Unit unit;
        final int oldHp;
        final int oldShield;
        StateSnapshot(Unit unit, int oldHp, int oldShield) {
            this.unit = unit;
            this.oldHp = oldHp;
            this.oldShield = oldShield;
        }
    }
}