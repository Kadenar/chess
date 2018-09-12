package com.chess.engine.moves;

import com.chess.engine.sound.SoundUtils;

public enum MoveType {
    CASTLE {
        @Override
        public void playSound() {
            SoundUtils.playMoveSound("castle");
        }
    },
    CAPTURE {
        @Override
        public void playSound() {
            SoundUtils.playMoveSound("capture2");
        }
    },
    REGULAR {
        @Override
        public void playSound() {
            SoundUtils.playMoveSound("mov2");
        }
    },
    CHECK {
        @Override
        public void playSound() {
            SoundUtils.playMoveSound("check1");
        }
    },
    INVALID {
        @Override
        public void playSound() {
            SoundUtils.playMoveSound("invalid");
        }
    },
    GAME_START {
        @Override
        public void playSound() { SoundUtils.playMoveSound("startGame"); }
    },
    GAME_OVER {
        @Override
        public void playSound() { /* TODO SoundUtils.playMoveSound("TBD"); */ }
    };

    public abstract void playSound();

}
