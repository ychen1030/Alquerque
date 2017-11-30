package qirkat;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/** Tests of the Board class.
 *  @author Yingying Chen
 */
public class BoardTest {

    private static final String INIT_BOARD =
        "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private static final String[] GAME1 =
    { "c2-c3", "c4-c2",
      "c1-c3", "a3-c1",
      "c3-a3", "c5-c4",
      "a3-c5-c3",
    };

    private static final String GAME1_BOARD =
        "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }

    @Test
    public void testInit1() {
        Board b0 = new Board();
        assertEquals(INIT_BOARD, b0.toString());
    }

    @Test
    public void testMoves1() {
        Board b0 = new Board();
        makeMoves(b0, GAME1);
        ArrayList<Move> tmp = b0.getMoves();
        for (Move t: tmp) {
            System.out.println(t.toString());
        }

        assertEquals(GAME1_BOARD, b0.toString());
    }


    @Test
    public void testJumps() {
        String initial = " -----\n  --bw-\n  "
                + "-bb--\n  -----\n  -----";
        Board b0 = new Board();
        b0.setPieces(initial, PieceColor.WHITE);

        ArrayList<Move> tmp = b0.getMoves();

    }

    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        makeMoves(b0, GAME1);
    }

}
