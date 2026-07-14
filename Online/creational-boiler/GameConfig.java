public class GameConfig {

    // 1. The settings to be held in memory
    private String resolution;
    private int audioVolume;
    private String difficultyLevel;

    // 2. Private constructor prevents direct instantiation by the client
    private GameConfig() {
        // Simulating the expensive operation of loading from disk
        System.out.println("Loading game settings from disk...");
        this.resolution = "1920x1080";
        this.audioVolume = 100;
        this.difficultyLevel = "Hard";
    }

    // 3. Static inner helper class holds the Singleton instance.
    // This is not loaded into memory until getInstance() is called for the first time.
    private static class ConfigHolder {
        private static final GameConfig INSTANCE = new GameConfig();
    }

    // 4. Public static method to provide global access to the instance
    public static GameConfig getInstance() {
        return ConfigHolder.INSTANCE;
    }

    // --- Getters and Setters for the settings ---
    
    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getAudioVolume() {
        return audioVolume;
    }

    public void setAudioVolume(int audioVolume) {
        this.audioVolume = audioVolume;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}

// Client Code to demonstrate the Singleton behavior
class GameEngine {
    public static void main(String[] args) {
        // The Graphics Engine requests the config
        GameConfig graphicsConfig = GameConfig.getInstance();
        System.out.println("Graphics Resolution: " + graphicsConfig.getResolution());

        // The Audio Engine requests the config
        GameConfig audioConfig = GameConfig.getInstance();
        
        // Changing a setting via one reference...
        audioConfig.setAudioVolume(50);

        // ...updates the state for all systems
        System.out.println("Graphics Audio Volume check: " + graphicsConfig.getAudioVolume());

        // Proof that both variables point to the exact same object in memory
        if (graphicsConfig == audioConfig) {
            System.out.println("Success: Both systems are using the same GameConfig instance.");
        }
    }
}