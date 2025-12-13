package AI;

import AbstractClass.Unit;
import Skills.ActiveSkill;
import Skills.Skill;
import TurnManager.ActionExecutor;
import java.util.Comparator;
import java.util.List;

public class SmartSupportStrategy implements AIStrategy {

    @Override
    public void executeTurn(Unit me, List<Unit> allies, List<Unit> enemies, ActionExecutor executor) {
        // 1. Check for allies in danger (< 50% HP)
        Unit dyingAlly = allies.stream()
                .filter(Unit::isAlive)
                .filter(u -> u.getHp() < (u.getMaxHp() * 0.5))
                .min(Comparator.comparingInt(Unit::getHp)) // Heal the most injured
                .orElse(null);

        // 2. Find a support skill
        ActiveSkill supportSkill = (ActiveSkill) me.getSkills().stream()
                .filter(Skill::isReady)
                .filter(s -> s instanceof ActiveSkill)
                .filter(s -> {
                    String name = s.getName().toLowerCase();
                    return name.contains("shield") || name.contains("heal") || name.contains("buff");
                })
                .findFirst()
                .orElse(null);

        // 3. Decision Tree
        if (dyingAlly != null && supportSkill != null) {
            // Save the ally!
            executor.executeSkill(me, supportSkill, dyingAlly, allies, enemies);
        } else {
            // No one to save? Attack the weakest enemy.
            Unit target = enemies.stream()
                .filter(Unit::isAlive)
                .min(Comparator.comparingInt(Unit::getHp))
                .orElse(null);
                
            if (target != null) {
                executor.executeBasicAttack(me, target);
            }
        }
    }
}