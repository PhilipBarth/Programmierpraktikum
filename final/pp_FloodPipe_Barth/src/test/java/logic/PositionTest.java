package logic;

import logic.enums.Direction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Class for testing the edge case and illegal arguments while creating a {@link Position}
 */
public class PositionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeX() {
        new Position(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeY() {
        new Position(0, -1);
    }

    @Test
    public void testZero() {
        new Position(0, 0);
    }

    @Test
    public void testNeighborPosition_NoOverflow_successfully() {
        Position pos = new Position(0, 0);
        Position res = pos.getNeighborPosition(3, 3, Direction.RIGHT, false);

        assertEquals(new Position(1, 0), res);
    }

    @Test
    public void testNeighborPosition_NoOverflow_unSuccessfully() {
        Position pos = new Position(0, 0);
        Position res = pos.getNeighborPosition(3, 3, Direction.TOP, false);

        assertNull(res);
    }


    @Test
    public void testNeighborPosition_Overflow_successfully() {
        Position pos = new Position(0, 0);
        Position res = pos.getNeighborPosition(3, 3, Direction.LEFT, true);

        assertEquals(new Position(2, 0), res);
    }
}
