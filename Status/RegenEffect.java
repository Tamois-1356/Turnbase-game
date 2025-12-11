package Status;

import AbstractClass.Unit;

public class RegenEffect implements StatusEffect {
    private final int healPerTurn;
    private int remainingTurns;

    public RegenEffect(int healPerTurn, int duration) {
        this.healPerTurn = healPerTurn;
        this.remainingTurns = duration;
    }

    @Override public void onApply(Unit target) { System.out.println(target.getName() + " gains regeneration."); }
    @Override public void onTurnStart(Unit target) {
        if (remainingTurns > 0 && target.isAlive()) {
            target.heal(healPerTurn);
            remainingTurns--;
        }
    }
    @Override public void onTurnEnd(Unit target) {}
    @Override public void onExpire(Unit target) { System.out.println(target.getName() + " regen ended."); }
    @Override public boolean isExpired() { return remainingTurns <= 0; }
    @Override public StatusEffect copy() { return new RegenEffect(healPerTurn, remainingTurns); }
}
