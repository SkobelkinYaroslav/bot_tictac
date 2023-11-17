package ru.bot_hak.bot_tictac.bot.negamax;


public class Evaluator {

    private static final int[] SCORES = {19, 15, 11, 7, 3};


    private static int scoreDirection(Field[] direction, int index) {
        int score = 0;

        // Pass a window of 5 across the field array
        for(int i = 0; (i + 4) < direction.length; i++) {
            int empty = 0;
            int stones = 0;
            for(int j = 0; j <= 4; j++) {
                if(direction[i + j].index == 0) {
                    empty++;
                }
                else if(direction[i + j].index == index) {
                    stones++;
                } else {
                    break;
                }
            }
            if(empty == 0 || empty == 5) continue;

            if(stones + empty == 5) {
                score += SCORES[empty];
            }
        }
        return score;
    }

    public static int evaluateState(State state, int depth) {
        int playerIndex = state.currentIndex;
        int opponentIndex = playerIndex == 1 ? 2 : 1;

        int terminal = state.terminal();
        if(terminal == playerIndex) return 10000 + depth;
        if(terminal == opponentIndex) return -10000 - depth;

        int score = 0;
        for(int i = 0; i < state.board.length; i++) {
            for(int j = 0; j < state.board.length; j++) {
                if(state.board[i][j].index == opponentIndex) {
                    score -= evaluateField(state, i, j, opponentIndex);
                } else if(state.board[i][j].index == playerIndex) {
                    score += evaluateField(state, i, j, playerIndex);
                }
            }
        }
        return score;
    }

    public static int evaluateField(State state, int row, int col, int index) {
        int score = 0;
        for(int direction = 0; direction < 4; direction++) {
            score += scoreDirection(state.directions[row][col][direction],
                    index);
        }
        return score;
    }

}
