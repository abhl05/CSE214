import java.util.ArrayList;
import java.util.List;

// Component — common interface for leaf and composite
interface Component {
    double getPrice();
    String getName();
}

// Leaf — individual item
class Leaf implements Component {
    private String name;
    private double price;

    Leaf(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() { return price; }
    public String getName() { return name; }
}

// Composite — a bundle that can hold leaves and/or other composites
class Composite implements Component {
    private String name;
    private List<Component> children = new ArrayList<>();

    Composite(String name) { this.name = name; }

    public void add(Component c) { children.add(c); }
    public void remove(Component c) { children.remove(c); }

    public double getPrice() {
        double total = 0;
        for (Component c : children) total += c.getPrice();
        return total;
    }

    public String getName() { return name; }
}

public class A2 {
    public static void main(String[] args) {
        Composite bundle = new Composite("Gaming Bundle");
        bundle.add(new Leaf("CPU", 300));
        bundle.add(new Leaf("GPU", 500));

        Composite nested = new Composite("Basic Setup");
        nested.add(new Leaf("RAM", 100));
        bundle.add(nested); // bundle-within-bundle

        System.out.println(bundle.getName() + " total price: " + bundle.getPrice());
    }
}