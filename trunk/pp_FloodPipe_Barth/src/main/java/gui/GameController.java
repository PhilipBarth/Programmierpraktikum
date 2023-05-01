package gui;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import gui.field.Field;
import gui.field.FieldCell;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.GameLogic;
import logic.Position;
import logic.enums.PipeType;
import logic.field.GameField;
import logic.field.GameFieldData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static gui.JavaFXGUI.*;

/**
 * Controller class for the interaction with the input of the user. Provides methods for initializing a Game, changing
 * the Settings of the game, and different button controls like initializing a new Field, mixing the field, loading Files,
 * saving files.
 *
 * @author Philip Barth
 */
public class GameController {

    /**
     * Difference between widths of game and editor to smoothly toggle between both
     */
    private static final int DIFF_WIDTH_GAME_EDITOR = 300;
    /**
     * Container for all the elements
     */
    @FXML
    private BorderPane borderPn;

    // <-- Menu-Items -->

    /**
     * Menu for the Animation Speed Handling. Contains the {@link #toggleGroupAnimation} with different animationSpeeds
     */
    @FXML
    private Menu menuAnimationSpeeds;
    /**
     * ToggleGroup for the different animationSpeeds
     */
    @FXML
    private ToggleGroup toggleGroupAnimation;

    /**
     * Menu Item for turning the animation off
     */
    @FXML
    private RadioMenuItem menuItemAnimationOff;

    /**
     * Menu Item for switching between the scenes
     */
    @FXML
    private MenuItem menuItemSwitchScene;

    // <-- Editor -->

    /**
     * Container for the elements of the editor
     */
    @FXML
    private HBox editorContainer;

    /**
     * CheckBox for toggling the overflow Setting in the editor
     */
    @FXML
    private CheckBox checkBoxOverflow;

    /**
     * Slider for changing the Columns in the editor
     */
    @FXML
    private Slider sliderCols;

    /**
     * Slider for changing the Rows in the editor
     */
    @FXML
    private Slider sliderRows;

    /**
     * GridPane for the {@link ImageView} instances representing the fields to be placed
     */
    @FXML
    private GridPane grdPnEditor;

    /**
     * Label for displaying the text, when the game is solved
     */
    @FXML
    private Label lblGameDone;

    /**
     * Graphical Representation of the {@link GameField}
     */
    private Field field;

    /**
     * Instance of the GameLogic
     */
    private GameLogic logic;

    /**
     * Variable to store the previously selected wall-Percentage to preset the {@link Slider} in the
     * {@link SettingController}
     */
    private int maxAmountWallsPercentage;

    /**
     * Instance of the GUI Handling, which is used for changing the GUI
     */
    private JavaFXGUI gui;

    /**
     * Initializes the GameController. Will be used by the {@link SettingController} to initialize a game based on the
     * settings provided by the user input.
     *
     * @param settings settings for the game including columns, rows, overflow and maxPercentageWalls
     */
    void initialize(Settings settings) {
        // Initialize the Editor and enable dragging
        ImageView[][] editorFields = initializeEditorImageViews();
        setImagesAndDrag(editorFields);

        this.sliderCols.setMin(GameField.MIN_AMOUNT_COLS);
        this.sliderCols.setMax(GameField.MAX_AMOUNT_COLS);
        this.sliderRows.setMin(GameField.MIN_AMOUNT_ROWS);
        this.sliderRows.setMax(GameField.MAX_AMOUNT_ROWS);

        // Set the elements of the editor
        initializeValuesFromSettings(settings);
        int cols = settings.cols();
        int rows = settings.rows();

        //Create a new GUI-GameField with drag-and-drop and onMouseClicked enabled
        this.field = new Field(cols, rows, this);
        // Place the GameField in the center of the container
        this.borderPn.setCenter(field);
        this.gui = new JavaFXGUI(field, lblGameDone);

        initAnimationSpeedOnAction(gui);


        // Create logic
        this.logic = new GameLogic(cols, rows, settings.maxPercentageWalls(), settings.overflow(), gui);
    }

    /**
     * Creates the {@link ImageView} instances and binds them to width and height of Editor-GridPane.
     *
     * @return ImageView Fields of the Editor
     */
    private ImageView[][] initializeEditorImageViews() {
        int colCount = grdPnEditor.getColumnCount();
        int rowCount = grdPnEditor.getRowCount();

        ImageView[][] imageViews = new ImageView[colCount][rowCount];
        int cellWidth = (int) grdPnEditor.getWidth() / colCount;
        int cellHeight = (int) grdPnEditor.getHeight() / rowCount;

        // bind each Imageview to a cell of the gridPane
        for (int x = 0; x < colCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                //creates an empty imageview
                imageViews[x][y] = FieldCell.createImageViewAndBindPropertiesToGrid(cellWidth, cellHeight, colCount,
                        rowCount, grdPnEditor);
                //add the imageview to the cell
                grdPnEditor.add(imageViews[x][y], x, y);
            }
        }
        return imageViews;
    }


    /**
     * Sets the Editor Images and enables drag
     *
     * @param editorFields Container for the Editor-ImageViews
     */
    private void setImagesAndDrag(ImageView[][] editorFields) {
        setEditorImages(editorFields);
        setDrag(editorFields);
    }

    /**
     * Displays the Images of the editor
     *
     * @param editorImageViews ImageViews of the editor
     */
    private void setEditorImages(ImageView[][] editorImageViews) {
        Queue<Image> editorImages = new LinkedList<>(Arrays.asList(LINE_EMPTY, CURVE_EMPTY, T_PIPE_EMPTY,
                DEAD_END_EMPTY, WALL, new Image("gui/graphics/sourceEditor.png")));
        // Display the images by the provided order of queue
        for (ImageView[] row : editorImageViews) {
            for (ImageView cell : row) {
                cell.setImage(editorImages.poll());
            }
        }
    }

    /**
     * Enables drag on the ImageView instances of the editor. Transfers String-Values and the Image in the Clipboard.
     *
     * @param editorImageViews ImageViews of the editor
     */
    private void setDrag(ImageView[][] editorImageViews) {
        // Values of the Strings to transfer
        Queue<String> nameOfImage = new LinkedList<>(Arrays.asList(PipeType.LINE.name(), PipeType.CURVE.name(),
                PipeType.T_PIPE.name(), PipeType.DEAD_END.name(), PipeType.WALL.name(), "SOURCE"));
        for (ImageView[] row : editorImageViews) {
            for (ImageView cell : row) {
                String value = nameOfImage.poll();
                // Put Values to Clipboard and set effect
                cell.setOnDragDetected((MouseEvent event) -> {
                    Dragboard db = cell.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(value);
                    db.setContent(content);
                    event.consume();
                    cell.setEffect(new InnerShadow(10.0, Color.BLUE));
                });
                // Remove Effect
                cell.setOnDragDone((DragEvent event) -> cell.setEffect(null));
            }
        }
    }

    /**
     * Initializes the Sliders and Checkbox from provided settings
     *
     * @param settings Settings (cols, rows, overflow, maxAmountWallsPercentage)
     */
    private void initializeValuesFromSettings(Settings settings) {
        this.sliderCols.setValue(settings.cols());
        this.sliderRows.setValue(settings.rows());
        this.checkBoxOverflow.setSelected(settings.overflow());
        this.maxAmountWallsPercentage = settings.maxPercentageWalls();
        // add Listener to Sliders to change the field
        this.sliderCols.setOnMouseReleased(event -> {
            this.changeColsAndRowsIfNeeded((int) sliderCols.getValue(), (int) sliderRows.getValue());
            logic.displayField();
        });
        this.sliderRows.setOnMouseReleased(event -> {
            this.changeColsAndRowsIfNeeded((int) sliderCols.getValue(), (int) sliderRows.getValue());
            logic.displayField();
        });
    }

    /**
     * Initializes the RadioMenuItems to change the animation speed on action with the provided userData
     */
    private void initAnimationSpeedOnAction(JavaFXGUI gui) {
        menuAnimationSpeeds.getItems().forEach(elem -> elem.setOnAction(actionEvent ->
                gui.setTimelineRate(Double.parseDouble(elem.getUserData().toString()))));
    }

    /**
     * Changes to the {@link SettingController} and provides the Settings from the current Game.
     *
     * @throws IOException Exception thrown, if the SettingScreen can not be loaded
     */
    public void newGame() throws IOException {
        Settings settings = new Settings((int) sliderCols.getValue(), (int) sliderRows.getValue(),
                maxAmountWallsPercentage, checkBoxOverflow.isSelected());
        new SettingScreenLoader().loadSettingScreen(getStage(), settings);
    }

    /**
     * Saves the current Game to an external File. Shows an Alert, if no source is present in the current Game.
     */
    public void saveGame() {
        if (logic.getSource() == null) {
            gui.showErrorSaveGameAlert("Das Spiel kann nicht gespeichert werden, wenn keine " +
                    "Quelle vorhanden ist.");
        } else {
            // Open Dialog to retrieve a file
            File selectedFile = openFileDialog(false);
            if (selectedFile != null) {
                try {
                    // Saves the current Game to a file
                    Gson gson = new Gson();
                    FileWriter w = new FileWriter(selectedFile);
                    w.write(gson.toJson(new GameFieldData(logic.getGameField())));
                    w.close();
                } catch (IOException e) {
                    // Error while trying to save to the file
                    gui.showErrorSaveGameAlert(e.getMessage());
                }
            }
        }
    }

    /**
     * Loads a game from an external File. Changes Rows and Cols of the Field-representation and setsDragAndDop and
     * mouseClicked, if the cols or rows have changed due to the loading. If something went wrong during the loading
     * process, uses the {@link JavaFXGUI} to display an {@link Alert}
     */
    public void loadGame() {
        File file = openFileDialog(true);
        if (file != null) {
            String errorMessage = null;
            try (FileReader fileReader = new FileReader(file)) {
                try {
                    // load GameFieldData from the File and convert load it in the logic
                    Gson gson = new Gson();
                    GameFieldData gameFieldData = gson.fromJson(fileReader, GameFieldData.class);
                    if (logic.loadGame(gameFieldData)) {
                        int cols = logic.getGameField().getCols();
                        int rows = logic.getGameField().getRows();
                        this.sliderCols.setValue(cols);
                        this.sliderRows.setValue(rows);
                        field.initNewFieldIfNeeded(cols, rows, this);
                        checkBoxOverflow.setSelected(logic.isOverflow());
                        logic.displayField();
                    }
                    // Error in Json Syntax
                } catch (JsonParseException e) {
                    errorMessage = "Die Datei enthaelt eine fehlerhafte Syntax: " + e.getMessage();
                }
                // Something went wrong during loading of file
            } catch (IOException e) {
                errorMessage = "Beim Laden der Datei ist ein Fehler aufgetreten: " + e.getMessage();
            } finally {
                if (errorMessage != null) {
                    gui.showErrorLoadGameAlert(errorMessage);
                }
            }

        }
    }

    /**
     * Opens a dialog to save or load a file, which is indicated by the provided boolean-parameter. The files are filtered
     * to fit the .json file extension
     *
     * @param load if true, showOpenDialog(), otherwise showSaveDialog()
     * @return Returns the selected file or null, if no file was selected
     */
    private File openFileDialog(boolean load) {
        //Step 1: Figure out where we are currently are, so we can open the dialog in
        //the same directory the jar is located. See also
        //https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
        File currDir = null;
        try {
            currDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            //oops... ¯\_(ツ)_/¯
            //guess we won't be opening the dialog in the right directory
        }
        //Step 2: Put it together
        FileChooser fileChooser = new FileChooser();
        if (currDir != null) {
            //ensure the dialog opens in the correct directory
            fileChooser.setInitialDirectory(currDir.getParentFile());
        }
        fileChooser.setTitle("Waehle eine Datei zum " + (load ? "Laden" : "Speichern") + "...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        Stage stage = getStage();
        return load ? fileChooser.showOpenDialog(stage) : fileChooser.showSaveDialog(stage);
    }

    /**
     * Shows the dialog to quit the game via the {@link JavaFXGUI}
     */
    public void quitGame() {
        gui.showQuitGameAlert(getStage());
    }

    /**
     * Helper method to get the current Stage
     *
     * @return stage of the GameController
     */
    private Stage getStage() {
        return (Stage) this.borderPn.getScene().getWindow();
    }

    /**
     * Sets overflow in the logic based on the {@link #checkBoxOverflow} and triggers the logic to display the field.
     * Triggered, if the checkbox is clicked
     */
    public void setOverflow() {
        logic.setOverflow(checkBoxOverflow.isSelected());
        logic.displayField();
    }

    /**
     * Rotates the logic Gamefield randomly and displays it afterwards
     */
    public void mixField() {
        logic.rotateFieldRandomly();
        logic.displayField();
    }

    /**
     * Method to change the Columns and Rows if needed. Adjusts the values of the sliders accordingly and calls the
     * corresponding methods in the {@link #logic} and the {@link #field}
     *
     * @param cols new amount of cols
     * @param rows new amount of rows
     */
    private void changeColsAndRowsIfNeeded(int cols, int rows) {
        sliderCols.setValue(cols);
        sliderRows.setValue(rows);
        this.logic.changeCols(cols);
        this.logic.changeRows(rows);
        this.field.initNewFieldIfNeeded(cols, rows, this);
    }

    /**
     * Switches the scene between editor and game scene. Shows an {@link Alert} if no source is provided in the game
     * and the user wants to switch to the game. Also changes the timeline rate based on the current scene. is triggered
     * by clicking on the {@link #menuItemSwitchScene}
     */
    public void switchScene() {
        boolean switchToGame = this.editorContainer.isVisible();
        // no source provided + switching to the Game
        if (switchToGame && logic.getGameField().getSource() == null) {
            String title = "Fehler - Wechsel zum Spiel.";
            String header = "Keine Quelle vorhanden.";
            String content = "Es kann nicht in den Spielmodus gewechselt werden, " +
                    "wenn keine Quelle auf dem Spielfeld vorhanden ist.";
            gui.showErrorAlert(title, header, content);
        } else {
            logic.resetCounter();
            // Menu item for switching the scene needs to be updated
            String newText = switchToGame ? "Zum Editor wechseln" : "Zum Spiel wechseln";
            menuItemSwitchScene.setText(newText);

            field.setDisable(switchToGame && logic.isGameSolved());
            // set width of scene based on editor or game and visibility of the editor container
            changeSceneToGameOrEditor(switchToGame);
            getStage().setWidth(getStage().getWidth() + (switchToGame ? -DIFF_WIDTH_GAME_EDITOR : DIFF_WIDTH_GAME_EDITOR));

            // change the timeline rate based on the scene (editor = instant) or game (menuItemAnimation which is clicked)
            String userDataAnimationSpeed = switchToGame ? toggleGroupAnimation.getSelectedToggle().getUserData().toString()
                    : menuItemAnimationOff.getUserData().toString();
            double animationSpeed = Double.parseDouble(userDataAnimationSpeed);
            gui.setTimelineRate(animationSpeed);
        }
    }

    /**
     * Changes the scene to editor or Game and sets the width of the stage to display or hide the editor container.
     * Informs the gui about the change between editor and game.
     *
     * @param toGame indicates whether we should switch to game or editor
     */
    private void changeSceneToGameOrEditor(boolean toGame) {
        editorContainer.setVisible(!toGame);
        editorContainer.setManaged(!toGame);
        menuAnimationSpeeds.setDisable(!toGame);
        gui.setGame(toGame);
        if (!toGame) {
            lblGameDone.setText(GAME_SOLVED_EDITOR);
        }
    }

    /**
     * Used for drag and drop handling. The string provided indicates which editor field was dragged and the Position
     * is the value of the coordinates of the cell where the drop was done on.
     *
     * @param pos   Position where the field should be changed
     * @param value String value to indicate the change to be made
     */
    public void onGameFieldCellDropped(Position pos, String value) {
        if (value.equals("SOURCE")) {
            this.logic.changeSourcePosition(pos);
        } else {
            this.logic.changeField(pos, PipeType.valueOf(value));
        }
    }

    /**
     * Initializes a new field only containing walls with the current dimensions.
     */
    public void initializeNewField() {
        logic.initNewField();
    }


    /**
     * Turns a field on the logic at the specified position. Can be used as a onMouseClickedEvent for a cell in
     * {@link Field}
     *
     * @param clockwise clockwise-flag
     * @param position  Position to be turned
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public void turn(boolean clockwise, Position position) throws IllegalArgumentException {
        this.logic.turn(clockwise, position);
    }


}
