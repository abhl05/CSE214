import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface Coffee {
    String getIngredients();

    int getCost();
}

class BasicMilkCoffee implements Coffee {
    @Override
    public String getIngredients() {
        return "Milk, Grinded coffee beans";
    }

    @Override
    public int getCost() {
        return 180;
    }
}

class BasicBlackCoffee implements Coffee {
    @Override
    public String getIngredients() {
        return "Water, Grinded coffee beans";
    }

    @Override
    public int getCost() {
        return 130;
    }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee decoratedCoffee) {
        this.decoratedCoffee = decoratedCoffee;
    }

    @Override
    public String getIngredients() {
        return decoratedCoffee.getIngredients();
    }

    @Override
    public int getCost() {
        return decoratedCoffee.getCost();
    }
}

class Americano extends CoffeeDecorator {
    public Americano(Coffee decoratedCoffee) {
        super(decoratedCoffee);
    }

    @Override
    public String getIngredients() {
        return super.getIngredients() + ", Additional grinded coffee beans";
    }

    @Override
    public int getCost() {
        return super.getCost() + 30;
    }
}

class Cappuccino extends CoffeeDecorator {
    public Cappuccino(Coffee decoratedCoffee) {
        super(decoratedCoffee);
    }

    @Override
    public String getIngredients() {
        return super.getIngredients() + ", Cinnamon powder";
    }

    @Override
    public int getCost() {
        return super.getCost() + 50;
    }
}

// Order class to handle multiple coffee orders
class Order {
    private List<Coffee> coffees = new ArrayList<>();

    public void addCoffee(Coffee coffee) {
        coffees.add(coffee);
    }

    public void printOrderDetails() {
        int totalCost = 0;
        int coffeeCount = 1;
        for (Coffee coffee : coffees) {
            System.out.println("Coffee " + coffeeCount + ":");
            System.out.println("Ingredients: " + coffee.getIngredients());
            System.out.println("Cost: " + coffee.getCost() + " taka");
            System.out.println();
            totalCost += coffee.getCost();
            coffeeCount++;
        }
        System.out.println("Total Cost for Order: " + totalCost + " taka");
    }
}

// Main Class
public class C1{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Order order = new Order();
        while (true) {
            System.out.println("Select coffee type (1: Americano, 2: Espresso, 3: Cappuccino, 4:            Mocha, 0: Finish): ");
            int choice = scanner.nextInt();
            if (choice == 0) break;
            Coffee coffee;
            switch (choice) {
                case 1:
                coffee = new Americano(new BasicBlackCoffee());
                break;
                case 3:
                coffee = new Cappuccino(new BasicMilkCoffee());
                break;
                default:
                System.out.println("Invalid choice.");
                continue;
            }
            order.addCoffee(coffee);
        }
        order.printOrderDetails();
        scanner.close();
    }
}