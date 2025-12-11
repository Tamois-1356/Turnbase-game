package Skills;

import AbstractClass.Unit;
import java.util.List;

public abstract class PassiveSkill extends Skill {
    public PassiveSkill(String name) { super(name, 0); }

    @Override
    public void use(Unit caster, Unit primaryTarget, List<Unit> allies, List<Unit> enemies) {
        // not invokable via use
    }

    public abstract void onTurnStart(Unit owner, List<Unit> allies, List<Unit> enemies);
    public abstract void onTurnEnd(Unit owner, List<Unit> allies, List<Unit> enemies);
}
