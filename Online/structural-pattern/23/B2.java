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

    public double getPrice() {
        return price;
    }

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

    public double getPrice() {
        return price;
    }

    public void print(String indent) {
        System.out.printf("%sGrocery: %s (£%.2f)%n", indent, name, price);
    }
}

// A set menu can only contain Food (no nested set menus, no groceries)
class SetMenu implements OrderItem {
    private String name;
    private List<Food> children = new ArrayList<>();

    public SetMenu(String name) {
        this.name = name;
    }

    public void addFood(Food f) {
        children.add(f);
    }

    public void removeFood(Food f) {
        children.remove(f);
    }

    public double getPrice() {
        double total = 0;
        for (Food c : children) total += c.getPrice();
        return total;
    }

    public void print(String indent) {
        System.out.println(indent + "Set Menu: " + name);
        for (Food c : children) c.print(indent + "  ");
    }
}

// A grocery package can contain Grocery items and/or other GroceryPackages
class GroceryPackage implements GroceryComponent {
    private String name;
    private List<GroceryComponent> children = new ArrayList<>();

    public GroceryPackage(String name) {
        this.name = name;
    }

    public void add(GroceryComponent c) {
        children.add(c);
    }

    public void remove(GroceryComponent c) {
        children.remove(c);
    }

    public double getPrice() {
        double total = 0;
        for (GroceryComponent c : children) total += c.getPrice();
        return total;
    }

    public void print(String indent) {
        System.out.println(indent + "Package: " + name);
        for (GroceryComponent c : children) c.print(indent + "  ");
    }
}

class Order {
    private List<OrderItem> items = new ArrayList<>();

    public void add(OrderItem item) {
        items.add(item);
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public void printReceipt() {
        System.out.println("========== RECEIPT ==========");
        for (OrderItem item : items) {
            item.print("");
        }
        System.out.println("-----------------------------");
        System.out.printf("Total Bill: £%.2f%n", getTotalPrice());
    }
}

public class B2 {
    public static void main(String[] args) {
        // Foods
        Food burger = new Food("Burger", 8);
        Food pizza = new Food("Pizza", 10);
        Food fries = new Food("French Fries", 3);

        // Set Menu
        SetMenu lunch = new SetMenu("Lunch Combo");
        lunch.addFood(burger);
        lunch.addFood(fries);

        // Grocery Items
        Grocery rice = new Grocery("Rice", 20);
        Grocery oil = new Grocery("Cooking Oil", 12);
        Grocery eggs = new Grocery("Eggs", 6);
        Grocery sugar = new Grocery("Sugar", 5);

        // Small Package
        GroceryPackage breakfastPack = new GroceryPackage("Breakfast Pack");
        breakfastPack.add(eggs);
        breakfastPack.add(sugar);

        // Large Package (contains another package)
        GroceryPackage monthlyPack = new GroceryPackage("Monthly Essentials");
        monthlyPack.add(rice);
        monthlyPack.add(oil);
        monthlyPack.add(breakfastPack);

        // Customer Order
        Order order = new Order();
        order.add(pizza);
        order.add(lunch);
        order.add(rice);
        order.add(monthlyPack);
        order.printReceipt();
    }
}