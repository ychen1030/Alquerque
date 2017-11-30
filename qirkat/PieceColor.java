package qirkat;

/** Describes the classes of Piece on a Qirkat board.
 *  @author P. N. Hilfinger
 */
enum PieceColor {

    /** EMPTY: no piece.
     *  WHITE, BLACK: piece colors. */
    EMPTY,
    WHITE {
        @Override
        PieceColor opposite() {
            return BLACK;
        }

        @Override
        boolean isPiece() {
            return true;
        }
    },
    BLACK {
        @Override
        PieceColor opposite() {
            return WHITE;
        }

        @Override
        boolean isPiece() {
            return true;
        }
    };

    /** Return the piece color of my opponent, if defined. */
    PieceColor opposite() {
        throw new UnsupportedOperationException();
    }

    /** Return true iff I denote a piece rather than an empty square. */
    boolean isPiece() {
        return false;
    }

    /** Return the standard one-character denotation of this piece ('b', 'w',
     *  or '-'). */
    String shortName() {
        return this == BLACK ? "b" : this == WHITE ? "w" : "-";
    }

    @Override
    public String toString() {
        return capitalize(super.toString().toLowerCase());
    }

    /** Return WORD with first letter capitalized. */
    static String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

}
