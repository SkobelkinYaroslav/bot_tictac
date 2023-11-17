package ru.bot_hak.bot_tictac.bot.negamax;


import java.util.ArrayList;
import java.util.List;


class ThreatUtils {

    private static final List<ThreatPattern> REFUTATIONS;
    private static final List<ThreatPattern> THREES;
    private static final List<ThreatPattern> FOURS;

    static {
        THREES = new ArrayList<>();
        FOURS = new ArrayList<>();
        REFUTATIONS = new ArrayList<>();

        THREES.add(new ThreatPattern(new int[] {0, 1, 1, 1, 0, 0}, new int[]
                {0, 4, 5}));
        THREES.add(new ThreatPattern(new int[] {0, 0, 1, 1, 1, 0}, new int[]
                {0, 1, 5}));
        THREES.add(new ThreatPattern(new int[] {0, 1, 0, 1, 1, 0}, new int[]
                {0, 2, 5}));
        THREES.add(new ThreatPattern(new int[] {0, 1, 1, 0, 1, 0}, new int[]
                {0, 3, 5}));

        FOURS.add(new ThreatPattern(new int[] {1, 1, 1, 1, 0}, new int[] {4} ));
        FOURS.add(new ThreatPattern(new int[] {1, 1, 1, 0, 1}, new int[] {3} ));
        FOURS.add(new ThreatPattern(new int[] {1, 1, 0, 1, 1}, new int[] {2} ));
        FOURS.add(new ThreatPattern(new int[] {1, 0, 1, 1, 1}, new int[] {1} ));
        FOURS.add(new ThreatPattern(new int[] {0, 1, 1, 1, 1}, new int[] {0} ));

        REFUTATIONS.add(new ThreatPattern(new int[] {1, 1, 1, 0, 0}, new
                int[] {3, 4}));
        REFUTATIONS.add(new ThreatPattern(new int[] {1, 1, 0, 0, 1}, new
                int[] {2, 3} ));
        REFUTATIONS.add(new ThreatPattern(new int[] {1, 0, 0, 1, 1}, new
                int[] {1, 2} ));
        REFUTATIONS.add(new ThreatPattern(new int[] {0, 0, 1, 1, 1}, new
                int[] {0, 1} ));
    }


    static List<Move> getThrees(State state, Field field, int
            playerIndex) {
        return getThreatMoves(THREES, state, field, playerIndex);
    }


    static List<Move> getFours(State state, Field field, int
            playerIndex) {
        return getThreatMoves(FOURS, state, field, playerIndex);
    }


    static List<Move> getRefutations(State state, Field field, int
            playerIndex) {
        return getThreatMoves(REFUTATIONS, state, field, playerIndex);
    }


    private static List<Move> getThreatMoves(
            List<ThreatPattern> patternList,
            State state,
            Field field,
            int playerIndex) {
        List<Move> threatMoves = new ArrayList<>();
        for(int direction = 0; direction < 4; direction++) {
            Field[] directionArray = state.directions[field.row][field.col]
                    [direction];
            for(ThreatPattern pattern : patternList) {
                int patternIndex = matchPattern(directionArray, pattern
                        .getPattern(playerIndex));
                if(patternIndex != -1) {
                    for(int patternSquareIndex : pattern.getPatternSquares()) {
                        Field patternSquareField = directionArray[patternIndex +
                                patternSquareIndex];
                        threatMoves.add(new Move(patternSquareField.row,
                                patternSquareField.col));
                    }
                }
            }
        }
        return threatMoves;
    }


    private static int matchPattern(Field[] direction, int[] pattern) {
        for(int i = 0; i < direction.length; i++) {
            if(i + (pattern.length - 1) < direction.length) {
                int count = 0;
                for(int j = 0; j < pattern.length; j++) {
                    if(direction[i + j].index == pattern[j]) {
                        count++;
                    } else {
                        break;
                    }
                }
                if(count == pattern.length) {
                    return i;
                }
            } else {
                break;
            }
        }
        return -1;
    }

}
