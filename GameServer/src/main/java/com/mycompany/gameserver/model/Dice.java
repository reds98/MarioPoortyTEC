// GameServer/model/Dice.java
package com.mycompany.gameserver.model;

import java.util.Random;

public class Dice {
    private static final int SIDES = 6;
    private static final int PENALTY = 6;
    private final Random random = new Random();
    
    public int roll() {
        return random.nextInt(SIDES) + 1;
    }
    
    public boolean isPenalty(int value) {
        return value == PENALTY;
    }
}
