import AbstractClass.Unit;
import Factory.UnitFactory;
import Status.PoisonEffect;

public class Main {
    public static void main(String[] args) {
        Unit tcell = UnitFactory.createUnit("tcell", "T-Killer");
        Unit bcell = UnitFactory.createUnit("bcell", "B-Helper");

        bcell.addEffect(new PoisonEffect(5, 3));

        for (int turn = 1; turn <= 4; turn++) {
            System.out.println("\n--- Turn " + turn + " ---");
            bcell.processEffects();
            System.out.println(bcell);
        }
    }
}
