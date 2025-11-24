package TurnManager;

import AbstractClass.Unit;
import Status.StunEffect;
import java.util.List;

public class TurnManager {

    private final List<Unit> units;
    private int currentIndex = 0;

    public TurnManager(List<Unit> units) {
        this.units = units;
    }

    public void nextTurn() {

        if (units.isEmpty())
            return;

        Unit current = units.get(currentIndex);

        System.out.println("\n===== " + current.name + "'s turn =====");

        //apply and expire effects
        current.processEffects();

        if (!current.isAlive()) {
            System.out.println(current.name + " has died!");
            advance();
            return;
        }

        //check stun
        boolean stunned = false;
        for (var effect : current.effects) {
            if (effect instanceof StunEffect) {
                stunned = true;
                break;
            }
        }

        if (stunned) {
            System.out.println(current.name + " is stunned and skips the turn!");
            advance();
            return;
        }

        //perform action
        Unit target = getNextAliveTarget();

        if (target != null) {
            System.out.println(current.name + " attacks " + target.name + "!");
            current.attack(target);
        } else {
            System.out.println("No targets remaining. Combat ends.");
        }

        advance();
    }

    private Unit getNextAliveTarget() {
        for (Unit u : units) {
            if (u != units.get(currentIndex) && u.isAlive())
                return u;
        }
        return null;
    }

    private void advance() {
        currentIndex = (currentIndex + 1) % units.size();
    }
}
