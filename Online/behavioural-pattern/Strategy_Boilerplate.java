/*
 * ================================================================================
 * STRATEGY PATTERN -- DROP-IN BOILERPLATE
 * ================================================================================
 *
 * HOW TO RECOGNIZE A STRATEGY PROBLEM (signal phrases in the question):
 *   - "multiple ways to do X" / "different algorithms for the same task"
 *   - "the user/customer should be able to CHOOSE or SWITCH the method"
 *   - "add new [payment methods / discount rules / routes / sorting
 *      criteria] later without changing existing code"
 *   - ONE fixed operation (e.g. "pay", "notify", "compute cost"), MANY
 *     interchangeable implementations of it, selected at runtime.
 *   - If several rules/algorithms should all be evaluated and the BEST
 *     result picked, that is still Strategy -- put the "pick the best"
 *     logic in the StrategyContext, not in the strategies themselves.
 *
 * NOT Strategy if:
 *   - The valid operations THEMSELVES change depending on an internal
 *     lifecycle/condition of the object -> that's STATE.
 *   - Many objects need to be told about ONE event -> that's OBSERVER.
 *   - All variants share an identical multi-step SEQUENCE and only 1-2
 *     steps differ -> that's TEMPLATE METHOD.
 *
 * STRUCTURE (GoF roles -> classes below):
 *   Strategy          -> Strategy (interface)          [RENAME ME]
 *   ConcreteStrategy   -> ConcreteStrategyA, ConcreteStrategyB, ...
 *   Context (GoF role) -> StrategyContext (holds current Strategy, exposes a
 *                          setter to swap it at runtime, delegates the
 *                          operation to it)
 *
 * HOW TO ADAPT THIS FILE DURING THE EXAM:
 *   1. Rename `Strategy` to the domain verb (e.g. PaymentStrategy,
 *      DiscountStrategy, RoutingStrategy).
 *   2. Rename/duplicate ConcreteStrategyA/B for however many concrete
 *      algorithms the problem describes; delete what you don't need.
 *   3. Change the method signature in the interface to match what the
 *      problem actually needs to pass in/return.
 *   4. Rename StrategyContext to the domain noun (e.g. Checkout, DiscountCalculator).
 *   5. If the problem wants "evaluate all, pick the best" instead of "pick
 *      one and use it", keep a LIST of strategies in StrategyContext (see the
 *      commented-out alternative near the bottom of the StrategyContext class)
 *      instead of a single field.
 *   6. Delete this header comment block and replace it with your own
 *      problem statement + pattern justification, per the exam
 *      instructions (every submitted solution should defend its pattern
 *      choice in its own header comment).
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Strategy: the common interface every concrete algorithm implements.
// TODO: rename, and change the method signature to match the problem.
// ---------------------------------------------------------------------------
interface Strategy {
    void execute(double input); // TODO: change return type / params as needed
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #1
// TODO: rename and implement real domain-specific logic.
// ---------------------------------------------------------------------------
class ConcreteStrategyA implements Strategy {
    @Override
    public void execute(double input) {
        System.out.println("[Strategy A] processed " + input);
    }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #2
// TODO: rename and implement real domain-specific logic.
// ---------------------------------------------------------------------------
class ConcreteStrategyB implements Strategy {
    @Override
    public void execute(double input) {
        System.out.println("[Strategy B] processed " + input);
    }
}

// ---------------------------------------------------------------------------
// StrategyContext: does NOT know which concrete strategy it holds. Only depends
// on the Strategy abstraction -- new strategies can be added without
// touching this class.
// TODO: rename to the domain noun.
// ---------------------------------------------------------------------------
class StrategyContext {
    private Strategy strategy;

    public StrategyContext(Strategy strategy) {
        this.strategy = strategy;
    }

    // Lets the caller switch the algorithm at runtime.
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void doWork(double input) {
        strategy.execute(input);
    }

    /*
     * ALTERNATIVE VARIANT -- "evaluate ALL strategies, keep the best":
     * use this shape instead of a single `strategy` field when the
     * problem says every rule is checked and the best/highest one wins
     * (e.g. "apply the largest applicable discount").
     *
     * private final java.util.List<Strategy> strategies = new java.util.ArrayList<>();
     * public void addStrategy(Strategy s) { strategies.add(s); }
     * public double pickBest(double input) {
     *     double best = Double.NEGATIVE_INFINITY;
     *     for (Strategy s : strategies) {
     *         double result = s.evaluate(input); // strategy returns a score
     *         if (result > best) best = result;
     *     }
     *     return best;
     * }
     */
}

// ---------------------------------------------------------------------------
// Demo -- proves the skeleton compiles and wires together correctly.
// TODO: delete/replace with the actual scenario from the problem.
// ---------------------------------------------------------------------------
public class Strategy_Boilerplate {
    public static void main(String[] args) {
        StrategyContext context = new StrategyContext(new ConcreteStrategyA());
        context.doWork(100);

        context.setStrategy(new ConcreteStrategyB()); // swap at runtime
        context.doWork(200);
    }
}
