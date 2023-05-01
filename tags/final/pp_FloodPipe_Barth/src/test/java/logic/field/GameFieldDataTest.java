package logic.field;


import logic.Position;
import logic.enums.FieldError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testcases for the {@link GameFieldData} class validating different kinds of inputs
 */
public class GameFieldDataTest {

    private static final int[][] DEFAULT_BOARD = {
            {1, 0, 0},
            {1, 0, 0},
            {1, 0, 1}
    };

    private static final Position DEFAULT_POSITION = new Position(0, 0);

    @Test
    public void testValidate_isValid() {
        boolean overflow = false;
        FieldError exp = new GameFieldData(DEFAULT_POSITION, overflow, DEFAULT_BOARD).validate();
        assertEquals(FieldError.ERR_NULL, exp);
    }

    @Test
    public void testValidate_noSource() {
        FieldError exp = new GameFieldData(null, false, DEFAULT_BOARD).validate();
        assertEquals(FieldError.ERR_NO_SOURCE, exp);
    }

    @Test
    public void testValidate_noOverflow() {
        FieldError exp = new GameFieldData(DEFAULT_POSITION, null, DEFAULT_BOARD).validate();
        assertEquals(FieldError.ERR_NO_OVERFLOW, exp);
    }

    @Test
    public void testValidate_noBoard() {
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, null).validate();
        assertEquals(FieldError.ERR_NO_BOARD, exp);
    }

    @Test
    public void testValidate_sourceOutOfBounds() {
        FieldError exp = new GameFieldData(new Position(3, 3), false, DEFAULT_BOARD).validate();
        assertEquals(FieldError.ERR_SOURCE_OUT_OF_BOUNDS, exp);
    }

    @Test
    public void testValidate_sourceMaxViable() {
        FieldError exp = new GameFieldData(new Position(2, 2), false, DEFAULT_BOARD).validate();
        assertEquals(FieldError.ERR_NULL, exp);
    }

    @Test
    public void testValidate_invalidColsMin() {
        int[][] board = new int[GameField.MIN_AMOUNT_COLS - 1][GameField.MIN_AMOUNT_ROWS];
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_INVALID_COLS, exp);
    }

    @Test
    public void testValidate_invalidColsMax() {
        int[][] board = new int[GameField.MAX_AMOUNT_COLS + 1][GameField.MIN_AMOUNT_ROWS];
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_INVALID_COLS, exp);
    }

    @Test
    public void testValidate_invalidRowsMin() {
        int[][] board = new int[GameField.MIN_AMOUNT_COLS][GameField.MIN_AMOUNT_ROWS - 1];
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_INVALID_ROWS, exp);
    }

    @Test
    public void testValidate_invalidRowsMax() {
        int[][] board = new int[GameField.MAX_AMOUNT_COLS][GameField.MAX_AMOUNT_ROWS + 1];
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_INVALID_ROWS, exp);
    }

    @Test
    public void testValidate_sourceOnWall() {
        FieldError exp = new GameFieldData(new Position(0, 1), false, DEFAULT_BOARD).validate();
        assertEquals(FieldError.ERR_SOURCE_POSITION_WALL, exp);
    }

    @Test
    public void testValidate_ColsUneven() {
        int[][] board = {
                {1, 2, 3},
                {2, 3}
        };
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_COLS_UNEVEN, exp);
    }

    @Test
    public void testValidate_wrongPipeValueMin() {
        int[][] board = {
                {GameFieldData.MIN_VALUE_PIPE - 1, 2},
                {2, 2}
        };
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_WRONG_PIPE_VALUE, exp);
    }

    @Test
    public void testValidate_wrongPipeValueMax() {
        int[][] board = {
                {GameFieldData.MAX_VALUE_PIPE + 1, 2},
                {2, 2}
        };
        FieldError exp = new GameFieldData(DEFAULT_POSITION, false, board).validate();
        assertEquals(FieldError.ERR_WRONG_PIPE_VALUE, exp);
    }

}
