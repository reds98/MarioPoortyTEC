package com.mycompany.gameserver.model;

public class BoardMovementManager {
    private static final int BOARD_SIZE = 26;
    
    public static class MoveResult {
        private final int finalPosition;
        private final boolean isWinningMove;
        private final boolean isOvershot;
        private final String message;
        
        public MoveResult(int finalPosition, boolean isWinningMove, boolean isOvershot, String message) {
            this.finalPosition = finalPosition;
            this.isWinningMove = isWinningMove;
            this.isOvershot = isOvershot;
            this.message = message;
        }
        
        public int getFinalPosition() { return finalPosition; }
        public boolean isWinningMove() { return isWinningMove; }
        public boolean isOvershot() { return isOvershot; }
        public String getMessage() { return message; }
    }
    
    public MoveResult calculateMove(int currentPosition, int diceTotal) {
        // Validar posición actual
        if (currentPosition < 0 || currentPosition >= BOARD_SIZE) {
            return new MoveResult(currentPosition, false, false, 
                "Posición inválida");
        }
        
        // Calcular nueva posición
        int newPosition = currentPosition + diceTotal;
        
        // Caso: Llegar exactamente a la última casilla
        if (newPosition == BOARD_SIZE - 1) {
            return new MoveResult(newPosition, true, false, 
                "¡Victoria! Has llegado exactamente a la meta");
        }
        
        // Caso: Pasarse de la última casilla
        if (newPosition >= BOARD_SIZE) {
            int excess = newPosition - (BOARD_SIZE - 1);
            int bouncePosition = (BOARD_SIZE - 1) - excess;
            return new MoveResult(bouncePosition, false, true,
                "Te pasaste por " + excess + " casillas. Retrocedes a la casilla " + bouncePosition);
        }
        
        // Caso: Movimiento normal
        return new MoveResult(newPosition, false, false,
            "Avanzas a la casilla " + newPosition);
    }
    
    public boolean isValidMove(int currentPosition, int diceTotal) {
        int newPosition = currentPosition + diceTotal;
        return newPosition >= 0 && newPosition < BOARD_SIZE;
    }
    
    public boolean isWinningPosition(int position) {
        return position == BOARD_SIZE - 1;
    }
    
    public int calculateBounceBack(int targetPosition) {
        if (targetPosition >= BOARD_SIZE) {
            int excess = targetPosition - (BOARD_SIZE - 1);
            return (BOARD_SIZE - 1) - excess;
        }
        return targetPosition;
    }
}