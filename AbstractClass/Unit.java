package AbstractClass;

import Skills.Skill;
import Status.StatusEffect;
import AI.AIStrategy; // Import Strategy
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Unit {
    public String name;
    protected int maxHp;
    protected int hp;
    protected int atk;
    protected int def;
    protected int shield = 0;

    protected final List<StatusEffect> statuses = new ArrayList<>();
    protected final List<Skill> skills = new ArrayList<>();
    
    // AI Strategy Field
    protected AIStrategy aiStrategy;

    public interface UnitEventListener {
        void onEvent(String message);
    }
    private UnitEventListener listener;

    public Unit(String name, int maxHp, int atk, int def) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.atk = atk;
        this.def = def;
    }

    // AI Strategy Getter/Setter
    public void setAIStrategy(AIStrategy strategy) {
        this.aiStrategy = strategy;
    }

    public AIStrategy getAIStrategy() {
        return aiStrategy;
    }

    public void setListener(UnitEventListener listener) {
        this.listener = listener;
    }

    protected void log(String msg) {
        if (listener != null) listener.onEvent(msg);
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
            log("[shield] " + name + " absorbs " + absorbed);
        }
        hp -= dmg;
        if (hp < 0) hp = 0;
        log("[hit] " + name + " takes " + dmg + " dmg (HP " + hp + "/" + maxHp + ")");
    }

    public void heal(int amount) {
        if (!isAlive()) return;
        hp += amount;
        if (hp > maxHp) hp = maxHp;
        log("[heal] " + name + " +" + amount + " HP");
    }

    public void addShield(int amount) {
        if (!isAlive()) return;
        shield += amount;
        if (shield > maxHp) shield = maxHp;
        log("[shield] " + name + " gains " + amount + " shield (S:" + shield + ")");
    }

    public void addStatus(StatusEffect s) {
        if (s == null) return;
        statuses.add(s);
        s.onApply(this);
    }

    public void processTurnStartStatuses() {
        if (!isAlive()) return;
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

    public void addSkill(Skill s) { if (s != null) skills.add(s); }
    public List<Skill> getSkills() { return skills; }

    public void tickSkillCooldowns() {
        for (Skill s : skills) s.tickCooldown();
    }

    public void basicAttack(Unit target) {
        if (!isAlive() || target == null || !target.isAlive()) return;
        log(name + " uses Basic Attack on " + target.getName());
        target.takeDamage(atk);
    }

    @Override
    public String toString() {
        String shieldStr = shield > 0 ? " [Shield:" + shield + "]" : "";
        return name + " HP:" + hp + "/" + maxHp + shieldStr;
    }
}