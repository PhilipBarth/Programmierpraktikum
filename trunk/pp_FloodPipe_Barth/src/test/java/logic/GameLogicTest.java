package logic;

import logic.enums.PipeType;
import logic.field.GameField;
import logic.field.Pipe;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Class for testing the methods of the GameLogic. Provides tests for changing the position of the source, tests for
 * changing fields in the gameField, fields to change the columns and rows of a gameField, and tests for turning of
 * pipes.
 */
public class GameLogicTest {

    /**
     * Constant for a FakeGui
     */
    private static final GUIConnector FAKE_GUI = new FakeGUI();

    // Constants for defining positions
    private static final Position POSITION_ZERO_ZERO = new Position(0, 0);
    private static final Position POSITION_ZERO_ONE = new Position(0, 1);
    private static final Position POSITION_ZERO_TWO = new Position(0, 2);

    private static final Position POSITION_ONE_ZERO = new Position(1, 0);
    private static final Position POSITION_ONE_ONE = new Position(1, 1);
    private static final Position POSITION_ONE_TWO = new Position(1, 2);

    private static final Position POSITION_TWO_ZERO = new Position(2, 0);
    private static final Position POSITION_TWO_ONE = new Position(2, 1);
    private static final Position POSITION_TWO_TWO = new Position(2, 2);

    private static final Position Position_OUT_OF_BOUNDS = new Position(GameField.MAX_AMOUNT_COLS, GameField.MAX_AMOUNT_ROWS);

    // Constants for board situations
    private static final String BOARD_SOLVED_NO_OVERFLOW =
            """
                    ┏━┓
                    ┃╳┃
                    ┗━┛""";

    private static final String BOARD_SOLVED_OVERFLOW =
            """
                    ┗━┛
                    ╳╳╳
                    ┏━┓""";

    private static final String BOARD_OPEN_ENDINGS_NO_OVERFLOW =
            """
                    ┏━┓
                    ┃╳┃
                    ┻━┛""";

    private static final String BOARD_OPEN_ENDINGS_OVERFLOW =
            """
                    ┻━┛
                    ╳╳╳
                    ┏━┓""";

    // Ses with connected Positions for BOARD_SOLVED_NO_OVERFLOW
    private static final Set<Position> ALL_CONNECTED_NO_OVERFLOW = Set.of(POSITION_ZERO_ZERO, POSITION_ZERO_ONE,
            POSITION_ZERO_TWO, POSITION_ONE_ZERO, POSITION_ONE_TWO, POSITION_TWO_ZERO, POSITION_TWO_ONE, POSITION_TWO_TWO);

    // Ses with connected Positions for BOARD_SOLVED_OVERFLOW
    private static final Set<Position> ALL_CONNECTED_OVERFLOW = Set.of(POSITION_ZERO_ZERO, POSITION_ZERO_TWO,
            POSITION_ONE_ZERO, POSITION_ONE_TWO, POSITION_TWO_ZERO, POSITION_TWO_TWO);

    /**
     * Method for providing a solved game with a source Position and two different boards for overflow or none overflow
     *
     * @param source   Position of source
     * @param overflow overflow-flag
     * @return default GameLogic
     */
    private static GameLogic getLogicSolvedGame(Position source, boolean overflow) {
        return new GameLogic(overflow ? BOARD_SOLVED_OVERFLOW : BOARD_SOLVED_NO_OVERFLOW, source, overflow, FAKE_GUI);
    }

    /**
     * Method for providing a game with a source Position and two different boards for overflow or none overflow, which
     * is not solved
     *
     * @param source   Position of source
     * @param overflow overflow-flag
     * @return default GameLogic
     */
    private static GameLogic getLogicBoardOpenEnds(Position source, boolean overflow) {
        return new GameLogic(overflow ? BOARD_OPEN_ENDINGS_OVERFLOW : BOARD_OPEN_ENDINGS_NO_OVERFLOW, source, overflow, FAKE_GUI);
    }

    // <--- Tests for Changing the SourcePosition --->

    @Test(expected = IllegalArgumentException.class)
    public void testChangeSourcePositionOutOfBounds() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(Position_OUT_OF_BOUNDS);
    }

    @Test
    public void testChangeSourcePositionNullCheckValue() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(null);
        Assert.assertNull(logic.getSource());
    }

    @Test
    public void testChangeSourcePositionNullCheckConnectedPositions_NoOverflow() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(null);
        Assert.assertTrue(logic.getGameField().getConnectedPositionsAsSet().isEmpty());
    }

    @Test
    public void testChangeSourcePositionNullCheckConnectedPositions_Overflow() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(null);
        Assert.assertTrue(logic.getGameField().getConnectedPositionsAsSet().isEmpty());
    }

    @Test
    public void testChangeSourcePositionNullCheckUnConnectedPositions_NoOverflow() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(null);
        assertEquals(ALL_CONNECTED_NO_OVERFLOW, logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeSourcePositionNullCheckUnConnectedPositions_Overflow() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(null);
        assertEquals(ALL_CONNECTED_NO_OVERFLOW, logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeSourcePositionNullCheckIsGameSolved_NoOverflow() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(null);
        Assert.assertFalse(logic.isGameSolved());
    }

    @Test
    public void testChangeSourcePositionNullCheckIsGameSolved_Overflow() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(null);
        Assert.assertFalse(logic.isGameSolved());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckValue() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        assertEquals(POSITION_ZERO_ONE, logic.getSource());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckConnectedPositionsNoOverflow_NoOpenEnds() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        assertEquals(ALL_CONNECTED_NO_OVERFLOW, logic.getGameField().getConnectedPositionsAsSet());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckConnectedPositionsNoOverflow_OpenEnds() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        assertEquals(ALL_CONNECTED_NO_OVERFLOW, logic.getGameField().getConnectedPositionsAsSet());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckConnectedPositionsOverflow_NoOpenEnds() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        assertEquals(ALL_CONNECTED_OVERFLOW, logic.getGameField().getConnectedPositionsAsSet());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckConnectedPositionsOverflow_OpenEnds() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(POSITION_ONE_ZERO);

        assertEquals(ALL_CONNECTED_OVERFLOW, logic.getGameField().getConnectedPositionsAsSet());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckUnConnectedPositionsNoOverflow_NoOpenEnds() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        assertEquals(Collections.emptySet(), logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckUnConnectedPositionsNoOverflow_OpenEnds() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        assertEquals(Collections.emptySet(), logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckUnConnectedPositionsOverflow_NoOpenEnds() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(POSITION_ONE_ZERO);

        assertEquals(Collections.emptySet(), logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckUnConnectedPositionsOverflow_OpenEnds() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(POSITION_ONE_ZERO);

        assertEquals(Collections.emptySet(), logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckIfSolvedNoOverflow_TRUE() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        Assert.assertTrue(logic.isGameSolved());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckIfSolvedOverflow_TRUE() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(POSITION_ONE_ZERO);

        Assert.assertTrue(logic.isGameSolved());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckIfSolvedNoOverflow_FALSE() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ZERO_ONE);

        Assert.assertFalse(logic.isGameSolved());
    }

    @Test
    public void testChangeSourcePositionSuccessfullyCheckIfSolvedOverflow_FALSE() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, true);
        logic.changeSourcePosition(POSITION_ONE_ZERO);

        Assert.assertFalse(logic.isGameSolved());
    }

    @Test
    public void testChangeSourcePositionToWallCheckValue() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, false);
        logic.changeSourcePosition(POSITION_ONE_ONE);
        assertEquals(POSITION_ZERO_ZERO, logic.getSource());
    }

    // <--- Tests for Changing a field --->

    @Test(expected = IllegalArgumentException.class)
    public void testChangeFieldOutOfBounds() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeField(Position_OUT_OF_BOUNDS, PipeType.LINE);
    }

    @Test
    public void testChangeFieldSolvedGameChangeWallToPipeCheckConnectedPositions() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeField(POSITION_ONE_ONE, PipeType.LINE);
        assertEquals(ALL_CONNECTED_NO_OVERFLOW, logic.getGameField().getConnectedPositionsAsSet());
    }

    @Test
    public void testChangeFieldSolvedGameChangeWallToPipeCheckUnConnectedPositions() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeField(POSITION_ONE_ONE, PipeType.LINE);
        assertEquals(Set.of(POSITION_ONE_ONE), logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeFieldSolvedGameChangeWallToPipeCheckSolved() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeField(POSITION_ONE_ONE, PipeType.LINE);
        assertFalse(logic.isGameSolved());
    }

    @Test
    public void testChangeFieldUnSolvedGameChangeTPipeToCurveCheckConnectedPositions() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.CURVE);
        logic.turn(false, POSITION_ZERO_TWO);
        assertEquals(ALL_CONNECTED_NO_OVERFLOW, logic.getGameField().getConnectedPositionsAsSet());
    }

    @Test
    public void testChangeFieldUnSolvedGameChangeTPipeToCurveCheckUnConnectedPositions() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_ZERO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.CURVE);
        logic.turn(false, POSITION_ZERO_TWO);
        assertTrue(logic.getGameField().getUnconnectedPositions().isEmpty());
    }

    @Test
    public void testChangeFieldUnSolvedGameChangeTPipeToCurveCheckSolved() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.CURVE);
        logic.turn(false, POSITION_ZERO_TWO);
        assertTrue(logic.isGameSolved());
    }

    @Test
    public void testChangeFieldToSourcePosition_Solved() {
        GameLogic logic = getLogicBoardOpenEnds(POSITION_ZERO_TWO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.CURVE);
        logic.turn(false, POSITION_ZERO_TWO);
        assertTrue(logic.isGameSolved());
    }

    @Test
    public void testChangeFieldOnSourcePosition_NotSolved() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_TWO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.LINE);
        assertFalse(logic.isGameSolved());
    }

    @Test
    public void testChangeFieldOnSourcePositionToWallCheckSourceValue() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_TWO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.WALL);
        assertNull(logic.getSource());
    }

    @Test
    public void testChangeFieldOnSourcePositionToWallCheckConnectedPositions() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_TWO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.WALL);
        assertTrue(logic.getGameField().getConnectedPositionsAsSet().isEmpty());
    }

    @Test
    public void testChangeFieldOnSourcePositionToWallCheckUnConnectedPositions() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_TWO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.WALL);
        Set<Position> exp = new HashSet<>(ALL_CONNECTED_NO_OVERFLOW);
        exp.remove(POSITION_ZERO_TWO);
        assertEquals(exp, logic.getGameField().getUnconnectedPositions());
    }

    @Test
    public void testChangeFieldOnSourcePositionToWallCheckIsSolved() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_TWO, false);
        logic.changeField(POSITION_ZERO_TWO, PipeType.WALL);
        assertFalse(logic.isGameSolved());
    }

    // <--- Tests for Changing the Columns and Rows of a GameField --->

    @Test
    public void testChangeColsToMinimumViable() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeCols(GameField.MIN_AMOUNT_COLS);
        assertEquals(GameField.MIN_AMOUNT_COLS, logic.getGameField().getCols());
    }

    @Test
    public void testChangeColsToMaximumViable() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeCols(GameField.MAX_AMOUNT_COLS);
        assertEquals(GameField.MAX_AMOUNT_COLS, logic.getGameField().getCols());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeColsNotEnough() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeCols(GameField.MIN_AMOUNT_COLS - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeColsTooMany() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeCols(GameField.MAX_AMOUNT_COLS + 1);
    }

    @Test
    public void testChangeRowsToMinimumViable() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeRows(GameField.MIN_AMOUNT_ROWS);
        assertEquals(GameField.MIN_AMOUNT_ROWS, logic.getGameField().getRows());
    }

    @Test
    public void testChangeRowsToMaximumViable() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeRows(GameField.MAX_AMOUNT_ROWS);
        assertEquals(GameField.MAX_AMOUNT_ROWS, logic.getGameField().getRows());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeRowsNotEnough() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeRows(GameField.MIN_AMOUNT_ROWS - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeRowsTooMany() {
        GameLogic logic = new GameLogic(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS,
                GameField.DEFAULT_AMOUNT_WALLS_PERCENT, false, FAKE_GUI);
        logic.changeRows(GameField.MAX_AMOUNT_ROWS + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTurn_IllegalPosition() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.turn(true, Position_OUT_OF_BOUNDS);
    }

    // <--- Tests for Turning a Field --->
    @Test
    public void testTurn_Wall() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        logic.turn(true, POSITION_ONE_ONE);
        assertEquals(logic.getGameField(), getLogicSolvedGame(POSITION_ZERO_ZERO, false).getGameField());
    }

    @Test
    public void testTurn_SourcePositionClockWiseNoOverflow_checkBoard() {
        String expectedBoard =
                """
                        ┛━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        String initialBoard =
                """
                        ┓━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        res.turn(true, POSITION_ZERO_ZERO);
        GameLogic exp = new GameLogic(expectedBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        assertEquals(exp.getGameField(), res.getGameField());
    }

    @Test
    public void testTurn_SourcePositionClockWiseNoOverflow_checkIsGameSolved() {
        String initialBoard =
                """
                        ┓━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        res.turn(true, POSITION_ZERO_ZERO);
        assertFalse(res.isGameSolved());
    }


    @Test
    public void testTurn_SourcePositionCounterClockWiseNoOverflow_checkBoard() {
        String expectedBoard =
                """
                        ┏━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        String initialBoard =
                """
                        ┓━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        res.turn(false, POSITION_ZERO_ZERO);
        GameLogic exp = new GameLogic(expectedBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        assertEquals(exp.getGameField(), res.getGameField());
    }

    @Test
    public void testTurn_SourcePositionCounterClockWiseNoOverflow_checkIsGameSolved() {
        String initialBoard =
                """
                        ┓━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        res.turn(false, POSITION_ZERO_ZERO);
        assertTrue(res.isGameSolved());
    }

    @Test
    public void testTurn_SourcePositionClockWiseOverflow_checkBoard() {
        String expectedBoard =
                """
                        ┛━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        String initialBoard =
                """
                        ┓━━┓
                        ┃╳╳┃
                        ┗━━┛""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        res.turn(true, POSITION_ZERO_ZERO);
        GameLogic exp = new GameLogic(expectedBoard, POSITION_ZERO_ZERO, false, FAKE_GUI);
        assertEquals(exp.getGameField(), res.getGameField());
    }

    @Test
    public void testTurn_SourcePositionClockWiseOverflow_checkIsGameSolved() {
        String initialBoard =
                """
                        ┏━━┛
                        ╳╳╳╳
                        ┏━━┓""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, true, FAKE_GUI);
        res.turn(true, POSITION_ZERO_ZERO);
        assertFalse(res.isGameSolved());
    }


    @Test
    public void testTurn_SourcePositionCounterClockWiseOverflow_checkIsGameSolved() {
        String initialBoard =
                """
                        ┏━━┛
                        ╳╳╳╳
                        ┏━━┓""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ZERO_ZERO, true, FAKE_GUI);
        res.turn(false, POSITION_ZERO_ZERO);
        assertTrue(res.isGameSolved());
    }

    @Test
    public void testTurn_EdgesWithOverflow_checkBoards() {
        String initialBoard =
                """
                        ┛┛
                        ┓┏""";
        String expBoard =
                """
                        ┛┗
                        ┓┏""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ONE_ZERO, true, FAKE_GUI);
        res.turn(true, POSITION_ONE_ZERO);
        GameLogic exp = new GameLogic(expBoard, POSITION_ONE_ZERO, true, FAKE_GUI);
        assertEquals(res.getGameField(), exp.getGameField());
    }

    @Test
    public void testTurn_EdgesWithOverflow_isSolved() {
        String initialBoard =
                """
                        ┛┛
                        ┓┏""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ONE_ZERO, true, FAKE_GUI);
        res.turn(true, POSITION_ONE_ZERO);
        assertTrue(res.isGameSolved());
    }

    @Test
    public void testTurn_EdgesWithoutOverflow_isNotSolved() {
        String initialBoard =
                """
                        ┛┛
                        ┓┏""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ONE_ZERO, false, FAKE_GUI);
        res.turn(true, POSITION_ONE_ZERO);
        assertFalse(res.isGameSolved());
    }

    @Test
    public void testTurn_EdgesWithOverflowOpeningsRemain_isNotSolved() {
        String initialBoard =
                """
                        ┛┣
                        ┓┏""";
        GameLogic res = new GameLogic(initialBoard, POSITION_ONE_ZERO, true, FAKE_GUI);
        res.turn(true, POSITION_ONE_ZERO);
        assertFalse(res.isGameSolved());
    }

    @Test
    public void testInitNewField() {
        GameLogic logic = getLogicSolvedGame(POSITION_ZERO_ZERO, false);
        Pipe[][] exp = new Pipe[logic.getGameField().getCols()][logic.getGameField().getRows()];
        for (int x = 0; x < exp.length; x++) {
            for (int y = 0; y < exp[x].length; y++) {
                exp[x][y] = new Pipe(PipeType.WALL);
            }
        }
        logic.initNewField();
        assertArrayEquals(logic.getGameField().getField(), exp);
    }

}
