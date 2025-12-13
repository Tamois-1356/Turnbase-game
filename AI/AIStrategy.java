package AI;

import AbstractClass.Unit;
import TurnManager.ActionExecutor;
import java.util.List;

public interface AIStrategy {
    void executeTurn(Unit me, List<Unit> allies, List<Unit> enemies, ActionExecutor executor);
}