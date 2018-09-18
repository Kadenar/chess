package com.chess.engine;

// The available colors for a player
public enum PlayerColor {
    WHITE {
        @Override
        public String toString() {
            return "w";
        }
    },
    BLACK {
        @Override
        public String toString() {
            return "b";
        }
    }
}

