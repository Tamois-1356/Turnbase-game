package AbstractClass;

import Skills.Skill;
import Status.StatusEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Core Unit class
 */
public abstract class Unit {
    public String name;
    protected int maxHp;
    protected int hp;
    protected int atk;
    protected int def;
    protected int shield = 0;

    protected final List<StatusEffect> statuses = new ArrayList<>();
    protected final List<Skill> skills = new ArrayList<>();

    public Unit(String name, int maxHp, int atk, int def) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.atk = atk;
        this.def = def;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getShield() { return shield; }
    public boolean isAlive() { return hp > 0; }

    public void takeDamage(int rawAmount) {
        if (!isAlive()) return;
        int dmg = Math.max(0, rawAmount - def);
        if (shield > 0) {
            int absorbed = Math.min(shield, dmg);
            shield -= absorbed;
            dmg -= absorbed;
            System.out.println("[shield] " + name + " absorbs " + absorbed);
        }
        hp -= dmg;
        if (hp < 0) hp = 0;
        System.out.println("[hit] " + name + " takes " + dmg + " dmg (HP " + hp + "/" + maxHp + ")");
    }

    public void heal(int amount) {
        if (!isAlive()) return;
        hp += amount;
        if (hp > maxHp) hp = maxHp;
        System.out.println("[heal] " + name + " +" + amount + " HP");
    }

    public void addShield(int amount) {
        if (!isAlive()) return;
        shield += amount;
        if (shield > maxHp) shield = maxHp;
        System.out.println("[shield] " + name + " gains " + amount + " shield (S:" + shield + ")");
    }

    // status management
    public void addStatus(StatusEffect s) {
        if (s == null) return;
        statuses.add(s);
        s.onApply(this);
    }

    public void processTurnStartStatuses() {
        if (!isAlive()) return;
        // copy to avoid modification during iteration
        for (StatusEffect s : List.copyOf(statuses)) s.onTurnStart(this);
        removeExpiredStatuses();
    }

    public void processTurnEndStatuses() {
        if (!isAlive()) return;
        for (StatusEffect s : List.copyOf(statuses)) s.onTurnEnd(this);
        removeExpiredStatuses();
    }

    public void removeExpiredStatuses() {
        Iterator<StatusEffect> it = statuses.iterator();
        while (it.hasNext()) {
            StatusEffect s = it.next();
            if (s.isExpired()) {
                s.onExpire(this);
                it.remove();
            }
        }
    }

    // skills
    public void addSkill(Skill s) { if (s != null) skills.add(s); }
    public List<Skill> getSkills() { return skills; }

    public void tickSkillCooldowns() {
        for (Skill s : skills) s.tickCooldown();
    }

    public void basicAttack(Unit target) {
        if (!isAlive() || target == null || !target.isAlive()) return;
        System.out.println(name + " uses Basic Attack on " + target.getName());
        target.takeDamage(atk);
    }

    @Override
    public String toString() {
        String shieldStr = shield > 0 ? " [Shield:" + shield + "]" : "";
        return name + " HP:" + hp + "/" + maxHp + shieldStr;
    }
}
