/*
 * ================================================================================
 * OBSERVER PATTERN -- DROP-IN BOILERPLATE
 * ================================================================================
 *
 * HOW TO RECOGNIZE AN OBSERVER PROBLEM (signal phrases in the question):
 *   - "notify all [subscribers/followers/listeners/widgets/groups]
 *      whenever X changes"
 *   - "users can follow/subscribe to Y, and should be told when Y updates"
 *   - "add or remove [subscribers] at runtime"
 *   - ONE subject broadcasts a change to MANY, possibly unrelated,
 *     interested parties, each of which reacts in its own way.
 *   - The subject should NOT need to know the specifics of what each
 *     subscriber does with the update.
 *
 * NOT Observer if:
 *   - It's one caller picking ONE algorithm to run -> that's STRATEGY.
 *   - Objects need to coordinate with EACH OTHER through a central
 *     coordinator, with the coordinator containing the actual cross-object
 *     RULES (not just relaying an event) -> that's MEDIATOR.
 *
 * STRUCTURE (GoF roles -> classes below):
 *   Subject           -> Subject (maintains observer list, exposes
 *                          attach()/detach()/notifyObservers())  [RENAME ME]
 *   Observer          -> Observer (interface)
 *   ConcreteObserver   -> ConcreteObserverA, ConcreteObserverB, ...
 *
 * HOW TO ADAPT THIS FILE DURING THE EXAM:
 *   1. Rename `Observer` to the domain concept (e.g. StockObserver,
 *      BoardGroup) and change `update(...)`'s parameters to whatever data
 *      needs to be pushed out (a price, a message, an event object, ...).
 *   2. One ConcreteObserver class per kind of subscriber named in the
 *      problem (e.g. User, TickerTapeWidget, GraphWidget, Scouts). Each
 *      one reacts to the update in its own way inside update(...).
 *   3. Rename `Subject` to the domain noun (e.g. Stock, RavenBoard,
 *      StockData) and add whatever state it tracks (price, name, ...).
 *      Trigger notifyObservers() from inside its setter/mutator method.
 *   4. Delete this header comment and write your own problem statement +
 *      pattern justification in its place.
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Observer: anything that wants to be notified of a change implements
// this.
// TODO: rename, and change update(...)'s parameters to match the problem.
// ---------------------------------------------------------------------------
interface Observer {
    void update(String data);
}

// ---------------------------------------------------------------------------
// ConcreteObserver #1
// TODO: rename and implement the real reaction.
// ---------------------------------------------------------------------------
class ConcreteObserverA implements Observer {
    private final String name;

    public ConcreteObserverA(String name) {
        this.name = name;
    }

    @Override
    public void update(String data) {
        System.out.println(name + " (Observer A) received update: " + data);
    }
}

// ---------------------------------------------------------------------------
// ConcreteObserver #2
// TODO: rename and implement the real reaction.
// ---------------------------------------------------------------------------
class ConcreteObserverB implements Observer {
    private final String name;

    public ConcreteObserverB(String name) {
        this.name = name;
    }

    @Override
    public void update(String data) {
        System.out.println(name + " (Observer B) received update: " + data);
    }
}

// ---------------------------------------------------------------------------
// Subject: keeps its own list of subscribed observers and notifies all of
// them whenever its state changes. Knows nothing about what a concrete
// observer actually does with the notification -- full decoupling.
// TODO: rename to the domain noun; add whatever state the problem needs.
// ---------------------------------------------------------------------------
class Subject {
    private final java.util.List<Observer> observers = new java.util.ArrayList<>();
    private String state; // TODO: replace with the real tracked state (e.g. price)

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    public void setState(String newState) {
        this.state = newState;
        notifyObservers();
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(state);
        }
    }
}

// ---------------------------------------------------------------------------
// Demo -- proves the skeleton compiles and wires together correctly.
// TODO: delete/replace with the actual scenario from the problem.
// ---------------------------------------------------------------------------
public class Observer_Boilerplate {
    public static void main(String[] args) {
        Subject subject = new Subject();

        Observer obs1 = new ConcreteObserverA("Alice");
        Observer obs2 = new ConcreteObserverB("Bob");

        subject.attach(obs1);
        subject.attach(obs2);

        subject.setState("first update"); // both notified

        subject.detach(obs2); // unsubscribe at runtime

        subject.setState("second update"); // only obs1 notified
    }
}
