package SmallClass;

import AbstractClass.Unit;
import Skills.ActiveSkill;
import Skills.PassiveSkill;
import Status.RegenEffect;

import java.util.List;

public class TCell extends Unit {
    public TCell(String name) {
        super(name, 120, 25, 5);

        // Piercing Strike - heavy damage single-target
        addSkill(new ActiveSkill("Piercing Strike", 2, (self, caster, target, allies, enemies) -> {
            if (target == null || !target.isAlive()) {
                System.out.println("No valid target.");
                return;
            }
            System.out.println(caster.getName() + " uses Piercing Strike!");
            target.takeDamage(40);
        }));

        // Passive regen
        addSkill(new PassiveSkill("Natural Regen") {
            @Override
            public void onTurnStart(Unit owner, List<Unit> allies, List<Unit> enemies) {
                owner.addStatus(new RegenEffect(4,1));
            }
            @Override
            public void onTurnEnd(Unit owner, List<Unit> allies, List<Unit> enemies) { }
        });
    }
}
