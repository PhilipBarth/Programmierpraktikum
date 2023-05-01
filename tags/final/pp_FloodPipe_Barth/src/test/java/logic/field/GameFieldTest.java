package logic.field;

import logic.Position;
import logic.enums.Direction;
import logic.enums.PipeType;
import org.junit.Test;

import java.util.*;

import static logic.field.GameField.*;
import static org.junit.Assert.*;

/**
 * Class for testing the methods of {@link GameField} class. Provides tests for creating a solved field, checking the
 * wall percentage limitations, testing reachable Pipes and unconnected pipes, and possible {@link PipeType} values for
 * different neighbor situations
 *
 * @author Philip Barth
 */
public class GameFieldTest {

    /**
     * Constant for multiple runs for checking the wall percentages
     */
    private static final int AMOUNT_OF_RUNS_100 = 100;

    /**
     * Constant for ten runs for checking the wall percentages. Used for
     * {@link #testCreateSolvedGameField_MaxColsRowsMinWalls_NoOverflow()}} because this otherwise takes a very
     * long time to finish
     */
    private static final int AMOUNT_OF_RUNS_10 = 10;

    /**
     * 25% walls
     */
    private static final int PERCENTAGE_QUARTER_WALLS = 25;

    /**
     * 50% walls
     */
    private static final int PERCENTAGE_HALF_WALLS = 50;

    /**
     * no walls
     */
    private static final int ZERO_WALLS = 0;

    /**
     * no walls with deviation to check for the bandwidth of created constellations
     */
    private static final int NO_WALLS_WITH_DEVIATION = 3;

    /**
     * max value half walls (Default Cols, Default Rows)
     */
    private static final int MAX_WALLS_DEFAULT_FIELD_HALF_WALLS = 50;

    /**
     * half walls with deviation (Default Cols, Default Rows)
     */
    private static final int MAX_WALLS_DEFAULT_FIELD_HALF_WALLS_WITH_DEVIATION = 40;

    /**
     * max value for half walls (Max Cols, Max Rows)
     */
    private static final int WALLS_MAX_FIELD_HALF_WALLS = 112;

    /**
     * half walls with deviation (Max Cols, Max Rows)
     */
    private static final int WALLS_MAX_FIELD_HALF_WALLS_WITH_DEVIATION = 100;

    /**
     * max value for quarter walls (default Cols, default Rows)
     */
    private static final int WALLS_DEFAULT_FIELD_QUARTER_WALLS = 25;

    /**
     * max value for quarter walls with deviation (default Cols, default Rows)
     */
    private static final int WALLS_DEFAULT_FIELD_QUARTER_WALLS_WITH_DEVIATION = 18;

    /**
     * max value for quarter walls (max Cols, max Rows)
     */
    private static final int WALLS_MAX_FIELD_QUARTER_WALLS = 56;

    /**
     * max value for quarter walls with deviation (max Cols, max Rows)
     */
    private static final int WALLS_MAX_FIELD_QUARTER_WALLS_WITH_DEVIATION = 45;

    /**
     * max value for max walls (max Cols, max Rows)
     */
    private static final int WALLS_MAX_FIELD_MAX_WALLS = 221;

    /**
     * max value for max walls with deviation (max Cols, max Rows)
     */
    private static final int WALLS_MAX_FIELD_MAX_WALLS_WITH_DEVIATION = 200;

    /**
     * max value for default walls (max Cols, max Rows)
     */
    private static final int WALLS_DEFAULT_FIELD_MAX_WALLS = 96;

    /**
     * max value for default walls with deviation (max Cols, max Rows)
     */
    private static final int WALLS_DEFAULT_FIELD_MAX_WALLS_WITH_DEVIATION = 85;

    /**
     * Creates a default GameField and returns it to use it in tests
     *
     * @return default GameField
     */
    private static GameField getDefaultGameField() {
        return new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, DEFAULT_AMOUNT_WALLS_PERCENT, false);
    }


    @Test
    public void testNonSquareGameFieldPossible() {
        // No exception should be thrown
        String board = """
                ┏━┳╸
                ┃╻┗┓
                """;
        new GameField(board, null, false);
    }

    // <---- Test errors in constructor ---->

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNotEnoughColumns() {
        new GameField(MIN_AMOUNT_COLS - 1, DEFAULT_AMOUNT_ROWS, DEFAULT_AMOUNT_WALLS_PERCENT, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorTooManyColumns() {
        new GameField(MAX_AMOUNT_COLS + 1, DEFAULT_AMOUNT_ROWS, DEFAULT_AMOUNT_WALLS_PERCENT, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNotEnoughRows() {
        new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS - 1, DEFAULT_AMOUNT_WALLS_PERCENT, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorTooManyRows() {
        new GameField(MIN_AMOUNT_COLS, MAX_AMOUNT_ROWS + 1, DEFAULT_AMOUNT_WALLS_PERCENT, false);
    }

    // <--- Test edge cases for the constructor --->
    @Test
    public void testConstructorMinAmounts() {
        new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, MIN_AMOUNT_WALLS_PERCENT, false);
    }

    @Test
    public void testConstructorMaxAmounts() {
        new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, false);
    }

    // <--- Test generation of gameField (within maxPercentage + min and max values) --->
    @Test
    public void testCreateSolvedGameField_minColsRowsWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, ZERO_WALLS, false);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_minColsRowsWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, ZERO_WALLS, true);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    // <--- Tests for the creation process of the gameField (tests bandwidths of walls and validations) --->

    @Test
    public void testCreateSolvedGameField_minColsRowsMaxWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, false);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_minColsRowsMaxWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, true);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_minColsRowsHalfWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, PERCENTAGE_HALF_WALLS, false);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_minColsRowsHalfWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, PERCENTAGE_HALF_WALLS, true);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_minColsRowsQuarterWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, PERCENTAGE_QUARTER_WALLS, false);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_minColsRowsQuarterWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MIN_AMOUNT_COLS, MIN_AMOUNT_ROWS, PERCENTAGE_QUARTER_WALLS, true);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsMinWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, MIN_AMOUNT_WALLS_PERCENT, false);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsMinWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, MIN_AMOUNT_WALLS_PERCENT, true);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsHalfWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, PERCENTAGE_HALF_WALLS, false);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= MAX_WALLS_DEFAULT_FIELD_HALF_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= MAX_WALLS_DEFAULT_FIELD_HALF_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsHalfWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, PERCENTAGE_HALF_WALLS, true);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= MAX_WALLS_DEFAULT_FIELD_HALF_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue > 0);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsQuarterWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, PERCENTAGE_QUARTER_WALLS, false);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_DEFAULT_FIELD_QUARTER_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_DEFAULT_FIELD_QUARTER_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsQuarterWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, PERCENTAGE_QUARTER_WALLS, true);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_DEFAULT_FIELD_QUARTER_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue > 0);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsMaxWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, false);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_DEFAULT_FIELD_MAX_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_DEFAULT_FIELD_MAX_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_DefaultColsRowsMaxWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(DEFAULT_AMOUNT_COLS, DEFAULT_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, true);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_DEFAULT_FIELD_MAX_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_DEFAULT_FIELD_MAX_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsMinWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_10 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, MIN_AMOUNT_WALLS_PERCENT, false);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsMinWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, MIN_AMOUNT_WALLS_PERCENT, true);
            isValid = gameField.getAmountOfWalls() == ZERO_WALLS;
        }
        assertTrue(isValid);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsHalfWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, PERCENTAGE_HALF_WALLS, false);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_MAX_FIELD_HALF_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_MAX_FIELD_HALF_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsHalfWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, PERCENTAGE_HALF_WALLS, true);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_MAX_FIELD_HALF_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue > 0 );
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsQuarterWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, PERCENTAGE_QUARTER_WALLS, false);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_MAX_FIELD_QUARTER_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_MAX_FIELD_QUARTER_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsQuarterWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, PERCENTAGE_QUARTER_WALLS, true);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_MAX_FIELD_QUARTER_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue > 0);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsMaxWalls_NoOverflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, false);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_MAX_FIELD_MAX_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_MAX_FIELD_MAX_WALLS_WITH_DEVIATION);
    }

    @Test
    public void testCreateSolvedGameField_MaxColsRowsMaxWalls_Overflow() {
        GameField gameField;
        boolean isValid = true;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int amountWalls;
        for (int i = 0; i < AMOUNT_OF_RUNS_100 && isValid; i++) {
            gameField = new GameField(MAX_AMOUNT_COLS, MAX_AMOUNT_ROWS, MAX_AMOUNT_WALLS_PERCENT, true);
            amountWalls = gameField.getAmountOfWalls();
            isValid = amountWalls <= WALLS_MAX_FIELD_MAX_WALLS;
            minValue = Integer.min(minValue, amountWalls);
            maxValue = Integer.max(maxValue, amountWalls);
        }
        assertTrue(isValid && minValue <= NO_WALLS_WITH_DEVIATION && maxValue >= WALLS_MAX_FIELD_MAX_WALLS_WITH_DEVIATION);
    }

    // <--- Tests for connected Positions from the source --->

    @Test
    public void testGetConnectedPositionsAsSet_AllConnected() {
        String board =
                "┏┓\n"
                        + "┗┛";

        Set<Position> exp = new HashSet<>();
        exp.add(new Position(0, 0));
        exp.add(new Position(0, 1));
        exp.add(new Position(1, 0));
        exp.add(new Position(1, 1));

        GameField gameField = new GameField(board, new Position(0, 0), false);
        Set<Position> res = gameField.getConnectedPositionsAsSet();
        assertTrue(res.containsAll(exp));
    }

    @Test
    public void testGetConnectedPositionsAsSet_NotConnected() {
        String board =
                "┏╸\n"
                        + "╹╺";

        Set<Position> exp = new HashSet<>();
        exp.add(new Position(0, 0));
        exp.add(new Position(0, 1));
        exp.add(new Position(1, 0));

        GameField gameField = new GameField(board, new Position(0, 0), false);
        Set<Position> res = gameField.getConnectedPositionsAsSet();
        assertTrue(res.containsAll(exp));
    }

    @Test
    public void testGetConnectedPositionsAsSet_WithWall() {
        String board =
                """
                        ┏━┓
                        ┃╳┃
                        ┗━┛
                        """;

        Set<Position> exp = new HashSet<>();
        exp.add(new Position(0, 0));
        exp.add(new Position(0, 1));
        exp.add(new Position(0, 2));
        exp.add(new Position(1, 0));
        exp.add(new Position(1, 2));
        exp.add(new Position(2, 0));
        exp.add(new Position(2, 1));
        exp.add(new Position(2, 2));


        GameField gameField = new GameField(board, new Position(0, 0), false);
        Set<Position> res = gameField.getConnectedPositionsAsSet();
        assertTrue(res.containsAll(exp));
    }

    @Test
    public void testGetConnectedPositionsAsSet_WithWallTwoParts() {
        String board =
                """
                        ╺━╸
                        ╳╳╳
                        ╺━╸
                        """;

        Set<Position> exp = new HashSet<>();
        exp.add(new Position(0, 0));
        exp.add(new Position(1, 0));
        exp.add(new Position(2, 0));


        GameField gameField = new GameField(board, new Position(0, 0), false);
        Set<Position> res = gameField.getConnectedPositionsAsSet();
        assertTrue(res.containsAll(exp));
    }

    @Test
    public void testGetConnectedPositionsAsSet_overflow() {
        String board =
                """
                        ┗━┛
                        ╳╳╳
                        ┏━┓
                        """;

        Set<Position> exp = new HashSet<>();
        exp.add(new Position(0, 0));
        exp.add(new Position(1, 0));
        exp.add(new Position(2, 0));
        exp.add(new Position(0, 2));
        exp.add(new Position(1, 2));
        exp.add(new Position(2, 2));

        GameField gameField = new GameField(board, new Position(0, 0), true);
        Set<Position> res = gameField.getConnectedPositionsAsSet();
        assertTrue(res.containsAll(exp));
    }

    // <--- Testing for different neighbor situations, which PipeTypes are suitable to be placed

    @Test
    public void testGetPossiblePipeTypes_empty() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM, Direction.LEFT, Direction.RIGHT, Direction.TOP);
        EnumSet<Direction> optional = EnumSet.noneOf(Direction.class);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        assertTrue(res.isEmpty());
    }

    @Test
    public void testGetPossiblePipeTypes_onlyDeadEnd_Mandatory() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM);
        EnumSet<Direction> optional = EnumSet.noneOf(Direction.class);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.DEAD_END);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyDeadEnd_Optional() {
        EnumSet<Direction> mandatory = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> optional = EnumSet.of(Direction.BOTTOM);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.DEAD_END);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyLineVertical_Mandatory() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM, Direction.TOP);
        EnumSet<Direction> optional = EnumSet.noneOf(Direction.class);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyLineVertical_Optional() {
        EnumSet<Direction> mandatory = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> optional = EnumSet.of(Direction.BOTTOM, Direction.TOP);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyLineVertical_Mix() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM);
        EnumSet<Direction> optional = EnumSet.of(Direction.TOP);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyLineHorizontal_Mandatory() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.RIGHT, Direction.LEFT);
        EnumSet<Direction> optional = EnumSet.noneOf(Direction.class);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyLineHorizontal_Optional() {
        EnumSet<Direction> mandatory = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> optional = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyLineHorizontal_Mix() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.LEFT);
        EnumSet<Direction> optional = EnumSet.of(Direction.RIGHT);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyCurve_Mandatory() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM, Direction.RIGHT);
        EnumSet<Direction> optional = EnumSet.noneOf(Direction.class);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.CURVE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyCurve_Optional() {
        EnumSet<Direction> mandatory = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> optional = EnumSet.of(Direction.BOTTOM, Direction.RIGHT);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.CURVE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyCurve_mix() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM);
        EnumSet<Direction> optional = EnumSet.of(Direction.RIGHT);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.CURVE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_onlyTPipe_Mandatory() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM, Direction.RIGHT, Direction.LEFT);
        EnumSet<Direction> optional = EnumSet.of(Direction.TOP);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.T_PIPE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_CurveAndTPipe() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.BOTTOM, Direction.RIGHT);
        EnumSet<Direction> optional = EnumSet.of(Direction.LEFT);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.CURVE, PipeType.T_PIPE);
        assertEquals(exp, res);
    }

    @Test
    public void testGetPossiblePipeTypes_LineAndTPipe() {
        EnumSet<Direction> mandatory = EnumSet.of(Direction.RIGHT, Direction.LEFT);
        EnumSet<Direction> optional = EnumSet.of(Direction.BOTTOM);
        List<PipeType> res = getDefaultGameField().getPossiblePipeTypes(mandatory, optional);
        List<PipeType> exp = List.of(PipeType.LINE, PipeType.T_PIPE);
        assertEquals(exp, res);
    }

    // Test the conversion from Pipes to int values
    @Test
    public void testToIntAllValues() {
        String board =
                """
                        ╳╹╺┗╻
                        ┃┏┣╸┛
                        ━┻┓┫┳
                        """;

        int[][] exp = {
                {0, 5, 10},
                {1, 6, 11},
                {2, 7, 12},
                {3, 8, 13},
                {4, 9, 14}
        };

        GameField field = new GameField(board, null, false);

        assertArrayEquals(exp, field.toInt());
    }

    // <--- Tests for getting connected Positions from the source with the distances to it --->
    @Test
    public void testGetConnectedPositionsWithDistancesNoOverflow() {
        String board =
                """
                        ┏━┓
                        ┗━┛
                        """;

        GameField field = new GameField(board, new Position(1, 0), false);

        Map<Integer, List<Position>> exp = Map.ofEntries(
                Map.entry(0, List.of(new Position(1, 0))),
                Map.entry(1, List.of(new Position(0, 0), new Position(2, 0))),
                Map.entry(2, List.of(new Position(0, 1), new Position(2, 1))),
                Map.entry(3, List.of(new Position(1, 1)))
        );

        Map<Integer, List<Position>> res = field.getConnectedPositionsWithDistances();

        for (Map.Entry<Integer, List<Position>> entry : exp.entrySet()) {
            Set<Position> expPositions = new HashSet<>(entry.getValue());
            Set<Position> resPositions = new HashSet<>(res.get(entry.getKey()));
            assertEquals(expPositions, resPositions);
        }
    }

    @Test
    public void testGetConnectedPositionsWithDistancesOverflow() {
        String board =
                """
                        ┗━┛
                        ┏━┓
                        """;

        GameField field = new GameField(board, new Position(1, 0), true);

        Map<Integer, List<Position>> exp = Map.ofEntries(
                Map.entry(0, List.of(new Position(1, 0))),
                Map.entry(1, List.of(new Position(0, 0), new Position(2, 0))),
                Map.entry(2, List.of(new Position(0, 1), new Position(2, 1))),
                Map.entry(3, List.of(new Position(1, 1)))
        );

        Map<Integer, List<Position>> res = field.getConnectedPositionsWithDistances();

        for (Map.Entry<Integer, List<Position>> entry : exp.entrySet()) {
            Set<Position> expPositions = new HashSet<>(entry.getValue());
            Set<Position> resPositions = new HashSet<>(res.get(entry.getKey()));
            assertEquals(expPositions, resPositions);
        }
    }

    @Test
    public void testGetUnconnectedPositions_AllConnected() {
        String board =
                """
                        ┏━┓
                        ┗━┛
                        """;

        GameField field = new GameField(board, new Position(1, 0), false);
        Set<Position> res = field.getUnconnectedPositions();
        Set<Position> exp = Collections.emptySet();

        assertEquals(exp, res);
    }

    @Test
    public void testGetUnconnectedPositions_AllConnectedWithWalls() {
        String board =
                """
                        ┏━┓
                        ┃╳┃
                        ┗━┛
                        """;

        GameField field = new GameField(board, new Position(1, 0), false);
        Set<Position> res = field.getUnconnectedPositions();
        Set<Position> exp = Collections.emptySet();

        assertEquals(exp, res);
    }

    @Test
    public void testGetUnconnectedPositions_PositionsNotConnected() {
        String board =
                """
                        ┏━┓
                        ┗━┛
                        ╺━╸""";

        GameField field = new GameField(board, new Position(1, 0), false);
        Set<Position> res = field.getUnconnectedPositions();
        Set<Position> exp = Set.of(new Position(0, 2), new Position(1, 2), new Position(2, 2));

        assertEquals(exp, res);
    }

    // <--- Tests for the openings that are connected with the source on the GameField --->
    @Test
    public void testAllOpeningsConnectedTrueNoOverflow() {
        String board =
                """
                        ┏━┓
                        ┗━┛
                        """;
        assertTrue(new GameField(board, new Position(0, 0), false).allOpeningsConnected());
    }

    @Test
    public void testAllOpeningsConnectedTrueOverflow() {
        String board =
                """
                        ┗━┛
                        ┏━┓
                        """;
        assertTrue(new GameField(board, new Position(0, 0), true).allOpeningsConnected());
    }

    @Test
    public void testAllOpeningsConnectedFalseNoOverflow() {
        String board =
                """
                        ┳━┓
                        ┗━┛
                        """;
        assertFalse(new GameField(board, new Position(0, 0), false).allOpeningsConnected());
    }

    @Test
    public void testAllOpeningsConnectedFalseOverflow() {
        String board =
                """
                        ┗━┛
                        ┳━┓
                        """;
        assertFalse(new GameField(board, new Position(0, 0), true).allOpeningsConnected());
    }
}
