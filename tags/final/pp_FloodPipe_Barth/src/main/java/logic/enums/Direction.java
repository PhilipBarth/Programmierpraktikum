package logic.enums;

import java.util.EnumSet;

/**
 * Enum for a Direction. Provides methods for calculating Directions from int-Values, and opposite Direction values.
 * Each direction is represented by a bit mask stored in the mask field. The class contains four enum constants:
 * TOP, RIGHT, BOTTOM, and LEFT, each representing a direction.
 *
 * @author Philip Barth
 */
public enum Direction {
    /**
     * Top Direction
     */
    TOP(0b0001),

    /**
     * Right Direction
     */
    RIGHT(0b0010),

    /**
     * Bottom Direction
     */
    BOTTOM(0b0100),

    /**
     * Left Direction
     */
    LEFT(0b1000);


    /**
     * Constant for the maximum Value of a {@link PipeType} possible
     */
    private static final int MAX_VALUE_MASK = 14;
    /**
     * Mask of this direction representing the int-Value
     */
    private final int mask;

    /**
     * Constructor with the corresponding mask
     *
     * @param mask mask of the Direction
     */
    Direction(int mask) {
        this.mask = mask;
    }

    /**
     * Provides an EnumSet of the Directions, which are set in the int-Value provided
     *
     * @param value value to which the Directions should be provided
     * @return Set of Directions set in the value
     * @throws IllegalArgumentException thrown, if an illegal int-Value is provided
     */
    public static EnumSet<Direction> getDirections(int value) throws IllegalArgumentException {
        if (value > MAX_VALUE_MASK || value < 0) {
            throw new IllegalArgumentException("Wert ist nicht im richtigen Bereich: " + value);
        } else {
            // Add Openings based on mask
            EnumSet<Direction> openings = EnumSet.noneOf(Direction.class);
            if ((value & TOP.mask) == TOP.mask) {
                openings.add(TOP);
            }
            if ((value & LEFT.mask) == LEFT.mask) {
                openings.add(LEFT);
            }
            if ((value & RIGHT.mask) == RIGHT.mask) {
                openings.add(RIGHT);
            }
            if ((value & BOTTOM.mask) == BOTTOM.mask) {
                openings.add(BOTTOM);
            }
            return openings;
        }
    }

    /**
     * Getter for the mask
     *
     * @return mask of the current Direction
     */
    public int getMask() {
        return mask;
    }

    /**
     * Provides the opposite Direction
     *
     * @return opposite Direction of this element
     */
    public Direction getOpposite() {
        return switch (this) {
            case TOP -> Direction.BOTTOM;
            case RIGHT -> Direction.LEFT;
            case BOTTOM -> Direction.TOP;
            case LEFT -> Direction.RIGHT;
        };
    }

}
