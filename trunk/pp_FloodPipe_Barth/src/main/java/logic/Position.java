package logic;

import logic.enums.Direction;

import java.util.Objects;

/**
 * Class that represents a position on the field. It provides methods to get a neighbor position, to calculate overflow
 * and to check, if the position is valid.
 *
 * @author Philip Barth
 */
public class Position {

    /**
     * x-Coordinate
     */
    private final Integer x;

    /**
     * y-Coordinate
     */
    private final Integer y;

    /**
     * Default Constructor
     *
     * @param x x-Coordinate
     * @param y y-Coordinate
     * @throws IllegalArgumentException Exception thrown, if the coordinates are negative
     */
    public Position(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Eine Koordinate darf nicht negativ sein.");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the x-Coordinate
     *
     * @return x-Coordinate
     */
    public Integer x() {
        return x;
    }

    /**
     * Getter for the y-Coordinate
     *
     * @return y-Coordinate
     */
    public Integer y() {
        return y;
    }

    /**
     * Method for getting the neighbor Position next to this Position in the provided direction considering overflow,
     * if it is selected
     *
     * @param cols      amount of Columns on the field
     * @param rows      amount of Rows on the field
     * @param direction direction of the neighbor
     * @param overflow  overflow-flag
     * @return neighbor Position, or null, if it is out of the field
     */
    public Position getNeighborPosition(int cols, int rows, Direction direction, boolean overflow) {
        int newX = x;
        int newY = y;
        // Calculate coordinate based on direction
        switch (direction) {
            case TOP -> newY--;
            case BOTTOM -> newY++;
            case RIGHT -> newX++;
            case LEFT -> newX--;
        }
        // Consider overflow if needed
        if (overflow) {
            newX = calculateCoordinateWithOverflow(newX, cols);
            newY = calculateCoordinateWithOverflow(newY, rows);
        }

        Position pos;
        try {
            pos = new Position(newX, newY);
        //negative coordinate
        } catch (IllegalArgumentException e) {
            return null;
        }
        return pos.isInvalidPosition(cols, rows) ? null : pos;
    }

    /**
     * Calculates a Coordinate considering overflow
     *
     * @param coordinate coordinate to be calculated
     * @param limit      maximum amount of walls or rows
     * @return coordinate considering the overflow
     */
    private int calculateCoordinateWithOverflow(int coordinate, int limit) {
        if (coordinate < 0) {
            return limit - 1;
        }
        return coordinate % limit;
    }

    /**
     * Checks, if the position is inside the bounds of cols and rows
     *
     * @param cols amount of cols
     * @param rows amount of rows
     * @return true, if the position is valid
     */
    public boolean isInvalidPosition(int cols, int rows) {
        return x >= cols || y >= rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x.equals(position.x) && y.equals(position.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}


