/*
 * ================================================================================
 * SOURCE: CSE-214, 2021 batch, "Online 3" (Section A2)  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * A banking platform needs a notification system supporting multiple
 * mediums: email, SMS, and mobile-app notifications, used for alerts such
 * as transaction updates, low-balance warnings, and promotional offers.
 *
 *   - Customers choose their preferred notification channel, and must be
 *     able to switch it dynamically without affecting the underlying logic
 *     that processes/dispatches notifications.
 *   - The system must be easy to extend with new channels (WhatsApp, voice
 *     calls, push notifications) without changing existing code.
 *   - Each channel handles its own formatting/sending logic independently
 *     (clear separation of responsibilities), while every notification
 *     still carries a consistent structure/content across channels.
 *
 * ================================================================================
 * DESIGN PATTERN USED: STRATEGY
 * ================================================================================
 *
 * WHY STRATEGY IS THE CORRECT CHOICE:
 *   - "Send this notification" is one fixed goal reachable through several
 *     interchangeable algorithms (email formatting + SMTP-style send, SMS
 *     formatting + short-message send, in-app formatting + push send).
 *     That is the exact definition of Strategy: encapsulate each algorithm,
 *     make them interchangeable behind a common interface.
 *   - The requirement "customers can switch channel dynamically without
 *     impacting the underlying dispatch logic" maps directly onto
 *     Strategy's Context holding a reference to the current Strategy
 *     object, reassignable at runtime via a setter -- the dispatch logic
 *     (NotificationService) never needs to change.
 *   - Extensibility (adding WhatsApp/voice/push later) is satisfied because
 *     each new channel is just one new class implementing
 *     NotificationChannel; NotificationService is never modified
 *     (Open/Closed Principle).
 *   - "Each channel handles its own formatting/sending logic independently"
 *     is precisely what a ConcreteStrategy encapsulates -- self-contained
 *     algorithm implementation, hidden behind the shared interface.
 *   - Rejected alternative: Observer would fit if ONE event needed to fan
 *     out to MANY simultaneously-subscribed channels at once. Here,
 *     instead, a customer picks exactly ONE preferred channel to receive
 *     alerts through at a time -- a single interchangeable algorithm
 *     selection, which is Strategy's job, not a broadcast to many
 *     subscribers.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Strategy          -> NotificationChannel (interface)
 *   ConcreteStrategy   -> EmailNotification, SmsNotification, AppNotification
 *   Context           -> NotificationService (holds current
 *                          NotificationChannel, exposes a consistent
 *                          send(alertType, message) API regardless of
 *                          which channel is active)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Strategy: common interface for every notification channel. Every channel
// receives the same, consistently-structured input (a subject/alert type
// and a message body) so the *content* stays uniform even though each
// channel formats/delivers it differently.
// ---------------------------------------------------------------------------
interface NotificationChannel {
    void send(String alertType, String message);
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #1
// ---------------------------------------------------------------------------
class EmailNotification implements NotificationChannel {
    private final String emailAddress;

    public EmailNotification(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public void send(String alertType, String message) {
        // Email-specific formatting: subject line + body.
        System.out.println("[Email -> " + emailAddress + "] Subject: " + alertType +
                " | Body: " + message);
    }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #2
// ---------------------------------------------------------------------------
class SmsNotification implements NotificationChannel {
    private final String phoneNumber;

    public SmsNotification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void send(String alertType, String message) {
        // SMS-specific formatting: short, single-line text.
        System.out.println("[SMS -> " + phoneNumber + "] " + alertType + ": " + message);
    }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #3
// ---------------------------------------------------------------------------
class AppNotification implements NotificationChannel {
    private final String deviceId;

    public AppNotification(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public void send(String alertType, String message) {
        // In-app push-notification-specific formatting.
        System.out.println("[App push -> device " + deviceId + "] " + alertType + " - " + message);
    }
}

// ---------------------------------------------------------------------------
// Context: dispatch logic that stays identical no matter which channel is
// active. It only depends on the NotificationChannel abstraction.
// ---------------------------------------------------------------------------
class NotificationService {
    private NotificationChannel channel;

    public NotificationService(NotificationChannel channel) {
        this.channel = channel;
    }

    // Lets the customer switch their preferred channel dynamically, at
    // runtime, with zero impact on how alerts are actually processed.
    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public void notifyTransaction(String details) {
        channel.send("Transaction Update", details);
    }

    public void notifyLowBalance(double balance) {
        channel.send("Low Balance Warning", "Your balance is now ৳" + balance);
    }

    public void notifyPromotion(String offer) {
        channel.send("Promotional Offer", offer);
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class Online3A2_NotificationSystem {
    public static void main(String[] args) {
        NotificationService service = new NotificationService(new EmailNotification("abhi@example.com"));

        service.notifyTransaction("Incoming payment of ৳5,000 received.");
        service.notifyLowBalance(450.0);

        // Customer switches their preferred channel to SMS -- dispatch
        // logic above is completely unaffected by this switch.
        service.setChannel(new SmsNotification("01812345678"));
        service.notifyTransaction("Outgoing payment of ৳2,000 sent.");

        // Customer switches to in-app notifications.
        service.setChannel(new AppNotification("device-9f21"));
        service.notifyPromotion("20% cashback on your next bill payment!");

        // Adding a new channel later, e.g. WhatsApp, is just:
        //   class WhatsAppNotification implements NotificationChannel {...}
        // with no changes required to NotificationService.
    }
}
