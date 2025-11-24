package Status;

import AbstractClass.Unit;

public class PoisonEffect implements StatusEffect {
    private final int damagePerTurn;
    private int duration;

    public PoisonEffect(int damagePerTurn, int duration) {
        this.damagePerTurn = damagePerTurn;
        this.duration = duration;
    }

    @Override
    public void apply(Unit target) {
        if (duration > 0) {
            System.out.println(target.name + " takes " + damagePerTurn + " poison damage.");
            target.takeDamage(damagePerTurn);
            duration--;
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public String getName() {
        return "Poison";
    }
}
