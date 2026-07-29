interface GiftInterface {
    String getName();
    double getPrice();
}

class DecorativeVase implements GiftInterface {

    public String getName() {
        return "Decorative Vase";
    }

    public double getPrice() {
        return 40;
    }
}

class WoodenSouvenir implements GiftInterface {

    public String getName() {
        return "Wooden Souvenir";
    }

    public double getPrice() {
        return 60;
    }
}

class CrystalShowpiece implements GiftInterface {

    public String getName() {
        return "Crystal Showpiece";
    }

    public double getPrice() {
        return 150;
    }
}

class GiftDecorator implements GiftInterface {
    private GiftInterface gift;

    public GiftDecorator(GiftInterface gift) {
        this.gift = gift;
    }

    public double getPrice() {
        return gift.getPrice();
    }

    public String getName() {
        return gift.getName();
    }
}

class WrappedGift extends GiftDecorator {
    public WrappedGift(GiftInterface gift) {
        super(gift);
    }

    public double getPrice() {
        return super.getPrice() + 2;
    }

    public String getName() {
        return "Wrapped " + super.getName();
    }
}

class DeliveryGift extends GiftDecorator {
    public DeliveryGift(GiftInterface gift) {
        super(gift);
    }

    public double getPrice() {
        return super.getPrice();
    }

    public String getName() {
        return "Delivery " + super.getName();
    }
}

interface Region {
    double getDeliveryCharge(double miles);
    String getEstimatedDeliveryTime();
}

class LocalRegion implements Region {
    public double getDeliveryCharge(double miles) {
        return miles * 1;
    }

    public String getEstimatedDeliveryTime() {
        return "1 week";
    }
}

class NationalRegion implements Region {
    public double getDeliveryCharge(double miles) {
        return miles * 1 + 20;
    }

    public String getEstimatedDeliveryTime() {
        return "1-2 week";
    }
}

class InternationalRegion implements Region {
    public double getDeliveryCharge(double miles) {
        return 500;
    }

    public String getEstimatedDeliveryTime() {
        return "2-3 week";
    }
}
// Abstraction  
abstract class GiftDelivery {
    protected Region region;

    public GiftDelivery(Region region) {
        this.region = region;
    }

    public abstract double getDeliveryCharge(double miles);
    public abstract String getEstimatedDeliveryTime();
    abstract String deliver(DeliveryGift gift);
}

class ExpressDelivery extends GiftDelivery {
    DeliveryGift gift;

    public ExpressDelivery(Region region) {
        super(region);
    }

    public double getDeliveryCharge(double miles) {
        return region.getDeliveryCharge(miles) + 10 + gift.getPrice();
    }

    public String getEstimatedDeliveryTime() {
        if(region instanceof LocalRegion) {
            return "2 days";
        } else if(region instanceof NationalRegion) {
            return "2 days";
        } else if(region instanceof InternationalRegion) {
            return "1 week";
        } else {
            return region.getEstimatedDeliveryTime();
        }
    }

    @Override
    String deliver(DeliveryGift gift) {
        this.gift = gift;
        return "Delivering " + gift.getName() + " via Express Delivery to " + region.getClass().getSimpleName();
    }
}

class PriorityDelivery extends GiftDelivery {
    DeliveryGift gift;

    public PriorityDelivery(Region region) {
        super(region);
    }

    public double getDeliveryCharge(double miles) {
        return region.getDeliveryCharge(miles) + 25 + gift.getPrice();
    }

    public String getEstimatedDeliveryTime() {
        if(region instanceof LocalRegion) {
            return "1 day";
        } else if(region instanceof NationalRegion) {
            return "1 days";
        } else if(region instanceof InternationalRegion) {
            return "5 days";
        } else {
            return region.getEstimatedDeliveryTime();
        }
    }

    @Override
    String deliver(DeliveryGift gift) {
        this.gift = gift;
        return "Delivering " + gift.getName() + " via Priority Delivery to " + region.getClass().getSimpleName();
    }
}

class NormalDelivery extends GiftDelivery {
    DeliveryGift gift;

    public NormalDelivery(Region region) {
        super(region);
    }

    public double getDeliveryCharge(double miles) {
        return region.getDeliveryCharge(miles) + gift.getPrice();
    }

    public String getEstimatedDeliveryTime() {
        return region.getEstimatedDeliveryTime();
    }

    @Override
    String deliver(DeliveryGift gift) {
        this.gift = gift;
        return "Delivering " + gift.getName() + " via Normal Delivery to " + region.getClass().getSimpleName();
    }
}

public class A1 {
    public static void main(String[] args) {
        // CASE 1:
        // Create a gift
        DeliveryGift gift = new DeliveryGift(new WrappedGift((new DecorativeVase())));

        // Create a delivery method and region
        GiftDelivery delivery = new NormalDelivery(new LocalRegion());

        // Deliver the gift
        System.out.println(delivery.deliver(gift));
        System.out.println("Delivery Charge: £" + delivery.getDeliveryCharge(10));
        System.out.println("Estimated Delivery Time: " + delivery.getEstimatedDeliveryTime());

        // CASE 2:
        // Create a gift
        DeliveryGift gift2 = new DeliveryGift(new WrappedGift((new WoodenSouvenir())));
        // Create a delivery method and region
        GiftDelivery delivery2 = new ExpressDelivery(new NationalRegion());
        // Deliver the gift
        System.out.println(delivery2.deliver(gift2));
        System.out.println("Delivery Charge: £" + delivery2.getDeliveryCharge(50));
        System.out.println("Estimated Delivery Time: " + delivery2.getEstimatedDeliveryTime());

        // CASE 3:
        // Create a gift
        DeliveryGift gift3 = new DeliveryGift((new CrystalShowpiece()));
        // Create a delivery method and region
        GiftDelivery delivery3 = new PriorityDelivery(new InternationalRegion());
        // Deliver the gift
        System.out.println(delivery3.deliver(gift3));
        System.out.println("Delivery Charge: £" + delivery3.getDeliveryCharge(100));
        System.out.println("Estimated Delivery Time: " + delivery3.getEstimatedDeliveryTime());
    }
}



