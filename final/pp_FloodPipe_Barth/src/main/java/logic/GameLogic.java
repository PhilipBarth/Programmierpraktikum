package logic;

import logic.enums.FieldError;
import logic.enums.PipeType;
import logic.field.GameField;
import logic.field.GameFieldData;

import java.util.Collections;

/**
 * Logic of the Game. Provides Methods to load a game, change the settings of the Game and getter and Setter methods.
 * Uses the Interface {@link GUIConnector} to display the field and change the GUI.
 *
 * @author Philip Barth
 */
public class GameLogic {

    /**
     * Connection to the GUI
     */
    private final GUIConnector gui;
    /**
     * GameField
     */
    private GameField gameField;
    /**
     * Counter representing the amount of turns the player took to solve the game
     */
    private int counter = 0;

    /**
     * Basic constructor to create a new solved Game from the provided values, rotating it randomly and displaying it
     * with the help of the gui
     *
     * @param cols               amount of Columns
     * @param rows               amount of Rows
     * @param maxPercentageWalls Maximum amount of Walls in percent
     * @param overflow           overflow-flag
     * @param gui                Connection to the GUI
     */
    public GameLogic(int cols, int rows, int maxPercentageWalls, boolean overflow, GUIConnector gui) {
        this.gui = gui;
        this.gameField = new GameField(cols, rows, maxPercentageWalls, overflow);
        gameField.rotateRandomly();
        displayField();
    }

    /**
     * Constructor to create a Game from Box
     *
     * @param boxDrawingCharacters String with fields for the GameField
     * @param source               Position of the source
     * @param overflow             overflow-flag
     * @param gui                  Connection to the gui
     */
    GameLogic(String boxDrawingCharacters, Position source, boolean overflow, GUIConnector gui) {
        gameField = new GameField(boxDrawingCharacters, source, overflow);
        this.gui = gui;
        displayField();
    }

    /**
     * Change the SourcePosition, if the new position is not a wall
     *
     * @param position position, where the source should be placed
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public void changeSourcePosition(Position position) {
        validatePosition(position);
        if (this.gameField.getPipeTypeAt(position) != PipeType.WALL) {
            gameField.setSource(position);
            // Change the Source Position graphically
            gui.changeSourcePosition(position);
            // empty the unconnected Fields and animate the newly filled Fields
            gui.displayFieldWithAnimation(gameField.getField(), gameField.getConnectedPositionsWithDistances(),
                    gameField.getUnconnectedPositions(), isGameSolved() ? this.counter : null);
        }
    }

    /**
     * Changes the Field at the provided position to a new Type and sets the Rotation to
     * {@link logic.enums.Rotation#NORMAL}. Also sets the SourcePosition to null if a wall will be placed on the source
     *
     * @param position Position to place the new Pipe at
     * @param pipeType new PipeType to be set
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public void changeField(Position position, PipeType pipeType) throws IllegalArgumentException {
        validatePosition(position);
        Position srcPos = gameField.getSource();
        // Remove source
        if (srcPos != null && srcPos.equals(position) && pipeType == PipeType.WALL) {
            gameField.setSource(null);
            gui.changeSourcePosition(null);
        }
        // Change the Field both logically and graphically
        gameField.setAt(position, pipeType);
        gui.changeField(position, gameField.getAt(position).getType());

        // Display field and animate the filling process. If the game is solved, also provide the counter
        gui.displayFieldWithAnimation(gameField.getField(), gameField.getConnectedPositionsWithDistances(),
                gameField.getUnconnectedPositions(), isGameSolved() ? this.counter : null);
    }

    /**
     * Getter for the {@link #gameField}
     *
     * @return gameField instance
     */
    public GameField getGameField() {
        return gameField;
    }

    /**
     * Getter for the source Position
     *
     * @return Source Position
     */
    public Position getSource() {
        return gameField.getSource();
    }

    /**
     * Changes the amount of Rows of the {@link #gameField} to the provided value
     *
     * @param newAmountOfRows new Amount of rows
     * @throws IllegalArgumentException if the amount of rows is not viable
     */
    public void changeRows(int newAmountOfRows) throws IllegalArgumentException {
        if (gameField.getRows() != newAmountOfRows) {
            this.gameField.changeAmountOfRows(newAmountOfRows);
        }
    }

    /**
     * Changes the amount of Cols of the {@link #gameField} to the provided value
     *
     * @param newAmountOfCols new Amount of Cols
     * @throws IllegalArgumentException if the amount of columns is not viable
     */
    public void changeCols(int newAmountOfCols) throws IllegalArgumentException {
        if (gameField.getCols() != newAmountOfCols) {
            this.gameField.changeAmountOfCols(newAmountOfCols);
        }
    }

    /**
     * Turns the field on the provided position according to the clockwise-flag, if it is not a Wall. Increments the
     * counter and displays the new fill status with the help of the gui
     *
     * @param clockwise clockwise-flag
     * @param position  Position, where the Field should be turned
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public void turn(boolean clockwise, Position position) throws IllegalArgumentException {
        validatePosition(position);
        if (this.gameField.getPipeTypeAt(position) != PipeType.WALL) {
            counter++;
            this.gameField.turn(position, clockwise);
            gui.turn(position, clockwise);
            gui.displayFieldWithAnimation(gameField.getField(), gameField.getConnectedPositionsWithDistances(),
                    gameField.getUnconnectedPositions(), isGameSolved() ? this.counter : null);
        }
    }

    /**
     * Checks whether the game is solved by checking, if a source is present and all openings on the field are connected
     * to the source
     *
     * @return true, if the game is solved
     */
    public boolean isGameSolved() {
        return gameField.getSource() != null &&
                gameField.allOpeningsConnected()
                && gameField.getUnconnectedPositions().size() == 0;
    }

    /**
     * Displays the {@link #gameField} in the gui by providing the filled positions and whether the game is solved.
     */
    public void displayField() {
        gui.displayFieldWithoutAnimation(gameField.getField(),
                gameField.getSource(),
                gameField.getConnectedPositionsAsSet(),
                isGameSolved());
    }

    /**
     * Sets the {@link #gameField} to the data provided after a validation step. If the data is valid,
     * the gameField will be set and displayed. Otherwise, the gui displays an error according to the {@link FieldError}
     * calculated by the validation step
     *
     * @param fieldData Data for the GameField
     * @return true, if the loading process was successful, false if not.
     */
    public boolean loadGame(GameFieldData fieldData) {
        FieldError err = fieldData == null ? FieldError.ERR_EMPTY : FieldError.ERR_NULL;

        // Validate the field data
        if (err == FieldError.ERR_NULL) {
            err = fieldData.validate();
        }
        // Display the field if no error occurred, otherwise display an error.
        if (err == FieldError.ERR_NULL) {
            this.gameField = new GameField(fieldData);
        } else {
            gui.showLoadingErrorAlert(err);
        }
        return err == FieldError.ERR_NULL;
    }

    /**
     * Initializes a new {@link #gameField} with {@link PipeType#WALL} fields and displays them on the gui.
     */
    public void initNewField() {
        this.gameField.initNewField();
        this.gui.displayFieldWithoutAnimation(gameField.getField(), gameField.getSource(),
                Collections.emptySet(), false);
    }

    /**
     * Resets the counter
     */
    public void resetCounter() {
        this.counter = 0;
    }

    /**
     * Getter method for the overflow
     *
     * @return overflow-flag
     */
    public boolean isOverflow() {
        return gameField.isOverflow();
    }

    /**
     * Setter for the overflow flag
     *
     * @param overflow flag
     */
    public void setOverflow(boolean overflow) {
        this.gameField.setOverflow(overflow);
    }

    /**
     * Rotates every cell in the field randomly
     */
    public void rotateFieldRandomly() {
        this.gameField.rotateRandomly();
    }


    /**
     * Validates a Position and throws an exception, if it is out of bounds.
     *
     * @param pos Position to be validated
     * @throws IllegalArgumentException if position is out of bounds
     */
    private void validatePosition(Position pos) throws IllegalArgumentException {
        if (pos != null && pos.isInvalidPosition(gameField.getCols(), getGameField().getRows())) {
            throw new IllegalArgumentException("Position ist nicht auf dem Spielfeld.");
        }
    }
}
