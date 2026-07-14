

class GameConfig {
    public static GameConfig instance;

    private GameConfig() {
    }

    public static GameConfig getInstance() {
        if(instance == null) {
            instance = new GameConfig();
        } 
        return instance;
    }
}

public class GameEngine {
    public static void main(String[] args) {
         
    }
}
