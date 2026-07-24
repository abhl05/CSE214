// 1. The common interface for all transport types
package transport;
interface Transport {
    void deliver();
}

// 2. Concrete implementation for Road delivery
class Truck implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering cargo by land in a box truck.");
    }
}

// 3. Concrete implementation for Sea delivery
class Ship implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering cargo by sea in a container ship.");
    }
}

// 4. The Factory class responsible for creating Transport objects
class TransportFactory {
    
    // This method encapsulates the creation logic
    public Transport createTransport(String mode) {
        if (mode == null || mode.isEmpty()) {
            return null;
        }
        
        switch (mode.toLowerCase()) {
            case "road":
                return new Truck();
            case "sea":
                return new Ship();
            // Future additions like "air" -> return new Airplane(); can easily be added here
            default:
                throw new IllegalArgumentException("Unknown transport mode: " + mode);
        }
    }
}

// 5. Client Code
public class LogisticsApp {
    public static void main(String[] args) {
        TransportFactory factory = new TransportFactory();

        // The client requests a transport mode without knowing the exact class being instantiated
        try {
            Transport roadTransport = factory.createTransport("road");
            roadTransport.deliver();

            Transport seaTransport = factory.createTransport("sea");
            seaTransport.deliver();
            
            // Uncommenting the below line will throw an exception since "air" isn't implemented yet
            // Transport airTransport = factory.createTransport("air");
            // airTransport.deliver();

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}