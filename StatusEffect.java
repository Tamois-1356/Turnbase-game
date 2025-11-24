package Status;

import AbstractClass.Unit;

public interface StatusEffect {
    void apply(Unit target);
    boolean isExpired();
    String getName();
    
}
