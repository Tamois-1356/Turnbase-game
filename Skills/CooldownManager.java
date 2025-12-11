package Skills;

import java.util.List;

public class CooldownManager {
    public static void tickAll(List<Skill> skills) {
        for (Skill s : skills) s.tickCooldown();
    }
}
