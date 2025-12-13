package Skills;

import AbstractClass.Unit;
import java.util.List;

public abstract class PassiveSkill extends Skill {
    public PassiveSkill(String name) { super(name, 0); }

    // Removed the "fake" use() override. It is no longer required by the parent class.

    public abstract void onTurnStart(Unit owner, List<Unit> allies, List<Unit> enemies);
    public abstract void onTurnEnd(Unit owner, List<Unit> allies, List<Unit> enemies);
}