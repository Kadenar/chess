package com.chess.engine.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundUtils {

    public static void playMoveSound(String soundName) {
        try {
            // Open an audio input stream.
            File soundFile = new File("sound/" + soundName + ".wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
