import java.util.*;
interface SmartDevice {
    void activate();
    void deactivate();
    double getPowerUsage();
    String getStatus();
}

interface DeviceGroup extends SmartDevice {
    List<SmartDevice> getChildren();
}

class SmartLight implements SmartDevice {
    private boolean on = false;

    @Override
    public void activate() {
        this.on = true;
    }

    @Override
    public void deactivate() {
        this.on = false;
    }

    @Override
    public double getPowerUsage() {
        return on ? 10.0 : 0.0; 
    }

    @Override
    public String getStatus() {
        return "Light: " + (on ? "ON" : "OFF");
    }
}

class SmartThermostat implements SmartDevice {
    private boolean on = false;

    @Override
    public void activate() {
        this.on = true;
    }

    @Override
    public void deactivate() {
        this.on = false;
    }

    @Override
    public double getPowerUsage() {
        return on ? 150.0 : 0.0;
    }

    @Override
    public String getStatus() {
        return "Thermostat: " + (on ? "ON" : "OFF"); 
    }

}


class SmartSpeaker implements SmartDevice {
    private boolean on = false;

    @Override
    public void activate() {
        this.on = true;
    }

    @Override
    public void deactivate() {
        this.on = false;
    }

    @Override
    public double getPowerUsage() {
        return on ? 5.0 : 0.0;
    }

    @Override
    public String getStatus() {
        return "Speaker: " + (on ? "Playing" : "Idle");
    }
    
}

class Room implements DeviceGroup {
    private final String name;
    private final List<SmartDevice> children = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public void addDevice(SmartDevice d) {
        children.add(d);
    }

    @Override
    public List<SmartDevice> getChildren() {
        return children;
    }

    @Override
    public void activate() {
        for(SmartDevice d : children) {
            d.activate();
        }
    }

    @Override
    public void deactivate() {
        for(SmartDevice d : children) {
            d.deactivate();
        }
    }

    @Override
    public double getPowerUsage() {
        double total = 0;
        for(SmartDevice d : children) {
            total += d.getPowerUsage();
        }
        return total;
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder("[" + name + "]");
        for (SmartDevice d : children) {
            sb.append("\n  ").append(d.getStatus());
        }
        return sb.toString();
    }
}

class Home implements DeviceGroup {
    private final String name;
    private final List<SmartDevice> rooms = new ArrayList<>();

    public Home(String name) {
        this.name = name;
    }

    public void addRoom(SmartDevice r) {
        rooms.add(r);
    }

    @Override
    public void activate() {
        for(SmartDevice r : rooms) {
            r.activate();
        }
    }

    @Override
    public void deactivate() {
        for(SmartDevice r : rooms) {
            r.deactivate();
        }
    }

    @Override
    public double getPowerUsage() {
        double total = 0;
        for (SmartDevice r : rooms) {
            total += r.getPowerUsage();
        }
        return total;
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder("=== " + name + " ===");
        for (SmartDevice r : rooms) {
            sb.append("\n").append(r.getStatus());
        }
        return sb.toString();
    }

    @Override
    public List<SmartDevice> getChildren() {
        return rooms;
    }
}

abstract class DeviceDecorator implements SmartDevice {
    protected final SmartDevice wrappee;

    public DeviceDecorator(SmartDevice d) {
        this.wrappee = d;
    }

    @Override
    public void activate() {
        wrappee.activate();
    }

    @Override
    public void deactivate() {
        wrappee.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return wrappee.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return wrappee.getStatus();
    }

    public SmartDevice getWrapped() { return wrappee; }
}

class AccessRestricted extends DeviceDecorator {
    private final int pin;
    private boolean locked = true;

    public AccessRestricted(SmartDevice d, int pin) {
        super(d);
        this.pin = pin;
    }

    public void unlock(int pin) {
        if (this.pin == pin) {
            locked = false;
        }
    }

    @Override
    public void activate() {
        if(locked) return;
        super.activate();
    }

    @Override
    public void deactivate() {
        if(locked) return;
        super.deactivate();
    }

    @Override
    public String getStatus() {
        String status = super.getStatus();
        return locked ? status + " [LOCKED]" : status;
    }
}

class TimerControlled extends DeviceDecorator {
    private final int timerSeconds;
    private boolean timerRunning = false;

    public TimerControlled(SmartDevice d, int timerSeconds) {
        super(d);
        this.timerSeconds = timerSeconds;
    }

    @Override
    public void activate() {
        super.activate();
        timerRunning = true;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        timerRunning = false;
    }

    public void simulateTimerExpiry() {
        if (timerRunning) {
            wrappee.deactivate();
            timerRunning = false;
        }
    }

    @Override
    public String getStatus() {
        String status = super.getStatus();
        return timerRunning ? status + " (auto-off in " + timerSeconds + "s)" : status;
    }
}

class PowerThrottled extends DeviceDecorator {
    private final double cap;
 
    public PowerThrottled(SmartDevice d, double cap) {
        super(d);
        this.cap = cap;
    }
 
    @Override
    public double getPowerUsage() {
        return Math.min(super.getPowerUsage(), cap);
    }
 
    @Override
    public String getStatus() {
        String status = super.getStatus();
        double raw = super.getPowerUsage();
        if (raw > cap) status += " [throttled to " + cap + "W]";
        return status;
    }
}

class EcoMode implements DeviceGroup {
    private final DeviceGroup group;
    private final double powerBudget;

    public EcoMode(DeviceGroup group, double powerBudget) {
        this.group = group;
        this.powerBudget = powerBudget;
    }

    @Override
    public List<SmartDevice> getChildren() {
        return group.getChildren();
    }

    @Override
    public void activate() {
        group.activate();
        List<SmartDevice> children = group.getChildren();

        for (int i = children.size() - 1; i >= 0 && group.getPowerUsage() > powerBudget; i--) {
            children.get(i).deactivate();
        }
    }

    @Override
    public void deactivate() {
        group.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return group.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return "[ECO: " + powerBudget + "W budget]\n" + group.getStatus();
    }
}

class GuestMode implements DeviceGroup {
    private final DeviceGroup group;
    private final Set<Class<?>> guestAllowed;

    public GuestMode(DeviceGroup group, Set<Class<?>> guestAllowed) {
        this.group = group;
        this.guestAllowed = guestAllowed;
    }

    private boolean isAllowed(SmartDevice device) {
        SmartDevice current = device;
        while (current instanceof DeviceDecorator) {
            current = ((DeviceDecorator) current).getWrapped();
        }
        return guestAllowed.contains(current.getClass());
    }

    @Override
    public void activate() {
        for (SmartDevice child : group.getChildren()) {
            if (isAllowed(child)) child.activate(); // others silently skipped
        }
    }
 
    @Override
    public void deactivate() {
        group.deactivate();
    }
 
    @Override
    public double getPowerUsage() {
        double total = 0;
        for (SmartDevice child : group.getChildren()) {
            if (isAllowed(child)) total += child.getPowerUsage();
        }
        return total;
    }
 
    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder("[GUEST MODE]");
        for (SmartDevice child : group.getChildren()) {
            sb.append("\n  ").append(child.getStatus());
            if (!isAllowed(child)) sb.append(" [guest-restricted]");
        }
        return sb.toString();
    }
 
    @Override
    public List<SmartDevice> getChildren() {
        return group.getChildren();
    }
}

public class SmartHomeDemo {
    public static void main(String[] args) {
        System.out.println("=== Order sensitivity demo ===");
 
        // Setup 1: throttle the thermostat to 80W BEFORE eco-wrapping the room.
        Room room1 = new Room("Lab-1");
        room1.addDevice(new SmartLight());                              // 10W
        room1.addDevice(new SmartLight());                              // 10W
        room1.addDevice(new PowerThrottled(new SmartThermostat(), 80));  // capped 80W
        SmartDevice eco1 = new EcoMode(room1, 100);
        eco1.activate();
        System.out.println("Throttled-then-Eco: " + eco1.getPowerUsage() + "W");
        System.out.println(eco1.getStatus());
 
        // Setup 2: same devices, but a raw (unthrottled) thermostat.
        Room room2 = new Room("Lab-2");
        room2.addDevice(new SmartLight());       // 10W
        room2.addDevice(new SmartLight());       // 10W
        room2.addDevice(new SmartThermostat());  // 150W raw
        SmartDevice eco2 = new EcoMode(room2, 100);
        eco2.activate();
        System.out.println("\nRaw-then-Eco: " + eco2.getPowerUsage() + "W");
        System.out.println(eco2.getStatus());
 
        System.out.println("\n>> Same devices, same budget, different order of "
                + "enhancement -> different outcome (" + eco1.getPowerUsage()
                + "W vs " + eco2.getPowerUsage() + "W).");
    }

}
