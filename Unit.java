package AbstractClass;

import Status.StatusEffect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Unit {
    public String name;
    protected int health;
    protected int attackPower;

    protected List<StatusEffect> effects = new ArrayList<>();

    public Unit(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public void addEffect(StatusEffect effect) {
        effects.add(effect);
        System.out.println(name + " gains effect: " + effect.getName());
    }

    public void processEffects() {
        Iterator<StatusEffect> iterator = effects.iterator();

        while (iterator.hasNext()) {
            StatusEffect eff = iterator.next();
            eff.apply(this);

            if (eff.isExpired()) {
                System.out.println(eff.getName() + " expired on " + name);
                iterator.remove();
            }
        }
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public abstract void attack(Unit target);

    @Override
    public String toString() {
        return name + " (HP: " + health + ", ATK: " + attackPower + ")";
    }
}
