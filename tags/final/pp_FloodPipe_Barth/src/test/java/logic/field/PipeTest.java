package logic.field;

import logic.enums.Direction;
import logic.enums.PipeType;
import logic.enums.Rotation;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testcases for the {@link Pipe} class. Tests the different constructors, the turning of pipes and the openings of the
 * different pipes
 */
public class PipeTest {

    /**
     * Opening left
     */
    private static final int MASK_LEFT = 0b1000;

    /**
     * Opening bottom
     */
    private static final int MASK_BOTTOM = 0b0100;

    /**
     * Opening right
     */
    private static final int MASK_RIGHT = 0b0010;

    /**
     * Opening Top
     */
    private static final int MASK_TOP = 0b0001;

    /**
     * Wall
     */
    private static final Pipe WALL = new Pipe(PipeType.WALL);

    /**
     * Dead End with {@link Rotation#NORMAL}
     */
    private static final Pipe DEAD_END_NORMAL = new Pipe(PipeType.DEAD_END);

    /**
     * Dead End with {@link Rotation#RIGHT}
     */
    private static final Pipe DEAD_END_RIGHT = new Pipe(PipeType.DEAD_END, Rotation.RIGHT);

    /**
     * Dead End with {@link Rotation#INVERTED}
     */
    private static final Pipe DEAD_END_INVERTED = new Pipe(PipeType.DEAD_END, Rotation.INVERTED);

    /**
     * Dead End with {@link Rotation#LEFT}
     */
    private static final Pipe DEAD_END_LEFT = new Pipe(PipeType.DEAD_END, Rotation.LEFT);

    /**
     * Line with {@link Rotation#NORMAL}
     */
    private static final Pipe LINE_NORMAL = new Pipe(PipeType.LINE);

    /**
     * Line with {@link Rotation#RIGHT}
     */
    private static final Pipe LINE_ROTATED = new Pipe(PipeType.LINE, Rotation.RIGHT);

    /**
     * Curve with {@link Rotation#NORMAL}
     */
    private static final Pipe CURVE_NORMAL = new Pipe(PipeType.CURVE);

    /**
     * Curve with {@link Rotation#RIGHT}
     */
    private static final Pipe CURVE_RIGHT = new Pipe(PipeType.CURVE, Rotation.RIGHT);

    /**
     * Curve with {@link Rotation#LEFT}
     */
    private static final Pipe CURVE_LEFT = new Pipe(PipeType.CURVE, Rotation.LEFT);

    /**
     * Curve with {@link Rotation#INVERTED}
     */
    private static final Pipe CURVE_INVERTED = new Pipe(PipeType.CURVE, Rotation.INVERTED);

    /**
     * T_Pipe with {@link Rotation#NORMAL}
     */
    private static final Pipe T_PIPE_NORMAL = new Pipe(PipeType.T_PIPE);

    /**
     * T_Pipe with {@link Rotation#INVERTED}
     */
    private static final Pipe T_PIPE_INVERTED = new Pipe(PipeType.T_PIPE, Rotation.INVERTED);

    /**
     * T_Pipe with {@link Rotation#RIGHT}
     */
    private static final Pipe T_PIPE_RIGHT = new Pipe(PipeType.T_PIPE, Rotation.RIGHT);

    /**
     * T_Pipe with {@link Rotation#LEFT}
     */
    private static final Pipe T_PIPE_LEFT = new Pipe(PipeType.T_PIPE, Rotation.LEFT);


    // <--- Tests to create Pipes from an int-Value --->
    @Test
    public void testPipeFromInt_Wall() {
        Pipe pipe = new Pipe(0);
        assertEquals(WALL, pipe);
    }

    @Test
    public void testPipeFromInt_DeadEndRotationNormal() {
        Pipe pipe = new Pipe(MASK_LEFT);
        assertEquals(DEAD_END_NORMAL, pipe);
    }

    @Test
    public void testPipeFromInt_DeadEndRotationRight() {
        Pipe pipe = new Pipe(MASK_TOP);
        assertEquals(DEAD_END_RIGHT, pipe);
    }

    @Test
    public void testPipeFromInt_DeadEndRotationLeft() {
        Pipe pipe = new Pipe(MASK_BOTTOM);
        assertEquals(DEAD_END_LEFT, pipe);
    }

    @Test
    public void testPipeFromInt_DeadEndRotationInverted() {
        Pipe pipe = new Pipe(MASK_RIGHT);
        assertEquals(DEAD_END_INVERTED, pipe);
    }

    @Test
    public void testPipeFromInt_LineRotationNormal() {
        Pipe pipe = new Pipe(MASK_RIGHT | MASK_LEFT);
        assertEquals(LINE_NORMAL, pipe);
    }

    @Test
    public void testPipeFromInt_LineRotationInverted() {
        Pipe pipe = new Pipe(MASK_TOP | MASK_BOTTOM);
        assertEquals(LINE_ROTATED, pipe);
    }

    @Test
    public void testPipeFromInt_CurveRotationNormal() {
        Pipe pipe = new Pipe(MASK_RIGHT | MASK_BOTTOM);
        assertEquals(CURVE_NORMAL, pipe);
    }

    @Test
    public void testPipeFromInt_CurveRotationRight() {
        Pipe pipe = new Pipe(MASK_LEFT | MASK_BOTTOM);
        assertEquals(CURVE_RIGHT, pipe);
    }

    @Test
    public void testPipeFromInt_CurveRotationLeft() {
        Pipe pipe = new Pipe(MASK_TOP | MASK_RIGHT);
        assertEquals(CURVE_LEFT, pipe);
    }

    @Test
    public void testPipeFromInt_CurveRotationInverted() {
        Pipe pipe = new Pipe(MASK_TOP | MASK_LEFT);
        assertEquals(CURVE_INVERTED, pipe);
    }

    @Test
    public void testPipeFromInt_TPieceRotationNormal() {
        Pipe pipe = new Pipe(MASK_TOP | MASK_BOTTOM | MASK_RIGHT);
        assertEquals(T_PIPE_NORMAL, pipe);
    }

    @Test
    public void testPipeFromInt_TPieceRotationInverted() {
        Pipe pipe = new Pipe(MASK_TOP | MASK_BOTTOM | MASK_LEFT);
        assertEquals(T_PIPE_INVERTED, pipe);
    }

    @Test
    public void testPipeFromInt_TPieceRotationRight() {
        Pipe pipe = new Pipe(MASK_RIGHT | MASK_BOTTOM | MASK_LEFT);
        assertEquals(T_PIPE_RIGHT, pipe);
    }

    @Test
    public void testPipeFromInt_TPieceRotationLeft() {
        Pipe pipe = new Pipe(MASK_RIGHT | MASK_TOP | MASK_LEFT);
        assertEquals(T_PIPE_LEFT, pipe);
    }

    // <--- Tests to create Pipes from String Values (BoxDrawingCharacters) --->

    @Test
    public void testPipeFromBoxDrawing_Wall() {
        Pipe pipe = new Pipe("╳");
        assertEquals(WALL, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_DeadEndRotationNormal() {
        Pipe pipe = new Pipe("╸");
        assertEquals(DEAD_END_NORMAL, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_DeadEndRotationRight() {
        Pipe pipe = new Pipe("╹");
        assertEquals(DEAD_END_RIGHT, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_DeadEndRotationLeft() {
        Pipe pipe = new Pipe("╻");
        assertEquals(DEAD_END_LEFT, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_DeadEndRotationInverted() {
        Pipe pipe = new Pipe("╺");
        assertEquals(DEAD_END_INVERTED, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_LineRotationNormal() {
        Pipe pipe = new Pipe("━");
        assertEquals(LINE_NORMAL, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_LineRotationInverted() {
        Pipe pipe = new Pipe("┃");
        assertEquals(LINE_ROTATED, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_CurveRotationNormal() {
        Pipe pipe = new Pipe("┏");
        assertEquals(CURVE_NORMAL, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_CurveRotationLeft() {
        Pipe pipe = new Pipe("┗");
        assertEquals(CURVE_LEFT, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_CurveRotationRight() {
        Pipe pipe = new Pipe("┓");
        assertEquals(CURVE_RIGHT, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_CurveRotationInverted() {
        Pipe pipe = new Pipe("┛");
        assertEquals(CURVE_INVERTED, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_TPieceRotationNormal() {
        Pipe pipe = new Pipe("┣");
        assertEquals(T_PIPE_NORMAL, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_TPieceRotationRight() {
        Pipe pipe = new Pipe("┳");
        assertEquals(T_PIPE_RIGHT, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_TPieceRotationLeft() {
        Pipe pipe = new Pipe("┻");
        assertEquals(T_PIPE_LEFT, pipe);
    }

    @Test
    public void testPipeFromBoxDrawing_TPieceRotationInverted() {
        Pipe pipe = new Pipe("┫");
        assertEquals(T_PIPE_INVERTED, pipe);
    }

    // <--- Tests to turn  pipes --->


    @Test
    public void testTurnClockwise_LineHorizontal() {
        Pipe res = LINE_NORMAL.copy();
        res.turn(true);
        assertEquals(LINE_ROTATED, res);
    }

    @Test
    public void testTurnCounterClockwise_LineHorizontal() {
        Pipe res = LINE_NORMAL.copy();
        res.turn(false);
        assertEquals(LINE_ROTATED, res);
    }

    @Test
    public void testTurnClockwise_LineVertical() {
        Pipe res = LINE_ROTATED.copy();
        res.turn(true);
        assertEquals(LINE_NORMAL, res);
    }

    @Test
    public void testTurnCounterClockwise_LineVertical() {
        Pipe res = LINE_ROTATED.copy();
        res.turn(false);
        assertEquals(LINE_NORMAL, res);
    }

    @Test
    public void testTurnClockwise_CurveNormal() {
        Pipe res = CURVE_NORMAL.copy();
        res.turn(true);
        assertEquals(CURVE_RIGHT, res);
    }

    @Test
    public void testTurnCounterClockwise_CurveNormal() {
        Pipe res = CURVE_NORMAL.copy();
        res.turn(false);
        assertEquals(CURVE_LEFT, res);
    }

    @Test
    public void testTurnClockwise_CurveRight() {
        Pipe res = CURVE_RIGHT.copy();
        res.turn(true);
        assertEquals(CURVE_INVERTED, res);
    }

    @Test
    public void testTurnCounterClockwise_CurveRight() {
        Pipe res = CURVE_RIGHT.copy();
        res.turn(false);
        assertEquals(CURVE_NORMAL, res);
    }

    @Test
    public void testTurnClockwise_CurveLeft() {
        Pipe res = CURVE_LEFT.copy();
        res.turn(true);
        assertEquals(CURVE_NORMAL, res);
    }

    @Test
    public void testTurnCounterClockwise_CurveLeft() {
        Pipe res = CURVE_LEFT.copy();
        res.turn(false);
        assertEquals(CURVE_INVERTED, res);
    }

    @Test
    public void testTurnClockwise_CurveInverted() {
        Pipe res = CURVE_INVERTED.copy();
        res.turn(true);
        assertEquals(CURVE_LEFT, res);
    }

    @Test
    public void testTurnCounterClockwise_CurveInverted() {
        Pipe res = CURVE_INVERTED.copy();
        res.turn(false);
        assertEquals(CURVE_RIGHT, res);
    }

    @Test
    public void testTurnClockwise_TPieceNormal() {
        Pipe res = T_PIPE_NORMAL.copy();
        res.turn(true);
        assertEquals(T_PIPE_RIGHT, res);
    }

    @Test
    public void testTurnCounterClockwise_TPiece_TopRightBottom() {
        Pipe res = T_PIPE_NORMAL.copy();
        res.turn(false);
        assertEquals(T_PIPE_LEFT, res);
    }

    @Test
    public void testTurnClockwise_TPieceInverted() {
        Pipe res = T_PIPE_INVERTED.copy();
        res.turn(true);
        assertEquals(T_PIPE_LEFT, res);
    }

    @Test
    public void testTurnCounterClockwise_TPieceInverted() {
        Pipe res = T_PIPE_INVERTED.copy();
        res.turn(false);
        assertEquals(T_PIPE_RIGHT, res);
    }

    @Test
    public void testTurnClockwise_TPieceRight() {
        Pipe res = T_PIPE_RIGHT.copy();
        res.turn(true);
        assertEquals(T_PIPE_INVERTED, res);
    }

    @Test
    public void testTurnCounterClockwise_TPieceRight() {
        Pipe res = T_PIPE_RIGHT.copy();
        res.turn(false);
        assertEquals(T_PIPE_NORMAL, res);
    }

    @Test
    public void testTurnClockwise_TPieceLeft() {
        Pipe res = T_PIPE_LEFT.copy();
        res.turn(true);
        assertEquals(T_PIPE_NORMAL, res);
    }

    @Test
    public void testTurnCounterClockwise_TPieceLeft() {
        Pipe res = T_PIPE_LEFT.copy();
        res.turn(false);
        assertEquals(T_PIPE_INVERTED, res);
    }

    @Test
    public void testTurnClockwise_DeadEndNormal() {
        Pipe res = DEAD_END_NORMAL.copy();
        res.turn(true);
        assertEquals(DEAD_END_RIGHT, res);
    }

    @Test
    public void testTurnCounterClockwise_DeadEndNormal() {
        Pipe res = DEAD_END_NORMAL.copy();
        res.turn(false);
        assertEquals(DEAD_END_LEFT, res);
    }

    @Test
    public void testTurnClockwise_DeadEndRight() {
        Pipe res = DEAD_END_RIGHT.copy();
        res.turn(true);
        assertEquals(DEAD_END_INVERTED, res);
    }

    @Test
    public void testTurnCounterClockwise_DeadEndRight() {
        Pipe res = DEAD_END_RIGHT.copy();
        res.turn(false);
        assertEquals(DEAD_END_NORMAL, res);
    }

    @Test
    public void testTurnClockwise_DeadEndInverted() {
        Pipe res = DEAD_END_INVERTED.copy();
        res.turn(true);
        assertEquals(DEAD_END_LEFT, res);
    }

    @Test
    public void testTurnCounterClockwise_DeadEndInverted() {
        Pipe res = DEAD_END_INVERTED.copy();
        res.turn(false);
        assertEquals(DEAD_END_RIGHT, res);
    }

    @Test
    public void testTurnClockwise_DeadEndLeft() {
        Pipe res = DEAD_END_LEFT.copy();
        res.turn(true);
        assertEquals(DEAD_END_NORMAL, res);
    }

    @Test
    public void testTurnCounterClockwise_DeadEndLeft() {
        Pipe res = DEAD_END_LEFT.copy();
        res.turn(false);
        assertEquals(DEAD_END_INVERTED, res);
    }

    @Test
    public void testTurnClockwise_Wall() {
        Pipe res = WALL.copy();
        res.turn(true);
        assertEquals(WALL, res);
    }

    @Test
    public void testTurnCounterClockwise_Wall() {
        Pipe res = WALL.copy();
        res.turn(false);
        assertEquals(WALL, res);
    }

    // <--- Get Openings Tests --->

    @Test
    public void testGetOpenings_Wall() {
        EnumSet<Direction> res = WALL.getOpenings();
        assertTrue(res.isEmpty());
    }

    @Test
    public void testGetOpenings_LineNormal() {
        EnumSet<Direction> res = LINE_NORMAL.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_LineRotated() {
        EnumSet<Direction> res = LINE_ROTATED.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP, Direction.BOTTOM);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_CurveNormal() {
        EnumSet<Direction> res = CURVE_NORMAL.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.RIGHT, Direction.BOTTOM);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_CurveRight() {
        EnumSet<Direction> res = CURVE_RIGHT.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.BOTTOM, Direction.LEFT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_CurveLeft() {
        EnumSet<Direction> res = CURVE_LEFT.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP, Direction.RIGHT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_CurveInverted() {
        EnumSet<Direction> res = CURVE_INVERTED.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP, Direction.LEFT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_TPipeNormal() {
        EnumSet<Direction> res = T_PIPE_NORMAL.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP, Direction.RIGHT, Direction.BOTTOM);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_TPipeRight() {
        EnumSet<Direction> res = T_PIPE_RIGHT.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.BOTTOM, Direction.LEFT, Direction.RIGHT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_TPipeLeft() {
        EnumSet<Direction> res = T_PIPE_LEFT.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP, Direction.RIGHT, Direction.LEFT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_TPipeInverted() {
        EnumSet<Direction> res = T_PIPE_INVERTED.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP, Direction.BOTTOM, Direction.LEFT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_DeadEndNormal() {
        EnumSet<Direction> res = DEAD_END_NORMAL.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.LEFT);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_DeadEndRight() {
        EnumSet<Direction> res = DEAD_END_RIGHT.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.TOP);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_DeadEndLeft() {
        EnumSet<Direction> res = DEAD_END_LEFT.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.BOTTOM);
        assertEquals(exp, res);
    }

    @Test
    public void testGetOpenings_DeadEndInverted() {
        EnumSet<Direction> res = DEAD_END_INVERTED.getOpenings();
        EnumSet<Direction> exp = EnumSet.of(Direction.RIGHT);
        assertEquals(exp, res);
    }

    // <--- toInt Tests --->

    @Test
    public void testToInt_Wall() {
        int res = WALL.toInt();
        assertEquals(0, res);
    }

    @Test
    public void testToInt_LineNormal() {
        int res = LINE_NORMAL.toInt();
        assertEquals(MASK_LEFT | MASK_RIGHT, res);
    }

    @Test
    public void testToInt_LineRotated() {
        int res = LINE_ROTATED.toInt();
        assertEquals(MASK_TOP | MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_CurveNormal() {
        int res = CURVE_NORMAL.toInt();
        assertEquals(MASK_RIGHT | MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_CurveRight() {
        int res = CURVE_RIGHT.toInt();
        assertEquals(MASK_LEFT | MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_CurveLeft() {
        int res = CURVE_LEFT.toInt();
        assertEquals(MASK_TOP | MASK_RIGHT, res);
    }

    @Test
    public void testToInt_CurveInverted() {
        int res = CURVE_INVERTED.toInt();
        assertEquals(MASK_TOP | MASK_LEFT, res);
    }

    @Test
    public void testToInt_TPipeNormal() {
        int res = T_PIPE_NORMAL.toInt();
        assertEquals(MASK_TOP | MASK_RIGHT | MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_TPipeRight() {
        int res = T_PIPE_RIGHT.toInt();
        assertEquals(MASK_LEFT | MASK_RIGHT | MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_TPipeLeft() {
        int res = T_PIPE_LEFT.toInt();
        assertEquals(MASK_TOP | MASK_RIGHT | MASK_LEFT, res);
    }

    @Test
    public void testToInt_TPipeInverted() {
        int res = T_PIPE_INVERTED.toInt();
        assertEquals(MASK_TOP | MASK_LEFT | MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_DeadEndNormal() {
        int res = DEAD_END_NORMAL.toInt();
        assertEquals(MASK_LEFT, res);
    }

    @Test
    public void testToInt_DeadEndRight() {
        int res = DEAD_END_RIGHT.toInt();
        assertEquals(MASK_TOP, res);
    }

    @Test
    public void testToInt_DeadEndLeft() {
        int res = DEAD_END_LEFT.toInt();
        assertEquals(MASK_BOTTOM, res);
    }

    @Test
    public void testToInt_DeadEndInverted() {
        int res = DEAD_END_INVERTED.toInt();
        assertEquals(MASK_RIGHT, res);
    }

    // <--- toString Tests --->

    @Test
    public void testToString_Wall() {
        String res = WALL.toString();
        assertEquals("╳", res);
    }

    @Test
    public void testToString_LineNormal() {
        String res = LINE_NORMAL.toString();
        assertEquals("━", res);
    }

    @Test
    public void testToString_LineRotated() {
        String res = LINE_ROTATED.toString();
        assertEquals("┃", res);
    }

    @Test
    public void testToString_CurveNormal() {
        String res = CURVE_NORMAL.toString();
        assertEquals("┏", res);
    }

    @Test
    public void testToString_CurveRight() {
        String res = CURVE_RIGHT.toString();
        assertEquals("┓", res);
    }

    @Test
    public void testToString_CurveLeft() {
        String res = CURVE_LEFT.toString();
        assertEquals("┗", res);
    }

    @Test
    public void testToString_CurveInverted() {
        String res = CURVE_INVERTED.toString();
        assertEquals("┛", res);
    }

    @Test
    public void testToString_TPipeNormal() {
        String res = T_PIPE_NORMAL.toString();
        assertEquals("┣", res);
    }

    @Test
    public void testToString_TPipeRight() {
        String res = T_PIPE_RIGHT.toString();
        assertEquals("┳", res);
    }

    @Test
    public void testToString_TPipeLeft() {
        String res = T_PIPE_LEFT.toString();
        assertEquals("┻", res);
    }

    @Test
    public void testToString_TPipeInverted() {
        String res = T_PIPE_INVERTED.toString();
        assertEquals("┫", res);
    }

    @Test
    public void testToString_DeadEndNormal() {
        String res = DEAD_END_NORMAL.toString();
        assertEquals("╸", res);
    }

    @Test
    public void testToString_DeadEndRight() {
        String res = DEAD_END_RIGHT.toString();
        assertEquals("╹", res);
    }

    @Test
    public void testToString_DeadEndLeft() {
        String res = DEAD_END_LEFT.toString();
        assertEquals("╻", res);
    }

    @Test
    public void testToString_DeadEndInverted() {
        String res = DEAD_END_INVERTED.toString();
        assertEquals("╺", res);
    }
}
