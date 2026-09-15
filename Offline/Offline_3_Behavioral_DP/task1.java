import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
interface Publisher {
    void register(Observer citizen);
    
    void subscribe(Observer citizen, String alertCategory); 
    
    void unsubscribe(Observer citizen, String alertCategory);
    
    void notifySubscribers(AlertInfo alertInfo);
}

interface Observer {
    void update(AlertInfo alertInfo);
    String getName();
}

class AlertInfo {
    private String alertTitle;
    private String alertCategory;
    private String affectedLocation;
    private String severityLevel;
    private String safetyInstructions;

    AlertInfo(String alertTitle, String alertCategory, String affectedLocation, String severityLevel, String safetyInstructions) {
        this.alertTitle = alertTitle;
        this.alertCategory = alertCategory;
        this.affectedLocation = affectedLocation;
        this.severityLevel = severityLevel;
        this.safetyInstructions = safetyInstructions;
    }

    public String getAlertType() {
        return alertCategory;
    }

    @Override
    public String toString() {
        return "AlertInfo{" +
                "alertTitle='" + alertTitle + '\'' +
                ", alertCategory='" + alertCategory + '\'' +
                ", affectedLocation='" + affectedLocation + '\'' +
                ", severityLevel='" + severityLevel + '\'' +
                ", safetyInstructions='" + safetyInstructions + '\'' +
                '}';
    }
}


class BDAlert implements Publisher {
    private List<Observer> registeredCitizens = new ArrayList<>();
    private Map<String, ArrayList<Observer>> subscribers = new HashMap<>();

    @Override
    public void register(Observer citizen) {
        if (!registeredCitizens.contains(citizen)) {
            registeredCitizens.add(citizen);
            System.out.println(citizen.getName() + " registered in the system.");
        }
    }

    @Override
    public void subscribe(Observer citizen, String alertCategory) {
        if(!registeredCitizens.contains(citizen)) {
            System.out.println(citizen.getName() + " is not registered. Cannot subscribe to alerts.");
            return;
        }
        subscribers.putIfAbsent(alertCategory, new ArrayList<>());
        if (!subscribers.get(alertCategory).contains(citizen)) {
            subscribers.get(alertCategory).add(citizen);
            System.out.println(citizen.getName() + " subscribed to " + alertCategory + " alerts.");
        }
    }

    @Override
    public void notifySubscribers(AlertInfo alertInfo) {
        for (Observer citizen : subscribers.getOrDefault(alertInfo.getAlertType(), new ArrayList<>())) {
            citizen.update(alertInfo);
        }
    }

    @Override
    public void unsubscribe(Observer citizen, String alertCategory) {
        if (subscribers.containsKey(alertCategory)) {
            subscribers.get(alertCategory).remove(citizen);
            System.out.println(citizen.getName() + " unsubscribed from " + alertCategory + " alerts.");
        }
    }
}


class Citizen implements Observer {
    private String name;
    private ArrayList<AlertInfo> notifs = new ArrayList<>();

    public Citizen(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void update(AlertInfo alertInfo) {
        notifs.add(alertInfo);
        System.out.println(name + " received " + alertInfo.toString());
    }

    public void displayNotifications() {
        System.out.println(name + "'s notifications history:");
        if(notifs.isEmpty()) {
            System.out.println("No notifications received.");
            return;
        }
        for (AlertInfo alert : notifs) {
            System.out.println(alert);
        }
    }
}

public class Task1 {
    public static void main(String[] args) {
        BDAlert system = new BDAlert();
 
        // 1. Register citizens
        Citizen abhi  = new Citizen("Abhi");
        Citizen mina  = new Citizen("Mina");
        Citizen karim = new Citizen("Karim");
        system.register(abhi);
        system.register(mina);
        system.register(karim);
 
        // 2. Subscribe citizens to categories
        system.subscribe(abhi, "EARTHQUAKE");
        system.subscribe(abhi, "FLOOD");
        system.subscribe(mina, "FIRE");
        system.subscribe(karim, "FLOOD");
        system.subscribe(karim, "FIRE");
 
        // 4 & 5. Publish alerts - one per category - only subscribers get notified
        system.notifySubscribers(new AlertInfo("Magnitude 5.6 tremor", "EARTHQUAKE", "Chittagong", "HIGH", "Move to open ground"));
        system.notifySubscribers(new AlertInfo("Rising water levels", "FLOOD", "Sylhet", "MODERATE", "Move valuables to higher floor"));
        system.notifySubscribers(new AlertInfo("Market fire outbreak", "FIRE", "Dhaka", "CRITICAL", "Evacuate immediately"));
 
        // 3. Update subscription: Karim unsubscribes from FLOOD, subscribes to EARTHQUAKE
        System.out.println("\n[SYSTEM] Karim unsubscribes from FLOOD, subscribes to EARTHQUAKE");
        system.unsubscribe(karim, "FLOOD");
        system.subscribe(karim, "EARTHQUAKE");
 
        // 6. A newly registered/subscribed citizen must only receive FUTURE alerts
        Citizen rina = new Citizen("Rina");
        system.register(rina);
        system.subscribe(rina, "FLOOD");
 
        // Publish more alerts to verify the subscription update and the "future alerts only" rule
        system.notifySubscribers(new AlertInfo("Flash flood warning", "FLOOD", "Sunamganj", "HIGH", "Avoid riverbanks"));
        system.notifySubscribers(new AlertInfo("Aftershock detected", "EARTHQUAKE", "Chittagong", "MODERATE", "Stay alert"));
 
        // 7. Display notifications received by each citizen
        abhi.displayNotifications();
        mina.displayNotifications();
        karim.displayNotifications();  
        rina.displayNotifications();   
    }
}
