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

public class AdapterPatternDemo {
    public static void main(String[] args) {
        Target target = new Adapter(new Adaptee());
        target.request();
    }
}