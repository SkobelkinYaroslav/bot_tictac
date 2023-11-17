package ru.bot_hak.bot_tictac.bot.negamax;

import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;


public class State {


    protected final Field[][] board;
    protected final Field[][][][] directions;

    protected int currentIndex;

    private long zobristHash;
    private final long[][][] zobristKeys;

    private Stack<Move> moveStack;
    

    public State(int intersections) {
        this.board = new Field[intersections][intersections];
        for(int i = 0; i < intersections; i++) {
            for(int j = 0; j < intersections; j++) {
                board[i][j] = new Field(i, j);
            }
        }
        this.directions = new Field[intersections][intersections][4][9];
        this.currentIndex = 1;
        this.zobristKeys = new long[2][intersections][intersections];
        this.zobristHash = 0;
        this.moveStack = new Stack<>();
        this.generateDirections(board);

        for(int i = 0; i < zobristKeys.length; i++) {
            for(int j = 0; j < zobristKeys[0].length; j++) {
                for(int k = 0; k < zobristKeys[0][0].length; k++) {
                    zobristKeys[i][j][k] = ThreadLocalRandom.current().nextLong
                            (Long.MAX_VALUE);
                }
            }
        }
    }


    public long getZobristHash() {
        return zobristHash;
    }
    

    public void makeMove(Move move) {
        moveStack.push(move);
        this.board[move.row][move.col].index = this.currentIndex;
        this.zobristHash ^= zobristKeys[board[move.row][move.col]
                .index - 1][move.row][move.col];
        this.currentIndex = this.currentIndex == 1 ? 2 : 1;
    }
    

    public void undoMove(Move move) {
        moveStack.pop();
        this.zobristHash ^= zobristKeys[board[move.row][move.col]
                .index - 1][move.row][move.col];
        this.board[move.row][move.col].index = 0;
        this.currentIndex = this.currentIndex == 1 ? 2 : 1;
    }
    

    protected boolean hasAdjacent(int row, int col, int distance) {
        for(int i = 0; i < 4; i++) {
            for(int j = 1; j <= distance; j++) {
                if(directions[row][col][i][4 + j].index == 1
                        || directions[row][col][i][4 - j].index == 1
                        || directions[row][col][i][4 + j].index == 2
                        || directions[row][col][i][4 - j].index == 2) {
                    return true;
                }
            }
        }
        return false;
    }
    

    private void generateDirections(Field[][] board) {
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board.length; col++) {
                directions[row][col][0][4] = board[row][col];
                directions[row][col][1][4] = board[row][col];
                directions[row][col][2][4] = board[row][col];
                directions[row][col][3][4] = board[row][col];

                for(int k = 0; k < 5; k++) {
                    if(row - k >= 0 && col - k >=0) {
                        directions[row][col][0][4 - k] = board[row -
                                k][col - k];
                    } else {
                        directions[row][col][0][4 - k] = new Field();
                    }

                    if(row + k < board.length && col + k < board.length) {
                        directions[row][col][0][4 + k] =
                                board[row + k][col + k];
                    } else {
                        directions[row][col][0][4 + k] = new Field();
                    }

                    if(row - k >= 0 && col + k < board.length) {
                        directions[row][col][1][4 - k] =
                                board[row - k][col + k];
                    } else {
                        directions[row][col][1][4 - k] = new Field();
                    }

                    if(row + k < board.length && col - k >=0) {
                        directions[row][col][1][4 + k] =
                                board[row + k][col - k];
                    } else {
                        directions[row][col][1][4 + k] = new Field();
                    }

                    if(row - k >= 0) {
                        directions[row][col][2][4 - k] =
                                board[row - k][col];
                    } else {
                        directions[row][col][2][4 - k] = new Field();
                    }

                    if(row + k < board.length) {
                        directions[row][col][2][4 + k] =
                                board[row + k][col];
                    } else {
                        directions[row][col][2][4 + k] = new Field();
                    }

                    if(col - k >= 0) {
                        directions[row][col][3][4 - k] =
                                board[row][col - k];
                    } else {
                        directions[row][col][3][4 - k] = new Field();
                    }

                    if(col + k < board.length) {
                        directions[row][col][3][4 + k] =
                                board[row][col + k];
                    } else {
                        directions[row][col][3][4 + k] = new Field();
                    }
                }
            }
        }
    }


    protected int terminal() {
        Move move = moveStack.peek();
        int row = move.row;
        int col = move.col;
        int lastIndex = currentIndex == 1 ? 2 : 1;

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 6; j++) {
                if(directions[row][col][i][j].index == lastIndex) {
                    int count = 0;
                    for(int k = 1; k < 5; k++) {
                        if(directions[row][col][i][j+k].index == lastIndex) {
                            count++;
                        } else {
                            break;
                        }
                    }
                    if(count == 4) return lastIndex;
                }
            }
        }
        return moveStack.size() == board.length * board.length ? 3 : 0;
    }


    protected int getMoves() {
        return moveStack.size();
    }


    public Field getField(int row, int col) {
        return this.board[row][col];
    }
    
}
