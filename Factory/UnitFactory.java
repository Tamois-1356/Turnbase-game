package Factory;

import AbstractClass.Unit;
import SmallClass.BCell;
import SmallClass.TCell;
import AI.AggressiveStrategy;
import AI.SmartSupportStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UnitFactory {
    private static final Map<String, Function<String, Unit>> registry = new HashMap<>();

    static {
        // T-Cells are fighters, give them Aggressive AI
        registerUnit("tcell", (name) -> {
            Unit u = new TCell(name);
            u.setAIStrategy(new AggressiveStrategy());
            return u;
        });
        
        // B-Cells are supporters, give them Smart Support AI
        registerUnit("bcell", (name) -> {
            Unit u = new BCell(name);
            u.setAIStrategy(new SmartSupportStrategy());
            return u;
        });
        
        // Example: If you treat Virus as TCells for now (as per original code), 
        // they also get AggressiveStrategy.
        // If you make a specific 'Virus' class later, you can give it a 'ZombieStrategy'.
    }

    public static void registerUnit(String type, Function<String, Unit> creator) {
        registry.put(type.toLowerCase(), creator);
    }

    public static Unit createUnit(String type, String name) {
        if (type == null) throw new IllegalArgumentException("Type cannot be null");
        
        Function<String, Unit> creator = registry.get(type.toLowerCase());
        if (creator == null) {
            throw new IllegalArgumentException("Unknown unit type: " + type);
        }
        
        return creator.apply(name);
    }
}