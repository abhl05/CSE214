/*
 * ================================================================================
 * TEMPLATE METHOD PATTERN -- DROP-IN BOILERPLATE
 * ================================================================================
 *
 * HOW TO RECOGNIZE A TEMPLATE METHOD PROBLEM (signal phrases in the
 * question):
 *   - "every [visit/order/report/process] must follow the SAME overall
 *      steps, IN THE SAME ORDER"
 *   - "only a couple of steps differ between [department/type/category]"
 *   - A fixed numbered sequence of steps is given, most of which are
 *     identical across variants, with one or two steps that vary.
 *   - The variation is in WHAT HAPPENS during a step, not in the ORDER or
 *     PRESENCE of steps.
 *
 * NOT Template Method if:
 *   - Different variants need entirely different algorithms chosen as a
 *     whole (not just 1-2 steps within a fixed shared sequence) -> that's
 *     STRATEGY.
 *   - The sequence itself needs to be reorderable or skippable based on
 *     runtime conditions -> reconsider; Template Method assumes a FIXED
 *     skeleton.
 *
 * STRUCTURE (GoF roles -> classes below):
 *   AbstractClass     -> AbstractClass (defines the template method,
 *                          shared/common steps, and abstract steps)
 *                          [RENAME ME]
 *   ConcreteClass      -> ConcreteClassA, ConcreteClassB, ...
 *                          (implement ONLY the steps that vary)
 *
 * HOW TO ADAPT THIS FILE DURING THE EXAM:
 *   1. Rename `AbstractClass` to the domain concept (e.g. HospitalVisit).
 *   2. In the template method (`process()` below, keep it `final`), list
 *      every step in the EXACT order given by the problem.
 *   3. Any step that is IDENTICAL across all variants -> implement it
 *      directly in AbstractClass as a private/protected method.
 *   4. Any step that VARIES -> declare it `abstract` in AbstractClass and
 *      implement it separately in each ConcreteClass.
 *   5. (Optional) "Hook" steps -- steps that have a default no-op
 *      implementation but MAY be overridden by a subclass if it needs to
 *      -- can be added as protected, non-abstract, empty/no-op methods
 *      that a ConcreteClass overrides only when relevant.
 *   6. Rename ConcreteClassA/B to the domain variants (e.g.
 *      GeneralDepartmentVisit, EmergencyDepartmentVisit) and delete/add as
 *      many as the problem needs.
 *   7. Delete this header comment and write your own problem statement +
 *      pattern justification in its place.
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// AbstractClass: defines the fixed skeleton of the algorithm (the
// Template Method itself) plus the steps shared by every variant. Steps
// that vary are left abstract for subclasses to specialise.
// TODO: rename, and replace stepA/stepB/stepC with the real step names
// and order given in the problem.
// ---------------------------------------------------------------------------
abstract class AbstractClass {

    // --- The Template Method: fixes the ORDER of steps, once, for every
    // variant. Keep this `final` so no subclass can override the overall
    // flow itself -- only the individual steps below can be customised. ---
    public final void process() {
        stepA();       // shared -- identical for every variant
        stepB();       // varies -- each variant implements its own
        optionalHook(); // hook -- default no-op, override only if needed
        stepC();       // shared -- identical for every variant
    }

    // Shared step -- implemented here once, used by every variant.
    private void stepA() {
        System.out.println("Step A (shared): common setup logic.");
    }

    // Varying step -- every ConcreteClass MUST implement this.
    protected abstract void stepB();

    // Hook -- has a default (no-op) implementation. A ConcreteClass
    // overrides this ONLY if it needs to add extra behaviour at this
    // point; otherwise it's simply skipped.
    protected void optionalHook() {
        // default: do nothing
    }

    // Shared step -- implemented here once, used by every variant.
    private void stepC() {
        System.out.println("Step C (shared): common wrap-up/finalization logic.");
    }
}

// ---------------------------------------------------------------------------
// ConcreteClass #1
// TODO: rename and implement the real varying step(s) for this variant.
// ---------------------------------------------------------------------------
class ConcreteClassA extends AbstractClass {
    @Override
    protected void stepB() {
        System.out.println("Step B (Variant A): specific behaviour for A.");
    }
}

// ---------------------------------------------------------------------------
// ConcreteClass #2
// TODO: rename and implement the real varying step(s) for this variant.
// This one also demonstrates overriding the optional hook.
// ---------------------------------------------------------------------------
class ConcreteClassB extends AbstractClass {
    @Override
    protected void stepB() {
        System.out.println("Step B (Variant B): specific behaviour for B.");
    }

    @Override
    protected void optionalHook() {
        System.out.println("Optional hook (Variant B): extra behaviour only B needs.");
    }
}

// ---------------------------------------------------------------------------
// Demo -- proves the skeleton compiles and wires together correctly.
// TODO: delete/replace with the actual scenario from the problem.
// ---------------------------------------------------------------------------
public class TemplateMethod_Boilerplate {
    public static void main(String[] args) {
        System.out.println("=== Variant A ===");
        AbstractClass a = new ConcreteClassA();
        a.process();

        System.out.println("\n=== Variant B ===");
        AbstractClass b = new ConcreteClassB();
        b.process();
    }
}
