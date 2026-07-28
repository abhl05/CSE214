interface ZBazarPackages {
    String getDescription();
    double getPrice();
}

// ConcreteComponent — the base object being decorated
class StandardPackage implements ZBazarPackages {
    public String getDescription() { return "StandardPackage"; }
    public double getPrice() { return 10.0; }
}

class SpecialPackage implements ZBazarPackages {
    public String getDescription() { return "SpecialPackage"; }
    public double getPrice() { return 15.0; }
}

class PremiumPackage implements ZBazarPackages {
    public String getDescription() { return "PremiumPackage"; }
    public double getPrice() { return 20.0; }
}

// Decorator — abstract wrapper, holds a reference to a Component
abstract class Decorator implements ZBazarPackages {
    protected ZBazarPackages wrapped;

    Decorator(ZBazarPackages c) { this.wrapped = c; }

    public String getDescription() { return wrapped.getDescription(); }
    public double getPrice() { return wrapped.getPrice(); }
}

// ConcreteDecorators — each adds its own behavior/cost
class FruitPackageDecorator extends Decorator {
    FruitPackageDecorator(ZBazarPackages c) { super(c); }
    @Override
    public String getDescription() { return super.getDescription() + " + Fruit Package on"; }
    public double getPrice() { return super.getPrice() + 5.0; }
}

class SweetPackageDecorator extends Decorator {
    SweetPackageDecorator(ZBazarPackages c) { super(c); }
    @Override
    public String getDescription() { return super.getDescription() + " + Sweet Package on"; }
    public double getPrice() { return super.getPrice() + 3.0; }
}

class PremiumwrapPackageDecorator extends Decorator {
    PremiumwrapPackageDecorator(ZBazarPackages c) { super(c); }
    public String getDescription() { return super.getDescription() + " + Premium Wrap on"; }
    public double getPrice() { return super.getPrice() + 7.0; }
}

public class C1 {
    public static void main(String[] args) {
        // Decorators wrap in any combination, in any order
        ZBazarPackages item = new SweetPackageDecorator(new FruitPackageDecorator(new PremiumwrapPackageDecorator(new StandardPackage())));
        System.out.println(item.getDescription());
        System.out.println("Price: $" + item.getPrice());
    }
}

