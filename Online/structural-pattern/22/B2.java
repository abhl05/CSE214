// Target interface expected by the client
interface SmartDevice {
    void turnOn();
    void turnOff();
}

class SmartLight implements SmartDevice {
    @Override
    public void turnOn(){}
    public void turnOff(){}
}

class SmartFan implements SmartDevice {
    @Override
    public void turnOn(){}
    public void turnOff(){}
}

class SmartAC implements SmartDevice {
    @Override
    public void turnOn(){}
    public void turnOff(){}
}

// Adaptee — existing incompatible class (cannot modify)
class OldSmartBulb {
    public void powerOn() {
        System.out.println("OldSmartBulb powerOn");
    }
    public void powerOff() {
        System.out.println("OldSmartBulb powerOff");
    }
}

class LegacyHeater {
    public void startHeating() {
        System.out.println("LegacyHeater startHeating");
    }
    public void stopHeating() {
        System.out.println("LegacyHeater stopHeating");
    }
}


// Adapter — implements Target, wraps Adaptee internally
class OldSmartBulbAdapter implements SmartDevice {
    private OldSmartBulb oldSmartBulb;

    public OldSmartBulbAdapter(OldSmartBulb oldSmartBulb) {
        this.oldSmartBulb = oldSmartBulb;
    }

    @Override
    public void turnOn() {
        oldSmartBulb.powerOn(); // translate call
    }
    public void turnOff() {
        oldSmartBulb.powerOff(); // translate call
    }
}

class LegacyHeaterAdapter implements SmartDevice {
    private LegacyHeater legacyHeater;

    public LegacyHeaterAdapter(LegacyHeater legacyHeater) {
        this.legacyHeater = legacyHeater;
    }

    @Override
    public void turnOn() {
        legacyHeater.startHeating(); // translate call
    }
    public void turnOff() {
        legacyHeater.stopHeating(); // translate call
    }
}

public class B2 {
    public static void main(String[] args) {
        SmartDevice target = new OldSmartBulbAdapter(new OldSmartBulb());
        target.turnOn();
        target.turnOff();
    }
}