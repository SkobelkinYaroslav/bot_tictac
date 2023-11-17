package ru.bot_hak.bot_tictac.bot.negamax;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;


@Data
public class Move implements Serializable {
    public final int row;
    public final int col;

}
