/*
 * ================================================================================
 * STATE PATTERN -- DROP-IN BOILERPLATE
 * ================================================================================
 *
 * HOW TO RECOGNIZE A STATE PROBLEM (signal phrases in the question):
 *   - "lifecycle", "stages", "phases", "conditions", "statuses" that an
 *     object moves through over time (e.g. order: Placed -> Shipped ->
 *     Delivered; traffic light: Red -> Yellow -> Green).
 *   - "the valid operations DEPEND ON the current [status/stage/condition]"
 *   - "some actions should be rejected / print an error if attempted at
 *     the wrong stage"
 *   - "the object should behave differently depending on what state it's
 *     currently in"
 *   - Explicit transition rules ("can only go from X to Y", "cannot skip
 *     from A directly to C").
 *
 * NOT State if:
 *   - There's no real lifecycle -- just picking between algorithms for one
 *     fixed operation, chosen by the CALLER rather than by the object's
 *     own history -> that's STRATEGY.
 *   - Many objects need to react to one event -> that's OBSERVER.
 *
 * STRUCTURE (GoF roles -> classes below):
 *   State             -> State (interface)              [RENAME ME]
 *   ConcreteState      -> ConcreteStateA, ConcreteStateB, ...
 *   Context (GoF role) -> StateContext (holds current State, delegates every
 *                          operation to it; only the state classes are
 *                          allowed to change the current state via the
 *                          package-private setState())
 *
 * HOW TO ADAPT THIS FILE DURING THE EXAM:
 *   1. Rename `State` to the domain concept (e.g. OrderState, LightState).
 *   2. List out EVERY operation the object supports as a method on the
 *      State interface (confirm(), cancel(), ship(), ... whatever the
 *      problem names).
 *   3. One ConcreteState class per stage/condition named in the problem.
 *      For each one, override ONLY the operations that are valid from
 *      that stage (BaseState below already rejects everything else with a
 *      standard message, so you don't need to write rejection code for
 *      every invalid operation by hand).
 *   4. Inside a valid operation, call `context.setState(new NextState())`
 *      to perform the transition.
 *   5. Rename StateContext to the domain noun (e.g. Order, TrafficLight,
 *      ReturnRequest) and add whatever fields the problem needs (reason,
 *      id, timestamps, ...).
 *   6. Delete this header comment and write your own problem statement +
 *      pattern justification in its place.
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// State: every stage/condition implements this. Declare every operation
// the object supports here, even if a given stage doesn't allow it --
// BaseState below provides the default "operation not valid" behaviour so
// each ConcreteState only needs to override what IS valid for it.
// TODO: rename, and replace opA/opB with the real operation names.
// ---------------------------------------------------------------------------
interface State {
    void opA(StateContext context);
    void opB(StateContext context);
    String getName();
}

// ---------------------------------------------------------------------------
// Convenience base class: every operation defaults to "not valid here".
// Concrete states only need to @Override the operations that DO apply.
// ---------------------------------------------------------------------------
abstract class BaseState implements State {
    private void reject(String operation) {
        System.out.println("Invalid operation '" + operation + "' while in state: " + getName());
    }

    @Override public void opA(StateContext context) { reject("opA"); }
    @Override public void opB(StateContext context) { reject("opB"); }
}

// ---------------------------------------------------------------------------
// ConcreteState #1
// TODO: rename, override only the operations valid in this stage, and
// call context.setState(new NextState()) to transition.
// ---------------------------------------------------------------------------
class ConcreteStateA extends BaseState {
    @Override
    public void opA(StateContext context) {
        System.out.println("Doing opA in State A -> transitioning to State B.");
        context.setState(new ConcreteStateB());
    }

    @Override
    public String getName() { return "StateA"; }
}

// ---------------------------------------------------------------------------
// ConcreteState #2
// TODO: rename, override only the operations valid in this stage.
// ---------------------------------------------------------------------------
class ConcreteStateB extends BaseState {
    @Override
    public void opB(StateContext context) {
        System.out.println("Doing opB in State B -> staying in State B (terminal example).");
    }

    @Override
    public String getName() { return "StateB"; }
}

// ---------------------------------------------------------------------------
// StateContext: holds a reference to the current state and forwards every
// public operation to it. Contains NO stage-specific rules itself.
// TODO: rename to the domain noun; add any extra fields the problem needs.
// ---------------------------------------------------------------------------
class StateContext {
    private State state;

    public StateContext() {
        this.state = new ConcreteStateA(); // TODO: set the correct initial state
    }

    // Only state classes should call this (it's how transitions happen).
    void setState(State state) {
        this.state = state;
        System.out.println("State changed to: " + state.getName());
    }

    public void opA() { state.opA(this); }
    public void opB() { state.opB(this); }
    public String getStatus() { return state.getName(); }
}

// ---------------------------------------------------------------------------
// Demo -- proves the skeleton compiles and wires together correctly.
// TODO: delete/replace with the actual scenario from the problem.
// ---------------------------------------------------------------------------
public class State_Boilerplate {
    public static void main(String[] args) {
        StateContext context = new StateContext();
        System.out.println("Initial state: " + context.getStatus());

        context.opB(); // invalid from State A -> prints rejection message
        context.opA(); // valid -> transitions to State B
        context.opB(); // valid from State B
        context.opA(); // invalid from State B -> prints rejection message
    }
}
