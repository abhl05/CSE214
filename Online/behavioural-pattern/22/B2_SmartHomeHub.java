/*
 * ================================================================================
 * SOURCE: CSE 314 Sessional, 2022 batch, "Online on Behavioral Patterns"
 *         Subsection B2  |  Time allotted: 20 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * Build a Smart Home Automation Hub with devices: a Light Sensor, Automatic
 * Blinds, and an Air Conditioner. To keep the system organized, devices
 * must NOT talk to each other directly -- instead, they all report to a
 * Central Hub.
 *   - When the Light Sensor detects "High Brightness", it notifies the Hub.
 *     The Hub then tells the Blinds to close.
 *   - When the Blinds close, they notify the Hub. The Hub then tells the
 *     Air Conditioner to turn on (because the room will get stuffy once
 *     the blinds are shut).
 * Task: choose the appropriate pattern and implement a minimal
 * demonstration.
 *
 * ================================================================================
 * DESIGN PATTERN USED: MEDIATOR
 * ================================================================================
 *
 * WHY MEDIATOR IS THE CORRECT CHOICE:
 *   - The problem explicitly states devices "should not talk to each other
 *     directly" and must instead "report to the Central Hub" -- this is
 *     the Mediator pattern's defining premise almost verbatim: "define an
 *     object that encapsulates how a set of objects interact, promoting
 *     loose coupling by keeping objects from referring to each other
 *     explicitly."
 *   - Each device (Colleague) only knows about the Hub (Mediator), never
 *     about any other device. The Light Sensor has no reference to Blinds,
 *     and Blinds has no reference to the Air Conditioner -- all
 *     cross-device coordination logic lives centrally inside CentralHub.
 *   - This keeps the interaction rules ("high brightness -> close blinds",
 *     "blinds closed -> turn on AC") in ONE place, so the coordination
 *     logic can change (or grow, e.g. adding a Heater device later) without
 *     touching the device classes themselves.
 *   - Rejected alternative: Observer would only get us halfway -- it can
 *     make the Hub react to a device's event, but a pure Observer setup
 *     would still tempt devices to also observe each other directly for
 *     convenience. The problem specifically forbids direct device-to-device
 *     communication and wants a single coordinating object driving
 *     inter-device *interaction rules* -- centralising both notification
 *     AND the resulting cross-device commands is exactly what Mediator
 *     (not just Observer) is for.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Mediator          -> SmartHomeMediator (interface)
 *   ConcreteMediator   -> CentralHub (knows about all devices, contains all
 *                          the coordination rules between them)
 *   Colleague         -> Device (abstract base, holds a reference to its
 *                          mediator)
 *   ConcreteColleague  -> LightSensor, Blinds, AirConditioner
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Mediator: the interface every device uses to report events, without
// knowing which other devices exist or how they should react.
// ---------------------------------------------------------------------------
interface SmartHomeMediator {
    void notify(Device sender, String event);
}

// ---------------------------------------------------------------------------
// Colleague: common base for every device. A device only ever talks to its
// mediator -- never to another device directly.
// ---------------------------------------------------------------------------
abstract class Device {
    protected final SmartHomeMediator mediator;

    protected Device(SmartHomeMediator mediator) {
        this.mediator = mediator;
    }
}

// ---------------------------------------------------------------------------
// ConcreteColleague #1
// ---------------------------------------------------------------------------
class LightSensor extends Device {
    public LightSensor(SmartHomeMediator mediator) {
        super(mediator);
    }

    public void detectHighBrightness() {
        System.out.println("LightSensor: detected High Brightness.");
        // Reports the event to the hub -- does NOT call Blinds directly.
        mediator.notify(this, "HighBrightness");
    }
}

// ---------------------------------------------------------------------------
// ConcreteColleague #2
// ---------------------------------------------------------------------------
class Blinds extends Device {
    public Blinds(SmartHomeMediator mediator) {
        super(mediator);
    }

    public void close() {
        System.out.println("Blinds: closing.");
        // Reports the event to the hub -- does NOT call AirConditioner directly.
        mediator.notify(this, "BlindsClosed");
    }
}

// ---------------------------------------------------------------------------
// ConcreteColleague #3
// ---------------------------------------------------------------------------
class AirConditioner extends Device {
    public AirConditioner(SmartHomeMediator mediator) {
        super(mediator);
    }

    public void turnOn() {
        System.out.println("AirConditioner: turning ON (room will get stuffy with blinds closed).");
    }
}

// ---------------------------------------------------------------------------
// ConcreteMediator: the Central Hub. This is the ONLY class that knows
// about every device and contains all the cross-device coordination rules.
// ---------------------------------------------------------------------------
class CentralHub implements SmartHomeMediator {
    private Blinds blinds;
    private AirConditioner airConditioner;

    // Devices register themselves with the hub after construction, since
    // each device needs the hub reference (mediator) and the hub needs
    // device references -- so wiring happens once, here.
    public void registerBlinds(Blinds blinds) {
        this.blinds = blinds;
    }

    public void registerAirConditioner(AirConditioner airConditioner) {
        this.airConditioner = airConditioner;
    }

    @Override
    public void notify(Device sender, String event) {
        if (sender instanceof LightSensor && event.equals("HighBrightness")) {
            System.out.println("CentralHub: High brightness reported -> instructing Blinds to close.");
            blinds.close();
        } else if (sender instanceof Blinds && event.equals("BlindsClosed")) {
            System.out.println("CentralHub: Blinds closed -> instructing Air Conditioner to turn on.");
            airConditioner.turnOn();
        }
        // Adding a new coordination rule (e.g. "AC on -> dim smart lights")
        // means adding one more branch here -- no device class changes.
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class B2_SmartHomeHub {
    public static void main(String[] args) {
        CentralHub hub = new CentralHub();

        LightSensor lightSensor = new LightSensor(hub);
        Blinds blinds = new Blinds(hub);
        AirConditioner airConditioner = new AirConditioner(hub);

        hub.registerBlinds(blinds);
        hub.registerAirConditioner(airConditioner);

        // A single device event triggers the whole coordinated chain,
        // entirely orchestrated by the hub:
        // LightSensor -> Hub -> Blinds -> Hub -> AirConditioner
        lightSensor.detectHighBrightness();
    }
}
