package logic;

import logic.enums.FieldError;
import logic.enums.PipeType;
import logic.field.Pipe;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for connecting the game logic to the graphical user interface. It provides methods to display the field
 * (with animation and without), change Source Position or fields, and a Method for displaying an
 * {@link javafx.scene.control.Alert} for a failed loading process
 *
 * @author Philip Barth
 */
public interface GUIConnector {

    /**
     * This method displays the fill status of the game field with animation. It takes in the current game field
     * represented as a 2D array of Pipe objects, a map of connected positions with distances as the key and a list of
     * positions as the value for animating the filling process, a set of unconnected positions,
     * and a counter for displaying the game as solved.
     *
     * @param gameField            current GameField
     * @param connectedPositions   Positions connected to the source
     * @param unconnectedPositions Positions not connected to the source
     * @param counter              amount of turns to finish the game or null, if the game is not finished
     */
    void displayFieldWithAnimation(Pipe[][] gameField, Map<Integer, List<Position>> connectedPositions,
                                   Set<Position> unconnectedPositions, Integer counter);

    /**
     * This method displays the current state of the game field. It takes in the current game field represented as a 2D
     * array of Pipe objects, the source position, a set of reachable positions, and a boolean indicating whether the
     * game has been solved or not.
     *
     * @param gameField          current GameField
     * @param sourcePosition     Position of the Source
     * @param reachablePositions Positions that can be reached by the source (filled)
     * @param solved             Solved-flag
     */
    void displayFieldWithoutAnimation(Pipe[][] gameField, Position sourcePosition, Set<Position> reachablePositions, boolean solved);

    /**
     * This method rotates a field at a given position. It takes in the position of the pipe to be rotated and a boolean
     * indicating whether the rotation should be clockwise or counter-clockwise.
     *
     * @param position  Position of the Field to be rotated
     * @param clockwise clockwise-flag
     */
    void turn(Position position, boolean clockwise);

    /**
     * Changes the source position of the game to the provided position
     *
     * @param position position where the source should be placed
     */
    void changeSourcePosition(Position position);

    /**
     * Changes the Field at a given position.
     *
     * @param pos  Position of the field to be changed
     * @param type new type to be displayed
     */
    void changeField(Position pos, PipeType type);

    /**
     * Displays an error alert when there is an error in loading the GameField based on the FieldError instance provided
     *
     * @param error Error that occurred in the loading Process
     */
    void showLoadingErrorAlert(FieldError error);
}
