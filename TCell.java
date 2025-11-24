package SmallClass;

import AbstractClass.Unit;

public class TCell extends Unit {
    public TCell(String name) {
        super(name, 120, 25); 
    }

    @Override
    public void attack(Unit target) {
        System.out.println(name + " attacks " + target.name + " for " + attackPower + " damage.");
        target.takeDamage(attackPower);
    }
     
}
