package Skills;

import AbstractClass.Unit;
import java.util.List;

/**
 * Base Skill API
 */
public abstract class Skill {
    protected final String name;
    protected final int cooldown;
    protected int cooldownLeft = 0;

    public Skill(String name, int cooldown) {
        this.name = name;
        this.cooldown = Math.max(0, cooldown);
    }

    public String getName() { return name; }
    public int getCooldown() { return cooldown; }
    public int getCooldownLeft() { return cooldownLeft; }
    public boolean isReady() { return cooldownLeft == 0; }

    public void tickCooldown() { if (cooldownLeft > 0) cooldownLeft--; }
    protected void triggerCooldown() { cooldownLeft = cooldown; }

    /**
     * use(caster, primaryTarget, alliesList, enemiesList)
     */
    public abstract void use(Unit caster, Unit primaryTarget, List<Unit> allies, List<Unit> enemies);
}
