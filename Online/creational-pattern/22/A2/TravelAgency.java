package Online.22.A2;
import java.util.Objects;

class HolidayPackage {
    private String flight;
    private String hotel;
    private String dailyActivities;

    public HolidayPackage(Builder b) {
        this.flight = b.flight;
        this.hotel = b.hotel;
        this.dailyActivities = b.dailyActivities;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getDailyActivities() {
        return dailyActivities;
    }

    public void setDailyActivities(String dailyActivities) {
        this.dailyActivities = dailyActivities;
    }   

    
}

class Builder {
    String flight;
    String hotel;
    String dailyActivities;

    public Builder(String flight, String hotel, String dailyActivites) {
        this.dailyActivities = dailyActivites;
        this.flight = flight;
        this.hotel = hotel;
    }

    

    public HolidayPackage build() {
        this.flight = Objects.requireNonNull(this.flight, "flight");
        this.hotel = Objects.requireNonNull(this.hotel, "hotel");
        this.dailyActivities = Objects.requireNonNull(this.dailyActivities, "dailyActivities");
        
        return new HolidayPackage(this);
    }

    public String getFlight() {
        return flight;
    }

    public Builder setFlight(String flight) {
        this.flight = flight;
        return this;
    }

    public String getHotel() {
        return hotel;
    }

    public Builder setHotel(String hotel) {
        this.hotel = hotel;
        return this;
    }

    public String getDailyActivities() {
        return dailyActivities;
    }

    public Builder setDailyActivities(String dailyActivities) {
        this.dailyActivities = dailyActivities;
        return this;
    }
}

class Director {
    public Builder constructRelaxationPackage(Builder b) {
        return b.setFlight("Business Class Flight").setDailyActivities("Spa Treatment").setHotel("5-Star Resort");
    }

    public Builder constructAdventurePackage(Builder b) {
        return b.setFlight("Economy Flight").setDailyActivities("Hiking Tour").setHotel("Mountain Cabin");
    }

    public Builder constructCustomPackage(Builder b, String flight, String hotel, String dailyActivites) {
        return b.setFlight(flight).setHotel(hotel).setDailyActivities(dailyActivites);
    }
}

public class TravelAgency {
    public static void main(String[] args) {   
        Director director = new Director();
        Builder builder = new Builder("Economy Flight", "3-Star Hotel", "City Tour");

        // Construct a relaxation package
        HolidayPackage relaxationPackage = director.constructRelaxationPackage(builder).build();
        System.out.println("Relaxation Package: Flight - " + relaxationPackage.getFlight() + ", Hotel - " + relaxationPackage.getHotel() + ", Activities - " + relaxationPackage.getDailyActivities());

        // Construct an adventure package
        HolidayPackage adventurePackage = director.constructAdventurePackage(builder).build();
        System.out.println("Adventure Package: Flight - " + adventurePackage.getFlight() + ", Hotel - " + adventurePackage.getHotel() + ", Activities - " + adventurePackage.getDailyActivities());

        // Construct a custom package
        HolidayPackage customPackage = director.constructCustomPackage(builder, "First Class Flight", "Luxury Villa", "Private Yacht Tour").build();
        System.out.println("Custom Package: Flight - " + customPackage.getFlight() + ", Hotel - " + customPackage.getHotel() + ", Activities - " + customPackage.getDailyActivities());
    }
}
