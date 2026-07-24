interface Notification {
    void notifyUser();
}

class SMSNotification implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("SMS Notification");
    }
    
}

class EmailNotification implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("Email Notification");
    }

}

class PushNotification implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("Push Notification");
    }

}

class NotificationFactory {
    public Notification createNotification(String x) {
        switch (x) {
            case "SMS" -> {
                return new SMSNotification();
            }
            case "Email" -> {
                return new EmailNotification();
            }
            case "Push" -> {
                return new PushNotification();
            }
            default -> throw new AssertionError();
        }
    }
}
 
public class NotifictionLibrary {
    public static void main(String[] args) {
        NotificationFactory factory = new NotificationFactory();

        try {
            Notification email = factory.createNotification("Email");
            email.notifyUser();

            Notification sms = factory.createNotification("SMS");
            sms.notifyUser();

            Notification push = factory.createNotification("Push");
            push.notifyUser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
