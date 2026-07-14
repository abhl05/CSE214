// 1. The Product
// The complex object we are building. It usually does not have a strict requirement
// for immutability, as it is assembled piece by piece by the Concrete Builder.
class House {
    private String foundation;
    private String structure;
    private String roof;

    public void setFoundation(String foundation) { this.foundation = foundation; }
    public void setStructure(String structure) { this.structure = structure; }
    public void setRoof(String roof) { this.roof = roof; }

    @Override
    public String toString() {
        return "House [foundation=" + foundation + ", structure=" + structure + ", roof=" + roof + "]";
    }
}

// 2. The Builder Interface
// Specifies the abstract steps required to build the Product.
interface HouseBuilder {
    void buildFoundation();
    void buildStructure();
    void buildRoof();
    House getResult(); // Method to retrieve the final product
}

// 3. Concrete Builder A
// Implements the steps to build a specific representation of the Product (e.g., Wooden House).
class WoodenHouseBuilder implements HouseBuilder {
    private House house;

    public WoodenHouseBuilder() {
        this.house = new House();
    }

    @Override
    public void buildFoundation() {
        house.setFoundation("Wooden Piles");
    }

    @Override
    public void buildStructure() {
        house.setStructure("Wood and Logs");
    }

    @Override
    public void buildRoof() {
        house.setRoof("Wood Shingles");
    }

    @Override
    public House getResult() {
        return this.house;
    }
}

// 4. Concrete Builder B
// Implements the steps to build a different representation (e.g., Stone House).
class StoneHouseBuilder implements HouseBuilder {
    private House house;

    public StoneHouseBuilder() {
        this.house = new House();
    }

    @Override
    public void buildFoundation() {
        house.setFoundation("Concrete slab");
    }

    @Override
    public void buildStructure() {
        house.setStructure("Stone and Brick");
    }

    @Override
    public void buildRoof() {
        house.setRoof("Slate Tiles");
    }

    @Override
    public House getResult() {
        return this.house;
    }
}

// 5. The Director
// Dictates the exact sequence of construction steps. It does not know the concrete 
// details of what is being built, only the process.
class ConstructionEngineer {
    private HouseBuilder builder;

    public ConstructionEngineer(HouseBuilder builder) {
        this.builder = builder;
    }

    public void constructHouse() {
        builder.buildFoundation();
        builder.buildStructure();
        builder.buildRoof();
    }
}

// 6. Client Code
public class GoFBuilderDemo {
    public static void main(String[] args) {
        // Build a Wooden House
        HouseBuilder woodenBuilder = new WoodenHouseBuilder();
        ConstructionEngineer engineer1 = new ConstructionEngineer(woodenBuilder);
        engineer1.constructHouse();
        House woodenHouse = woodenBuilder.getResult();
        System.out.println(woodenHouse);

        // Build a Stone House using the exact same process
        HouseBuilder stoneBuilder = new StoneHouseBuilder();
        ConstructionEngineer engineer2 = new ConstructionEngineer(stoneBuilder);
        engineer2.constructHouse();
        House stoneHouse = stoneBuilder.getResult();
        System.out.println(stoneHouse);
    }
}