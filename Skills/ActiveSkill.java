package Skills;

import AbstractClass.Unit;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * ActiveSkill that runs a BiConsumer action (caster, target).
 * The lambda must implement the effect and call triggerCooldown() via this.triggerCooldown();
 */
public class ActiveSkill extends Skill {
    // simple action; more advanced skills can override use()
    private final TriAction action;

    public interface TriAction {
        void apply(ActiveSkill self, Unit caster, Unit target, java.util.List<Unit> allies, java.util.List<Unit> enemies);
    }

    public ActiveSkill(String name, int cooldown, TriAction action) {
        super(name, cooldown);
        this.action = action;
    }

    @Override
    public void use(Unit caster, Unit primaryTarget, List<Unit> allies, List<Unit> enemies) {
        if (!isReady()) {
            System.out.println("[skill] " + name + " is on cooldown (" + cooldownLeft + ")");
            return;
        }
        action.apply(this, caster, primaryTarget, allies, enemies);
        triggerCooldown();
    }
}
