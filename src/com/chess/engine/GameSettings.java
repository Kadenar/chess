package com.chess.engine;

public class GameSettings {

    // Configurable settings
    private boolean enableHighlighting = false;
    private boolean enableDebugging = false;

    public static final GameSettings INSTANCE = getInstance();

    private static GameSettings getInstance() {
        if(INSTANCE == null) {
            return new GameSettings();
        }

        return INSTANCE;
    }

    // This object can only reference one game at a time
    private GameSettings() { /*Singleton*/ }

    // Highlighting of tiles on click of a piece
    public boolean isEnableHighlighting() { return this.enableHighlighting; }
    public void setEnableHighlighting(boolean enabled) {
        this.enableHighlighting = enabled;
    }

    // Debugging for fixing problems
    public boolean isEnableDebugging() { return this.enableDebugging; }
    public void setEnableDebugging(boolean enabled) { this.enableDebugging = enabled; }
}
