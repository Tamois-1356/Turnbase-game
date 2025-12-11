package Status;

import AbstractClass.Unit;

public class BleedEffect implements StatusEffect {
    private final int damagePerTurn;
    private int remainingTurns;

    public BleedEffect(int damagePerTurn, int duration) {
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = duration;
    }

    @Override public void onApply(Unit target) { System.out.println(target.getName() + " starts bleeding."); }
    @Override public void onTurnStart(Unit target) {
        if (remainingTurns > 0 && target.isAlive()) {
            target.takeDamage(damagePerTurn);
            remainingTurns--;
        }
    }
    @Override public void onTurnEnd(Unit target) {}
    @Override public void onExpire(Unit target) { System.out.println(target.getName() + " bleed ended."); }
    @Override public boolean isExpired() { return remainingTurns <= 0; }
    @Override public StatusEffect copy() { return new BleedEffect(damagePerTurn, remainingTurns); }
}
