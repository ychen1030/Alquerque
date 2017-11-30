package qirkat;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/** The main program for Qirkat.
 *  @author P. N. Hilfinger */
public class Main {

    /** Run Qirkat game.  Use display if ARGS[k] is '--display', timing
     *  if ARGS[k] is "--timing". */
    public static void main(String[] args) {
        boolean useGUI;
        System.out.println("CS61B Qirkat! Version 2.0");
        useGUI = false;
        _timing = false;
        for (int i = 0; i < args.length; i += 1) {
            switch (args[i]) {
            case "--display":
                useGUI = true;
                break;
            case "--timing":
                _timing = true;
                break;
            default:
                usage();
                break;
            }
        }

        Game game;
        Board board = new Board();

        game = null;
        if (useGUI) {
            try {
                System.err.printf("Sorry, --display not implemented.%n");
                System.exit(2);

                PipedWriter writer = new PipedWriter();
                GUI display = new GUI("Qirkat", board, writer);
                game = new Game(board,
                                new ReaderSource(new PipedReader(writer,
                                                                 BUFFER_LEN),
                                                 false),
                                display);
                display.display(true);
            } catch (IOException excp) {
                System.err.printf("Could not connect to display.%n");
                System.exit(1);
            }
        } else {
            game = new Game(board,
                            new ReaderSource(new InputStreamReader(System.in),
                                             true),
                            new TextReporter());
        }
        game.process();
    }

    /** Give usage message and exit. */
    static void usage() {
        System.err.println("Usage: java qirkat.Main [--display] [--timing]"
                           + " [--strict]");
        System.exit(1);
    }

    /* TIMING */

    /** Start timing an operation. */
    static void startTiming() {
        if (_timing) {
            _startTime = System.currentTimeMillis();
        }
    }

    /** End the timing started with the last call to startTiming().
     *  Report result if we are timing. */
    static void endTiming() {
        if (_timing) {
            long time = System.currentTimeMillis() - _startTime;
            System.err.printf("[%d msec]%n", time);
            _maxTime = Math.max(_maxTime, time);
            _totalTime += time;
            _numTimedOps += 1;
        }
    }

    /** Report total time statistics, if timing. */
    static void reportTotalTimes() {
        if (_timing && _numTimedOps > 0) {
            System.err.printf("[Total time: %d msec for %d operations. "
                              + "Avg: %d msec/operation. "
                              + "Max: %d msec]%n", _totalTime,
                              _numTimedOps, _totalTime / _numTimedOps,
                              _maxTime);
        }
    }

    /** True iff AIs should time. */
    private static boolean _timing;

    /** Accumulated time. */
    private static long _totalTime;

    /** Last start time. */
    private static long _startTime;

    /** Number of operations timed. */
    private static int _numTimedOps;

    /** Maximum operation time. */
    private static long _maxTime;

    /** Size of the buffer for reading commands from a GUI (bytes). */
    private static final int BUFFER_LEN = 128;

}
