package qirkat;

import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Formatter;
import static qirkat.PieceColor.*;
import static qirkat.Move.*;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Yingying Chen
 */
class Board extends Observable {
    /** A new, cleared board at the start of the game. FIXed. */
    Board() {
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. FIXed. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;

        String initial = " w w w w w\n  w w w w w\n  "
                + "b b - w w\n  b b b b b\n  b b b b b";
        setPieces(initial, whoseMove());

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }


    /** Copy B into me. FIXed. */
    private void internalCopy(Board b) {
        _whoseMove = b.whoseMove();
        _positions = new PieceColor[b.positions().length];
        _directions = new int[b.directions().length];
        _oldpost = new MoveList();
        System.arraycopy(b.positions(), 0, _positions, 0, b.positions().length);
        System.arraycopy(b.directions(), 0,
                _directions, 0, b.directions().length);
        _oldpost.addAll(b.oldpost());
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     *  FIXed. */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        _positions = new PieceColor[MAX_INDEX + 1];
        _directions = new int[MAX_INDEX + 1];
        _whoseMove = nextMove;
        _oldpost = new MoveList();

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }
        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'. FIXed. */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K.
     * FIXed. */
    PieceColor get(int k) {
        assert validSquare(k);
        return _positions[k];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. FIXed. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. FIXed. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _positions[k] = v;
    }

    /** Return true iff MOV is legal on the current board.
     * FIXed. */
    boolean legalMove(Move mov) {
        if (mov == null) {
            return false;
        }
        int a = mov.fromIndex();
        int b = mov.toIndex();

        if (!validSquare(a) || !validSquare(b)
                || get(b) != EMPTY || get(a) != whoseMove()) {
            return false;
        }

        if (mov.isJump()) {
            if (get(a).opposite() != get(mov.jumpedIndex())) {
                return false;
            }
        } else {
            if (mov.isLeftMove() || mov.isRightMove()) {
                if ((mov.isLeftMove() && _directions[a] == 1)
                        || (mov.isRightMove() && _directions[a] == -1)) {
                    return false;
                }

                if ((get(a) == WHITE && mov.row0() == '5')
                        || (get(a) == BLACK && mov.row0() == '1')) {
                    return false;
                }
            } else {
                if (mov.jumpTail() != null) {
                    return false;
                }
                if ((get(a) == WHITE && a >= b)
                        || (get(a) == BLACK && a <= b)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }

        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                if (positions()[k].equals(whoseMove())) {
                    getJumps(moves, k);
                }
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. FIXed. */
    private void getMoves(ArrayList<Move> moves, int k) {
        int[][] tmp;

        if (k % 2 == 0) {
            tmp = new int[][] {new int[] {1, 0}, new int[] {1, 1},
                new int[] {0, 1}, new int[] {-1, 1}, new int[] {-1, 0},
                new int[] {-1, -1}, new int[] {0, -1}, new int[] {1, -1}};
        } else {
            tmp = new int[][] {new int[] {1, 0}, new int[] {0, 1},
                new int[] {-1, 0}, new int[] {0, -1}};
        }
        for (int[] i: tmp) {
            Move next = move(col(k), row(k),
                    (char) (col(k) + i[0]), (char) (row(k) + i[1]));
            if (legalMove(next)) {
                moves.add(next);
            }
        }
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. FIXed. */
    private void getJumps(ArrayList<Move> moves, int k) {
        int[][] tmp;

        if (k % 2 == 0) {
            tmp = new int[][] {new int[] {2, 0}, new int[] {2, 2},
                new int[] {0, 2}, new int[] {-2, 2}, new int[] {-2, 0},
                new int[] {-2, -2}, new int[] {0, -2}, new int[] {2, -2}};
        } else {
            tmp = new int[][] {new int[] {2, 0}, new int[] {0, 2},
                new int[] {-2, 0}, new int[] {0, -2}};
        }

        for (int[] i: tmp) {
            Move next = move(col(k), row(k),
                    (char) (col(k) + i[0]), (char) (row(k) + i[1]));
            Board b = new Board(this);

            if (b.checkJump(next, true)) {

                b.makeMove(next);
                b.revertWhoseMove();

                ArrayList<Move> result = new ArrayList<>();
                b.getJumps(result, next.toIndex());

                if (result.isEmpty()) {
                    moves.add(next);
                } else {
                    for (Move move: result) {
                        moves.add(move(next, move));
                    }
                }

            }
        }
    }

    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go. FIXed. */
    boolean checkJump(Move mov, boolean allowPartial) {
        if (!allowPartial) {
            if (mov == null) {
                return true;
            }
            return legalMove(mov) && checkJump(mov.jumpTail(), false);
        } else {
            return legalMove(mov);
        }
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. FIXed. */
    boolean jumpPossible(int k) {
        if (!positions()[k].equals(whoseMove())) {
            return false;
        }
        ArrayList<Move> temp = new ArrayList<>();
        getJumps(temp, k);
        return !(temp.size() == 0);
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, assuming it is legal.
     * FIXed. */
    void makeMove(Move mov) {
        assert legalMove(mov);

        _oldpost.add(mov);
        set(mov.fromIndex(), EMPTY);
        set(mov.toIndex(), whoseMove());
        _directions[mov.fromIndex()] = 0;
        _directions[mov.toIndex()] = 0;

        if (mov.isJump()) {
            set(mov.jumpedIndex(), EMPTY);
        } else if (mov.isRightMove()) {
            _directions[mov.toIndex()] = 1;
        } else if (mov.isLeftMove()) {
            _directions[mov.toIndex()] = -1;
        }

        while (mov.jumpTail() != null) {
            mov = mov.jumpTail();
            set(mov.fromIndex(), EMPTY);
            set(mov.toIndex(), whoseMove());
            if (mov.isJump()) {
                set(mov.jumpedIndex(), EMPTY);
            }
        }

        _whoseMove = whoseMove().opposite();
        setChanged();
        notifyObservers();
    }

    /** Undo the last move, if any.
     * FIXed. */
    void undo() {
        if (_oldpost.isEmpty()) {
            throw new IllegalArgumentException("no previous step");
        } else {
            Move prev = _oldpost.get(_oldpost.size() - 1);
            _whoseMove = whoseMove().opposite();
            if (prev.isJump()) {
                undoJumps(prev);
            } else {
                set(prev.fromIndex(), whoseMove());
                set(prev.toIndex(), EMPTY);
            }
            _oldpost.remove(_oldpost.size() - 1);
        }
        setChanged();
        notifyObservers();
    }

    /** Undo sequential jumps.
     * @param jump a sequential jump to be undone. */
    private void undoJumps(Move jump) {
        if (jump.jumpTail() == null) {
            set(jump.fromIndex(), whoseMove());
            set(jump.jumpedIndex(), whoseMove().opposite());
            set(jump.toIndex(), EMPTY);
        } else {
            undoJumps(jump.jumpTail());
            set(jump.fromIndex(), whoseMove());
            set(jump.jumpedIndex(), whoseMove().opposite());
            set(jump.toIndex(), EMPTY);
        }
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Tentative override method for test purposes.
     * public boolean equals(Object o) {
        if (o instanceof Board) {
            Board b = (Board) o;
            return (this.toString().equals(b.toString())
                    && _whoseMove == b.whoseMove()
                    && _oldpost.toString().equals(b.oldpost().toString()));
        } else {
            return false;
        }
    }*/

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges.
     *  FIXed. */
    String toString(boolean legend) {
        Formatter out = new Formatter();

        for (int k = 4; k >= 0; k--) {
            for (int i = 0; i <= 4; i++) {
                if (i == 0) {
                    out.format("  %s", get(k * 5 + i).shortName());
                } else if (k == 0) {
                    out.format(" %s", get(k * 5 + i).shortName());
                } else if (i == 4) {
                    out.format(" %s\n", get(k * 5 + i).shortName());
                } else {
                    out.format(" %s", get(k * 5 + i).shortName());
                }
            }
        }
        return out.toString();
    }

    /** Return true iff there is a move for the current player.
     * FIXed. */
    public boolean isMove() {
        for (int k = 0; k <= MAX_INDEX; k++) {
            for (int j = 0; j <= MAX_INDEX; j++) {
                if (legalMove(move(col(k), row(k), col(j), row(j)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set _whosemove back to my move. */
    public void revertWhoseMove() {
        _whoseMove = _whoseMove.opposite();
    }

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** Create an array to store all positions.*/
    private PieceColor[] _positions;

    /** @return the _positions of the object. */
    public PieceColor[] positions() {
        return _positions;
    }

    /** Create an array to store all directions.*/
    private int[] _directions;

    /** @return the _directions of the object. */
    public int[] directions() {
        return _directions;
    }

    /** Create a MoveList to store all passed moves.*/
    private MoveList _oldpost;

    /** @return the old positions of the object. */
    public MoveList oldpost() {
        return _oldpost;
    }


    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}
