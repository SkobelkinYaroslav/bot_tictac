package ru.bot_hak.bot_tictac.bot.negamax;


import java.util.*;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;



@Data
@Component
public class NegamaxPlayer{

    private final long timeNanos=950000000;
    private long startTime;

    private int totalNodeCount;
    private int nonLeafCount;
    private int branchesExploredSum;

    private State state;
    private final int size = 19;
    private List<Move> moves = new ArrayList<>();


    private List<Move> getThreatResponses(State state) {
        int playerIndex = state.currentIndex;
        int opponentIndex = state.currentIndex == 2 ? 1 : 2;

        HashSet<Move> fours = new HashSet<>();
        HashSet<Move> threes = new HashSet<>();
        HashSet<Move> refutations = new HashSet<>();

        HashSet<Move> opponentFours = new HashSet<>();
        HashSet<Move> opponentThrees = new HashSet<>();
        HashSet<Move> opponentRefutations = new HashSet<>();

        for(int i = 0; i < state.board.length; i++) {
            for(int j = 0; j < state.board.length; j++) {
                if(state.board[i][j].index == opponentIndex) {
                    opponentFours.addAll(ThreatUtils.getFours(state,
                            state.board[i][j], opponentIndex));
                    opponentThrees.addAll(ThreatUtils.getThrees(state,
                            state.board[i][j], opponentIndex));
                    opponentRefutations.addAll(ThreatUtils.getRefutations
                            (state, state.board[i][j], opponentIndex));
                }
                else if(state.board[i][j].index == playerIndex) {
                    fours.addAll(ThreatUtils.getFours(state, state.board[i][j],
                            playerIndex));
                    threes.addAll(ThreatUtils.getThrees(state, state
                            .board[i][j], playerIndex));
                    refutations.addAll(ThreatUtils.getRefutations(state, state
                            .board[i][j], playerIndex));
                }
            }
        }

        if(!fours.isEmpty()) {
            return new ArrayList<>(fours);
        }

        if(!opponentFours.isEmpty()) {
            return new ArrayList<>(opponentFours);
        }

        if(!threes.isEmpty()) {
            threes.addAll(opponentRefutations);
            return new ArrayList<>(threes);
        }

        if(!opponentThrees.isEmpty()) {
            opponentThrees.addAll(refutations);
            return new ArrayList<>(opponentThrees);
        }

        return new ArrayList<>();
    }

    private List<Move> getSortedMoves(State state) {
        if(state.getMoves() == 0) {
            List<Move> moves = new ArrayList<>();
            moves.add(new Move(state.board.length / 2, state.board.length / 2));
            return moves;
        }

        List<Move> threatResponses = getThreatResponses(state);
        if(!threatResponses.isEmpty()) {
            return threatResponses;
        }

        List<ScoredMove> scoredMoves = new ArrayList<>();

        List<Move> moves = new ArrayList<>();
        for(int i = 0; i < state.board.length; i++) {
            for(int j = 0; j < state.board.length; j++) {
                if(state.board[i][j].index == 0) {
                    if(state.hasAdjacent(i, j, 2)) {
                        int score = Evaluator.evaluateField(state, i, j,
                                state.currentIndex);
                        scoredMoves.add(new ScoredMove(new Move(i, j), score));
                    }
                }
            }
        }

        Collections.sort(scoredMoves);
        for(ScoredMove move : scoredMoves) {
            moves.add(move.move);
        }
        return moves;
    }


    private int negamax(State state, int depth, int alpha, int beta)
            throws InterruptedException {
        totalNodeCount++;
        if(Thread.interrupted() || (System.nanoTime() - startTime) > timeNanos) {
            throw new InterruptedException();
        }
        if(depth == 0 || state.terminal() != 0) {
            return Evaluator.evaluateState(state, depth);
        }
        nonLeafCount++;

        int value;
        int best = Integer.MIN_VALUE;
        int countBranches = 0;

        List<Move> moves = getSortedMoves(state);

        for (Move move : moves) {
            countBranches++;
            state.makeMove(move);
            value = -negamax(state, depth - 1, -beta, -alpha);
            state.undoMove(move);
            if(value > best) {
                best = value;
            }
            if(best > alpha) alpha = best;
            if(best >= beta) {
                break;
            }
        }
        branchesExploredSum += countBranches;
        return best;
    }


    private List<Move> searchMoves(State state, List<Move> moves, int depth)
            throws InterruptedException {

        List<ScoredMove> scoredMoves = new ArrayList<>();
        for(Move move : moves) {
            scoredMoves.add(new ScoredMove(move, Integer.MIN_VALUE));
        }

        int alpha = -11000;
        int beta = 11000;
        int best = Integer.MIN_VALUE;

        for(ScoredMove move : scoredMoves) {
            state.makeMove(move.move);
            move.score = -negamax(state, depth - 1, -beta, -alpha);
            state.undoMove(move.move);
            if(move.score > best) best = move.score;
            if(best > alpha) alpha = best;
            if(best >= beta) break;
        }

        scoredMoves.sort((move1, move2) -> move2.score - move1.score);

        moves.clear();
        for(ScoredMove move : scoredMoves) moves.add(move.move);
        return moves;
    }


    private Move iterativeDeepening(int startDepth, int endDepth)  {
        this.startTime = System.nanoTime();
        List<Move> moves = getSortedMoves(state);
        if(moves.size() == 1) return moves.get(0);
        for(int i = startDepth; i <= endDepth; i++) {
            try {
                moves = searchMoves(state, moves, i);
            } catch (InterruptedException e) {
                break;
            }
        }
        return moves.get(0);
    }


    private void printPerformanceInfo() {
        if(totalNodeCount > 0) {
            long duration = (System.nanoTime() - startTime) / 1000000;
            double nodesPerMs = totalNodeCount / (duration > 0 ? duration : 1);
            double avgBranches = (double) branchesExploredSum / (double)
                    nonLeafCount;
        }
    }


    public Move getMove(Move opponentsMove) {
        moves.add(opponentsMove);
        Move bestMove = getBestMove();
        moves.add(bestMove);
        return bestMove;
    }

    private Move getBestMove() {
        this.totalNodeCount = 0;
        this.nonLeafCount = 0;
        this.branchesExploredSum = 0;

        this.state = new State(size);
        moves.forEach((move) -> {
            state.makeMove(move);
        });

        Move best = iterativeDeepening(2, 8);
        printPerformanceInfo();
        return best;
    }

    public Move beginGame() {
        Move move = new Move(10, 10);
        moves.add(move);
        return move;
    }

    private class ScoredMove implements Comparable<ScoredMove> {
        public Move move;
        public int score;
        public ScoredMove(Move move, int score) {
            this.move = move;
            this.score = score;
        }

        @Override
        public int compareTo(ScoredMove move) {
            return move.score - this.score;
        }
    }
}