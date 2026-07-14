
import java.util.Objects;

class Bicycle {
    private String frame;
    private String gearSystem;
    private String tireType;

    public Bicycle(Builder b) {
        this.frame = b.frame;
        this.gearSystem = b.gearSystem;
        this.tireType = b.tireType;
    }

    @Override
    public String toString() {
        return frame + " " + gearSystem + " " + tireType;
    }
}

class Builder {
    String frame;
    String gearSystem;
    String tireType;
    public Builder() {
    }
    public Builder setFrame(String frame) {
        this.frame = frame;
        return this;
    }
    public Builder setGearSystem(String gearSystem) {
        this.gearSystem = gearSystem;
        return this;
    }
    public Builder setTireType(String tireType) {
        this.tireType = tireType;
        return this;
    }

    public Bicycle build() {
        this.frame = Objects.requireNonNull(this.frame);
        this.gearSystem = Objects.requireNonNull(this.gearSystem);
        this.tireType = Objects.requireNonNull(this.tireType);
        return new Bicycle(this);
    }
}

class Director {
    public void constructTheCommuter(Builder b) {
            b.setFrame("Aluminum Frame")
                .setGearSystem("Single Speed Gear")
                .setTireType("Road Tires");
    }

    public void constructTheMountainBeast(Builder b) {
        b.setFrame("Carbon Fiber Frame")
                .setGearSystem("12-Speed Gear")
                .setTireType("Off-road Grip Tires");
    }
}

public class BicycleManufacture {
    public static void main(String[] args) {
        Director d = new Director();

        Builder commuter = new Builder();
        d.constructTheCommuter(commuter);
        Bicycle commuterbike = commuter.build();
        System.out.println(commuterbike);

        Builder mountain = new Builder();
        d.constructTheMountainBeast(mountain);
        Bicycle mountainbike = mountain.build();
        System.out.println(mountainbike);
    }
}
