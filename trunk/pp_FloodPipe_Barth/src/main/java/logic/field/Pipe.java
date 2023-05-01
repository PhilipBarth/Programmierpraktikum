package logic.field;

import logic.enums.Direction;
import logic.enums.PipeType;
import logic.enums.Rotation;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static logic.enums.PipeType.*;

/**
 * Class that represents a Pipe on the {@link GameField}. It provides Constructors from int-Values, PipeType and PipeType
 * and Rotation. Also provides Getter and Setter methods,  a method to determine the openings, and a method to rotate
 * the Pipe randomly
 *
 * @author Philip Barth
 */
public class Pipe {


    /**
     * Type of the pipe
     */
    private final PipeType type;

    /**
     * Rotation value of the pipe
     */
    private Rotation rotation;

    /**
     * Constructor that creates a Pipe instance from an int-Value representing the openings as a bit mask (e.g. 0b0101)
     *
     * @param openings value for the openings as a mask
     */
    Pipe(int openings) {
        EnumSet<Direction> openingSet = Direction.getDirections(openings);

        type = determinePipeType(openingSet);
        rotation = determineRotation(type, openingSet);
    }

    /**
     * Constructor for a pipe with  {@link Rotation#NORMAL} and a provided PipeType
     *
     * @param pipeType type to be set
     */
    public Pipe(PipeType pipeType) {
        this(pipeType, Rotation.NORMAL);
    }

    /**
     * Constructor for a Pipe with a provided type and rotation
     *
     * @param pipeType type
     * @param rotation rotation
     */
    Pipe(PipeType pipeType, Rotation rotation) {
        this.type = pipeType;
        this.rotation = rotation;
    }

    /**
     * Constructor for a Pipe from BoxDrawingCharacters. Used for testcases only.
     *
     * @param character BoxDrawingCharacter
     */
    Pipe(String character) {
        switch (character) {
            case "╸" -> {
                this.type = PipeType.DEAD_END;
                this.rotation = Rotation.NORMAL;
            }
            case "╹" -> {
                this.type = PipeType.DEAD_END;
                this.rotation = Rotation.RIGHT;
            }
            case "╻" -> {
                this.type = PipeType.DEAD_END;
                this.rotation = Rotation.LEFT;
            }
            case "╺" -> {
                this.type = PipeType.DEAD_END;
                this.rotation = Rotation.INVERTED;
            }
            case "━" -> {
                this.type = PipeType.LINE;
                this.rotation = Rotation.NORMAL;
            }
            case "┃" -> {
                this.type = PipeType.LINE;
                this.rotation = Rotation.RIGHT;
            }
            case "┏" -> {
                this.type = PipeType.CURVE;
                this.rotation = Rotation.NORMAL;
            }
            case "┗" -> {
                this.type = PipeType.CURVE;
                this.rotation = Rotation.LEFT;
            }
            case "┓" -> {
                this.type = PipeType.CURVE;
                this.rotation = Rotation.RIGHT;
            }
            case "┛" -> {
                this.type = PipeType.CURVE;
                this.rotation = Rotation.INVERTED;
            }
            case "┣" -> {
                this.type = PipeType.T_PIPE;
                this.rotation = Rotation.NORMAL;
            }
            case "┻" -> {
                this.type = PipeType.T_PIPE;
                this.rotation = Rotation.LEFT;
            }
            case "┳" -> {
                this.type = PipeType.T_PIPE;
                this.rotation = Rotation.RIGHT;
            }
            case "┫" -> {
                this.type = PipeType.T_PIPE;
                this.rotation = Rotation.INVERTED;
            }
            case "╳" -> {
                this.type = PipeType.WALL;
                this.rotation = Rotation.NORMAL;
            }
            default ->
                    throw new IllegalArgumentException("Der übergebene BoxDrawingCharacter ist falsch: " + character);
        }
    }

    /**
     * Getter method for the type
     *
     * @return {@link #type}
     */
    public PipeType getType() {
        return this.type;
    }

    /**
     * Getter method for the rotation
     *
     * @return {@link #rotation}
     */
    public Rotation getRotation() {
        return this.rotation;
    }

    /**
     * Method to get a PipeType from a Set of Directions representing the openings
     *
     * @param openings Set of openings
     * @return PipeType representing the openings
     */
    private PipeType determinePipeType(EnumSet<Direction> openings) {
        int numOpenings = openings.size();

        if (numOpenings == 0) {
            return WALL;
        } else if (numOpenings == 1) {
            return DEAD_END;
        } else if (numOpenings == 2) {
            if (openings.containsAll(Set.of(Direction.TOP, Direction.BOTTOM))
                    || openings.containsAll(Set.of(Direction.RIGHT, Direction.LEFT))) {
                return LINE;
            }
            return CURVE;
        } else if (numOpenings == 3) {
            return T_PIPE;
        }
        return null;
    }

    /**
     * Method to get a Rotation from a Set of Directions representing the openings and the corresponding pipeType.
     *
     * @param openings Set of openings
     * @return Rotation representing the openings
     */
    private Rotation determineRotation(PipeType type, EnumSet<Direction> openings) {
        if (type == DEAD_END) {
            if (openings.contains(Direction.LEFT)) {
                return Rotation.NORMAL;
            } else if (openings.contains(Direction.RIGHT)) {
                return Rotation.INVERTED;
            } else if (openings.contains(Direction.TOP)) {
                return Rotation.RIGHT;
            } else if (openings.contains(Direction.BOTTOM)) {
                return Rotation.LEFT;
            }
        } else if (type == LINE) {
            if (openings.contains(Direction.LEFT) && openings.contains(Direction.RIGHT)) {
                return Rotation.NORMAL;
            } else if (openings.contains(Direction.TOP) && openings.contains(Direction.BOTTOM)) {
                return Rotation.RIGHT;
            }
        } else if (type == CURVE) {
            if (openings.contains(Direction.RIGHT) && openings.contains(Direction.BOTTOM)) {
                return Rotation.NORMAL;
            } else if (openings.contains(Direction.LEFT) && openings.contains(Direction.BOTTOM)) {
                return Rotation.RIGHT;
            } else if (openings.contains(Direction.LEFT) && openings.contains(Direction.TOP)) {
                return Rotation.INVERTED;
            } else if (openings.contains(Direction.RIGHT) && openings.contains(Direction.TOP)) {
                return Rotation.LEFT;
            }
        } else if (type == T_PIPE) {
            if (!openings.contains(Direction.LEFT)) {
                return Rotation.NORMAL;
            } else if (!openings.contains(Direction.RIGHT)) {
                return Rotation.INVERTED;
            } else if (!openings.contains(Direction.TOP)) {
                return Rotation.RIGHT;
            } else if (!openings.contains(Direction.BOTTOM)) {
                return Rotation.LEFT;
            }
        } else if (type == WALL) {
            return Rotation.NORMAL;
        }
        return null;
    }

    /**
     * Provides the current openings considering the {@link #type} and {@link #rotation}
     *
     * @return Set of Direction values representing the openings
     */
    EnumSet<Direction> getOpenings() {
        EnumSet<Direction> openings = EnumSet.noneOf(Direction.class);
        // Check Dead End Rotation
        if (type == PipeType.DEAD_END) {
            if (rotation == Rotation.NORMAL) {
                openings.add(Direction.LEFT);
            } else if (rotation == Rotation.INVERTED) {
                openings.add(Direction.RIGHT);
            } else if (rotation == Rotation.RIGHT) {
                openings.add(Direction.TOP);
            } else if (rotation == Rotation.LEFT) {
                openings.add(Direction.BOTTOM);
            }
            // Check Line Rotation
        } else if (type == PipeType.LINE) {
            if (rotation == Rotation.NORMAL || rotation == Rotation.INVERTED) {
                openings.add(Direction.LEFT);
                openings.add(Direction.RIGHT);
            } else if (rotation == Rotation.RIGHT || rotation == Rotation.LEFT) {
                openings.add(Direction.TOP);
                openings.add(Direction.BOTTOM);
            }
            // Check Curve Rotation
        } else if (type == PipeType.CURVE) {
            if (rotation == Rotation.NORMAL) {
                openings.add(Direction.RIGHT);
                openings.add(Direction.BOTTOM);
            } else if (rotation == Rotation.RIGHT) {
                openings.add(Direction.LEFT);
                openings.add(Direction.BOTTOM);
            } else if (rotation == Rotation.INVERTED) {
                openings.add(Direction.LEFT);
                openings.add(Direction.TOP);
            } else if (rotation == Rotation.LEFT) {
                openings.add(Direction.RIGHT);
                openings.add(Direction.TOP);
            }
            // Check T Pipe Rotation
        } else if (type == PipeType.T_PIPE) {
            if (rotation == Rotation.NORMAL) {
                openings.add(Direction.RIGHT);
                openings.add(Direction.TOP);
                openings.add(Direction.BOTTOM);
            } else if (rotation == Rotation.INVERTED) {
                openings.add(Direction.LEFT);
                openings.add(Direction.TOP);
                openings.add(Direction.BOTTOM);
            } else if (rotation == Rotation.RIGHT) {
                openings.add(Direction.LEFT);
                openings.add(Direction.RIGHT);
                openings.add(Direction.BOTTOM);
            } else if (rotation == Rotation.LEFT) {
                openings.add(Direction.LEFT);
                openings.add(Direction.RIGHT);
                openings.add(Direction.TOP);
            }
        }
        return openings;
    }

    /**
     * Turns the Pipe clockwise or counterclockwise
     *
     * @param clockwise clockwise-flag
     */
    void turn(boolean clockwise) {
        if (type != WALL) {
            if (clockwise) {
                this.rotation = Rotation.values()[(this.rotation.ordinal() + Rotation.ROTATE_CLOCKWISE) % Rotation.values().length];
            } else {
                this.rotation = Rotation.values()[(this.rotation.ordinal() + Rotation.ROTATE_COUNTER_CLOCKWISE) % Rotation.values().length];
            }
        }
    }

    /**
     * Rotates this field randomly
     */
    void rotateRandomly() {
        for (int i = 0; i < new Random().nextInt(Rotation.values().length); i++) {
            this.turn(true);
        }
    }

    /**
     * Provides an int-Value representing the openings
     *
     * @return int-representation
     */
    int toInt() {
        int val = 0;
        // Get mask for the openings and connect them with an OR
        for (Direction opening : getOpenings()) {
            val |= opening.getMask();
        }
        return val;
    }

    @Override
    public String toString() {
        return switch (type) {
            case DEAD_END -> switch (rotation) {
                case NORMAL -> "╸";
                case RIGHT -> "╹";
                case LEFT -> "╻";
                case INVERTED -> "╺";
            };
            case LINE -> switch (rotation) {
                case NORMAL, INVERTED -> "━";
                case LEFT, RIGHT -> "┃";
            };
            case CURVE -> switch (rotation) {
                case NORMAL -> "┏";
                case LEFT -> "┗";
                case RIGHT -> "┓";
                case INVERTED -> "┛";
            };
            case T_PIPE -> switch (rotation) {
                case NORMAL -> "┣";
                case LEFT -> "┻";
                case RIGHT -> "┳";
                case INVERTED -> "┫";
            };
            case WALL -> "╳";
        };
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pipe pipe = (Pipe) o;
        return type == pipe.type && getOpenings().equals(((Pipe) o).getOpenings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, rotation);
    }

    /**
     * Returns a copy of the current Pipe
     *
     * @return copy of the current Pipe
     */
    Pipe copy() {
        return new Pipe(this.getType(), this.getRotation());
    }
}
