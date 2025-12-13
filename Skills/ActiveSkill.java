package Skills;

import AbstractClass.Unit;
import java.util.List;

/**
 * ActiveSkill that runs a TriAction.
 * Implements the specific behavior of a skill that can be 'used'.
 */
public class ActiveSkill extends Skill {
    private final TriAction action;

    public interface TriAction {
        void apply(ActiveSkill self, Unit caster, Unit target, List<Unit> allies, List<Unit> enemies);
    }

    public ActiveSkill(String name, int cooldown, TriAction action) {
        super(name, cooldown);
        this.action = action;
    }

    public void use(Unit caster, Unit primaryTarget, List<Unit> allies, List<Unit> enemies) {
        if (!isReady()) {
            // Can log this via return value or UI if needed, but keeping simple for now
            return;
        }
        action.apply(this, caster, primaryTarget, allies, enemies);
        triggerCooldown();
    }
}