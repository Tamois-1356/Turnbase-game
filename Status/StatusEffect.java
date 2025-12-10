package Status;

import AbstractClass.Unit;

public interface StatusEffect {
    void onApply(Unit target);
    void onTurnStart(Unit target);
    void onTurnEnd(Unit target);
    void onExpire(Unit target);
    boolean isExpired();
    StatusEffect copy();
}
