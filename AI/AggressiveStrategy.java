package AI;

import AbstractClass.Unit;
import Skills.ActiveSkill;
import Skills.Skill;
import TurnManager.ActionExecutor;
import java.util.Comparator;
import java.util.List;

public class AggressiveStrategy implements AIStrategy {

    @Override
    public void executeTurn(Unit me, List<Unit> allies, List<Unit> enemies, ActionExecutor executor) {
        // 1. Find the weakest enemy (Lowest HP)
        Unit target = enemies.stream()
                .filter(Unit::isAlive)
                .min(Comparator.comparingInt(Unit::getHp))
                .orElse(null);

        if (target == null) return;

        // 2. Try to find a damaging skill
        // Heuristic: Look for "Strike", "Fire", or just the first active skill available
        ActiveSkill bestSkill = (ActiveSkill) me.getSkills().stream()
                .filter(Skill::isReady)
                .filter(s -> s instanceof ActiveSkill)
                .filter(s -> {
                    String name = s.getName().toLowerCase();
                    return name.contains("strike") || name.contains("fire") || name.contains("attack");
                })
                .findFirst()
                .orElse(null);

        // If no specific attack skill found, try any active skill
        if (bestSkill == null) {
            bestSkill = (ActiveSkill) me.getSkills().stream()
                .filter(Skill::isReady)
                .filter(s -> s instanceof ActiveSkill)
                .findFirst()
                .orElse(null);
        }

        // 3. Execute
        if (bestSkill != null) {
            // Pass the target explicitly so we don't ask the UI
            executor.executeSkill(me, bestSkill, target, allies, enemies);
        } else {
            executor.executeBasicAttack(me, target);
        }
    }
}