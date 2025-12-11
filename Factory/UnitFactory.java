package Factory;

import AbstractClass.Unit;
import SmallClass.BCell;
import SmallClass.TCell;

public class UnitFactory {
    public static Unit createUnit(String type, String name) {
        if (type == null) throw new IllegalArgumentException("type null");
        switch (type.toLowerCase()) {
            case "tcell" -> {
                return new TCell(name);
            }
            case "bcell" -> {
                return new BCell(name);
            }
            default -> throw new IllegalArgumentException("Unknown unit type: " + type);
        }
    }
}
