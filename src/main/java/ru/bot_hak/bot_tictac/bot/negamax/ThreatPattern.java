package ru.bot_hak.bot_tictac.bot.negamax;


public class ThreatPattern {
    private int[][] pattern;
    private final int[] patternSquares;


    public ThreatPattern(int[] pattern, int[] patternSquares) {
        this.pattern = new int[2][1];
        this.pattern[0] = pattern;
        this.pattern[1] = switchPattern(pattern);
        this.patternSquares = patternSquares;
    }


    public int[] getPattern(int playerIndex) {
        return this.pattern[playerIndex - 1];
    }


    public int[] getPatternSquares() {
        return this.patternSquares;
    }


    private int[] switchPattern(int[] pattern) {
        int[] patternSwitched = new int[pattern.length];
        for(int i = 0; i < pattern.length; i++) {
            if(pattern[i] == 1) {
                patternSwitched[i] = 2;
            }
        }
        return patternSwitched;
    }
}