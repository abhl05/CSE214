// Component — common interface for base object and decorators
interface Component {
    String getDescription();
    double getCost();
}

// ConcreteComponent — the base object being decorated
class ConcreteComponent implements Component {
    public String getDescription() { return "Base Component"; }
    public double getCost() { return 100; }
}

// Decorator — abstract wrapper, holds a reference to a Component
abstract class Decorator implements Component {
    protected Component wrapped;

    Decorator(Component c) { this.wrapped = c; }

    public String getDescription() { return wrapped.getDescription(); }
    public double getCost() { return wrapped.getCost(); }
}

// ConcreteDecorators — each adds its own behavior/cost
class FeatureA extends Decorator {
    FeatureA(Component c) { super(c); }
    public String getDescription() { return super.getDescription() + " + Feature A"; }
    public double getCost() { return super.getCost() + 30; }
}

class FeatureB extends Decorator {
    FeatureB(Component c) { super(c); }
    public String getDescription() { return super.getDescription() + " + Feature B"; }
    public double getCost() { return super.getCost() + 50; }
}

public class DecoratorPatternDemo {
    public static void main(String[] args) {
        // Decorators wrap in any combination, in any order
        Component item = new FeatureB(new FeatureA(new ConcreteComponent()));
        System.out.println(item.getDescription() + " -> Cost: " + item.getCost());
    }
}