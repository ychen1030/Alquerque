package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import qirkat.Command.Type;
import static qirkat.Command.Type.*;

/** Test command parsing.
 *  @author
 */
public class CommandTest {

    void check(String cmnd, Type type, String... operands) {
        Command c = Command.parseCommand(cmnd);
        assertEquals("Wrong type of command identified", type,
                     c.commandType());
        if (operands.length == 0) {
            assertEquals("Command has wrong number of operands", 0,
                         c.operands() == null ? 0 : c.operands().length);
        } else {
            assertArrayEquals("Operands extracted incorrectly",
                              operands, c.operands());
        }
    }

    void checkError(String cmnd) {
        check(cmnd, ERROR);
    }

    @Test public void testAUTO() {
        check("auto white", AUTO, "white");
        check("auto black", AUTO, "black");
        checkError("auto green");
        checkError("auto");
        checkError("auto red foo");
    }

    @Test public void testSEED() {
        check("seed 142", SEED, "142");
        checkError("seed");
        checkError("seed 14x");
        checkError("seed 142 foo");
    }

    @Test public void testSTART() {
        check("start", START);
        checkError("start foo");
    }

    @Test public void testQUIT() {
        check("quit", QUIT);
        checkError("quit foo");
    }

    @Test public void testCLEAR() {
        check("clear", CLEAR);
        checkError("clear foo");
    }

    @Test public void testMOVE() {
        check("a3-b3", PIECEMOVE, "a3-b3");
        checkError("a3b3");
        checkError("a3-b3 foo");
        checkError("3a-3b");
        checkError("h3-g3");
        checkError("a0-a1");
        checkError("a7-a8");
    }

}
