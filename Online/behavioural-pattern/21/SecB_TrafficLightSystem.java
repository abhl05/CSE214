/*
 * ================================================================================
 * SOURCE: CSE-214, 2021 batch, "Online 3" (Sec B)  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * Build a traffic light system that cycles through three colours:
 *   - Starts RED, stays for 5 seconds.
 *   - Switches to YELLOW, stays for 2 seconds.
 *   - Switches to GREEN, stays for 10 seconds.
 *   - Switches back to RED, and the cycle repeats forever.
 * The design should make the light "efficient and easy to manage".
 * (Provided timing snippet: Thread.sleep(1000) inside a try/catch for
 * InterruptedException -- used below, scaled per colour's duration.)
 *
 * ================================================================================
 * DESIGN PATTERN USED: STATE
 * ================================================================================
 *
 * WHY STATE IS THE CORRECT CHOICE:
 *   - The traffic light's behaviour (how long it stays lit, and which
 *     colour comes next) depends entirely on which colour is CURRENTLY
 *     active. That is precisely what the State pattern models: an object
 *     (the TrafficLight) whose behaviour changes based on its internal
 *     state, and each state knows how to transition to the next one.
 *   - Each colour is a self-contained unit of behaviour (its own wait
 *     duration + its own "what comes next" rule). Modelling each colour as
 *     its own class keeps this logic isolated and easy to manage/extend --
 *     e.g. adding a flashing-yellow "caution" state later only means
 *     adding one new class, with zero changes to the others. This
 *     directly satisfies the "efficient and easy to manage" requirement.
 *   - The transition rule ("after RED comes YELLOW", "after YELLOW comes
 *     GREEN", ...) is naturally expressed as each ConcreteState object
 *     handing control to the NEXT ConcreteState object -- the classic
 *     State-machine implementation technique.
 *   - Rejected alternative: Strategy would apply if we were picking between
 *     different, mutually exclusive ALGORITHMS for one fixed operation
 *     (e.g. different lighting *schedules* chosen by an operator). Here,
 *     instead, there is one single, automatic, self-driven progression
 *     through a fixed cycle of internal states -- that is State's job.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   State             -> LightState (interface)
 *   ConcreteState      -> RedState, YellowState, GreenState
 *   Context           -> TrafficLight (holds current LightState, exposes
 *                          run()/change(), delegates the "what happens
 *                          while lit + what comes next" logic to the state)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// State: every colour implements this. `handle` performs the "stay lit for
// N seconds" behaviour and then transitions the context to the next state.
// ---------------------------------------------------------------------------
interface LightState {
    void handle(TrafficLight light);
    String getColorName();
}

// ---------------------------------------------------------------------------
// ConcreteState: RED -- stays 5 seconds, then transitions to YELLOW.
// ---------------------------------------------------------------------------
class RedState implements LightState {
    private static final int DURATION_SECONDS = 5;

    @Override
    public void handle(TrafficLight light) {
        System.out.println("Light is RED. Staying for " + DURATION_SECONDS + " seconds.");
        sleepSeconds(DURATION_SECONDS);
        light.setState(new YellowState());
    }

    @Override
    public String getColorName() { return "RED"; }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L); // provided timing snippet, scaled per state
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ---------------------------------------------------------------------------
// ConcreteState: YELLOW -- stays 2 seconds, then transitions to GREEN.
// ---------------------------------------------------------------------------
class YellowState implements LightState {
    private static final int DURATION_SECONDS = 2;

    @Override
    public void handle(TrafficLight light) {
        System.out.println("Light is YELLOW. Staying for " + DURATION_SECONDS + " seconds.");
        sleepSeconds(DURATION_SECONDS);
        light.setState(new GreenState());
    }

    @Override
    public String getColorName() { return "YELLOW"; }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ---------------------------------------------------------------------------
// ConcreteState: GREEN -- stays 10 seconds, then transitions back to RED,
// completing the cycle.
// ---------------------------------------------------------------------------
class GreenState implements LightState {
    private static final int DURATION_SECONDS = 10;

    @Override
    public void handle(TrafficLight light) {
        System.out.println("Light is GREEN. Staying for " + DURATION_SECONDS + " seconds.");
        sleepSeconds(DURATION_SECONDS);
        light.setState(new RedState());
    }

    @Override
    public String getColorName() { return "GREEN"; }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ---------------------------------------------------------------------------
// Context: the traffic light itself. It knows nothing about durations or
// colour ordering -- it just holds "whatever the current state is" and
// asks that state to handle itself. This is what makes the system easy to
// manage: TrafficLight never needs to change when the cycle rules change.
// ---------------------------------------------------------------------------
class TrafficLight {
    private LightState currentState;

    public TrafficLight() {
        this.currentState = new RedState(); // system starts RED
    }

    void setState(LightState newState) {
        this.currentState = newState;
    }

    public String getCurrentColor() {
        return currentState.getColorName();
    }

    // Runs one state's "stay lit + transition" step.
    public void tick() {
        currentState.handle(this);
    }

    // Runs the light for a fixed number of colour changes (used for the
    // demo below so it terminates instead of looping forever).
    public void runCycles(int numberOfColorChanges) {
        for (int i = 0; i < numberOfColorChanges; i++) {
            tick();
        }
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class SecB_TrafficLightSystem {
    public static void main(String[] args) {
        TrafficLight light = new TrafficLight();
        System.out.println("Traffic light system started. Initial color: " + light.getCurrentColor());

        // Run one full cycle (RED -> YELLOW -> GREEN -> RED) to demonstrate
        // the automatic state transitions. In a real deployment this loop
        // would simply run forever (e.g. `while (true) light.tick();`).
        light.runCycles(4);

        System.out.println("Demo finished. Current color: " + light.getCurrentColor());
    }
}
