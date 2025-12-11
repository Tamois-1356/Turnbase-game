package UI;

import AbstractClass.Unit;

public class UnitIconProvider {
    
    public static String getIcon(Unit unit) {
        String name = unit.getName().toLowerCase();
        
        if (name.contains("tcell") || name.contains("t-")) {
            return "T";
        } else if (name.contains("bcell") || name.contains("b-")) {
            return "B";
        } else if (name.contains("virus")) {
            return "V";
        }
        
        return "X";
    }
}