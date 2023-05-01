package logic.enums;

import logic.field.Pipe;

/**
 * Enum representing the Rotation value of a {@link Pipe}
 *
 * @author Philip Barth
 */
public enum Rotation {


    /**
     * Not rotated
     */
    NORMAL,

    /**
     * rotated right
     */
    RIGHT,

    /**
     * Inverted
     */
    INVERTED,

    /**
     * Rotated left
     */
    LEFT;

    /**
     * Value for rotating the Pipe counterClockwise
     */
    public static final int ROTATE_COUNTER_CLOCKWISE = 3;

    /**
     * Value for rotating the Pipe clockwise
     */
    public static final int ROTATE_CLOCKWISE = 1;
}
