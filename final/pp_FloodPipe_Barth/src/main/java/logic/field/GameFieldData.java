package logic.field;

import logic.Position;
import logic.enums.FieldError;

import static logic.field.GameField.*;

/**
 * Class that contains the data of a gameField containing a position, if overflow is selected and an int-representation
 * of the pipes on the board. Provides a method to validate the Data.
 *
 * @author Philip Barth
 */
public class GameFieldData {

    /**
     * minimum amount of openings
     */
    public static final int MIN_VALUE_PIPE = 0;
    /**
     * maximum amount of openings
     */
    public static final int MAX_VALUE_PIPE = 14;

    /**
     * Position of the source
     */
    private final Position source;

    /**
     * overflow flag
     */
    private final Boolean overflow;

    /**
     * int-representation of the board
     */
    private final int[][] board;

    /**
     * Constructor from a GameField-instance
     *
     * @param gameField GameField to be transformed
     */
    public GameFieldData(GameField gameField) {
        this.source = gameField.getSource();
        this.overflow = gameField.isOverflow();
        this.board = gameField.toInt();
    }


    /**
     * Constructor used for testcases
     *
     * @param source   Position of the source
     * @param overflow overflow-flag (can be null)
     * @param board    board of GameField
     */
    GameFieldData(Position source, Boolean overflow, int[][] board) {
        this.source = source;
        this.overflow = overflow;
        this.board = board;
    }

    /**
     * Getter for the Source position
     *
     * @return Position of source
     */
    Position getSource() {
        return source;
    }

    /**
     * Getter for the overflow-flag
     *
     * @return overflow-flag
     */
    Boolean isOverflow() {
        return overflow;
    }

    /**
     * Getter for the field
     *
     * @return field
     */
    int[][] getBoard() {
        return board;
    }

    /**
     * Validates the data and returns an Error based on the structure
     *
     * @return ERR_NULL, if the data is valid
     */
    public FieldError validate() {
        if (source == null) {
            return FieldError.ERR_NO_SOURCE;
        }

        if (source.x() == null || source.y() == null) {
            return FieldError.ERR_SOURCE_ONLY_ONE_VALUE;
        }

        int x = source.x();
        int y = source.y();
        if (x < 0 || y < 0) {
            return FieldError.ERR_NEGATIVE_SOURCE;
        }

        if (overflow == null) {
            return FieldError.ERR_NO_OVERFLOW;
        }

        if (board == null) {
            return FieldError.ERR_NO_BOARD;
        }

        if (x >= board.length || y >= board[0].length) {
            return FieldError.ERR_SOURCE_OUT_OF_BOUNDS;
        }

        if (board.length < MIN_AMOUNT_COLS || board.length > MAX_AMOUNT_COLS) {
            return FieldError.ERR_INVALID_COLS;
        }

        if (board[0].length < MIN_AMOUNT_ROWS || board[0].length > MAX_AMOUNT_ROWS) {
            return FieldError.ERR_INVALID_ROWS;
        }

        if (board[x][y] == 0) {
            return FieldError.ERR_SOURCE_POSITION_WALL;
        }

        if (!rowsEven()) {
            return FieldError.ERR_COLS_UNEVEN;
        }

        if (!cellsInRange()) {
            return FieldError.ERR_WRONG_PIPE_VALUE;
        }
        return FieldError.ERR_NULL;
    }

    /**
     * Checks if every row on the field has the same amount of values
     *
     * @return true, if the gameField is even
     */
    private boolean rowsEven() {
        int referenceLength = board[0].length;
        boolean even = true;

        //Check, if every row is equal to the length of the first one
        for (int i = 1; i < board.length && even; i++) {
            even = board[i].length == referenceLength;
        }
        return even;
    }

    /**
     * Checks if every cell in the field has a value between {@link #MIN_VALUE_PIPE} (inclusive) and
     * {@link #MAX_VALUE_PIPE} (inclusive)
     *
     * @return true, if all cells are in range
     */
    private boolean cellsInRange() {
        boolean inRange = true;
        for (int x = 0; x < board.length && inRange; x++) {
            for (int y = 0; y < board[x].length && inRange; y++) {
                inRange = board[x][y] >= MIN_VALUE_PIPE && board[x][y] <= MAX_VALUE_PIPE;
            }
        }
        return inRange;
    }
}
