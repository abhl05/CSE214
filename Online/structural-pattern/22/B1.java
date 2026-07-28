// Implementor — the "how" side (e.g. transport method / channel)
interface CommunicationChannel {
    void sendMessage(String content);
}

class EmailChannel implements CommunicationChannel {
    @Override
    public void sendMessage(String content) {
        System.out.println("Sending email: " + content);
    }
}

class SmsChannel implements CommunicationChannel {
    @Override
    public void sendMessage(String content) {
        System.out.println("Sending SMS: " + content);
    }
}

class PushChannel implements CommunicationChannel {
    @Override
    public void sendMessage(String content) {
        System.out.println("Sending push notification: " + content);
    }
}

// Abstraction — the "what" side (e.g. delivery type / event type)
abstract class NotificationEvent {
    protected CommunicationChannel channel;

    NotificationEvent(CommunicationChannel channel) { this.channel = channel; }
    // abstract method to be implemented by subclasses, 
    // IMPORTANT: This is the bridge between the two hierarchies
    abstract void sendNotification();
}

class OrderDispatchedEvent extends NotificationEvent {
    OrderDispatchedEvent(CommunicationChannel channel) { super(channel); }
    void sendNotification() {
        channel.sendMessage("Your order has been dispatched!");
    }
}

class PaymentFailedEvent extends NotificationEvent {
    PaymentFailedEvent(CommunicationChannel channel) { super(channel); }
    void sendNotification() {
        channel.sendMessage("Payment failed. Please try again.");
    }
}

class OrderConfirmedEvent extends NotificationEvent {
    OrderConfirmedEvent(CommunicationChannel channel) { super(channel); }
    void sendNotification() {
        channel.sendMessage("Your order has been confirmed!");
    }
}

public class B1 {
    public static void main(String[] args) {
        // Abstraction and Implementor can be mixed and matched freely
        // Setup channels
        CommunicationChannel email = new EmailChannel();
        CommunicationChannel sms = new SmsChannel();
        CommunicationChannel push = new PushChannel();

        System.out.println("=== Dalchal Notification System ===\n");

        // A user prefers SMS for Dispatched alerts
        NotificationEvent dispatchAlert = new OrderDispatchedEvent(sms);
        dispatchAlert.sendNotification();

        // A user prefers Push for Confirmation alerts
        NotificationEvent confirmAlert = new OrderConfirmedEvent(push);
        confirmAlert.sendNotification();

        // System sends an Email for a Payment Failed alert
        NotificationEvent paymentAlert = new PaymentFailedEvent(email);
        paymentAlert.sendNotification();
    }
}