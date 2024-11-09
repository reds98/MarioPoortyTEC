// GameClient/src/main/java/com/mycompany/gameclient/model/GameCharacter.java
package com.mycompany.gameclient.model;

import java.awt.Color;

public enum GameCharacter {
    MARIO("Mario", new Color(255, 0, 0)),
    LUIGI("Luigi", new Color(0, 255, 0)),
    PEACH("Peach", new Color(255, 192, 203)),
    YOSHI("Yoshi", new Color(50, 205, 50)),
    TOAD("Toad", new Color(255, 255, 255)),
    BOWSER("Bowser", new Color(139, 69, 19)),
    WARIO("Wario", new Color(255, 215, 0)),
    WALUIGI("Waluigi", new Color(75, 0, 130)),
    DONKEY_KONG("DK", new Color(139, 69, 19)),
    KOOPA("Koopa", new Color(0, 255, 255));

    private final String name;
    private final Color color;

    GameCharacter(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}