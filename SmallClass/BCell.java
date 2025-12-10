package SmallClass;

import AbstractClass.Unit;
import Skills.ActiveSkill;
import Status.ShieldEffect;

import java.util.List;

public class BCell extends Unit {
    public BCell(String name) {
        super(name, 100, 12, 3);

        // Rapid Fire - multi shot; ensure triggerCooldown called by ActiveSkill wrapper
        addSkill(new ActiveSkill("Rapid Fire", 3, (self, caster, target, allies, enemies) -> {
            if (target == null || !target.isAlive()) {
                System.out.println("No valid target for Rapid Fire.");
                return;
            }
            System.out.println(caster.getName() + " fires Rapid Fire!");
            int shots = 3;
            int perShot = Math.max(1, caster.getAtk() / 2);
            for (int i = 0; i < shots; i++) {
                if (!target.isAlive()) break;
                target.takeDamage(perShot);
                try { Thread.sleep(120); } catch (InterruptedException ignored) {}
            }
        }));

        // Antibody Shield - buff ALLIES (not enemies)
        addSkill(new ActiveSkill("Antibody Shield", 4, (self, caster, target, allies, enemies) -> {
            if (allies == null || allies.isEmpty()) {
                System.out.println("No allies to shield.");
                return;
            }
            System.out.println(caster.getName() + " casts Antibody Shield on allies!");
            for (Unit u : allies) {
                if (u.isAlive()) u.addStatus(new ShieldEffect(20,2));
            }
        }));
    }
}
