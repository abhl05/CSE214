/*
 * ================================================================================
 * SOURCE: CSE-214, 2021 batch, "Online 3" (Section C2)  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * HungryHippo (a food-delivery app) tracks every order through a journey of
 * steps: "Placed" -> "Confirmed" -> "Shipped" -> "Delivered", with the
 * possibility of moving to "Cancelled" before shipment.
 *
 *   - A new order starts as Placed, and waits for confirmation.
 *   - Once confirmed, it becomes Confirmed, then later Shipped, then
 *     finally Delivered (successful completion).
 *   - An order may be Cancelled, but only before it has shipped.
 *   - Each step must govern its OWN set of valid actions, and must enforce
 *     valid transitions -- e.g. an order can never jump directly from
 *     Placed to Delivered, nor revert from Shipped back to Confirmed.
 *   - The design must stay flexible for future steps such as "Returned" or
 *     "On Hold" (not implemented now, but the design should not resist
 *     adding them later).
 *
 * ================================================================================
 * DESIGN PATTERN USED: STATE
 * ================================================================================
 *
 * WHY STATE IS THE CORRECT CHOICE:
 *   - The order's ALLOWED BEHAVIOUR changes depending on which step of its
 *     lifecycle it is currently in -- this is the textbook definition of
 *     the State pattern: "allow an object to alter its behaviour when its
 *     internal state changes; the object will appear to change its class."
 *   - Each step (Placed, Confirmed, Shipped, Delivered, Cancelled) has its
 *     own rules about which transitions are legal. Encoding this with
 *     if/else or switch statements inside a single Order class would very
 *     quickly become an unmaintainable tangle of conditionals, and would
 *     violate the requirement to keep "behaviour associated with one part
 *     of the workflow" isolated from the others.
 *   - State solves exactly this by giving each step its OWN class that
 *     implements a common interface and knows only its own valid
 *     transitions. The Order (Context) simply forwards every request to
 *     its current state object.
 *   - Extensibility requirement (adding "Returned"/"On Hold" later) is
 *     satisfied because adding a new step is just adding one new class
 *     implementing OrderState -- no existing state class needs to change
 *     (Open/Closed Principle).
 *   - Rejected alternative: Strategy would fit if we were choosing between
 *     interchangeable ALGORITHMS for a single fixed operation. Here,
 *     instead, the set of VALID OPERATIONS itself changes as the order
 *     progresses, and transitions are driven by the object's own history
 *     -- that is State's job, not Strategy's.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   State             -> OrderState (interface)
 *   ConcreteState      -> PlacedState, ConfirmedState, ShippedState,
 *                          DeliveredState, CancelledState
 *   Context           -> Order (holds current OrderState, delegates every
 *                          operation to it, and is the only place allowed
 *                          to change the current state)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// State: every step of the order lifecycle implements this interface.
// Only the operations relevant to the domain are declared; each concrete
// state decides for itself whether an operation is legal right now.
// ---------------------------------------------------------------------------
interface OrderState {
    void confirm(Order order);
    void ship(Order order);
    void deliver(Order order);
    void cancel(Order order);
    String getName();
}

// ---------------------------------------------------------------------------
// ConcreteState: Placed -- can be confirmed or cancelled.
// ---------------------------------------------------------------------------
class PlacedState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("Order confirmed.");
        order.setState(new ConfirmedState());
    }

    @Override
    public void ship(Order order) {
        System.out.println("Invalid transition: cannot ship an order that has not been confirmed.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Invalid transition: cannot deliver an order that has not shipped.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order cancelled.");
        order.setState(new CancelledState());
    }

    @Override
    public String getName() { return "Placed"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Confirmed -- can be shipped or cancelled (not yet shipped).
// ---------------------------------------------------------------------------
class ConfirmedState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("Invalid transition: order is already confirmed.");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Order shipped.");
        order.setState(new ShippedState());
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Invalid transition: cannot deliver an order that has not shipped.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order cancelled.");
        order.setState(new CancelledState());
    }

    @Override
    public String getName() { return "Confirmed"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Shipped -- can only be delivered. Cancellation is no
// longer allowed once shipped, per the requirements.
// ---------------------------------------------------------------------------
class ShippedState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("Invalid transition: order has already been confirmed and shipped.");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Invalid transition: order is already shipped.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Order delivered.");
        order.setState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Invalid transition: cannot cancel an order that has already shipped.");
    }

    @Override
    public String getName() { return "Shipped"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Delivered -- terminal, successful state. No further
// operations are valid.
// ---------------------------------------------------------------------------
class DeliveredState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("Invalid transition: order journey has already completed (Delivered).");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Invalid transition: order journey has already completed (Delivered).");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Invalid transition: order is already delivered.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Invalid transition: cannot cancel a delivered order.");
    }

    @Override
    public String getName() { return "Delivered"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Cancelled -- terminal state. No further operations valid.
// ---------------------------------------------------------------------------
class CancelledState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("Invalid transition: order has been cancelled.");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Invalid transition: order has been cancelled.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Invalid transition: order has been cancelled.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Invalid transition: order is already cancelled.");
    }

    @Override
    public String getName() { return "Cancelled"; }
}

/*
 * FUTURE EXTENSION NOTE (addresses the "Returned"/"On Hold" requirement):
 * To add a "Returned" step later, we would simply write a new class
 * `ReturnedState implements OrderState` and let DeliveredState transition
 * into it via a new `returnOrder(Order order)` operation. No existing
 * state class (Placed/Confirmed/Shipped/Cancelled) needs to be touched.
 */

// ---------------------------------------------------------------------------
// Context: holds a reference to the current state and simply forwards
// every public operation to it. This class never contains any
// step-specific business rules itself -- that is exactly what keeps the
// behaviour of each step isolated and independently modifiable.
// ---------------------------------------------------------------------------
class Order {
    private final String orderId;
    private OrderState state;

    public Order(String orderId) {
        this.orderId = orderId;
        this.state = new PlacedState(); // every order starts as Placed
        System.out.println("Order " + orderId + " created. Status: " + state.getName());
    }

    // Only the state classes (via this package-private setter) are allowed
    // to move the order forward -- the Context does not decide transitions
    // itself, it delegates that decision to the current state.
    void setState(OrderState state) {
        this.state = state;
        System.out.println("Order " + orderId + " status is now: " + state.getName());
    }

    public void confirm() { state.confirm(this); }
    public void ship() { state.ship(this); }
    public void deliver() { state.deliver(this); }
    public void cancel() { state.cancel(this); }

    public String getStatus() { return state.getName(); }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class Online3C2_OrderStatusWorkflow {
    public static void main(String[] args) {
        System.out.println("--- Scenario 1: happy path ---");
        Order order1 = new Order("ORD-1001");
        order1.confirm();
        order1.ship();
        order1.deliver();
        order1.cancel(); // invalid: already delivered

        System.out.println("\n--- Scenario 2: cancellation before shipment ---");
        Order order2 = new Order("ORD-1002");
        order2.confirm();
        order2.cancel();
        order2.ship(); // invalid: already cancelled

        System.out.println("\n--- Scenario 3: invalid skip ---");
        Order order3 = new Order("ORD-1003");
        order3.deliver(); // invalid: cannot skip straight to Delivered
        order3.confirm();
        order3.ship();
        order3.confirm(); // invalid: cannot revert/re-confirm once shipped
    }
}
