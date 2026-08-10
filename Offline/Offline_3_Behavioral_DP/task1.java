import java.util.ArrayList;

abstract class BDAlert {
    ArrayList<Citizen> subscribers = new ArrayList<>();
    
    void subscribe(Citizen citizen) {
        subscribers.add(citizen);
    }

    void unsubscribe(Citizen citizen) {
        subscribers.remove(citizen);
    }

    abstract void notifySubscribers();
}

// helper alert class to pass alert type to citizens
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

// Concrete alert classes

class FloodAlert extends BDAlert {
    @Override
    void notifySubscribers() {
        for (Citizen citizen : subscribers) {
            citizen.update(new AlertInfo("Flood Alert", "Flood", "Local Area", "High", "Evacuate immediately!"));
        }
    }

    void floodWarning() {
        System.out.println("Flood warning issued!");
        notifySubscribers();
    }
}

class EarthquakeAlert extends BDAlert {
    @Override
    void notifySubscribers() {
        for (Citizen citizen : subscribers) {
            citizen.update(new AlertInfo("Earthquake Alert", "Earthquake", "Local Area", "High", "Drop, cover, and hold on!"));
        }
    }

    void earthquakeWarning() {
        System.out.println("Earthquake warning issued!");
        notifySubscribers();
    }
}

class FireAlert extends BDAlert {
    @Override
    void notifySubscribers() {
        for (Citizen citizen : subscribers) {
            citizen.update(new AlertInfo("Fire Alert", "Fire", "Local Area", "High", "Evacuate immediately!"));
        }
    }

    void fireWarning() {
        System.out.println("Fire warning issued!");
        notifySubscribers();
    }
}

interface Citizen {
    void update(AlertInfo alertInfo);
}

// Example implementation of a Citizen
class LocalResident implements Citizen {
    private String name;

    LocalResident(String name) {
        this.name = name;
    }

    @Override
    public void update(AlertInfo alertInfo) {
        System.out.println(name + " received " + alertInfo.getAlertType() + " alert.");
        System.out.println(alertInfo);
    }
}

public class task1 {
    public static void main(String[] args) {
        // Create alert instances
        FloodAlert floodAlert = new FloodAlert();
        EarthquakeAlert earthquakeAlert = new EarthquakeAlert();
        FireAlert fireAlert = new FireAlert();

        // Create citizen instances
        LocalResident resident1 = new LocalResident("Alice");
        LocalResident resident2 = new LocalResident("Bob");

        // Subscribe citizens to alerts
        floodAlert.subscribe(resident1);
        earthquakeAlert.subscribe(resident1);
        fireAlert.subscribe(resident2);

        // Trigger alerts
        floodAlert.floodWarning();
        earthquakeAlert.earthquakeWarning();
        fireAlert.fireWarning();
        // Unsubscribe a citizen and trigger another alert
        floodAlert.unsubscribe(resident1);
        floodAlert.floodWarning();
    }
}
