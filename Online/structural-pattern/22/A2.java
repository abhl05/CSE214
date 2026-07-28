import java.util.ArrayList;
import java.util.List;

// Component — common interface for leaf and composite
interface Zbazar {
    double getPrice();
    String getName();
    double getWeight();
}

// Leaf — individual item
class Leaf implements Zbazar {
    private String name;
    private double price;
    private double weight;

    Leaf(String name, double price, double weight) {
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

    public double getPrice() { return price; }
    public String getName() { return name; }
    public double getWeight() { return weight; }
}

// Composite — a bundle that can hold leaves and/or other composites
class Composite implements Zbazar {
    private String name;
    private List<Zbazar> children = new ArrayList<>();

    Composite(String name) { this.name = name; }

    public void add(Zbazar c) { children.add(c); }
    public void remove(Zbazar c) { children.remove(c); }

    public double getPrice() {
        double total = 0;
        for (Zbazar c : children) total += c.getPrice();
        return total;
    }

    public double getWeight() {
        double total = 0;
        for (Zbazar c : children) total += c.getWeight();
        return total;
    }

    public String getName() { return name; }
}

// not necessary
class zBazarCatalog {

    // Predefined Single Items
    public static Zbazar getRice() {
        return new Leaf("Miniket Rice", 30.0, 5.0);
    }

    public static Zbazar getOil() {
        return new Leaf("Soybean Oil", 15.0, 2.0);
    }

    public static Zbazar getSugar() {
        return new Leaf("Sugar", 5.0, 1.0);
    }

    // Predefined Packages
    public static Zbazar getSmallPackage() {
        Composite smallPackage = new Composite("Small Preset Package");
        smallPackage.add(getRice());
        smallPackage.add(getOil());
        return smallPackage;
    }

    public static Zbazar getFamilyPackage() {
        Composite familyPackage = new Composite("Family Preset Package");
        familyPackage.add(getRice());
        familyPackage.add(getRice()); // 2 bags of rice
        familyPackage.add(getOil());
        familyPackage.add(getSugar());
        return familyPackage;
    }
}

public class A2 {
    public static void main(String[] args) {
        Composite bundle = new Composite("Rice Bundle");
        bundle.add(new Leaf("chal", 300, 1));
        bundle.add(new Leaf("dal", 500, 1));

        Composite nested = new Composite("Basic meal");
        nested.add(new Leaf("chal", 100, 0.4));
        bundle.add(nested); // bundle-within-bundle

        System.out.println(bundle.getName() + " total price: " + bundle.getPrice());
    }
}
