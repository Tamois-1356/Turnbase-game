package SmallClass;

import AbstractClass.Unit;

public class BCell extends Unit {

    public BCell(String name) {
        super(name, 100, 10);
    }

    @Override
    public void attack(Unit target) {
        System.out.println(name + " shoots antibodies at " + target.name + "!");
        target.takeDamage(attackPower);
    }
}
