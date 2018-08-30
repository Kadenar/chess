package com.chess.ui.menus;

import com.chess.ui.ChessFrame;
import javax.swing.JMenuBar;

public class ChessMenuBar extends JMenuBar {

    public ChessMenuBar(ChessFrame owningFrame) {
        super();
        add(new GameOptionsMenu(owningFrame));
        add(new DebugOptionsMenu(owningFrame.getBoardPanel().getBoard()));
        // TODO Add more menus here later
    }
}
