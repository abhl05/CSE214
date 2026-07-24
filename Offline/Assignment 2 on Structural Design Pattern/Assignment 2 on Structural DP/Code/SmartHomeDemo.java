import java.util.*;

interface NexaInterface {
    void setGuestMode(boolean guestMode, Set<String> guestAllowed);

    void setEcoMode(boolean ecoMode, double ecoBudget);
}

class Home implements NexaInterface {
    String name;
    List<Room> roomsList = new ArrayList<>();
    // Home-level eco/guest — duplicated from Room
    boolean ecoMode = false;
    double ecoBudget = 0;
    boolean guestMode = false;
    Set<String> guestAllowed = new HashSet<>();

    Home(String name) { this.name = name; }
    void addRoom(Room r) { roomsList.add(r); }

    void activate() {
        for (Room r : roomsList) r.activate();
        // Home-level eco — completely separate logic from Room-level eco
        if (ecoMode && getPowerUsage() > ecoBudget) {
            // Shed entire rooms in reverse order... ugly
            for (int i = roomsList.size() - 1; i >= 0 && getPowerUsage() > ecoBudget; i--) {
                roomsList.get(i).deactivate();
            }
        }
    }

    void deactivate() {
        for (Room r : roomsList) r.deactivate();
    }

    double getPowerUsage() {
        double total = 0;
        for (Room r : roomsList) total += r.getPowerUsage();
        return total;
    }

    String getStatus() {
        StringBuilder sb = new StringBuilder("=== " + name + " ===");
        if (ecoMode) sb.insert(0, "[ECO: " + ecoBudget + "W budget]\n");
        if (guestMode) sb.insert(0, "[GUEST MODE]\n");
        for (Room r : roomsList) sb.append("\n").append(r.getStatus());
        return sb.toString();
    }
    @Override
    public void setGuestMode(boolean guestMode, Set<String> guestAllowed) {
        this.guestMode = guestMode;
        this.guestAllowed = guestAllowed;
        for(Room r : roomsList) {
            r.setGuestMode(guestMode, guestAllowed);
        }
    }
    @Override
    public void setEcoMode(boolean ecoMode, double ecoBudget) {
        this.ecoMode = ecoMode;
        this.ecoBudget = ecoBudget;
        for(Room r : roomsList) {
            r.setEcoMode(ecoMode, ecoBudget);
        }
    }
}

class Room implements NexaInterface {
    String name;

    List<SmartDevice> devicesList = new ArrayList<>();

    // Room-level enhancement flags
    boolean ecoMode = false;
    double ecoBudget = 0;
    boolean guestMode = false;
    Set<String> guestAllowed = new HashSet<>(); // "light", "thermostat", "speaker"

    Room(String name) { this.name = name; }

    void addDevice(SmartDevice d) {
        devicesList.add(d);
    }

    void activate() {
        for (SmartDevice d : devicesList) {
            if(guestMode && guestAllowed.contains(d.getClass().getName().toLowerCase())) continue;
            d.activate();
        }

        // EcoMode: shed in reverse insertion order
        if (ecoMode && getPowerUsage() > ecoBudget) {
            for (int i = devicesList.size() - 1; i >= 0 && getPowerUsage() > ecoBudget; i--) {
                SmartDevice dev = devicesList.get(i);
                dev.deactivate();
                System.out.println("    >> EcoMode: shed [" + dev.getStatus() + "]");
            }
        }
    }

    void deactivate() {
        for (SmartDevice d : devicesList) d.deactivate();
    }

    double getPowerUsage() {
        double total = 0;
        for(SmartDevice d : devicesList) {
            total += d.getPowerUsage();
        }
        return total;
    }

    String getStatus() {
        StringBuilder sb = new StringBuilder("[" + name + "]");
        if (ecoMode) sb.insert(0, "[ECO: " + ecoBudget + "W budget]\n");
        if (guestMode) sb.insert(0, "[GUEST MODE]\n");

        // Can't just loop "devices" — have to loop each list separately
        for (SmartDevice dev : devicesList) {
            String className = dev.getClass().getName().toLowerCase();
            sb.append("\n  ").append(dev.getStatus());
            if (guestMode && !guestAllowed.contains(className))
                sb.append(" [guest-restricted]");
        }
        
        return sb.toString();
    }

    @Override
    public void setGuestMode(boolean guestMode, Set<String> guestAllowed) {
        this.guestMode = guestMode;
        this.guestAllowed = guestAllowed;
        for(SmartDevice d : devicesList) {
            String className = d.getClass().getName().toLowerCase();
            if(guestAllowed.contains(className)) {
                d.guestAccess = false;
            } 
        }
    }

    @Override
    public void setEcoMode(boolean ecoMode, double ecoBudget) {
        this.ecoMode = ecoMode;
        this.ecoBudget = ecoBudget;
    }
}

abstract class SmartDevice {
    // Pro upgrade flags
    boolean accessRestricted;
    int pin;
    boolean locked;
    boolean timerControlled;
    int timerSeconds;
    boolean timerRunning;
    boolean powerThrottled;
    double powerCap;
    boolean guestAccess;

    abstract public void activate();

    abstract public void deactivate();

    abstract public double getPowerUsage();

    abstract public String getStatus();

    abstract void setAccessRestricted(int pin, boolean locked);

    abstract void setTimerControlled(int timerSeconds);

    abstract void setPowerThrottled(double powerCap);
}

class SmartLight extends SmartDevice {
    boolean on = false;
    boolean guestAccess = true;
    
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
    public double getPowerUsage() {
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
        accessRestricted = false;
        pin = 0;
        locked = false;
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        timerControlled = false;
        timerSeconds = 0;
        timerRunning = false;
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        powerThrottled = false;
        powerCap = 0;
    }
}

class SmartThermostat extends SmartDevice {
    boolean on = false;
    boolean guestAccess = true;

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
    public double getPowerUsage() {
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
        accessRestricted = false;
        pin = 0;
        locked = false;
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        timerControlled = false;
        timerSeconds = 0;
        timerRunning = false;
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        powerThrottled = false;
        powerCap = 0;
    }
}

class SmartSpeaker extends SmartDevice {
    boolean on = false;
    boolean guestAccess = true;

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
    public double getPowerUsage() {
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
        accessRestricted = false;
        pin = 0;
        locked = false;
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        timerControlled = false;
        timerSeconds = 0;
        timerRunning = false;
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        powerThrottled = false;
        powerCap = 0;
    }
}

class BaseDeco extends SmartDevice{
    SmartDevice decoDevice;

    public BaseDeco(SmartDevice c) {
        decoDevice = c;
    }

    @Override
    public void setAccessRestricted(int pin, boolean locked) {
        decoDevice.setAccessRestricted(pin, locked);
    }

    @Override
    public void setTimerControlled(int timerSeconds) {
        decoDevice.setTimerControlled(timerSeconds);
    }

    @Override
    public void setPowerThrottled(double powerCap) {
        decoDevice.setPowerThrottled(powerCap);
    }

    @Override
    public void activate() {
        decoDevice.activate();
    }

    @Override
    public void deactivate() {
        decoDevice.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return decoDevice.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return decoDevice.getStatus();
    }
    }
class AccessRestricted extends BaseDeco {

    public AccessRestricted(SmartDevice c, int pin) {
        super(c);
        if(!accessRestricted) {
            decoDevice.accessRestricted = true;
            decoDevice.pin = pin;
        }
    }

    public void unlock(int pin) {
        if(decoDevice.pin == pin) {
            decoDevice.accessRestricted = false;
        }
    }
}

class TimerControlled extends BaseDeco {

    public TimerControlled(SmartDevice c, int timerSeconds) {
        super(c);
        if(!timerControlled) {
            timerControlled = true;
        }
        decoDevice.timerSeconds = timerSeconds;
    }

    public void simulateTimerExpiry() {
        decoDevice.timerSeconds = 0;
        decoDevice.timerControlled = false;
        decoDevice.deactivate();
    }
}

class PowerThrottled extends BaseDeco {

    public PowerThrottled(SmartDevice c, double powerCap) {
        super(c);
        if(!powerThrottled) {
            powerThrottled = true;
        }
        decoDevice.powerCap = powerCap;
    }
}

class EcoMode extends BaseDeco {

    public EcoMode() {
        super(null);
    }
    
}









public class SmartHomeDemo {


}
