package ru.bot_hak.bot_tictac.bot.negamax;


public class Field {
    protected final int row;
    protected final int col;
    
    protected int index;


    protected Field() {
        this.row = 0;
        this.col = 0;
        this.index = 3;
    }


    protected Field(int row, int col) {
        this.row = row;
        this.col = col;
        this.index = 0;
    }
}
