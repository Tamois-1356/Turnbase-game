package Status;

import AbstractClass.Unit;

public class StunEffect implements StatusEffect {
    private int duration;

    public StunEffect(int duration) {
        this.duration = duration;
    }
    
    @Override
    public void apply(Unit target) {
        if (duration > 0) {
            System.out.println(target.name + " is stunned and cannot act!");
            duration--;
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }   

    @Override
    public String getName() {
        return "Stun";
    }
}
