
// Client Interface expected by the application
interface WeatherProvider {
    String fetchWeather();
}

class LegacyWeatherService {
    public String getWeatherData() {
        return "Legacy weather data";
    }
}

// Adapter 
class Adapter implements WeatherProvider {
    private LegacyWeatherService legacyWeatherService;

    public Adapter(LegacyWeatherService legacyWeatherService) {
        this.legacyWeatherService = legacyWeatherService;
    }

    @Override
    public String fetchWeather() {
        return legacyWeatherService.getWeatherData();
    }
}


// Application class that depends on the WeatherProvider interface
class WeatherApp {
    private WeatherProvider weatherProvider;

    public WeatherApp(WeatherProvider weatherProvider) {
        this.weatherProvider = weatherProvider;
    }
    
    public void displayWeather() {
        System.out.println(weatherProvider.fetchWeather());
    }
}

public class A1 {
    public static void main(String[] args) {
    // Legacy service instance
    LegacyWeatherService legacyWeatherService = new LegacyWeatherService();
    // ??
    WeatherApp app = new WeatherApp(new Adapter(legacyWeatherService));
    app.displayWeather(); // Output: Legacy weather data
    }
}
