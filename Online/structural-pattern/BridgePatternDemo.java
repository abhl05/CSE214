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

public class BridgePatternDemo {
    public static void main(String[] args) {
        // Abstraction and Implementor can be mixed and matched freely
        Abstraction a = new RefinedAbstractionX(new ImplementorA());
        a.operation("payload1");

        Abstraction b = new RefinedAbstractionY(new ImplementorB());
        b.operation("payload2");
    }
}