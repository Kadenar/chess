package com.chess.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameSettings {

    // Configurable settings
    private boolean enableHighlighting = false;
    private boolean enableDebugging = false;
    private boolean enableCoordinateDisplay = false;

    public static final GameSettings INSTANCE = getInstance();

    private static GameSettings getInstance() {
        if(INSTANCE == null) {
            return new GameSettings();
        }

        return INSTANCE;
    }

    // This object can only reference one game at a time
    private GameSettings() {
        parseUserSettings();
    }

    /**
     * Parse our settings file
     * TODO this is really just crap code at the moment.
     * Just wanted a way to toggle values without having to enable each run
     */
    private void parseUserSettings() {
        File f = new File("settings/settings.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("highlight:")) {
                    setEnableHighlighting(Boolean.valueOf(line.substring(line.indexOf(":") + 1)));
                } else if(line.startsWith("debugging:")) {
                    setEnableDebugging(Boolean.valueOf(line.substring(line.indexOf(":") + 1)));
                } else if(line.startsWith("coordinates:")) {
                    setDisplayTilePositions(Boolean.valueOf(line.substring(line.indexOf(":") + 1)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Highlighting of tiles on click of a piece
    public boolean isEnableHighlighting() { return this.enableHighlighting; }
    public void setEnableHighlighting(boolean enabled) {
        this.enableHighlighting = enabled;
    }

    // Displaying tile coordinates
    public boolean isDisplayTilePositions() { return this.enableCoordinateDisplay; }
    public void setDisplayTilePositions(boolean display) { this.enableCoordinateDisplay = display; }

    // Debugging for fixing problems
    public boolean isEnableDebugging() { return this.enableDebugging; }
    public void setEnableDebugging(boolean enabled) { this.enableDebugging = enabled; }
}
