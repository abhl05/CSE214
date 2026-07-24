// 1. Abstract Products
// These are the base interfaces for the distinct objects that make up a "family."

// Abstract Product A
public interface Button {
    void paint();
}

// Abstract Product B
public interface Checkbox {
    void paint();
}

// 2. Concrete Products
// These are the specific implementations of the abstract products, grouped by their "variant" or "family."

// --- Variant 1: Windows Family ---

public class WindowsButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering a button in Windows style.");
    }
}

public class WindowsCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering a checkbox in Windows style.");
    }
}

// --- Variant 2: MacOS Family ---

public class MacButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering a button in MacOS style.");
    }
}

public class MacCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering a checkbox in MacOS style.");
    }
}

// 3. The Abstract Factory  
// This interface declares a set of creation methods for each of the abstract products.

public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// 4. Concrete Factories
// Each concrete factory implements the abstract factory interface and is responsible for creating a single, specific family of products.
//  This guarantees that the products created by a factory are always compatible with one another.

// Creates ONLY Windows components
public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

// Creates ONLY MacOS components
public class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

// 5. The Client
// The client code interacts strictly with the abstract interfaces (GUIFactory, Button, Checkbox). 
// It has absolutely no idea which concrete variants it is working with.

public class Application {
    private Button button;
    private Checkbox checkbox;

    // The client accepts a factory through constructor injection.
    // It doesn't know (or care) if it's a WindowsFactory or MacFactory.
    public Application(GUIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        button.paint();
        checkbox.paint();
    }
}

// 6. Application Configuration (Execution)
// At runtime, the app determines which concrete factory to instantiate 
// (usually based on environment variables or configuration files) and passes it to the client.

public class AbstractFactoryDemo {
    
    /**
     * This method simulates reading a configuration to decide which 
     * factory family to instantiate.
     */
    private static Application configureApplication() {
        Application app;
        GUIFactory factory;
        
        // Simulating checking the OS environment
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("mac")) {
            factory = new MacFactory();
        } else {
            factory = new WindowsFactory();
        }
        
        // Inject the chosen factory into the client
        app = new Application(factory);
        return app;
    }

    public static void main(String[] args) {
        Application app = configureApplication();
        
        // The application renders the correct family of UI components
        // without ever explicitly calling 'new WindowsButton()' or 'new MacButton()'
        app.renderUI();
    }
}