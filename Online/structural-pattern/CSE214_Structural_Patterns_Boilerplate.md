# CSE-214 Structural Design Patterns — Question Bank Categorization & Boilerplate

## 1. Categorization of Past Questions

| Question | Pattern | Core Signal |
|---|---|---|
| A1 — Legacy Weather Service | **Adapter** | Existing class has wrong interface, can't modify it |
| C2 — SQL/NoSQL integration | **Adapter** | Same: wrap incompatible class behind expected interface |
| Online-2 B2 — Smart Home (OldSmartBulb, LegacyHeater) | **Adapter** | Multiple third-party classes, each with a different interface, must fit `SmartDevice` |
| A2 — Computer hardware bundles | **Composite** | Bundles containing items and/or other bundles, treated uniformly |
| Online-2 A2 — ZBazar custom bazar packages | **Composite** | Same tree-of-packages structure |
| C1 — CoffeeTong | **Decorator** | Base coffee + wrap-on ingredients, cost/description accumulate |
| B1 — Delivery discounts | **Decorator** | Multiple discounts stack independently on a base price |
| B2 — Hardware component add-ons | **Decorator** | Warranty/installation/perf boost stack on a base component |
| Online-2 C1 — ZBazar Ramadan add-ons | **Decorator** | Fruit/sweets/gift-wrap stack on a base package |
| Online-2 A1 — IoT notification options | **Decorator** | Encryption/priority/logging stack on a base notification send |
| Online-2 B1 — Dalchal notifications | **Bridge** | Channels (email/SMS/push/WhatsApp) × event types vary independently |
| Online-3 C2 — ZBazar delivery × transport | **Bridge** | Delivery type × transport tech vary independently |

**Rule of thumb:**
- One object, optional stackable extras → **Decorator**
- Tree of parts/wholes → **Composite**
- Incompatible existing interface → **Adapter**
- Two dimensions multiplying against each other → **Bridge**

---

## 2. Adapter Pattern Boilerplate

```java
// Target interface expected by the client
interface Target {
    void request();
}

// Adaptee — existing incompatible class (cannot modify)
class Adaptee {
    void specificRequest() {
        System.out.println("Adaptee's specific request");
    }
}

// Adapter — implements Target, wraps Adaptee internally
class Adapter implements Target {
    private Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {
        adaptee.specificRequest(); // translate call
    }
}

public class Main {
    public static void main(String[] args) {
        Target target = new Adapter(new Adaptee());
        target.request();
    }
}
```

**Multiple incompatible classes?** (like Online-2 B2 with OldSmartBulb + LegacyHeater) — write one Adapter class per legacy class, each implementing the same target interface (`SmartDevice`), each wrapping its own legacy object.

---

## 3. Composite Pattern Boilerplate

```java
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

public class Main {
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
```

---

## 4. Decorator Pattern Boilerplate

```java
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

public class Main {
    public static void main(String[] args) {
        // Decorators wrap in any combination, in any order
        Component item = new FeatureB(new FeatureA(new ConcreteComponent()));
        System.out.println(item.getDescription() + " -> Cost: " + item.getCost());
    }
}
```

---

## 5. Bridge Pattern Boilerplate

```java
// Implementor — the "how" side (e.g. transport method / channel)
interface Implementor {
    void execute(String data);
}

class ImplementorA implements Implementor {
    public void execute(String data) {
        System.out.println("ImplementorA handling: " + data);
    }
}

class ImplementorB implements Implementor {
    public void execute(String data) {
        System.out.println("ImplementorB handling: " + data);
    }
}

// Abstraction — the "what" side (e.g. delivery type / event type)
abstract class Abstraction {
    protected Implementor implementor;

    Abstraction(Implementor implementor) { this.implementor = implementor; }

    abstract void operation(String data);
}

class RefinedAbstractionX extends Abstraction {
    RefinedAbstractionX(Implementor implementor) { super(implementor); }
    void operation(String data) {
        System.out.println("RefinedAbstractionX preparing...");
        implementor.execute(data);
    }
}

class RefinedAbstractionY extends Abstraction {
    RefinedAbstractionY(Implementor implementor) { super(implementor); }
    void operation(String data) {
        System.out.println("RefinedAbstractionY preparing...");
        implementor.execute(data);
    }
}

public class Main {
    public static void main(String[] args) {
        // Abstraction and Implementor can be mixed and matched freely
        Abstraction a = new RefinedAbstractionX(new ImplementorA());
        a.operation("payload1");

        Abstraction b = new RefinedAbstractionY(new ImplementorB());
        b.operation("payload2");
    }
}
```

**Mapping to Online-3 C2:** `Abstraction` = delivery type (Standard/Express/Scheduled), `Implementor` = transport tech (Bike/Van/Drone/Robot). Adding a new transport method only means writing one new `Implementor` class — no changes to delivery-type logic, and vice versa.
