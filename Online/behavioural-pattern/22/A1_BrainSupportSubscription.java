/*
 * ================================================================================
 * SOURCE: CSE 314 Sessional, 2022 batch, "Online on Behavioral Patterns"
 *         Subsection A1  |  Time allotted: 30 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * A brain-support subscription (for patients with severe brain injury) has
 * three tiers: Common, Plus, Lux. Each tier provides a coverage radius from
 * regional server towers, which determines whether the patient stays
 * responsive while travelling:
 *   - Common: safe range is 0-10 km
 *   - Plus and Lux: safe range is 0-50 km
 *
 * Five operations must be simulated:
 *   - travelCheck(km): prints STABLE if the given distance is within the
 *     current tier's safe range, otherwise UNSTABLE -- the patient blacks
 *     out, and an alert message asks to bring the patient back into
 *     coverage. Once back in coverage, the simulation continues by calling
 *     travelCheck(0) and the patient regains consciousness.
 *   - activateLux(hours): temporarily switches the service to Lux for the
 *     given duration (simulated with sleep), then AUTOMATICALLY reverts to
 *     whichever tier (Common or Plus) was active immediately before Lux
 *     was activated.
 *   - setMood(calm|exhausted|happy): only works while Lux is active
 *     (whether permanently subscribed to Lux, or temporarily activated);
 *     otherwise prints "Mood control unavailable."
 *   - promote(): Common -> Plus -> Lux -> Lux (Lux has no higher tier).
 *   - demote(): Lux -> Plus -> Common -> Common (Common has no lower tier).
 *
 * ================================================================================
 * DESIGN PATTERN USED: STATE
 * ================================================================================
 *
 * WHY STATE IS THE CORRECT CHOICE:
 *   - The patient/subscription's behaviour -- its safe coverage range,
 *     whether mood control is available, and what promote()/demote() do --
 *     depends entirely on which TIER is currently active. That is exactly
 *     what State models: behaviour that varies with, and is owned by, the
 *     object's current internal state.
 *   - Each tier's rules (safe range, and what the next tier up/down is)
 *     belong naturally inside that tier's own class, rather than a big
 *     if/else ladder inside the context. This keeps each tier's behaviour
 *     independently understandable and modifiable, and trivially
 *     extensible if a new tier (e.g. "Elite") were added later.
 *   - promote()/demote() are direct, textbook State transitions: the
 *     current state object determines and returns the next state object,
 *     and the Context (Patient) just swaps its current-state reference.
 *   - "activateLux temporarily, then automatically revert to whichever
 *     tier was active before" is naturally handled by the Context
 *     remembering ONE extra reference -- the state to return to -- which
 *     is a standard, well-known extension of the State pattern (the
 *     Context, not the states, is responsible for remembering history).
 *     This does not change the pattern choice: the actual coverage-range
 *     and mood-availability behaviour still fully lives inside the State
 *     objects.
 *   - Rejected alternative: Strategy would fit if we were picking between
 *     interchangeable ALGORITHMS for one fixed operation chosen by a
 *     caller. Here, instead, promote()/demote() cause the object's OWN
 *     internal condition to progress through an ordered sequence, and that
 *     sequence itself controls what operations behave how -- that is
 *     State's defining characteristic, not Strategy's.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   State             -> SubscriptionTier (interface)
 *   ConcreteState      -> CommonTier, PlusTier, LuxTier
 *   Context           -> Patient (holds current tier, remembers the tier
 *                          to revert to after a temporary Lux activation,
 *                          delegates travelCheck/setMood to the tier)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// State: every tier implements this. Each tier knows its own safe range
// and its own promote/demote destinations.
// ---------------------------------------------------------------------------
interface SubscriptionTier {
    int safeRangeKm();
    SubscriptionTier promote(); // returns the tier one level up (or self if already at top)
    SubscriptionTier demote();  // returns the tier one level down (or self if already at bottom)
    boolean isLux();
    String getName();
}

// ---------------------------------------------------------------------------
// ConcreteState: Common -- safe range 0-10km, bottom tier (demote is a
// no-op), promotes to Plus.
// ---------------------------------------------------------------------------
class CommonTier implements SubscriptionTier {
    @Override public int safeRangeKm() { return 10; }
    @Override public SubscriptionTier promote() { return new PlusTier(); }
    @Override public SubscriptionTier demote() { return this; } // Common -> Common (no change)
    @Override public boolean isLux() { return false; }
    @Override public String getName() { return "Common"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Plus -- safe range 0-50km, promotes to Lux, demotes to
// Common.
// ---------------------------------------------------------------------------
class PlusTier implements SubscriptionTier {
    @Override public int safeRangeKm() { return 50; }
    @Override public SubscriptionTier promote() { return new LuxTier(); }
    @Override public SubscriptionTier demote() { return new CommonTier(); }
    @Override public boolean isLux() { return false; }
    @Override public String getName() { return "Plus"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Lux -- safe range 0-50km, top tier (promote is a no-op),
// demotes to Plus. Only tier where mood control is available.
// ---------------------------------------------------------------------------
class LuxTier implements SubscriptionTier {
    @Override public int safeRangeKm() { return 50; }
    @Override public SubscriptionTier promote() { return this; } // Lux -> Lux (no change)
    @Override public SubscriptionTier demote() { return new PlusTier(); }
    @Override public boolean isLux() { return true; }
    @Override public String getName() { return "Lux"; }
}

// ---------------------------------------------------------------------------
// Context: the patient's subscription. Delegates coverage/mood behaviour
// to the current tier (State), and separately remembers the tier to
// restore after a temporary Lux activation ends.
// ---------------------------------------------------------------------------
class Patient {
    private final String name;
    private SubscriptionTier currentTier;

    // Remembers which tier was active right before a temporary Lux
    // activation, so activateLux() can automatically revert to it.
    // null means "no temporary Lux activation currently in progress".
    private SubscriptionTier tierBeforeTemporaryLux = null;

    public Patient(String name, SubscriptionTier startingTier) {
        this.name = name;
        this.currentTier = startingTier;
        System.out.println(name + "'s subscription starts at tier: " + currentTier.getName());
    }

    public void travelCheck(int km) {
        if (km <= currentTier.safeRangeKm()) {
            System.out.println(name + " travelCheck(" + km + "km) under " + currentTier.getName() +
                    " tier -> STABLE.");
        } else {
            System.out.println(name + " travelCheck(" + km + "km) under " + currentTier.getName() +
                    " tier -> UNSTABLE. ALERT: patient has blacked out -- bring them back into coverage!");
            // Once back in coverage, the simulation continues by calling
            // travelCheck(0) and the patient regains consciousness.
            System.out.println("(Patient brought back into coverage...)");
            travelCheck(0);
            System.out.println(name + " has regained consciousness.");
        }
    }

    public void promote() {
        SubscriptionTier next = currentTier.promote();
        if (next.getName().equals(currentTier.getName())) {
            System.out.println(name + " is already at the top tier (" + currentTier.getName() + "); promote() has no effect.");
        } else {
            currentTier = next;
            System.out.println(name + " promoted to tier: " + currentTier.getName());
        }
    }

    public void demote() {
        SubscriptionTier next = currentTier.demote();
        if (next.getName().equals(currentTier.getName())) {
            System.out.println(name + " is already at the bottom tier (" + currentTier.getName() + "); demote() has no effect.");
        } else {
            currentTier = next;
            System.out.println(name + " demoted to tier: " + currentTier.getName());
        }
    }

    public void activateLux(int hours) {
        // Remember what tier to come back to -- but only if we are not
        // already permanently on Lux (nothing to "return" to in that case).
        if (!currentTier.isLux()) {
            tierBeforeTemporaryLux = currentTier;
            currentTier = new LuxTier();
            System.out.println(name + "'s tier temporarily switched to Lux for " + hours + " hour(s).");
        } else {
            System.out.println(name + " is already on Lux; activateLux() has no additional effect.");
            return;
        }

        sleepHours(hours);

        // Automatically return to whichever tier was active before Lux.
        currentTier = tierBeforeTemporaryLux;
        tierBeforeTemporaryLux = null;
        System.out.println(name + "'s temporary Lux activation ended -- reverted back to tier: " + currentTier.getName());
    }

    public void setMood(String mood) {
        if (currentTier.isLux()) {
            System.out.println(name + "'s mood set to: " + mood + " (Lux mood control).");
        } else {
            System.out.println("Mood control unavailable.");
        }
    }

    private void sleepHours(int hours) {
        try {
            // Scaled down (100ms per "hour") purely so the demo finishes quickly.
            Thread.sleep(hours * 100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class A1_BrainSupportSubscription {
    public static void main(String[] args) {
        Patient patient = new Patient("Mr. Tarek", new CommonTier());

        System.out.println("\n--- Distance checks under Common tier ---");
        patient.travelCheck(5);   // within 10km -> STABLE
        patient.travelCheck(20);  // exceeds 10km -> UNSTABLE, then auto-recover

        System.out.println("\n--- Promotions/demotions ---");
        patient.promote();  // Common -> Plus
        patient.promote();  // Plus -> Lux
        patient.promote();  // Lux -> Lux (no change)

        System.out.println("\n--- Mood control while permanently on Lux ---");
        patient.setMood("calm"); // works, currently Lux

        patient.demote();   // Lux -> Plus

        System.out.println("\n--- Mood control while not on Lux ---");
        patient.setMood("exhausted"); // unavailable, currently Plus

        System.out.println("\n--- Distance check under Plus tier ---");
        patient.travelCheck(40); // within 50km -> STABLE

        System.out.println("\n--- Temporary Lux activation and automatic return ---");
        patient.activateLux(2); // Plus -> Lux temporarily -> back to Plus
        patient.setMood("happy"); // unavailable again, back on Plus

        System.out.println("\n--- Demote back down to Common ---");
        patient.demote();  // Plus -> Common
        patient.demote();  // Common -> Common (no change)
    }
}
