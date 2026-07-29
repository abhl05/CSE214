import java.util.ArrayList;
import java.util.List;

// Common interface for everything that can sit in an Order
interface OrderItem {
    double getPrice();
    void print(String indent);
}

// Marker interface: only grocery-family objects may nest inside a GroceryPackage
interface GroceryComponent extends OrderItem {}

class Food implements OrderItem {
    private String name;
    private double price;

    public Food(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() { return price; }

    public void print(String indent) {
        System.out.printf("%sFood: %s (£%.2f)%n", indent, name, price);
    }
}

class Grocery implements GroceryComponent {
    private String name;
    private double price;

    public Grocery(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() { return price; }

    public void print(String indent) {
        System.out.printf("%sGrocery: %s (£%.2f)%n", indent, name, price);
    }
}

// Shared composite logic, reused by SetMenu and GroceryPackage
abstract class AbstractComposite<T extends OrderItem> implements OrderItem {
    protected String name;
    protected List<T> children = new ArrayList<>();

    AbstractComposite(String name) { this.name = name; }

    public void add(T item) { children.add(item); }
    public void remove(T item) { children.remove(item); }

    public double getPrice() {
        double total = 0;
        for (T c : children) total += c.getPrice();
        return applyPricing(total);
    }

    // Hook for subclass-specific pricing rules (e.g. discounts)
    protected double applyPricing(double rawTotal) { return rawTotal; }

    protected abstract String label();

    public void print(String indent) {
        System.out.println(indent + label() + ": " + name);
        for (T c : children) c.print(indent + "  ");
    }
}

// Only Food allowed; 10% discount applied
class SetMenu extends AbstractComposite<Food> {
    public SetMenu(String name) { super(name); }

    public void addFood(Food f) { add(f); }
    public void removeFood(Food f) { remove(f); }

    @Override
    protected double applyPricing(double rawTotal) {
        return rawTotal * 0.9; // 10% off
    }

    @Override
    protected String label() { return "Set Menu"; }
}

// Only Grocery or nested GroceryPackage allowed
class GroceryPackage extends AbstractComposite<GroceryComponent> implements GroceryComponent {
    public GroceryPackage(String name) { super(name); }

    @Override
    protected String label() { return "Package"; }
}

class Order {
    private List<OrderItem> items = new ArrayList<>();

    public void add(OrderItem item) { items.add(item); }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) total += item.getPrice();
        return total;
    }

    public void printReceipt() {
        System.out.println("========== RECEIPT ==========");
        for (OrderItem item : items) item.print("");
        System.out.println("-----------------------------");
        System.out.printf("Total Bill: £%.2f%n", getTotalPrice());
    }
}

public class B2 {
    public static void main(String[] args) {
        Food burger = new Food("Burger", 8);
        Food pizza = new Food("Pizza", 10);
        Food fries = new Food("French Fries", 3);

        SetMenu lunch = new SetMenu("Lunch Combo");
        lunch.addFood(burger);
        lunch.addFood(fries);

        Grocery rice = new Grocery("Rice", 20);
        Grocery oil = new Grocery("Cooking Oil", 12);
        Grocery eggs = new Grocery("Eggs", 6);
        Grocery sugar = new Grocery("Sugar", 5);

        GroceryPackage breakfastPack = new GroceryPackage("Breakfast Pack");
        breakfastPack.add(eggs);
        breakfastPack.add(sugar);

        GroceryPackage monthlyPack = new GroceryPackage("Monthly Essentials");
        monthlyPack.add(rice);
        monthlyPack.add(oil);
        monthlyPack.add(breakfastPack);

        Order order = new Order();
        order.add(pizza);
        order.add(lunch);
        order.add(rice);
        order.add(monthlyPack);
        order.printReceipt();
    }
}