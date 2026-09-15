/*
 * ================================================================================
 * SOURCE: CSE 314 Sessional, 2022 batch, "Online on Behavioral Patterns"
 *         Subsection A2  |  Time allotted: 20 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * King's Landing has a giant message board where ravens deliver scrolls
 * (messages) such as "Enemy spotted near the river". Build a RavenBoard
 * system where multiple groups -- Commander, Scouts, Supply Team, etc. --
 * can receive new messages and act on them (e.g. Scouts print "Dispatch
 * riders!", Supply Team prints "Update inventory!"). Groups can
 * subscribe/unsubscribe from the board AT RUNTIME (e.g. Scouts leaving the
 * board room). Demonstrate with 3 messages, some subscribe/unsubscribe
 * actions, and the resulting response prints.
 *
 * ================================================================================
 * DESIGN PATTERN USED: OBSERVER
 * ================================================================================
 *
 * WHY OBSERVER IS THE CORRECT CHOICE:
 *   - This is a textbook one-to-many broadcast: ONE message board (Subject)
 *     posts a new scroll, and every currently-subscribed group (Observer)
 *     must be told about it and react in its own way. The board itself
 *     does not need to know HOW each group reacts -- only that it must
 *     notify them.
 *   - "Groups can subscribe/unsubscribe at runtime" is precisely Observer's
 *     attach()/detach() mechanism -- the board keeps a mutable list of
 *     currently-interested observers, and that list can change between
 *     any two messages.
 *   - Each group's reaction (Scouts -> "Dispatch riders!", Supply Team ->
 *     "Update inventory!") is self-contained inside that group's own
 *     ConcreteObserver class, keeping reactions decoupled from each other
 *     and from the board -- new groups can be added later with zero
 *     changes to RavenBoard.
 *   - Rejected alternative: Mediator would fit if the GROUPS needed to
 *     coordinate with EACH OTHER through a central object (e.g. Scouts'
 *     action triggering a change in Supply Team's state). Here, instead,
 *     every group reacts independently and only to the broadcast message
 *     itself -- a simple one-directional notification fan-out, which is
 *     Observer's job.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Subject           -> RavenBoard (maintains subscriber list, offers
 *                          subscribe()/unsubscribe()/postMessage())
 *   Observer          -> BoardGroup (interface)
 *   ConcreteObserver   -> Commander, Scouts, SupplyTeam
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Observer: any group that wants to receive scrolls posted to the board
// implements this.
// ---------------------------------------------------------------------------
interface BoardGroup {
    void receiveMessage(String message);
}

// ---------------------------------------------------------------------------
// ConcreteObserver #1 -- reacts by simply acknowledging the message.
// ---------------------------------------------------------------------------
class Commander implements BoardGroup {
    @Override
    public void receiveMessage(String message) {
        System.out.println("Commander received: \"" + message + "\". Noted, awaiting further reports.");
    }
}

// ---------------------------------------------------------------------------
// ConcreteObserver #2 -- always mobilises riders on any scroll.
// ---------------------------------------------------------------------------
class Scouts implements BoardGroup {
    @Override
    public void receiveMessage(String message) {
        System.out.println("Scouts received: \"" + message + "\". Dispatch riders!");
    }
}

// ---------------------------------------------------------------------------
// ConcreteObserver #3 -- always updates inventory on any scroll.
// ---------------------------------------------------------------------------
class SupplyTeam implements BoardGroup {
    @Override
    public void receiveMessage(String message) {
        System.out.println("Supply Team received: \"" + message + "\". Update inventory!");
    }
}

// ---------------------------------------------------------------------------
// Subject: the raven message board itself. Holds the list of currently
// subscribed groups and broadcasts every new scroll to all of them.
// ---------------------------------------------------------------------------
class RavenBoard {
    private final java.util.List<BoardGroup> subscribers = new java.util.ArrayList<>();

    public void subscribe(BoardGroup group) {
        subscribers.add(group);
    }

    public void unsubscribe(BoardGroup group) {
        subscribers.remove(group);
    }

    public void postMessage(String message) {
        System.out.println("\nA raven delivers a new scroll: \"" + message + "\"");
        for (BoardGroup group : subscribers) {
            group.receiveMessage(message);
        }
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class A2_RavenBoard {
    public static void main(String[] args) {
        RavenBoard board = new RavenBoard();

        Commander commander = new Commander();
        Scouts scouts = new Scouts();
        SupplyTeam supplyTeam = new SupplyTeam();

        // All three groups start subscribed.
        board.subscribe(commander);
        board.subscribe(scouts);
        board.subscribe(supplyTeam);

        board.postMessage("Enemy spotted near the river");

        // Scouts leave the board room (unsubscribe at runtime).
        board.unsubscribe(scouts);
        board.postMessage("Winter supplies running low");

        // Scouts return later (re-subscribe at runtime).
        board.subscribe(scouts);
        board.postMessage("Ships seen in the east");
    }
}
