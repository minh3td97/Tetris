// Piece.java
package tetris;

import java.util.*;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * Each piece is defined by the blocks that make up its body.
 * <p>
 * Typical client code looks like...
 * <pre>
 * Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 * int width = pyra.getWidth();			// 3
 * Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way
 *
 * Piece[] pieces = Piece.getPieces();	// the array of root pieces
 * Piece stick = pieces[STICK];
 * int width = stick.getWidth();		// get its width
 * Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 * </pre>
 */

public class Piece {
    // Starter code specs out a few basic things, leaving
    // the algorithms to be done.
    public final int NUMBER_CELL = 4;

    private TPoint[] body;
    private int[] skirt;
    private int width;
    private int height;
    private Piece next; // "next" rotation
    private TPoint maxPoint;
    static private Piece[] pieces;    // singleton static array of first rotations

    /**
     * Defines a new piece given a TPoint[] array of its body.
     * Makes its own copy of the array and the TPoints inside it.
     */
    public Piece(TPoint[] points) {
        this.body = points;

        TPoint max = new TPoint(0, 0);
        maxPoint = new TPoint(0, 0);

        for (int i = 0; i < NUMBER_CELL; ++i) {
            if (this.body[i].x > max.x) max.x = this.body[i].x;
            if (this.body[i].y > max.y) max.y = this.body[i].y;

            if (maxPoint.x < this.body[i].x || (maxPoint.x == this.body[i].x && maxPoint.y <= this.body[i].y) ) {
                maxPoint.x = this.body[i].x;
                maxPoint.y = this.body[i].y;
            }
        }

        width = max.x + 1;
        height = max.y + 1;

        boolean[] isChecked = new boolean[width];
        for (int i = 0; i < width; ++i) {
            isChecked[i] = false;
        }
        skirt = new int[width];
        for (int i = 0; i < NUMBER_CELL; ++i) {
            if (!isChecked[this.body[i].x]) {
                skirt[this.body[i].x] = this.body[i].y;
                isChecked[this.body[i].x] = true;
            } else if (skirt[this.body[i].x] > this.body[i].y) {
                skirt[this.body[i].x] = this.body[i].y;
            }
        }
    }

    /**
     * Alternate constructor, takes a String with the x,y body points
     * all separated by spaces, such as "0 0  1 0  2 0	1 1".
     * (provided)
     */
    public Piece(String points) {
        this(parsePoints(points));
    }

    /**
     * Returns the width of the piece measured in blocks.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the piece measured in blocks.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns a pointer to the piece's body. The caller
     * should not modify this array.
     */
    public TPoint[] getBody() {
        return body;
    }

    /**
     * Returns a pointer to the piece's skirt. For each x value
     * across the piece, the skirt gives the lowest y value in the body.
     * This is useful for computing where the piece will land.
     * The caller should not modify this array.
     */
    public int[] getSkirt() {
        return skirt;
    }

    private boolean equalSkirt(Piece other) {
        if (this.width == other.width) {
            for (int i = 0; i < this.width; ++i) {
                if (this.skirt[i] != other.skirt[i]) {
                    return false;
                }
            }
            return true;
        } else return false;
    }

    /**
     * Returns a new piece that is 90 degrees counter-clockwise
     * rotated from the receiver.
     */
    public Piece computeNextRotation() {
        TPoint[] rotation = new TPoint[NUMBER_CELL];
        for (int i = 0; i < NUMBER_CELL; ++i){
            rotation[i] = new TPoint(0, 0);
        }

        TPoint min = new TPoint(0, 0);
        for (int i = 0; i < NUMBER_CELL; ++i) {
            int x = this.body[i].x;
            int y = this.body[i].y;
            rotation[i].x = -1 * y;
            rotation[i].y = x;

            if (min.x > rotation[i].x) min.x = rotation[i].x;
            if (min.y > rotation[i].y) min.y = rotation[i].y;
        }

        for (int i = 0; i < NUMBER_CELL; ++i) {
            rotation[i].x = rotation[i].x - min.x;
            rotation[i].y = rotation[i].y - min.y;
        }

        return new Piece(rotation);
    }

    /**
     * Returns a pre-computed piece that is 90 degrees counter-clockwise
     * rotated from the receiver.	 Fast because the piece is pre-computed.
     * This only works on pieces set up by makeFastRotations(), and otherwise
     * just returns null.
     */
    public Piece fastRotation() {
        return next;
    }


    /**
     * Returns true if two pieces are the same --
     * their bodies contain the same points.
     * Interestingly, this is not the same as having exactly the
     * same body arrays, since the points may not be
     * in the same order in the bodies. Used internally to detect
     * if two rotations are effectively the same.
     */
    public boolean equals(Object obj) {
        // standard equals() technique 1
        if (obj == this) return true;

        // standard equals() technique 2
        // (null will be false)
        if (!(obj instanceof Piece)) return false;
        Piece other = (Piece) obj;


        return (this.getHeight() == other.getHeight()
                && this.getWidth() == other.getWidth()
                && this.equalSkirt(other)
                && this.maxPoint.equals(((Piece) obj).maxPoint));
    }


    // String constants for the standard 7 tetris pieces
    public static final String STICK_STR = "0 0	0 1	0 2 0 3";
    public static final String L1_STR = "0 0 0 1 0 2 1 0";
    public static final String L2_STR = "0 0 1 0 1 1 1 2";
    public static final String S1_STR = "0 0 1 0 1 1 2 1";
    public static final String S2_STR = "0 1 1 1 1 0 2 0";
    public static final String SQUARE_STR = "0 0 0 1 1 0 1 1";
    public static final String PYRAMID_STR = "0 0 1 0 1 1 2 0";

    // Indexes for the standard 7 pieces in the pieces array
    public static final int STICK = 0;
    public static final int L1 = 1;
    public static final int L2 = 2;
    public static final int S1 = 3;
    public static final int S2 = 4;
    public static final int SQUARE = 5;
    public static final int PYRAMID = 6;

    /**
     * Returns an array containing the first rotation of
     * each of the 7 standard tetris pieces in the order
     * STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
     * The next (counterclockwise) rotation can be obtained
     * from each piece with the {@link #fastRotation()} message.
     * In this way, the client can iterate through all the rotations
     * until eventually getting back to the first rotation.
     * (provided code)
     */
    public static Piece[] getPieces() {
        // lazy evaluation -- create static array if needed
        if (Piece.pieces == null) {
            // use makeFastRotations() to compute all the rotations for each piece
            Piece.pieces = new Piece[]{
                    makeFastRotations(new Piece(STICK_STR)),
                    makeFastRotations(new Piece(L1_STR)),
                    makeFastRotations(new Piece(L2_STR)),
                    makeFastRotations(new Piece(S1_STR)),
                    makeFastRotations(new Piece(S2_STR)),
                    makeFastRotations(new Piece(SQUARE_STR)),
                    makeFastRotations(new Piece(PYRAMID_STR)),
            };
        }


        return Piece.pieces;
    }


    /**
     * Given the "first" root rotation of a piece, computes all
     * the other rotations and links them all together
     * in a circular list. The list loops back to the root as soon
     * as possible. Returns the root piece. fastRotation() relies on the
     * pointer structure setup here.
     */
    /*
     Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	*/
    private static Piece makeFastRotations(Piece root) {

        Piece rotation = root.computeNextRotation();
        if (root.equals(rotation)){
            root.next = root;
        }
        else
        {
            root.next = rotation;
            Piece temp = rotation.computeNextRotation();

            while (!root.equals(temp)){
                rotation.next = temp;
                rotation = temp;
                temp = temp.computeNextRotation();
            }

            rotation.next = root;
        }

        return root;
    }


    /**
     * Given a string of x,y pairs ("0 0 0 1 0 2 1 0"), parses
     * the points into a TPoint[] array.
     * (Provided code)
     */
    private static TPoint[] parsePoints(String string) {
        List<TPoint> points = new ArrayList<>();
        StringTokenizer tok = new StringTokenizer(string);
        try {
            while (tok.hasMoreTokens()) {
                int x = Integer.parseInt(tok.nextToken());
                int y = Integer.parseInt(tok.nextToken());

                points.add(new TPoint(x, y));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse x,y string:" + string);
        }

        // Make an array out of the collection
        TPoint[] array = points.toArray(new TPoint[0]);
        return array;
    }

}
