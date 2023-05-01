package logic.enums;


import logic.field.GameFieldData;

/**
 * Errortype used for informing about errors in a {@link GameFieldData} instance
 *
 * @author Philip Barth
 */
public enum FieldError {
    /**
     * No Error
     */
    ERR_NULL,

    /**
     * File was Empty
     */
    ERR_EMPTY,

    /**
     * No source provided
     */
    ERR_NO_SOURCE,

    /**
     * No overflow or invalid overflow provided
     */
    ERR_NO_OVERFLOW,

    /**
     * Source with negative value(s)
     */
    ERR_NEGATIVE_SOURCE,

    /**
     * Source coordinate(s) are out of bounds
     */
    ERR_SOURCE_OUT_OF_BOUNDS,

    /**
     * No board provided
     */
    ERR_NO_BOARD,

    /**
     * invalid amount of columns
     */
    ERR_INVALID_COLS,

    /**
     * invalid amount of rows
     */
    ERR_INVALID_ROWS,

    /**
     * source Position is on a wall field
     */
    ERR_SOURCE_POSITION_WALL,

    /**
     * Cols are not even
     */
    ERR_COLS_UNEVEN,

    /**
     * Only one coordinate for source provided
     */
    ERR_SOURCE_ONLY_ONE_VALUE,
    /**
     * Pipe value(s) too high or negativ
     */
    ERR_WRONG_PIPE_VALUE
}
