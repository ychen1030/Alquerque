package qirkat;

import static qirkat.PieceColor.*;
import static qirkat.Command.Type.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author Yingying Chen
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _prompt = myColor + ": ";
    }

    /** myMove for Manual player. FIXed. */
    @Override
    Move myMove() {

        Command move = game().getMoveCmnd(_prompt);
        if (move == null) {
            return null;
        }
        return Move.parseMove(move.toString());
    }

    /** Identifies the player serving as a source of input commands. */
    private String _prompt;
}

