/*
 * ================================================================================
 * SOURCE: CSE-214, 2021 batch, "Online 3" (Sec C)  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * A stock-trading app lets clients track stock prices in real time. If the
 * price of a stock a client is interested in changes, the app should
 * automatically notify that client. Each user can follow multiple stocks;
 * whenever a stock's price changes, every user tracking that stock must be
 * notified. Users can add/remove stocks they track at any time. Each stock
 * has a name and a price.
 *
 * The problem statement supplies this exact main()/output contract, which
 * this file reproduces faithfully:
 *   - Alice follows both Google and Apple stock; Bob follows only Google.
 *   - Google's price changes to 1550 -> both Alice and Bob are notified.
 *   - Apple's price changes to 1250 -> only Alice is notified.
 *   - Alice then unfollows Google.
 *   - Google's price changes to 1600 -> only Bob is notified (since Alice
 *     unfollowed it).
 *
 * ================================================================================
 * DESIGN PATTERN USED: OBSERVER
 * ================================================================================
 *
 * WHY OBSERVER IS THE CORRECT CHOICE:
 *   - This is the canonical Observer scenario: a one-to-many dependency
 *     between a Subject (a Stock) and multiple Observers (Users), where a
 *     state change in the Subject (price update) must automatically
 *     propagate to every currently-subscribed Observer, without the
 *     Subject knowing anything about what its observers actually do with
 *     that information.
 *   - The requirement that users can "add or remove the stocks they want
 *     to track" at any time maps directly onto Observer's
 *     attach()/detach() (subscribe/unsubscribe) operations.
 *   - A single stock can have many interested users, and a single user can
 *     be interested in many stocks -- Observer naturally supports this
 *     many-to-many relationship because each Stock (Subject) keeps its own
 *     independent list of subscribed User (Observer) objects.
 *   - Rejected alternative: Mediator would fit if Stocks and Users needed
 *     to coordinate complex, centralized interaction logic through a
 *     third-party coordinator object. Here the relationship is a simple,
 *     direct "publish price change -> notify subscribers" broadcast, which
 *     is exactly Observer's purpose, without any need for a mediating
 *     object.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Subject           -> Stock (maintains a list of observers, offers
 *                          follow()/unfollow()/notifyObservers())
 *   Observer          -> StockObserver (interface)
 *   ConcreteObserver   -> User (implements update(), reacts by printing a
 *                          notification)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Observer: anything that wants to be notified of a stock price change
// implements this.
// ---------------------------------------------------------------------------
interface StockObserver {
    void update(String stockName, double newPrice);
}

// ---------------------------------------------------------------------------
// Subject: a Stock keeps track of its own list of subscribed observers and
// notifies all of them whenever its price changes. The Stock has no idea
// what a "User" is or what it does with the notification -- full
// decoupling between subject and observers.
// ---------------------------------------------------------------------------
class Stock {
    private final String name;
    private double price;
    private final java.util.List<StockObserver> observers = new java.util.ArrayList<>();

    public Stock(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    // "Following" a stock == subscribing as an observer.
    public void follow(StockObserver observer) {
        observers.add(observer);
    }

    // "Unfollowing" a stock == unsubscribing.
    public void unfollow(StockObserver observer) {
        observers.remove(observer);
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
        notifyObservers();
    }

    private void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(name, price);
        }
    }
}

// ---------------------------------------------------------------------------
// ConcreteObserver: a User reacts to a price-change notification by
// printing an alert. Any other reaction (push notification, email, etc.)
// could be substituted here without changing Stock at all.
// ---------------------------------------------------------------------------
class User implements StockObserver {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(String stockName, double newPrice) {
        System.out.println(name + " has been notified: The price of " + stockName + " is now " + newPrice);
    }
}

// ---------------------------------------------------------------------------
// Client / demo -- reproduces the exact scenario given in the problem
// statement, including its sample output.
// ---------------------------------------------------------------------------
public class SecC_StockPriceTracker {
    public static void main(String[] args) {
        // Create stocks
        Stock googleStock = new Stock("Google", 1500);
        Stock appleStock = new Stock("Apple", 1200);

        // Create users
        User user1 = new User("Alice");
        User user2 = new User("Bob");

        // Code for following stocks
        googleStock.follow(user1); // Alice follows Google
        googleStock.follow(user2); // Bob follows Google
        appleStock.follow(user1);  // Alice follows Apple

        // Simulate price changes
        System.out.println("Updating Google stock price...");
        googleStock.setPrice(1550);

        System.out.println("\nUpdating Apple stock price...");
        appleStock.setPrice(1250);

        // Code for unfollowing stocks
        googleStock.unfollow(user1); // Alice unfollows Google

        // Simulate price changes again
        System.out.println("\nUpdating Google stock price again...");
        googleStock.setPrice(1600);
    }
}
