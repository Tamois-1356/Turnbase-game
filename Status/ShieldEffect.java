package Status;

import AbstractClass.Unit;

public class ShieldEffect implements StatusEffect {
    private final int amount;
    private int remainingTurns;
    private boolean applied = false;

    public ShieldEffect(int amount, int duration) {
        this.amount = amount;
        this.remainingTurns = duration;
    }

    @Override public void onApply(Unit target) {
        if (!applied && target.isAlive()) {
            target.addShield(amount);
            applied = true;
        }
    }
    @Override public void onTurnStart(Unit target) { if (remainingTurns>0) remainingTurns--; }
    @Override public void onTurnEnd(Unit target) {}
    @Override public void onExpire(Unit target) { System.out.println(target.getName() + "'s shield expired."); }
    @Override public boolean isExpired() { return remainingTurns <= 0; }
    @Override public StatusEffect copy() { return new ShieldEffect(amount, remainingTurns); }
}
