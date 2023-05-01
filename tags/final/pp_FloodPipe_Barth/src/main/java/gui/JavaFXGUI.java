package gui;

import gui.field.Field;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GUIConnector;
import logic.Position;
import logic.enums.FieldError;
import logic.enums.PipeType;
import logic.enums.Rotation;
import logic.field.GameField;
import logic.field.GameFieldData;
import logic.field.Pipe;

import java.util.*;

/**
 * Class to change elements in the gui. Will be used by the {@link GameController} and the {@link logic.GameLogic} class
 * and implements the {@link GUIConnector} so it can be used by the logic.
 * Provides methods for displaying a GameField of the GameLogic, changing and turning fields, and showing the filling
 * of connected fields to the source with an animation. Can also show alerts for faults in the user interactions
 *
 * @author Philip Barth
 */
public class JavaFXGUI implements GUIConnector {

    /**
     * String for displaying, that the game is solved.
     */
    public static final String GAME_SOLVED_EDITOR = "Das Spiel ist geloest.";
    /**
     * Image of an empty line
     */
    public static final Image LINE_EMPTY = new Image("gui/graphics/line_empty.png");
    /**
     * Image of an empty curve
     */
    public static final Image CURVE_EMPTY = new Image("gui/graphics/curve_empty.png");
    /**
     * Image of an empty T-Pipe
     */
    public static final Image T_PIPE_EMPTY = new Image("gui/graphics/t_pipe_empty.png");
    /**
     * Image of an empty DeadEnd
     */
    public static final Image DEAD_END_EMPTY = new Image("gui/graphics/dead_end_empty.png");
    /**
     * Image of a Wall
     */
    public static final Image WALL = new Image("gui/graphics/wall.png");
    /**
     * Constant for the initial height of an alert. Is used to display the full content initially.
     */
    private static final int INITIAL_HEIGHT_ALERTS = 250;
    /**
     * Image of a filled line
     */
    private static final Image LINE_FILLED = new Image("gui/graphics/line_filled.png");
    /**
     * Image of a filled curve
     */
    private static final Image CURVE_FILLED = new Image("gui/graphics/curve_filled.png");
    /**
     * Image of a filled T-Pipe
     */
    private static final Image T_PIPE_FILLED = new Image("gui/graphics/t_pipe_filled.png");
    /**
     * Image of a filled DeadEnd
     */
    private static final Image DEAD_END_FILLED = new Image("gui/graphics/dead_end_filled.png");
    /**
     * Graphical representation of the GameField
     */
    private final Field field;
    /**
     * Label that indicates, that the game is solved
     */
    private final Label lblGameDone;
    /**
     * Animation to be shown and edited
     */
    private final Timeline timeline;
    /**
     * Indicator for the current scene status (Game/Editor)
     */
    private boolean isGame = true;


    /**
     * Default constructor for the GUI
     *
     * @param field       graphical representation of the GameField
     * @param lblGameDone Game Solved label
     */
    public JavaFXGUI(Field field, Label lblGameDone) {
        this.field = field;
        this.timeline = new Timeline();
        this.lblGameDone = lblGameDone;
    }

    @Override
    public void displayFieldWithoutAnimation(Pipe[][] gameField, Position sourcePosition,
                                             Set<Position> reachablePositions, boolean solved) {
        // Stop Timeline and clear keyValues
        stopTimeline();
        int cols = gameField.length;
        int rows = gameField[0].length;
        Image img;
        PipeType type;
        Position pos;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                pos = new Position(x, y);
                type = gameField[x][y].getType();
                // Get filled or empty image
                img = reachablePositions.contains(pos) ? getFilledImageFromPipeType(type)
                        : getEmptyImageFromPipeType(type);
                field.setImageAt(pos, img);
                field.setRotationAt(pos, getRotation(gameField[x][y].getRotation()));
            }
        }
        field.setSourcePosition(sourcePosition);
        showGameDoneIfSolved(solved, 0);
    }

    /**
     * Shows or hides the {@link #lblGameDone} if the game is solved. Also sets the text accordingly
     *
     * @param visible show the Label or hide it.
     * @param counter Counter for the amount of turns the player needed. (null, if the game is not done)
     */
    private void showGameDoneIfSolved(boolean visible, Integer counter) {
        // Hide the Container of the Label
        this.lblGameDone.getParent().setVisible(visible);
        this.lblGameDone.getParent().setManaged(visible);
        // Disable the field, if the game is solved, and we are in the game scene
        field.setDisable(isGame && visible && counter != null);
        String text = isGame ? "Das Spiel wurde mit " + counter + " Zuegen gelöst. Herzlichen Glueckwunsch!"
                : GAME_SOLVED_EDITOR;
        this.lblGameDone.setText(text);
    }

    @Override
    public void showLoadingErrorAlert(FieldError error) {
        if (error != FieldError.ERR_NULL) {
            showErrorLoadGameAlert(getAlertContentFromErrorType(error));
        }
    }

    @Override
    public void displayFieldWithAnimation(Pipe[][] gameField, Map<Integer, List<Position>> connectedPositions,
                                          Set<Position> unconnectedPositions, Integer counter) {
        stopTimeline();

        // Set empty Pipe Images on Positions, which are not connected to the source
        for (Position position : unconnectedPositions) {
            field.setImageAt(position, getEmptyImageFromPipeType(gameField[position.x()][position.y()].getType()));
        }


        double dur = 0;
        List<KeyValue> fieldsToFillAtTheSameTime;
        KeyFrame keyFrame;
        // Add Fill animations to the timeline if the image is currently not filled. The distance to the source
        // represents the order of fields to be filled
        for (int i = 0; i < connectedPositions.size() - 1; i++) {
            // Get KeyValues for all the fields to be filled at the same time
            fieldsToFillAtTheSameTime = getListOfFieldsToFillInTheSameDistanceToSource(gameField, connectedPositions.get(i));

            // Check if fields in the distance "i" need to be changed and change the duration accordingly
            if (!fieldsToFillAtTheSameTime.isEmpty()) {
                keyFrame = new KeyFrame(Duration.seconds(++dur), fieldsToFillAtTheSameTime.toArray(new KeyValue[0]));
                this.timeline.getKeyFrames().add(keyFrame);
            }
        }
        // if game is done, show the Label
        if (counter != null) {
            this.timeline.setOnFinished(event -> showGameDoneIfSolved(true, counter));
        }

        // Filling animation
        this.timeline.play();
    }


    /**
     * Returns a List of Fields, that need a filling animation at the same time, meaning, that the image of the field is
     * currently a non-filled one
     *
     * @param gameField                         GameField provided by the logic
     * @param positionsWithSameDistanceToSource List of positions with the same distances to the sourcePosition
     * @return List of KeyValues for the {@link #timeline} to change
     */
    private List<KeyValue> getListOfFieldsToFillInTheSameDistanceToSource(Pipe[][] gameField,
                                                                          List<Position> positionsWithSameDistanceToSource) {
        List<KeyValue> values = new LinkedList<>();
        Set<Image> emptyPipeImages = Set.of(LINE_EMPTY, CURVE_EMPTY, T_PIPE_EMPTY, DEAD_END_EMPTY);
        for (Position position : positionsWithSameDistanceToSource) {
            // If the image of the current position is empty we need a filling animation
            if (emptyPipeImages.contains(field.getImageAt(position))) {
                values.add(provideKeyValue(position, gameField));
            }
        }
        return values;
    }


    /**
     * Provides a KeyValue for the filling animation of the {@link #timeline} by getting the image property of the field
     * and the Filled Image for the current {@link PipeType}
     *
     * @param position  Position to be changed
     * @param gameField logical GameField
     * @return KeyValue with the change Animation of this field
     */
    private KeyValue provideKeyValue(Position position, Pipe[][] gameField) {
        if (position == null || gameField == null) {
            return null;
        } else {
            return new KeyValue(field.getImagePropertyAt(position),
                    getFilledImageFromPipeType(gameField[position.x()][position.y()].getType()));
        }
    }


    @Override
    public void turn(Position position, boolean clockwise) {
        field.turn(position, clockwise);
    }

    @Override
    public void changeSourcePosition(Position position) {
        field.setSourcePosition(position);
    }

    @Override
    public void changeField(Position pos, PipeType type) {
        field.setImageAt(pos, getEmptyImageFromPipeType(type));
        field.setRotationAt(pos, 0);
    }

    /**
     * Returns an int-Value for the rotation value provided
     *
     * @param rotation Enum-Value for the rotation
     * @return rotationValue as an int-representation
     */
    private int getRotation(Rotation rotation) {
        return switch (rotation) {
            case NORMAL -> 0;
            case RIGHT -> 90;
            case INVERTED -> 180;
            case LEFT -> 270;
        };
    }

    /**
     * Show an alert for an error, which occurred while trying to save a file
     *
     * @param content content for the alert with specifications about the error
     */
    public void showErrorSaveGameAlert(String content) {
        showErrorAlert("Fehler - Spiel speichern", "Fehler beim Speichern der Datei", content);
    }

    /**
     * Show an alert for an error, which occurred while trying to load a file
     *
     * @param content content for the alert with specifications about the error
     */
    public void showErrorLoadGameAlert(String content) {
        showErrorAlert("Fehler - Spiel laden", "Fehler beim Laden der Datei", content);
    }

    /**
     * Returns an Alert with title, header, content and initial Height
     *
     * @param type    AlertType to be displayed
     * @param title   title of the alert
     * @param header  header of the alert
     * @param content content of the alert
     * @return alert with set Values
     */
    private Alert initDefaultAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(true);
        alert.setHeight(INITIAL_HEIGHT_ALERTS);
        return alert;
    }

    /**
     * Shows an alert for Quitting the game with {@link javafx.scene.control.Alert.AlertType#CONFIRMATION}.
     *
     * @param stage Stage, where the alert should be displayed
     */
    public void showQuitGameAlert(Stage stage) {
        Alert alert = initDefaultAlert(Alert.AlertType.CONFIRMATION, "Spiel beenden",
                "Moechtest du das Spiel wirklich beenden?",
                "Alle nicht gespeicherten Fortschritte gehen verloren.");


        ButtonType btnYes = new ButtonType("Ja");
        ButtonType btnCancel = new ButtonType("Nein", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnYes, btnCancel);

        Optional<ButtonType> result = alert.showAndWait();

        // Close the Game, if "Ja" is pressed
        if (result.isPresent() && result.get() == btnYes) {
            stage.close();
        }
    }

    /**
     * Displays an Alert of the {@link javafx.scene.control.Alert.AlertType#ERROR} with only one button to close the
     * alert.
     *
     * @param title   title of the alert
     * @param header  header of the alert
     * @param content content of the alert
     */
    public void showErrorAlert(String title, String header, String content) {
        Alert alert = initDefaultAlert(Alert.AlertType.ERROR, title, header, content);
        ButtonType btnOkay = new ButtonType("Okay", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnOkay);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            alert.close();
        }
    }

    /**
     * Changes the rate of the Timeline
     *
     * @param newRate new Rate of the timeline
     */
    public void setTimelineRate(double newRate) {
        this.timeline.setRate(newRate);
    }

    /**
     * Setter for the boolean variable indicating the current status of the scene
     *
     * @param isGame true, if we currently are in the game scene
     */
    public void setGame(boolean isGame) {
        this.isGame = isGame;
    }

    /**
     * Provides the content for an {@link Alert} for the provided error
     *
     * @param error error which should be displayed in an alert
     * @return String representing the current error
     */
    private String getAlertContentFromErrorType(FieldError error) {
        return switch (error) {
            case ERR_NULL -> "Sollte nicht passieren";
            case ERR_EMPTY -> "Die Datei ist leer.";
            case ERR_NO_SOURCE -> "Es wurde keine Position für die Quelle angegeben.";
            case ERR_NO_OVERFLOW -> "Es wurde ein kein oder ein falscher Wert für den Überlauf angegeben.";
            case ERR_NEGATIVE_SOURCE -> "Negative Koordinate für die Position der Quelle angegeben.";
            case ERR_SOURCE_OUT_OF_BOUNDS -> "Die Position der Quelle liegt nicht innerhalb des Spielfelds.";
            case ERR_NO_BOARD -> "In der Datei ist kein Spielfeld vorhanden.";
            case ERR_INVALID_COLS -> "Das Spielfeld enthaelt nicht zwischen " + GameField.MIN_AMOUNT_COLS + " und "
                    + GameField.MAX_AMOUNT_COLS + " Spalten.";
            case ERR_INVALID_ROWS -> "Das Spielfeld enthaelt nicht zwischen " + GameField.MIN_AMOUNT_ROWS + " und "
                    + GameField.MAX_AMOUNT_ROWS + " Reihen.";
            case ERR_SOURCE_POSITION_WALL ->
                    "Die Position der Quelle darf nicht auf einem Feld mit einer " + "Mauer liegen.";
            case ERR_COLS_UNEVEN -> "Die Spalten des Spielfelds haben nicht die gleiche Anzahl an " + "Feldern.";
            case ERR_WRONG_PIPE_VALUE -> "Es wurde ein falscher Wert für den Rohrtypen angegeben " + "(nicht zwischen "
                    + GameFieldData.MIN_VALUE_PIPE + " und " + GameFieldData.MAX_VALUE_PIPE + ").";
            case ERR_SOURCE_ONLY_ONE_VALUE -> "Es wurden nicht zwei Koordinaten für die Position der Quelle angegeben.";
        };
    }


    /**
     * Provides an empty image for the pipeType, which is provided
     *
     * @param pipeType type of the Pipe
     * @return Image for the provided pipeType
     */
    private Image getEmptyImageFromPipeType(PipeType pipeType) {
        return switch (pipeType) {
            case LINE -> LINE_EMPTY;
            case CURVE -> CURVE_EMPTY;
            case T_PIPE -> T_PIPE_EMPTY;
            case DEAD_END -> DEAD_END_EMPTY;
            case WALL -> WALL;
        };
    }

    /**
     * Provides a filled image for the pipeType, which is provided
     *
     * @param pipeType type of the Pipe
     * @return Image for the provided pipeType
     */
    private Image getFilledImageFromPipeType(PipeType pipeType) {
        return switch (pipeType) {
            case LINE -> LINE_FILLED;
            case CURVE -> CURVE_FILLED;
            case T_PIPE -> T_PIPE_FILLED;
            case DEAD_END -> DEAD_END_FILLED;
            case WALL -> WALL;
        };
    }

    /**
     * Stops the animation and clears it. Does not create a new timeline to preserve the rate. Also sets the
     * GameDoneLabel to invisible
     */
    void stopTimeline() {
        timeline.stop();
        timeline.getKeyFrames().clear();
        timeline.setOnFinished(null);
        lblGameDone.getParent().setVisible(false);
        lblGameDone.getParent().setManaged(false);
    }
}
