interface Notification {
    String notifBody(String message);
}

// ConcreteComponent — the base object being decorated
class Push implements Notification {
    public String notifBody(String message) { return "push" + message; }
}

class Email implements Notification {
    public String notifBody(String message) { return "Email" + message; }
}

class Sms implements Notification {
    public String notifBody(String message) { return "Sms" + message; }
}

// Decorator — abstract wrapper, holds a reference to a Component
abstract class Decorator implements Notification {
    protected Notification wrapped;

    Decorator(Notification c) { this.wrapped = c; }

    public String notifBody(String message) { return wrapped.notifBody(message); }
}

// ConcreteDecorators — each adds its own behavior/cost
class Encryption extends Decorator {
    Encryption(Notification c) { super(c); }
    public String notifBody(String message) { return super.notifBody(message) + " + Encryption on"; }
}

class Logging extends Decorator {
    Logging(Notification c) { super(c); }
    public String notifBody(String message) { return super.notifBody(message) + " + Logging on"; }
}

class Priority extends Decorator {
    Priority(Notification c) { super(c); }
    public String notifBody(String message) { return super.notifBody(message) + " + Priority on"; }
}

public class A1 {
    public static void main(String[] args) {
        // Decorators wrap in any combination, in any order
        Notification item = new Logging(new Encryption(new Push()));
        System.out.println(item.notifBody("hello"));
    }
}

