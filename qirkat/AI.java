package qirkat;
import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Yingying Chen
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */

    private static final int MAX_DEPTH = 5;

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();

        Move move = findMove();
        Main.endTiming();

        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. FIXed. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || !board.isMove()) {
            return simpleFindMove(board, sense, alpha, beta);
        }

        int best = sense * -INFTY;
        for (Move M: board.getMoves()) {
            Board next = new Board(board);
            next.makeMove(M);
            int respond = findMove(next, depth - 1, false, -sense, alpha, beta);

            if (sense == 1) {
                if (respond > best) {
                    best = respond;
                    if (saveMove) {
                        _lastFoundMove = M;
                    }
                    alpha = Integer.max(alpha, respond);
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else {

                if (respond < best) {
                    best = respond;

                    if (saveMove) {
                        _lastFoundMove = M;
                    }
                    beta = Integer.min(beta, respond);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        return best;
    }

    /** one level search for a move.
     * @param board the current board.
     * @param sense sense fot min/max search.
     * @param alpha minimal value or value < ALPHA if SENSE==-1.
     * @param beta maximal value or have value > BETA if SENSE==1.
     * @return Find a move from position BOARD and return its value. */
    private int simpleFindMove(Board board, int sense, int alpha, int beta) {
        if (sense == 1 && !board.isMove()) {
            return -WINNING_VALUE;
        }
        if (sense == -1 && !board.isMove()) {
            return WINNING_VALUE;
        }
        int best = sense * -INFTY;

        for (Move M: board.getMoves()) {
            Board next = new Board(board);
            next.makeMove(M);
            int nextVal = staticScore(next);
            if (sense == 1) {
                if (nextVal >= best) {
                    best = nextVal;
                    alpha = Integer.max(alpha, nextVal);
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else {
                if (nextVal <= best) {
                    best = nextVal;
                    beta = Integer.min(beta, nextVal);
                    if (beta <= alpha) {
                        break;
                    }

                }
            }
        }
        return best;
    }

    /** Return a heuristic value for BOARD. FIXed. */
    private int staticScore(Board board) {
        int w = 0, b = 0;
        for (PieceColor s: board.positions()) {
            if (s.equals(WHITE)) {
                w++;
            }
            if (s.equals(BLACK)) {
                b++;
            }
        }
        return w - b;
    }

}
