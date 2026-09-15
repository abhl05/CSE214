/*
 * ================================================================================
 * SOURCE: CSE 314 Sessional, 2022 batch, "Online on Behavioral Patterns"
 *         Subsection C2  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * Build a financial dashboard for a day-trading firm. Traders need to see
 * real-time changes in stock prices across several distinct, unrelated
 * widgets:
 *   1. A Ticker Tape widget needs to scroll the new price.
 *   2. A Graph widget needs to plot the new data point.
 *   3. A Buy/Sell Bot needs to evaluate whether the new price triggers an
 *      automated trade.
 *
 * The core StockData feed must NOT know the specifics of these widgets,
 * but must be able to notify all of them automatically whenever a price
 * update occurs. There must also be a way to add or remove widgets.
 *
 * ================================================================================
 * DESIGN PATTERN USED: OBSERVER
 * ================================================================================
 *
 * WHY OBSERVER IS THE CORRECT CHOICE:
 *   - This is the same one-to-many "subject changes -> notify all
 *     interested parties" shape the Observer pattern was designed for: one
 *     StockData feed (Subject), several unrelated widgets (Observers) that
 *     must all react automatically whenever the feed's state changes.
 *   - "StockData feed doesn't know the specifics of these widgets" is a
 *     direct restatement of Observer's core benefit: the Subject only
 *     depends on a generic Observer interface, never on the concrete
 *     widget classes -- so TickerTape, Graph, and BuySellBot can each
 *     react completely differently without StockData knowing or caring
 *     how.
 *   - "Provision of adding or removing widgets" maps directly onto
 *     Observer's attach()/detach() operations, letting the set of
 *     interested widgets change dynamically at runtime.
 *   - Rejected alternative: Strategy would fit if only ONE component
 *     needed to pick ONE algorithm to process the price. Here, instead,
 *     potentially MANY independent components must all be informed of
 *     every single price change simultaneously -- a broadcast, not an
 *     algorithm choice, so Observer is correct.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Subject           -> StockData (maintains observer list, exposes
 *                          addWidget()/removeWidget(), notifies on
 *                          setPrice())
 *   Observer          -> PriceObserver (interface)
 *   ConcreteObserver   -> TickerTapeWidget, GraphWidget, BuySellBot
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Observer: any widget that wants to react to a stock price update
// implements this.
// ---------------------------------------------------------------------------
interface PriceObserver {
    void onPriceUpdate(String stockSymbol, double newPrice);
}

// ---------------------------------------------------------------------------
// ConcreteObserver #1
// ---------------------------------------------------------------------------
class TickerTapeWidget implements PriceObserver {
    @Override
    public void onPriceUpdate(String stockSymbol, double newPrice) {
        System.out.println("[TickerTape] Scrolling new price: " + stockSymbol + " -> " + newPrice);
    }
}

// ---------------------------------------------------------------------------
// ConcreteObserver #2
// ---------------------------------------------------------------------------
class GraphWidget implements PriceObserver {
    @Override
    public void onPriceUpdate(String stockSymbol, double newPrice) {
        System.out.println("[Graph] Plotting new data point for " + stockSymbol + ": " + newPrice);
    }
}

// ---------------------------------------------------------------------------
// ConcreteObserver #3
// ---------------------------------------------------------------------------
class BuySellBot implements PriceObserver {
    // A simple illustrative threshold-based trading rule.
    private final double buyBelow;
    private final double sellAbove;

    public BuySellBot(double buyBelow, double sellAbove) {
        this.buyBelow = buyBelow;
        this.sellAbove = sellAbove;
    }

    @Override
    public void onPriceUpdate(String stockSymbol, double newPrice) {
        if (newPrice <= buyBelow) {
            System.out.println("[BuySellBot] " + stockSymbol + " at " + newPrice +
                    " is at/below threshold " + buyBelow + " -> triggering BUY order.");
        } else if (newPrice >= sellAbove) {
            System.out.println("[BuySellBot] " + stockSymbol + " at " + newPrice +
                    " is at/above threshold " + sellAbove + " -> triggering SELL order.");
        } else {
            System.out.println("[BuySellBot] " + stockSymbol + " at " + newPrice +
                    " is within normal range -> no trade triggered.");
        }
    }
}

// ---------------------------------------------------------------------------
// Subject: the core feed. Knows nothing about TickerTapeWidget, GraphWidget,
// or BuySellBot specifically -- only about the generic PriceObserver
// interface.
// ---------------------------------------------------------------------------
class StockData {
    private final String symbol;
    private double price;
    private final java.util.List<PriceObserver> widgets = new java.util.ArrayList<>();

    public StockData(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.price = initialPrice;
    }

    public void addWidget(PriceObserver widget) {
        widgets.add(widget);
    }

    public void removeWidget(PriceObserver widget) {
        widgets.remove(widget);
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
        notifyWidgets();
    }

    private void notifyWidgets() {
        for (PriceObserver widget : widgets) {
            widget.onPriceUpdate(symbol, price);
        }
    }
}

// ---------------------------------------------------------------------------
// Client / demo
// ---------------------------------------------------------------------------
public class C2_FinancialDashboard {
    public static void main(String[] args) {
        StockData teslaFeed = new StockData("TSLA", 250.00);

        TickerTapeWidget ticker = new TickerTapeWidget();
        GraphWidget graph = new GraphWidget();
        BuySellBot bot = new BuySellBot(200.00, 300.00);

        teslaFeed.addWidget(ticker);
        teslaFeed.addWidget(graph);
        teslaFeed.addWidget(bot);

        System.out.println("--- Price update #1 ---");
        teslaFeed.setPrice(275.50); // all three widgets react

        System.out.println("\n--- Removing the Graph widget ---");
        teslaFeed.removeWidget(graph);

        System.out.println("\n--- Price update #2 (Graph no longer notified) ---");
        teslaFeed.setPrice(305.00); // ticker + bot react; bot triggers SELL

        System.out.println("\n--- Price update #3 ---");
        teslaFeed.setPrice(190.00); // ticker + bot react; bot triggers BUY
    }
}
