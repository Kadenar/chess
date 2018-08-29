package com.chess.engine;

public class GameSettings {

    private boolean enableHighlighting = true;

    private static final GameSettings INSTANCE = getInstance();

    public static GameSettings getInstance() {
        if(INSTANCE == null) {
            return new GameSettings();
        }

        return INSTANCE;
    }

    // This object can only reference one game at a time
    private GameSettings() { /*Singleton*/ }

    public boolean isEnableHighlighting() {
        return this.enableHighlighting;
    }

    public void setEnableHighlighting(boolean enabled) {
        this.enableHighlighting = enabled;
    }
}
