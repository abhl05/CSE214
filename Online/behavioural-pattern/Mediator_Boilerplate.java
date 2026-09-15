/*
 * ================================================================================
 * MEDIATOR PATTERN -- DROP-IN BOILERPLATE
 * ================================================================================
 *
 * HOW TO RECOGNIZE A MEDIATOR PROBLEM (signal phrases in the question):
 *   - "objects/devices/components should NOT talk to each other directly"
 *   - "report to a central hub/coordinator/controller instead"
 *   - "when X happens, the hub should tell Y to do Z" (chains of
 *      cross-object reactions routed through one central object)
 *   - Several distinct components need to stay decoupled from each other
 *     while still reacting to each other's events, and the CROSS-OBJECT
 *     COORDINATION RULES themselves ("if A then tell B", "if B then tell
 *     C") are the actual thing being asked for.
 *
 * NOT Mediator if:
 *   - It's just "one subject notifies many independent listeners" with no
 *     rule about telling one listener to command another -> that's
 *     OBSERVER (simpler, use that unless direct object-to-object commands
 *     are explicitly forbidden AND a central routing object is asked for).
 *
 * STRUCTURE (GoF roles -> classes below):
 *   Mediator          -> Mediator (interface)             [RENAME ME]
 *   ConcreteMediator   -> ConcreteMediator (knows about every colleague,
 *                          contains ALL the coordination rules between
 *                          them)
 *   Colleague         -> Colleague (abstract base, holds a reference to
 *                          its mediator, never a reference to another
 *                          colleague)
 *   ConcreteColleague  -> ConcreteColleagueA, ConcreteColleagueB, ...
 *
 * HOW TO ADAPT THIS FILE DURING THE EXAM:
 *   1. Rename `Mediator` to the domain concept (e.g. SmartHomeMediator).
 *   2. One ConcreteColleague class per device/component named in the
 *      problem. Each one only ever calls `mediator.notify(this, "Event")`
 *      -- never another colleague directly.
 *   3. Put every "if event X from component Y, then do Z" rule inside
 *      ConcreteMediator.notify(...). This is the ONLY place cross-object
 *      coordination logic should live.
 *   4. Wire everything together in main(): construct the mediator first,
 *      then the colleagues (passing the mediator in), then register the
 *      colleagues with the mediator so it can command them.
 *   5. Delete this header comment and write your own problem statement +
 *      pattern justification in its place.
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Mediator: the interface every colleague uses to report events, without
// knowing which other colleagues exist or how they should react.
// TODO: rename, and change notify(...)'s signature if needed.
// ---------------------------------------------------------------------------
interface Mediator {
    void notify(Colleague sender, String event);
}

// ---------------------------------------------------------------------------
// Colleague: common base for every component. A colleague only ever talks
// to its mediator -- never to another colleague directly.
// TODO: rename if the domain has a more specific shared base concept
// (e.g. "Device").
// ---------------------------------------------------------------------------
abstract class Colleague {
    protected final Mediator mediator;

    protected Colleague(Mediator mediator) {
        this.mediator = mediator;
    }
}

// ---------------------------------------------------------------------------
// ConcreteColleague #1
// TODO: rename and add the real domain behaviour/trigger method(s).
// ---------------------------------------------------------------------------
class ConcreteColleagueA extends Colleague {
    public ConcreteColleagueA(Mediator mediator) {
        super(mediator);
    }

    public void doSomething() {
        System.out.println("ColleagueA: something happened.");
        mediator.notify(this, "EventFromA"); // report to the mediator, not to ColleagueB directly
    }
}

// ---------------------------------------------------------------------------
// ConcreteColleague #2
// TODO: rename and add the real domain behaviour/trigger method(s).
// ---------------------------------------------------------------------------
class ConcreteColleagueB extends Colleague {
    public ConcreteColleagueB(Mediator mediator) {
        super(mediator);
    }

    public void reactToA() {
        System.out.println("ColleagueB: reacting, as instructed by the mediator.");
    }
}

// ---------------------------------------------------------------------------
// ConcreteMediator: the ONLY class that knows about every colleague and
// contains all the cross-colleague coordination rules.
// TODO: rename, add fields for every colleague, and write the real rules
// inside notify(...).
// ---------------------------------------------------------------------------
class ConcreteMediator implements Mediator {
    private ConcreteColleagueB colleagueB;

    public void registerColleagueB(ConcreteColleagueB colleagueB) {
        this.colleagueB = colleagueB;
    }

    @Override
    public void notify(Colleague sender, String event) {
        if (sender instanceof ConcreteColleagueA && event.equals("EventFromA")) {
            System.out.println("Mediator: received EventFromA -> instructing ColleagueB.");
            colleagueB.reactToA();
        }
        // TODO: add one branch per coordination rule described in the problem.
    }
}

// ---------------------------------------------------------------------------
// Demo -- proves the skeleton compiles and wires together correctly.
// TODO: delete/replace with the actual scenario from the problem.
// ---------------------------------------------------------------------------
public class Mediator_Boilerplate {
    public static void main(String[] args) {
        ConcreteMediator mediator = new ConcreteMediator();

        ConcreteColleagueA colleagueA = new ConcreteColleagueA(mediator);
        ConcreteColleagueB colleagueB = new ConcreteColleagueB(mediator);
        mediator.registerColleagueB(colleagueB);

        colleagueA.doSomething(); // ColleagueA -> Mediator -> ColleagueB
    }
}
