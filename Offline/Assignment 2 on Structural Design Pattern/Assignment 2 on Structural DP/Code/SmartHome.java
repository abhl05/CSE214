import java.util.Timer;
import java.util.TimerTask;

interface NexaHomeAppliances {
    void activate();

    void deactivate();

    double getPower();

    String getStatus();

    void setAccessRestricted(int pin, boolean locked);

    void setTimerControlled(int timerSeconds);

    void setPowerThrottled(double powerCap);
}

class SmartLight implements NexaHomeAppliances {
    boolean on = false;
    // Pro upgrade flags
    boolean accessRestricted = false;
    private int pin = 0;
    boolean locked = false;
    boolean timerControlled = false;
    int timerSeconds = 0;
    boolean timerRunning = false;
    boolean powerThrottled = false;
    double powerCap = 0;

    @Override
    public void activate() {
        if (accessRestricted && locked) return;
        on = true;
        if (timerControlled) timerRunning = true;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                deactivate();
            }
        }, timerSeconds*1000);   
    }
    @Override
    public void deactivate() {
        if (accessRestricted && locked) return;
        on = false;
        timerRunning = false;
    }
    @Override
    public double getPower() {
        double p = on ? 10.0 : 0.0;
        if (powerThrottled && p > powerCap) p = powerCap;
        return p;
    }
    @Override
    public String getStatus() {
        String s = "Light: " + (on ? "ON" : "OFF");
        if (accessRestricted && locked) s += " [LOCKED]";
        if (timerControlled && timerRunning) s += " (auto-off in " + timerSeconds + "s)";
        if (powerThrottled && on && 10.0 > powerCap) s += " [throttled to " + powerCap + "W]";
        return s;
    }

    @Override
    public void setAccessRestricted(int pin, boolean locked) {
        if(!accessRestricted) {
            accessRestricted = true;
            this.pin = pin;
        }
        if (this.pin == pin) {
            this.locked = locked;
        }
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        if(!timerControlled) {
            timerControlled = true;
        }
        this.timerSeconds = timerSeconds;
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        if(!powerThrottled) {
            powerThrottled = true;
        }
        this.powerCap = powerCap;
    }
}

class Thermostat implements NexaHomeAppliances {
    boolean on = false;
    // Same flags copy-pasted from Light
    boolean accessRestricted = false;
    int pin = 0;
    boolean locked = false;
    boolean timerControlled = false;
    int timerSeconds = 0;
    boolean timerRunning = false;
    boolean powerThrottled = false;
    double powerCap = 0;

    @Override
    public void activate() {
        if (accessRestricted && locked) return;
        on = true;
        if (timerControlled) timerRunning = true;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                deactivate();
            }
        }, timerSeconds*1000);  
    }
    @Override
    public void deactivate() {
        if (accessRestricted && locked) return;
        on = false;
        timerRunning = false;
    }
    @Override
    public double getPower() {
        double p = on ? 150.0 : 0.0;
        if (powerThrottled && p > powerCap) p = powerCap;
        return p;
    }
    @Override
    public String getStatus() {
        String s = "Thermostat: " + (on ? "ON" : "OFF");
        if (accessRestricted && locked) s += " [LOCKED]";
        if (timerControlled && timerRunning) s += " (auto-off in " + timerSeconds + "s)";
        if (powerThrottled && on && 150.0 > powerCap) s += " [throttled to " + powerCap + "W]";
        return s;
    }

    @Override
    public void setAccessRestricted(int pin, boolean locked) {
        if(!accessRestricted) {
            accessRestricted = true;
            this.pin = pin;
        }
        if (this.pin == pin) {
            this.locked = locked;
        }
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        if(!timerControlled) {
            timerControlled = true;
        }
        this.timerSeconds = timerSeconds;
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        if(!powerThrottled) {
            powerThrottled = true;
        }
        this.powerCap = powerCap;
    }
}

class Speaker implements NexaHomeAppliances {
    boolean on = false;
    // Same flags AGAIN — copy-pasted a third time
    boolean accessRestricted = false;
    int pin = 0;
    boolean locked = false;
    boolean timerControlled = false;
    int timerSeconds = 0;
    boolean timerRunning = false;
    boolean powerThrottled = false;
    double powerCap = 0;

    @Override
    public void activate() {
        if (accessRestricted && locked) return;
        on = true;
        if (timerControlled) timerRunning = true;

        // no timer for speaker, can be implemented

        // new Timer().schedule(new TimerTask() {
        //     @Override
        //     public void run() {
        //         deactivate();
        //     }
        // }, timerSeconds*1000);  
    }
    @Override
    public void deactivate() {
        if (accessRestricted && locked) return;
        on = false;
        timerRunning = false;
    }
    @Override
    public double getPower() {
        double p = on ? 5.0 : 0.0;
        if (powerThrottled && p > powerCap) p = powerCap;
        return p;
    }
    @Override
    public String getStatus() {
        String s = "Speaker: " + (on ? "Playing" : "Idle");
        if (accessRestricted && locked) s += " [LOCKED]";
        if (timerControlled && timerRunning) s += " (auto-off in " + timerSeconds + "s)";
        if (powerThrottled && on && 5.0 > powerCap) s += " [throttled to " + powerCap + "W]";
        return s;
    }

    @Override
    public void setAccessRestricted(int pin, boolean locked) {
        if(!accessRestricted) {
            accessRestricted = true;
            this.pin = pin;
        }
        if (this.pin == pin) {
            this.locked = locked;
        }
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        if(!timerControlled) {
            timerControlled = true;
        }
        this.timerSeconds = timerSeconds;
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        if(!powerThrottled) {
            powerThrottled = true;
        }
        this.powerCap = powerCap;
    }
}

class BaseNexaApplianceDeco implements NexaHomeAppliances{
    NexaHomeAppliances wrapee;

    public BaseNexaApplianceDeco(NexaHomeAppliances c) {
        wrapee = c;
    }

    @Override
    public void setAccessRestricted(int pin, boolean locked) {
        wrapee.setAccessRestricted(pin, locked);
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        wrapee.setTimerControlled(timerSeconds);
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        wrapee.setPowerThrottled(powerCap);
    }
}








public class SmartHome {
    public static void main(String[] args) {
        
    }
}
