package qirkat;

import ucb.gui2.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.Observer;
import java.util.Observable;

import java.awt.event.MouseEvent;

import static qirkat.PieceColor.*;

/** Widget for displaying a Qirkat board.
 *  @author Yingying Chen
 */
class BoardWidget extends Pad implements Observer {

    /** Length of side of one square, in pixels. */
    static final int SQDIM = 50;
    /** Number of squares on a side. */
    static final int SIDE = Move.SIDE;
    /** Radius of circle representing a piece. */
    static final int PIECE_RADIUS = 15;

    /** Color of white pieces. */
    private static final Color WHITE_COLOR = Color.WHITE;
    /** Color of "phantom" white pieces. */
    /** Color of black pieces. */
    private static final Color BLACK_COLOR = Color.BLACK;
    /** Color of painted lines. */
    private static final Color LINE_COLOR = Color.BLACK;
    /** Color of blank squares. */
    private static final Color BLANK_COLOR = new Color(100, 100, 100);

    /** Stroke for lines.. */
    private static final BasicStroke LINE_STROKE = new BasicStroke(1.0f);

    /** Stroke for outlining pieces. */
    private static final BasicStroke OUTLINE_STROKE = LINE_STROKE;

    /** Model being displayed. */
    private static Board _model;

    /** A new widget displaying MODEL. */
    BoardWidget(Board model) {
        _model = model;
        setMouseHandler("click", this::readMove);
        _model.addObserver(this);
        _dim = SQDIM * SIDE;
        setPreferredSize(_dim, _dim);
    }

    /** Indicate that the squares indicated by MOV are the currently selected
     *  squares for a pending move. */
    void indicateMove(Move mov) {
        _selectedMove = mov;
        repaint();
    }

    @Override
    /* FIXME */
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(BLANK_COLOR);
        g.fillRect(0, 0, _dim, _dim);
        g.setColor(LINE_COLOR);
        g.setStroke(LINE_STROKE);
        for (int i = 1; i < SIDE; i++) {
            g.drawLine(SQDIM * i, 0, SQDIM * i, _dim);
            g.drawLine(0, SQDIM * i, _dim, SQDIM * i);

        }
        g.fillOval(0, 0, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
    }

    /** Notify observers of mouse's current position from click event WHERE. */
    private void readMove(String unused, MouseEvent where) {
        int x = where.getX(), y = where.getY();
        char mouseCol, mouseRow;
        if (where.getButton() == MouseEvent.BUTTON1) {
            mouseCol = (char) (x / SQDIM + 'a');
            mouseRow = (char) ((SQDIM * SIDE - y) / SQDIM + '1');
            if (mouseCol >= 'a' && mouseCol <= 'g'
                && mouseRow >= '1' && mouseRow <= '7') {
                setChanged();
                notifyObservers("" + mouseCol + mouseRow);
            }
        }
    }

    @Override
    public synchronized void update(Observable model, Object arg) {
        repaint();
    }




    /** Dimension of current drawing surface in pixels. */
    private int _dim;

    /** A partial Move indicating selected squares. */
    private Move _selectedMove;
}
