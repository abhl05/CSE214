/*
 * ================================================================================
 * SOURCE: CSE-214, 2021 batch, "Online 3" (Sec A)  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * Build a payment system for an e-commerce platform that supports multiple
 * payment methods -- credit card, BKash, and cryptocurrency (e.g. Bitcoin).
 * During checkout a customer chooses a payment method and the system must
 * process the payment using logic specific to that method.
 *
 * Requirements:
 *   1. A customer must be able to switch payment methods easily at checkout.
 *   2. The platform owner must be able to add brand-new payment methods in
 *      the future WITHOUT making significant changes to existing code.
 *   3. When a method is selected, the system uses the matching processing
 *      logic automatically.
 *
 * ================================================================================
 * DESIGN PATTERN USED: STRATEGY
 * ================================================================================
 *
 * WHY STRATEGY IS THE CORRECT CHOICE:
 *   - We have a family of algorithms that all accomplish the same job
 *     ("pay this amount") but differ in HOW they do it (card networks,
 *     BKash's mobile-wallet API, a blockchain transaction, ...). Strategy
 *     is the textbook pattern for "same goal, interchangeable algorithms".
 *   - The client (Checkout) should be decoupled from the concrete payment
 *     logic -- it should only depend on an abstraction (PaymentStrategy).
 *     This satisfies requirement 2: adding CryptoPaymentV2 or WhatsAppPay
 *     later only means writing one new class; Checkout is never touched
 *     (Open/Closed Principle).
 *   - The algorithm must be swappable AT RUNTIME by the customer during
 *     checkout (requirement 1). Strategy stores the algorithm as an
 *     object reference that can be reassigned with a simple setter --
 *     exactly the mechanism Strategy provides.
 *   - Rejected alternatives:
 *       * State would fit if payment METHODS caused the object itself to
 *         change behaviour over time/transitions (e.g. an order moving
 *         through "paid -> shipped"). Here there is no such lifecycle --
 *         each payment attempt is a one-shot algorithm choice, not a state
 *         transition. So Strategy, not State.
 *       * Template Method would fit if all payment methods shared an
 *         identical multi-step skeleton with only a few varying steps.
 *         The problem does not describe such a shared skeleton, and
 *         methods are chosen wholesale by the client, so Strategy (whole
 *         algorithm swapped) is the better fit.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Strategy          -> PaymentStrategy (interface)
 *   ConcreteStrategy   -> CreditCardPayment, BKashPayment, CryptoPayment
 *   Context           -> Checkout (holds a PaymentStrategy reference,
 *                          delegates pay() to it, exposes a setter so the
 *                          strategy can change at runtime)
 *   Client            -> main()
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Strategy: the common interface every concrete payment algorithm implements.
// ---------------------------------------------------------------------------
interface PaymentStrategy {
    void pay(double amount);
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #1
// ---------------------------------------------------------------------------
class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        // Credit-card specific processing logic would go here (talk to the
        // card network, run fraud checks, etc.). We simulate it with a print.
        System.out.println("[CreditCard] Charged ৳" + amount +
                " to card ending in " + cardNumber.substring(cardNumber.length() - 4));
    }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #2
// ---------------------------------------------------------------------------
class BKashPayment implements PaymentStrategy {
    private final String phoneNumber;

    public BKashPayment(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void pay(double amount) {
        // BKash-specific mobile-wallet API call would go here.
        System.out.println("[BKash] Sent ৳" + amount + " request to " + phoneNumber);
    }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #3
// ---------------------------------------------------------------------------
class CryptoPayment implements PaymentStrategy {
    private final String walletAddress;

    public CryptoPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void pay(double amount) {
        // Blockchain transaction broadcasting would go here.
        System.out.println("[Crypto] Broadcast transaction of ৳" + amount +
                " (BTC-equivalent) to wallet " + walletAddress);
    }
}

// ---------------------------------------------------------------------------
// Context: does NOT know which concrete strategy it is using. It only
// depends on the PaymentStrategy abstraction, so new payment methods can be
// plugged in without touching this class -- satisfying the extensibility
// requirement.
// ---------------------------------------------------------------------------
class Checkout {
    private PaymentStrategy paymentStrategy;

    public Checkout(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    // Lets the customer switch payment methods at checkout, at runtime.
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class SecA_PaymentSystem {
    public static void main(String[] args) {
        Checkout checkout = new Checkout(new CreditCardPayment("4111111111111234"));
        checkout.checkout(2500);

        // Customer switches to BKash for the next purchase -- no change
        // needed anywhere except selecting a different strategy object.
        checkout.setPaymentStrategy(new BKashPayment("01712345678"));
        checkout.checkout(800);

        // Customer switches to crypto.
        checkout.setPaymentStrategy(new CryptoPayment("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"));
        checkout.checkout(15000);

        // Adding a brand new method later would just mean writing a new
        // class, e.g. `class WhatsAppPay implements PaymentStrategy {...}`,
        // with zero changes to Checkout.
    }
}
