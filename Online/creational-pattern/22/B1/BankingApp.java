// 1. The Singleton Class
class Logger {
    // 1a. A private, static variable to hold the ONE instance of the class.
    private static Logger instance;

    // 1b. A private constructor to prevent other classes from using the 'new' keyword.
    private Logger() {
        System.out.println("--> A new Logger instance was created.");
    }

    // 1c. A public, static method to provide global access to the instance.
    public static Logger getInstance() {
        // If the instance doesn't exist yet, create it.
        if (instance == null) {
            instance = new Logger();
        }
        // If it already exists, just return the existing one.
        return instance;
    }

    // A standard business method to handle the actual logging
    public void log(String message) {
        System.out.println("[AUDIT TRAIL] " + message);
    }
}

// 2. Client Code Demonstrating the Solution
public class BankingApp {
    public static void main(String[] args) {
        System.out.println("Starting Banking Application...\n");

        // Module 1: The Deposit System needs to log a transaction
        System.out.println("Deposit Module running:");
        Logger depositLogger = Logger.getInstance();
        depositLogger.log("User 123 deposited $500.00");

        System.out.println("\nWithdrawal Module running:");
        // Module 2: The Withdrawal System needs to log a transaction
        Logger withdrawalLogger = Logger.getInstance();
        withdrawalLogger.log("User 123 withdrew $100.00");

        System.out.println("\n--- Audit Verification ---");
        // We use the '==' operator to check if both variables point to the EXACT same object in memory
        if (depositLogger == withdrawalLogger) {
            System.out.println("SUCCESS: Both modules are writing to the exact same Logger instance.");
        } else {
            System.out.println("FAILURE: Different Logger instances were created.");
        }
    }
}