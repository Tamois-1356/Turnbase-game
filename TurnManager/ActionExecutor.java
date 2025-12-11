package TurnManager;

import AbstractClass.Unit;
import Skills.Skill;
import UI.BattleConsoleUI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ActionExecutor {
    private final BattleConsoleUI ui;

    public ActionExecutor(BattleConsoleUI ui) {
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

    public void executeSkill(Unit caster, Skill skill, List<Unit> allies, List<Unit> enemies) {
        ui.setInstruction(caster.getName() + " uses " + skill.getName());
        
        // Check if skill requires target selection
        SkillTargetType targetType = determineSkillTargetType(skill);
        
        try {
            Unit primaryTarget = null;
            
            if (null != targetType) switch (targetType) {
                case SINGLE_ENEMY -> {
                    CompletableFuture<Unit> targetFuture = ui.requestTarget(enemies, BattleConsoleUI.TargetType.ENEMY);
                    primaryTarget = targetFuture.get();
                    }
                case SINGLE_ALLY -> {
                    CompletableFuture<Unit> targetFuture = ui.requestTarget(allies, BattleConsoleUI.TargetType.ALLY);
                    primaryTarget = targetFuture.get();
                    }
                case ALL_ALLIES -> // No target selection needed, use first ally as primary target
                    primaryTarget = allies.isEmpty() ? null : allies.get(0);
                case ALL_ENEMIES -> // No target selection needed, use first enemy as primary target
                    primaryTarget = enemies.isEmpty() ? null : enemies.get(0);
                default -> {
                }
            }
            
            if (primaryTarget == null) return;
            
            // Track values before skill execution
            List<StateSnapshot> snapshots = captureStates(allies, enemies);
            
            // Execute skill
            skill.use(caster, primaryTarget, allies, enemies);
            ui.pause(200);
            
            // Show visual effects based on changes
            showEffectsFromChanges(snapshots, allies, enemies);
            
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    private SkillTargetType determineSkillTargetType(Skill skill) {
        String skillName = skill.getName().toLowerCase();
        
        // Determine based on skill name patterns
        if (skillName.contains("Shield") || skillName.contains("Heal") || skillName.contains("Buff")) {
            if (skillName.contains("Antibody") || skillName.contains("All")) {
                return SkillTargetType.ALL_ALLIES;
            }
            return SkillTargetType.SINGLE_ALLY;
        }
        
        if (skillName.contains("Rapid") || skillName.contains("Multi") || skillName.contains("Aoe")) {
            return SkillTargetType.ALL_ENEMIES;
        }
        
        return SkillTargetType.SINGLE_ENEMY;
    }

    private List<StateSnapshot> captureStates(List<Unit> allies, List<Unit> enemies) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        
        for (Unit u : allies) {
            snapshots.add(new StateSnapshot(u, u.getHp(), u.getShield()));
        }
        for (Unit u : enemies) {
            snapshots.add(new StateSnapshot(u, u.getHp(), u.getShield()));
        }
        
        return snapshots;
    }

    private void showEffectsFromChanges(List<StateSnapshot> snapshots, List<Unit> allies, List<Unit> enemies) {
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(allies);
        allUnits.addAll(enemies);
        
        for (int i = 0; i < Math.min(snapshots.size(), allUnits.size()); i++) {
            StateSnapshot snapshot = snapshots.get(i);
            Unit unit = allUnits.get(i);
            
            if (snapshot.unit != unit) continue; // Safety check
            
            int hpChange = unit.getHp() - snapshot.oldHp;
            int shieldChange = unit.getShield() - snapshot.oldShield;
            
            if (hpChange > 0) {
                ui.showHealing(unit, hpChange);
            } else if (hpChange < 0) {
                ui.showDamage(unit, -hpChange);
            }
            
            if (shieldChange > 0) {
                ui.showShield(unit, shieldChange);
            }
        }
    }

    private enum SkillTargetType {
        SINGLE_ENEMY,
        SINGLE_ALLY,
        ALL_ENEMIES,
        ALL_ALLIES
    }

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