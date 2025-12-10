package UI;

import AbstractClass.Unit;
import Skills.Skill;

import java.util.List;
import java.util.Scanner;

/**
 * BattleConsoleUI — advanced console renderer with ANSI colors, bars, animations and input helpers.
 *
 * Paste this file as-is. It's intentionally long (~400 lines) to provide many UI niceties.
 */
public class BattleConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final StringBuilder logBuffer = new StringBuilder();

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    // small helpers to print with color
    private void printlnColor(String color, String s) { System.out.println(color + s + RESET); }
    private void printColor(String color, String s) { System.out.print(color + s + RESET); }
    private void println(String s) { System.out.println(s); logBuffer.append(s).append("\n"); }
    public void log(String s) { System.out.print(s); logBuffer.append(s); }

    public void clearScreen() {
        // Clear console (works in many terminals)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void displayHeader(int turn) {
        clearScreen();
        String header = BOLD + CYAN + "  ░░░ IMMUNE WAR ░░░   " + RESET;
        System.out.println(header);
        System.out.println(BOLD + " Turn: " + turn + RESET);
        System.out.println("============================================\n");
        // show recent log
        if (logBuffer.length() > 0) {
            System.out.println("-- Recent Log --");
            String[] lines = logBuffer.toString().split("\n");
            int start = Math.max(0, lines.length - 6);
            for (int i = start; i < lines.length; i++) System.out.println(lines[i]);
            System.out.println("--------------------------------------------");
        }
    }

    public void displayTeams(List<Unit> teamA, List<Unit> teamB) {
        // Print Team A and Team B side by side (approx) with bars
        System.out.println("\n[IMMUNE TEAM]");
        for (int i = 0; i < teamA.size(); i++) {
            Unit u = teamA.get(i);
            System.out.printf("%2d. %-18s %s\n", i, u.getName(), renderHpBar(u));
            System.out.printf("    ATK:%-3d DEF:%-3d %s\n", u.getAtk(), u.getDef(), renderSkillList(u));
        }
        System.out.println("\n[VIRUS TEAM]");
        for (int i = 0; i < teamB.size(); i++) {
            Unit u = teamB.get(i);
            System.out.printf("%2d. %-18s %s\n", i, u.getName(), renderHpBar(u));
            System.out.printf("    ATK:%-3d DEF:%-3d %s\n", u.getAtk(), u.getDef(), renderSkillList(u));
        }
        System.out.println();
    }

    private String renderHpBar(Unit u) {
        int barWidth = 28;
        int hp = Math.max(0, u.getHp());
        int max = Math.max(1, u.getMaxHp());
        int filled = (int) Math.round((hp / (double) max) * barWidth);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < barWidth; i++) {
            if (i < filled) sb.append(GREEN + "█" + RESET);
            else sb.append(" ");
        }
        sb.append("] ");
        sb.append(hp + "/" + max);
        if (u.getShield() > 0) sb.append(" " + CYAN + "(S:" + u.getShield() + ")" + RESET);
        if (!u.isAlive()) sb.append(" " + RED + "[DEAD]" + RESET);
        return sb.toString();
    }

    private String renderSkillList(Unit u) {
        StringBuilder sb = new StringBuilder();
        sb.append("Skills: ");
        for (Skill s : u.getSkills()) {
            String cd = s.getCooldownLeft() > 0 ? "(" + s.getCooldownLeft() + ")" : "";
            String name = s.getName();
            if (s.getCooldownLeft() > 0) sb.append(YELLOW).append(name).append(cd).append(RESET).append(" ");
            else sb.append(WHITE).append(name).append(RESET).append(" ");
        }
        return sb.toString();
    }

    // ================== INPUT HELPERS ==================
    public int askAction(Unit unit) {
        println(unit.getName() + " — Choose action:");
        println("[0] Basic Attack   [1] Use Skill   [2] Skip");
        println("Enter number: ");
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                int v = Integer.parseInt(line);
                if (v >=0 && v <=2) return v;
            } catch (NumberFormatException ignored) {}
            println("Invalid, try again: ");
        }
    }

    public Unit askTarget(List<Unit> enemies) {
        println("Choose target index:");
        for (int i = 0; i < enemies.size(); i++) {
            Unit u = enemies.get(i);
            String state = u.isAlive() ? "" : "(DEAD)";
            println("[" + i + "] " + u.getName() + " " + state);
        }
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(line);
                if (idx >= 0 && idx < enemies.size()) {
                    Unit sel = enemies.get(idx);
                    if (!sel.isAlive()) {
                        println("Target is dead, choose another.");
                        continue;
                    }
                    return sel;
                } else {
                    println("Index out of range.");
                }
            } catch (NumberFormatException ignored) {
                println("Please type a number.");
            }
        }
    }

    public Skill askSkill(Unit unit) {
        println("Choose skill (index) or -1 to cancel:");
        List<Skill> skills = unit.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            String name = s.getName();
            String cd = s.getCooldownLeft() > 0 ? " (CD " + s.getCooldownLeft() + ")" : "";
            println("[" + i + "] " + name + cd);
        }
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(line);
                if (idx == -1) return null;
                if (idx >= 0 && idx < skills.size()) {
                    Skill s = skills.get(idx);
                    if (!s.isReady()) { println("Skill on cooldown."); return null; }
                    return s;
                } else println("Invalid index.");
            } catch (NumberFormatException ignored) { println("Enter a number."); }
        }
    }

    // small animated message helper
    public void animateText(String s, int delayMillis) {
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(delayMillis); } catch (InterruptedException ignored) {}
        }
        System.out.println();
    }

    // convenience
    public void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
