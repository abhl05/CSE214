/*
 * ================================================================================
 * SOURCE: CSE-214 Online, 2023 batch, "Online-afternoon"  |  Duration: 30 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * An e-commerce platform gives the customer the BEST possible discount at
 * checkout. Three independent discount policies are each evaluated for
 * every purchase; the discounts are never combined/added -- only the
 * single highest percentage among them is applied.
 *
 *   1. Purchase Amount Discount: +5% for every complete ৳1,000 of the
 *      purchase amount, capped at 25% (so: <৳1,000 -> 0%, ৳1,000-1,999 ->
 *      5%, ৳2,000-2,999 -> 10%, ৳3,000-3,999 -> 15%, ৳4,000-4,999 -> 20%,
 *      ৳5,000+ -> 25%).
 *   2. Customer Category Discount: REGULAR -> 5%, PREMIUM -> 15%.
 *   3. Payment Method Discount: CARD -> 2%, MFS -> 5%, CASH -> 8%.
 *
 * All three policies are evaluated independently for every purchase; the
 * system then selects the SINGLE highest percentage among them (never
 * combines them) and applies only that one. If two policies tie for the
 * maximum, either may be treated as "the" selected one -- the final
 * payable amount is identical either way.
 *
 * Example: Purchase ৳3,500, PREMIUM, CASH ->
 *   Amount 15%, Category 15%, Payment 8% -> Applied discount = 15%.
 * Example: Purchase ৳5,500, PREMIUM, CASH ->
 *   Amount 25%, Category 15%, Payment 8% -> Applied discount = 25%.
 *
 * ================================================================================
 * DESIGN PATTERN USED: STRATEGY
 * ================================================================================
 *
 * WHY STRATEGY IS THE CORRECT CHOICE:
 *   - Each discount policy is an independent ALGORITHM that computes a
 *     discount percentage from the same input (a Purchase), following its
 *     own, self-contained business rule. This is exactly Strategy's
 *     purpose: encapsulate a family of interchangeable algorithms behind a
 *     common interface (`DiscountStrategy.calculateDiscount(Purchase)`),
 *     so each one can be implemented, tested, and modified independently.
 *   - Isolating each policy in its own class also satisfies an implicit
 *     extensibility need: adding a fourth policy later (e.g. a seasonal
 *     discount) is just one new class implementing DiscountStrategy, with
 *     no change to the other policies or to the selection logic.
 *   - The "selection logic" (evaluate ALL strategies, then keep the
 *     maximum) is deliberately kept inside the CONTEXT
 *     (DiscountCalculator), not inside the strategies themselves -- each
 *     strategy only knows how to compute ITS OWN discount, matching the
 *     Strategy pattern's separation between "the algorithm" (Strategy) and
 *     "how/when the algorithm's result is used" (Context).
 *   - Why NOT Chain of Responsibility: CoR passes a request along a chain
 *     until ONE handler decides to handle it and the rest are typically
 *     skipped -- order along the chain matters, and not every handler
 *     necessarily runs. Here, by contrast, EVERY policy is always
 *     evaluated (order is irrelevant), and the context deterministically
 *     compares all of their results -- that is a Strategy-with-selection
 *     scenario, not a Chain of Responsibility.
 *   - Why NOT State: none of the three discount rules represent the
 *     Purchase object transitioning through a lifecycle of internal
 *     conditions -- they are three parallel ways of scoring the exact same
 *     purchase, which is Strategy's domain.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Strategy          -> DiscountStrategy (interface)
 *   ConcreteStrategy   -> PurchaseAmountDiscountStrategy,
 *                          CustomerCategoryDiscountStrategy,
 *                          PaymentMethodDiscountStrategy
 *   Context           -> DiscountCalculator (holds the list of available
 *                          strategies, evaluates all of them, and selects
 *                          the best result -- never combines them)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Supporting domain types describing a single purchase.
// ---------------------------------------------------------------------------
enum CustomerType { REGULAR, PREMIUM }
enum PaymentMethod { CARD, MFS, CASH }

class Purchase {
    private final double amount;
    private final CustomerType customerType;
    private final PaymentMethod paymentMethod;

    public Purchase(double amount, CustomerType customerType, PaymentMethod paymentMethod) {
        this.amount = amount;
        this.customerType = customerType;
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() { return amount; }
    public CustomerType getCustomerType() { return customerType; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
}

// ---------------------------------------------------------------------------
// Strategy: common interface for every discount policy. Each policy looks
// only at the parts of the Purchase relevant to it and returns a discount
// PERCENTAGE (e.g. 15.0 means 15%).
// ---------------------------------------------------------------------------
interface DiscountStrategy {
    double calculateDiscount(Purchase purchase);
    String policyName();
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #1: +5% per complete ৳1,000, capped at 25%.
// ---------------------------------------------------------------------------
class PurchaseAmountDiscountStrategy implements DiscountStrategy {
    private static final double PERCENT_PER_THOUSAND = 5.0;
    private static final double MAX_PERCENT = 25.0;

    @Override
    public double calculateDiscount(Purchase purchase) {
        int completeThousands = (int) (purchase.getAmount() / 1000.0);
        double discount = completeThousands * PERCENT_PER_THOUSAND;
        return Math.min(discount, MAX_PERCENT);
    }

    @Override
    public String policyName() { return "Purchase Amount Discount"; }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #2: fixed percentage by customer category.
// ---------------------------------------------------------------------------
class CustomerCategoryDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(Purchase purchase) {
        switch (purchase.getCustomerType()) {
            case PREMIUM: return 15.0;
            case REGULAR:
            default:      return 5.0;
        }
    }

    @Override
    public String policyName() { return "Customer Category Discount"; }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #3: fixed percentage by payment method.
// ---------------------------------------------------------------------------
class PaymentMethodDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(Purchase purchase) {
        switch (purchase.getPaymentMethod()) {
            case CARD: return 2.0;
            case MFS:  return 5.0;
            case CASH: return 8.0;
            default:   return 0.0;
        }
    }

    @Override
    public String policyName() { return "Payment Method Discount"; }
}

// ---------------------------------------------------------------------------
// Context: knows about all available strategies, evaluates EVERY one of
// them independently for a given purchase, and picks the single highest
// result -- the discounts are never summed/combined.
// ---------------------------------------------------------------------------
class DiscountCalculator {
    private final java.util.List<DiscountStrategy> strategies = new java.util.ArrayList<>();

    public void addStrategy(DiscountStrategy strategy) {
        strategies.add(strategy);
    }

    public double applyBestDiscount(Purchase purchase) {
        double bestDiscountPercent = 0.0;
        String bestPolicyName = "None";

        for (DiscountStrategy strategy : strategies) {
            double discount = strategy.calculateDiscount(purchase);
            System.out.println("  " + strategy.policyName() + " -> " + discount + "%");
            if (discount > bestDiscountPercent) {
                bestDiscountPercent = discount;
                bestPolicyName = strategy.policyName();
            }
        }

        System.out.println("  Applied Discount = " + bestDiscountPercent + "% (from: " + bestPolicyName + ")");
        double finalAmount = purchase.getAmount() * (1 - bestDiscountPercent / 100.0);
        System.out.println("  Final Payable Amount = ৳" + finalAmount);
        return bestDiscountPercent;
    }
}

// ---------------------------------------------------------------------------
// Client / demo -- reproduces both worked examples from the problem
// statement.
// ---------------------------------------------------------------------------
public class OnlineAfternoon_SmartDiscountCalculator {
    public static void main(String[] args) {
        DiscountCalculator calculator = new DiscountCalculator();
        calculator.addStrategy(new PurchaseAmountDiscountStrategy());
        calculator.addStrategy(new CustomerCategoryDiscountStrategy());
        calculator.addStrategy(new PaymentMethodDiscountStrategy());

        System.out.println("Example 1: ৳3,500, PREMIUM, CASH");
        calculator.applyBestDiscount(new Purchase(3500, CustomerType.PREMIUM, PaymentMethod.CASH));
        // Expected: Amount 15%, Category 15%, Payment 8% -> Applied = 15%

        System.out.println("\nExample 2: ৳5,500, PREMIUM, CASH");
        calculator.applyBestDiscount(new Purchase(5500, CustomerType.PREMIUM, PaymentMethod.CASH));
        // Expected: Amount 25%, Category 15%, Payment 8% -> Applied = 25%

        System.out.println("\nExample 3: ৳600, REGULAR, MFS (below ৳1,000 threshold)");
        calculator.applyBestDiscount(new Purchase(600, CustomerType.REGULAR, PaymentMethod.MFS));
        // Expected: Amount 0%, Category 5%, Payment 5% -> Applied = 5%
    }
}
