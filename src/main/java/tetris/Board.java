// Board.java
package tetris;

/**
 * CS108 Tetris Board.
 * Represents a Tetris board -- essentially a 2-d grid
 * of booleans. Supports tetris pieces and row clearing.
 * Has an "undo" feature that allows clients to add and remove pieces efficiently.
 * Does not do any drawing or have any idea of pixels. Instead,
 * just represents the abstract 2-d board.
 */
public class Board {
    // Some ivars are stubbed out for you:
    private int width;
    private int height;
    private boolean[][] grid;
    private boolean DEBUG = true;
    boolean committed;
    public int[] widths;
    public int[] heights;
    private int maxHeight = 0;

    // back up
    private boolean[][] backupGrid; //grid was backup
    private int[] backupWidths; //widths[] was backup
    private int[] backupHeights;
    private int backupMaxHeight;

    // Here a few trivial methods are provided:

    /**
     * Creates an empty board of the given width and height
     * measured in blocks.
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new boolean[width][height];
        committed = true;


        widths = new int[height];

        heights = new int[width];

        backupGrid = new boolean[width][height];
        backupWidths = new int[height];
        backupHeights = new int[width];
    }


    /**
     * Returns the width of the board in blocks.
     */
    public int getWidth() {
        return width;
    }


    /**
     * Returns the height of the board in blocks.
     */
    public int getHeight() {
        return height;
    }


    /**
     * Returns the max column height present in the board.
     * For an empty board this is 0.
     */
    public int getMaxHeight() {
        return maxHeight;
    }


    /**
     * Checks the board for internal consistency -- used
     * for debugging.
     */
    public void sanityCheck() {
        if (DEBUG) {
            int[] checkWidths = new int[height];
            int[] checkHeights = new int[width];
            int checkMaxHeight = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (grid[x][y]) {
                        ++checkWidths[y];
                        checkHeights[x] = y + 1;
                        if (checkMaxHeight < checkHeights[x])
                            checkMaxHeight = checkHeights[x];
                    }
                }
            }
            //throw exceptions
            for (int x = 0; x < width; ++x) {
                if (checkHeights[x] != heights[x])
                    throw new RuntimeException("Heights[" + x + "]:"
                            + "\n\tExpect\t:" + checkHeights[x]
                            + "\n\tActual\t:" + heights[x] + "\n");
            }

            for (int y = 0; y < height; ++y) {
                if (checkWidths[y] != widths[y])
                    throw new RuntimeException("Widths[" + y + "]:"
                            + "\n\tExpect\t:" + checkWidths[y]
                            + "\n\tActual\t:" + widths[y] + "\n");
            }

            if (checkMaxHeight != maxHeight)
                throw new RuntimeException("Max height:"
                        + "\n\tExpect\t:" + checkMaxHeight
                        + "\n\tActual\t:" + maxHeight + "\n");
        }
    }

    /**
     * Given a piece and an x, returns the y
     * value where the piece would come to rest
     * if it were dropped straight down at that x.
     * <p>
     * <p>
     * Implementation: use the skirt and the col heights
     * to compute this fast -- O(skirt length).
     */
    public int dropHeight(Piece piece, int x) {
        int y = 0;

        int[] pieceSkirt = piece.getSkirt();

        for (int i = 0; i < piece.getWidth(); ++i) {
            if (y < heights[x + i] - pieceSkirt[i]) {
                y = heights[x + i] - pieceSkirt[i];
            }
        }

        return y;
    }


    /**
     * Returns the height of the given column --
     * i.e. the y value of the highest block + 1.
     * The height is 0 if the column contains no blocks.
     */
    public int getColumnHeight(int x) {
        return heights[x];
    }


    /**
     * Returns the number of filled blocks in
     * the given row.
     */
    public int getRowWidth(int y) {
        return widths[y];
    }


    /**
     * Returns true if the given block is filled in the board.
     * Blocks outside of the valid width/height area
     * always return true.
     */
    public boolean getGrid(int x, int y) {
        return grid[x][y];
    }


    public static final int PLACE_OK = 0;
    public static final int PLACE_ROW_FILLED = 1;
    public static final int PLACE_OUT_BOUNDS = 2;
    public static final int PLACE_BAD = 3;

    /**
     * Attempts to add the body of a piece to the board.
     * Copies the piece blocks into the board grid.
     * Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
     * for a regular placement that causes at least one row to be filled.
     * <p>
     * <p>Error cases:
     * A placement may fail in two ways. First, if part of the piece may falls out
     * of bounds of the board, PLACE_OUT_BOUNDS is returned.
     * Or the placement may collide with existing blocks in the grid
     * in which case PLACE_BAD is returned.
     * In both error cases, the board may be left in an invalid
     * state. The client can use undo(), to recover the valid, pre-place state.
     */
    public int place(Piece piece, int x, int y) {
        // flag !committed problem
        if (!committed) throw new RuntimeException("place commit problem");
        committed = false;
        backup();
        int result = PLACE_OK;

        TPoint[] currentPiece = piece.getBody();

        for (int i = 0; i < piece.NUMBER_CELL; i++) {

            int currentX = x + currentPiece[i].x;
            int currentY = y + currentPiece[i].y;
            if (currentX >= width || currentX < 0 || currentY >= height || currentY < 0){
                return PLACE_OUT_BOUNDS;
            }
            if (grid[currentX][currentY]) return PLACE_BAD;
        }

        for (int i = 0; i < piece.NUMBER_CELL; i++) {

            int currentX = x + currentPiece[i].x;
            int currentY = y + currentPiece[i].y;

            grid[currentX][currentY] = true;

            ++widths[currentY];
            if (widths[currentY] == this.width) result = PLACE_ROW_FILLED;

            if (heights[currentX] < currentY + 1) heights[currentX] = currentY + 1;
            if (maxHeight < heights[currentX]) maxHeight = heights[currentX];
        }
        sanityCheck();
        return result;
    }


    /**
     * Deletes rows that are filled all the way across, moving
     * things above down. Returns the number of rows cleared.
     */

    public int clearRows() {
        if (committed){
            committed = false;
            backup();
        }

        boolean filled = false;
        int rowRecieve, rowSend, rowsCleared;
        rowsCleared = 0;

        // clearing row using a single pass method given in the handout
        for (rowRecieve = 0, rowSend = 1; rowSend < maxHeight; ++rowRecieve, ++rowSend) {
            if (!filled && widths[rowRecieve] == width) {
                filled = true;
                rowsCleared++;
            }

            while (filled && rowSend < maxHeight && widths[rowSend] == width) {
                rowsCleared++;
                rowSend++;
            }

            if (filled)
                copyRow(rowRecieve, rowSend);
        }

        if (filled)
            fillRow(rowRecieve, maxHeight);

        for (int i = 0; i < heights.length; i++) {
            heights[i] -= rowsCleared;
            if (heights[i] > 0 && !grid[i][heights[i] - 1]) {
                heights[i] = 0;
                for (int j = 0; j < maxHeight; j++)
                    if (grid[i][j])
                        heights[i] = j + 1;
            }
        }

        maxHeight = maxHeight - rowsCleared;

        sanityCheck();
        return rowsCleared;
    }

    private void fillRow(int begin, int end) {

        for (int j = begin; j < end; j++) {
            widths[j] = 0;
            for (int i = 0; i < width; i++)
                grid[i][j] = false;

        }
    }

    private void copyRow(int rowTo, int rowFrom) {

        if (rowFrom < maxHeight) {
            for (int i = 0; i < width; i++) {
                grid[i][rowTo] = grid[i][rowFrom];
            }
            widths[rowTo] = widths[rowFrom];
        } else {
            for (int i = 0; i < width; i++) {
                grid[i][rowTo] = false;
            }
            widths[rowTo] = 0;
        }
    }

    public void backup() {
        System.arraycopy(widths, 0, backupWidths, 0, widths.length);
        System.arraycopy(heights, 0, backupHeights, 0, heights.length);

        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, backupGrid[i], 0, grid[i].length);
        }

        backupMaxHeight = maxHeight;
    }

    /**
     * Reverts the board to its state before up to one place
     * and one clearRows();
     * If the conditions for undo() are not met, such as
     * calling undo() twice in a row, then the second undo() does nothing.
     * See the overview docs.
     */

    public void undo() {
        if (!committed) {
            int[] temp = backupWidths;
            backupWidths = widths;
            widths = temp;

            temp = backupHeights;
            backupHeights = heights;
            heights = temp;

            boolean[][] tempGrid = backupGrid;
            backupGrid = grid;
            grid = tempGrid;

            maxHeight = backupMaxHeight;
        }
        commit();
        sanityCheck();
    }

    /**
     * Puts the board in the committed state.
     */
    public void commit() {
        committed = true;
    }


    /*
     Renders the board state as a big String, suitable for printing.
     This is the sort of print-obj-state utility that can help see complex
     state change over time.
     (provided debugging utility)
     */
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (int y = height - 1; y >= 0; y--) {
            buff.append('|');
            for (int x = 0; x < width; x++) {
                if (getGrid(x, y)) buff.append('+');
                else buff.append(' ');
            }
            buff.append("|\n");
        }
        for (int x = 0; x < width + 2; x++) buff.append('-');
        return (buff.toString());
    }
}


