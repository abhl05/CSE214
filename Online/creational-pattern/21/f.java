// 1. The common interface for all document processors
interface DocumentProcessor {
    void loadDocument(String fileName);
    void saveDocument(String fileName);
}

// 2. Concrete implementation for .docx files
class DocxProcessor implements DocumentProcessor {
    @Override
    public void loadDocument(String fileName) {
        System.out.println("Loading .docx file: " + fileName);
    }

    @Override
    public void saveDocument(String fileName) {
        System.out.println("Saving .docx file: " + fileName);
    }
}

// 3. Concrete implementation for .pdf files
class PdfProcessor implements DocumentProcessor {
    @Override
    public void loadDocument(String fileName) {
        System.out.println("Loading .pdf file: " + fileName);
    }

    @Override
    public void saveDocument(String fileName) {
        System.out.println("Saving .pdf file: " + fileName);
    }
}

// 4. Concrete implementation for .txt files
class TxtProcessor implements DocumentProcessor {
    @Override
    public void loadDocument(String fileName) {
        System.out.println("Loading .txt file: " + fileName);
    }

    @Override
    public void saveDocument(String fileName) {
        System.out.println("Saving .txt file: " + fileName);
    }
}

// 5. The Factory Class
class ProcessorFactory {
    
    // This method parses the file name and returns the correct processor
    public DocumentProcessor createProcessor(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name provided.");
        }

        // Extract the file extension, converting to lowercase to ensure consistency
        String extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();

        switch (extension) {
            case ".docx":
                return new DocxProcessor();
            case ".pdf":
                return new PdfProcessor();
            case ".txt":
                return new TxtProcessor();
            default:
                throw new IllegalArgumentException("Unsupported file type: " + extension);
        }
    }
}

// 6. Client Code
public class DocumentEditor {
    public static void main(String[] args) {
        ProcessorFactory factory = new ProcessorFactory();
        
        // Array of test file names
        String[] files = {"report.pdf", "notes.txt", "assignment.docx", "unknown.xyz"};

        for (String file : files) {
            System.out.println("--- Processing: " + file + " ---");
            try {
                // The client requests a processor without knowing the exact concrete class
                DocumentProcessor processor = factory.createProcessor(file);
                
                // The client uses the common interface methods
                processor.loadDocument(file);
                processor.saveDocument(file);
                
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }
}

// 1. Abstract Products
interface Processor {
    String getDetails();
}

interface Display {
    String getDetails();
}

// 2. Concrete Products (Processors from Company A)
class IntelXeon implements Processor {
    @Override
    public String getDetails() { return "Intel Xeon Processor"; }
}

class ARM implements Processor {
    @Override
    public String getDetails() { return "ARM Processor"; }
}

// 3. Concrete Products (Displays from Company B)
class IPS implements Display {
    @Override
    public String getDetails() { return "IPS Display"; }
}

class OLED implements Display {
    @Override
    public String getDetails() { return "OLED Display"; }
}

// 4. Abstract Factory
interface ComputerFactory {
    Processor createProcessor();
    Display createDisplay();
}

// 5. Concrete Factories (Representing the Models)
class WorkProFactory implements ComputerFactory {
    @Override
    public Processor createProcessor() { return new IntelXeon(); }
    @Override
    public Display createDisplay() { return new IPS(); }
}

class LiteMaxFactory implements ComputerFactory {
    @Override
    public Processor createProcessor() { return new ARM(); }
    @Override
    public Display createDisplay() { return new OLED(); }
}

// 6. The Computer Class (Client)
class Computer {
    private String modelName;
    private Processor processor;
    private Display display;

    public Computer(String modelName, ComputerFactory factory) {
        this.modelName = modelName;
        this.processor = factory.createProcessor();
        this.display = factory.createDisplay();
    }

    public void printDescription() {
        System.out.println("Model: " + modelName);
        System.out.println("Components: " + processor.getDetails() + " & " + display.getDetails() + "\n");
    }
}

// 7. Execution
public class ComputerApp {
    public static void main(String[] args) {
        // User selects WorkPro
        Computer workPro = new Computer("WorkPro", new WorkProFactory());
        workPro.printDescription();

        // User selects LiteMax
        Computer liteMax = new Computer("LiteMax", new LiteMaxFactory());
        liteMax.printDescription();
    }
}

// 1. The Common Product Interface
interface PaymentMethod {
    void processPayment(double amount);
}

// 2. Concrete Products
class CreditCardPayment implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Successfully processed $" + amount + " via Credit Card.");
    }
}

class PayPalPayment implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Successfully processed $" + amount + " via PayPal.");
    }
}

class CryptoPayment implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Successfully processed $" + amount + " via Cryptocurrency (Bitcoin).");
    }
}

// 3. The Factory Class
class PaymentFactory {
    public static PaymentMethod getPaymentMethod(String type) {
        if (type == null) {
            return null;
        }
        switch (type.toLowerCase()) {
            case "creditcard":
                return new CreditCardPayment();
            case "paypal":
                return new PayPalPayment();
            case "crypto":
                return new CryptoPayment();
            default:
                throw new IllegalArgumentException("Unknown payment method: " + type);
        }
    }
}

// 4. Execution (Client)
public class ECommerceApp {
    public static void main(String[] args) {
        String userPreference = "paypal"; 
        double cartTotal = 150.75;

        try {
            PaymentMethod method = PaymentFactory.getPaymentMethod(userPreference);
            method.processPayment(cartTotal);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}

// 1. Abstract Products
interface Letter { void generate(); }
interface Resume { void generate(); }

// 2. Concrete Products (Formal Family)
class FormalLetter implements Letter {
    public void generate() { System.out.println("Generating a Professional/Formal Letter."); }
}
class FormalResume implements Resume {
    public void generate() { System.out.println("Generating a Professional/Formal Resume."); }
}

// 3. Concrete Products (Informal Family)
class InformalLetter implements Letter {
    public void generate() { System.out.println("Generating a Casual/Informal Letter."); }
}
class InformalResume implements Resume {
    public void generate() { System.out.println("Generating a Casual/Informal Resume."); }
}

// 4. Abstract Factory
interface DocumentCreator {
    Letter createLetter();
    Resume createResume();
}

// 5. Concrete Factories
class FormalDocumentCreator implements DocumentCreator {
    public Letter createLetter() { return new FormalLetter(); }
    public Resume createResume() { return new FormalResume(); }
}

class InformalDocumentCreator implements DocumentCreator {
    public Letter createLetter() { return new InformalLetter(); }
    public Resume createResume() { return new InformalResume(); }
}

// 6. Execution (Client)
public class DocumentApp {
    public static void main(String[] args) {
        // Client selects Formal mode
        DocumentCreator formalMode = new FormalDocumentCreator();
        Letter formalLetter = formalMode.createLetter();
        Resume formalResume = formalMode.createResume();
        
        formalLetter.generate();
        formalResume.generate();

        System.out.println("---");

        // Client selects Informal mode
        DocumentCreator informalMode = new InformalDocumentCreator();
        Letter informalLetter = informalMode.createLetter();
        informalLetter.generate();
    }
}

// 1. The Complex Product
class Meal {
    private String starter;
    private String mainDish;
    private String dessert;

    public void setStarter(String starter) { this.starter = starter; }
    public void setMainDish(String mainDish) { this.mainDish = mainDish; }
    public void setDessert(String dessert) { this.dessert = dessert; }

    @Override
    public String toString() {
        return "Meal Plan [Starter: " + starter + ", Main: " + mainDish + ", Dessert: " + dessert + "]";
    }
}

// 2. The Builder Interface
interface MealBuilder {
    void buildStarter();
    void buildMainDish();
    void buildDessert();
    Meal getMeal();
}

// 3. Concrete Builders
class BengaliMealBuilder implements MealBuilder {
    private Meal meal = new Meal();

    public void buildStarter() { meal.setStarter("Vegetable"); }
    public void buildMainDish() { meal.setMainDish("Chicken Curry"); }
    public void buildDessert() { meal.setDessert("Sweet Curd"); }
    public Meal getMeal() { return meal; }
}

class ChineseMealBuilder implements MealBuilder {
    private Meal meal = new Meal();

    public void buildStarter() { meal.setStarter("Soup"); }
    public void buildMainDish() { meal.setMainDish("Peking Duck"); }
    public void buildDessert() { meal.setDessert("Pudding"); }
    public Meal getMeal() { return meal; }
}

// 4. The Director
class RestaurantDirector {
    public Meal constructMeal(MealBuilder builder) {
        builder.buildStarter();
        builder.buildMainDish();
        builder.buildDessert();
        return builder.getMeal();
    }
}

// 5. Execution (Client)
public class RestaurantApp {
    public static void main(String[] args) {
        RestaurantDirector director = new RestaurantDirector();

        // User chooses Bengali Meal
        MealBuilder bengaliBuilder = new BengaliMealBuilder();
        Meal bengaliMeal = director.constructMeal(bengaliBuilder);
        System.out.println("Bengali " + bengaliMeal);

        // User chooses Chinese Meal
        MealBuilder chineseBuilder = new ChineseMealBuilder();
        Meal chineseMeal = director.constructMeal(chineseBuilder);
        System.out.println("Chinese " + chineseMeal);
    }
}

