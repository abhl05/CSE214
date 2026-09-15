/*
 * ================================================================================
 * SOURCE: CSE-214 Online - 3, 2023 batch  |  Duration: 30 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * An e-commerce platform must manage the lifecycle of a product return
 * request. The operations that can be performed on a request depend on its
 * CURRENT condition:
 *
 *   - Requested: starting condition. Reason may be updated multiple times.
 *     May be approved, rejected, or cancelled.
 *   - Approved: reason can no longer be modified. May still be cancelled
 *     (if the item has not yet been delivered). Item may be marked
 *     delivered.
 *   - Delivered: can no longer be cancelled. Item may be inspected. If it
 *     satisfies the return policy, refund processing begins; otherwise the
 *     request becomes Rejected.
 *   - Processing Refund: reason cannot be modified, cannot be cancelled.
 *     Refund may succeed or fail; if it fails it may be retried later.
 *   - Refunded / Rejected / Cancelled: final conditions -- no further
 *     operation should change the request.
 *
 * Operations to support: updateReason(reason), approve(), reject(),
 * cancel(), itemDelivered(), inspect(eligible), refundSuccessful(),
 * refundFailed(). Any operation invalid at the current point in the
 * workflow should produce an appropriate message rather than performing
 * the operation.
 *
 * Task requirements:
 *   1. Correctly handle each operation's behaviour throughout the lifecycle.
 *   2. Perform the appropriate transition when an operation succeeds.
 *   3. Prevent operations that are not valid at the current point.
 *   4. Keep the implementation modular, so behaviour for one part of the
 *      workflow can change without significantly affecting the others.
 *   5. Allow new conditions/behaviours to be added later with minimal
 *      modification to existing code.
 *
 * ================================================================================
 * DESIGN PATTERN USED: STATE
 * ================================================================================
 *
 * WHY STATE IS THE CORRECT CHOICE:
 *   - The problem literally calls each step of the lifecycle a "condition"
 *     and says the operations "that can be performed... depend on its
 *     current condition" -- this is the State pattern's textbook
 *     description: an object whose set of valid behaviours changes based
 *     on its current internal state, appearing to change class as it
 *     transitions.
 *   - There are EIGHT operations and SEVEN conditions, each condition
 *     allowing a different subset of operations with different resulting
 *     transitions. Implementing this as nested if/else inside one
 *     ReturnRequest class would be a large, error-prone conditional block
 *     that violates requirement 4 (modularity: one part of the workflow
 *     changeable without affecting others). State instead gives each
 *     condition its OWN class, so e.g. changing what "Approved" allows
 *     touches only the ReturnApprovedState class.
 *   - Requirement 5 (new conditions added later with minimal modification)
 *     is satisfied because adding a new condition (e.g. "Returned to
 *     Sender") means writing one new class implementing ReturnState;
 *     existing state classes are untouched unless they need a NEW outgoing
 *     transition into it (Open/Closed Principle, as far as is achievable
 *     for a new transition target).
 *   - "Any operation not valid at a particular point should produce an
 *     appropriate message rather than performing the operation" maps
 *     directly onto each ConcreteState implementing every operation, but
 *     only performing the actual transition/behaviour for the operations
 *     that make sense in that state, and printing a rejection message for
 *     the rest.
 *   - Rejected alternative: Strategy would fit if we were choosing between
 *     interchangeable ways to perform ONE fixed operation. Here, instead,
 *     the entire set of VALID operations, and their outcomes, changes as
 *     the request's own condition progresses through its lifecycle --
 *     State's exact domain.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   State             -> ReturnState (interface)
 *   ConcreteState      -> ReturnRequestedState, ReturnApprovedState, ReturnDeliveredState,
 *                          ReturnProcessingRefundState, ReturnRefundedState,
 *                          ReturnRejectedState, ReturnCancelledState
 *   Context           -> ReturnRequest (holds current state, current
 *                          reason, and delegates every operation to the
 *                          current state)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// State: every condition of a return request implements this. Every
// operation is declared here so each concrete state must explicitly decide
// (rather than silently inherit) what happens for every possible action.
// ---------------------------------------------------------------------------
interface ReturnState {
    void updateReason(ReturnRequest request, String reason);
    void approve(ReturnRequest request);
    void reject(ReturnRequest request);
    void cancel(ReturnRequest request);
    void itemDelivered(ReturnRequest request);
    void inspect(ReturnRequest request, boolean eligible);
    void refundSuccessful(ReturnRequest request);
    void refundFailed(ReturnRequest request);
    String getName();
}

// ---------------------------------------------------------------------------
// A convenience base class so every ConcreteState only has to override the
// operations that are actually valid for it; everything else automatically
// prints the standard "not valid" message. This keeps each ConcreteState
// short and focused only on ITS OWN valid behaviour (requirement 4), while
// still satisfying the ReturnState interface in full.
// ---------------------------------------------------------------------------
abstract class BaseReturnState implements ReturnState {
    private void rejectOperation(ReturnRequest request, String operation) {
        System.out.println("Operation '" + operation + "' is not valid while the request is " +
                getName() + ".");
    }

    @Override public void updateReason(ReturnRequest request, String reason) { rejectOperation(request, "updateReason"); }
    @Override public void approve(ReturnRequest request) { rejectOperation(request, "approve"); }
    @Override public void reject(ReturnRequest request) { rejectOperation(request, "reject"); }
    @Override public void cancel(ReturnRequest request) { rejectOperation(request, "cancel"); }
    @Override public void itemDelivered(ReturnRequest request) { rejectOperation(request, "itemDelivered"); }
    @Override public void inspect(ReturnRequest request, boolean eligible) { rejectOperation(request, "inspect"); }
    @Override public void refundSuccessful(ReturnRequest request) { rejectOperation(request, "refundSuccessful"); }
    @Override public void refundFailed(ReturnRequest request) { rejectOperation(request, "refundFailed"); }
}

// ---------------------------------------------------------------------------
// ConcreteState: Requested -- reason updatable; may approve, reject, cancel.
// ---------------------------------------------------------------------------
class ReturnRequestedState extends BaseReturnState {
    @Override
    public void updateReason(ReturnRequest request, String reason) {
        request.setReason(reason);
        System.out.println("Return reason updated to: \"" + reason + "\"");
    }

    @Override
    public void approve(ReturnRequest request) {
        System.out.println("Return request approved.");
        request.setState(new ReturnApprovedState());
    }

    @Override
    public void reject(ReturnRequest request) {
        System.out.println("Return request rejected.");
        request.setState(new ReturnRejectedState());
    }

    @Override
    public void cancel(ReturnRequest request) {
        System.out.println("Return request cancelled.");
        request.setState(new ReturnCancelledState());
    }

    @Override
    public String getName() { return "Requested"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Approved -- reason locked; may cancel (item not yet
// delivered) or mark delivered.
// ---------------------------------------------------------------------------
class ReturnApprovedState extends BaseReturnState {
    @Override
    public void cancel(ReturnRequest request) {
        System.out.println("Return request cancelled (item had not yet been delivered).");
        request.setState(new ReturnCancelledState());
    }

    @Override
    public void itemDelivered(ReturnRequest request) {
        System.out.println("Returned item marked as delivered.");
        request.setState(new ReturnDeliveredState());
    }

    @Override
    public String getName() { return "Approved"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Delivered -- cannot cancel; may inspect, which leads
// either to ProcessingRefund (eligible) or Rejected (not eligible).
// ---------------------------------------------------------------------------
class ReturnDeliveredState extends BaseReturnState {
    @Override
    public void inspect(ReturnRequest request, boolean eligible) {
        if (eligible) {
            System.out.println("Inspection passed: item satisfies return policy. Beginning refund processing.");
            request.setState(new ReturnProcessingRefundState());
        } else {
            System.out.println("Inspection failed: item does not satisfy return policy.");
            request.setState(new ReturnRejectedState());
        }
    }

    @Override
    public String getName() { return "Delivered"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Processing Refund -- reason locked, cannot cancel; refund
// may succeed or fail (failure allows a later retry, i.e. stays in this
// same state so refundFailed()/refundSuccessful() can be tried again).
// ---------------------------------------------------------------------------
class ReturnProcessingRefundState extends BaseReturnState {
    @Override
    public void refundSuccessful(ReturnRequest request) {
        System.out.println("Refund succeeded.");
        request.setState(new ReturnRefundedState());
    }

    @Override
    public void refundFailed(ReturnRequest request) {
        // Stays in ReturnProcessingRefundState -- "if it fails, it may be
        // attempted again later" means refundSuccessful()/refundFailed()
        // remain valid to call again from here.
        System.out.println("Refund attempt failed. It may be attempted again later.");
    }

    @Override
    public String getName() { return "Processing Refund"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Refunded -- terminal. No operations valid (all inherited
// from BaseReturnState, which rejects everything by default).
// ---------------------------------------------------------------------------
class ReturnRefundedState extends BaseReturnState {
    @Override
    public String getName() { return "Refunded"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Rejected -- terminal. No operations valid.
// ---------------------------------------------------------------------------
class ReturnRejectedState extends BaseReturnState {
    @Override
    public String getName() { return "Rejected"; }
}

// ---------------------------------------------------------------------------
// ConcreteState: Cancelled -- terminal. No operations valid.
// ---------------------------------------------------------------------------
class ReturnCancelledState extends BaseReturnState {
    @Override
    public String getName() { return "Cancelled"; }
}

// ---------------------------------------------------------------------------
// Context: holds the request's current state and reason, and simply
// forwards every public operation to the current state object.
// ---------------------------------------------------------------------------
class ReturnRequest {
    private final String requestId;
    private ReturnState state;
    private String reason = "(no reason given yet)";

    public ReturnRequest(String requestId, String initialReason) {
        this.requestId = requestId;
        this.state = new ReturnRequestedState(); // every request starts as Requested
        this.reason = initialReason;
        System.out.println("Return request " + requestId + " created with reason: \"" + initialReason + "\"");
    }

    void setState(ReturnState state) {
        this.state = state;
        System.out.println("Return request " + requestId + " condition is now: " + state.getName());
    }

    void setReason(String reason) { this.reason = reason; }
    public String getReason() { return reason; }
    public String getStatus() { return state.getName(); }

    public void updateReason(String reason) { state.updateReason(this, reason); }
    public void approve() { state.approve(this); }
    public void reject() { state.reject(this); }
    public void cancel() { state.cancel(this); }
    public void itemDelivered() { state.itemDelivered(this); }
    public void inspect(boolean eligible) { state.inspect(this, eligible); }
    public void refundSuccessful() { state.refundSuccessful(this); }
    public void refundFailed() { state.refundFailed(this); }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class CSE214Online3_ReturnRefundWorkflow {
    public static void main(String[] args) {
        System.out.println("--- Scenario 1: cancellation while still approved (not yet delivered) ---");
        ReturnRequest r1 = new ReturnRequest("RR-001", "Item arrived damaged");
        r1.updateReason("Item arrived damaged and box was crushed");
        r1.approve();
        r1.cancel();          // valid here: item not yet delivered, so cancellation succeeds

        System.out.println("\n--- Scenario 2: full happy path to refund ---");
        ReturnRequest r2 = new ReturnRequest("RR-002", "Wrong size shipped");
        r2.approve();
        r2.updateReason("Trying to change reason after approval");  // invalid: reason locked
        r2.itemDelivered();
        r2.cancel();                 // invalid: cannot cancel once delivered
        r2.inspect(true);            // eligible -> Processing Refund
        r2.refundFailed();           // fails once, can retry
        r2.refundSuccessful();       // retried -> success -> Refunded
        r2.refundFailed();           // invalid: already Refunded (terminal)

        System.out.println("\n--- Scenario 3: item fails inspection ---");
        ReturnRequest r3 = new ReturnRequest("RR-003", "Changed my mind");
        r3.approve();
        r3.itemDelivered();
        r3.inspect(false);           // not eligible -> Rejected
        r3.approve();                // invalid: terminal state
    }
}
