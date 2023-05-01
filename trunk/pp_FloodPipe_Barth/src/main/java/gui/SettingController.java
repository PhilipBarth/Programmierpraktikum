package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.field.GameField;

import java.io.IOException;

/**
 * Controller for the SettingScreen. Changes the Settings of the game and provides a method for starting the game.
 *
 * @author Philip Barth
 */
public class SettingController {

    /**
     * Initial Width of the Game Screen
     */
    private static final int WIDTH_GAME_SCREEN = 800;

    /**
     * Initial Height of the Game Screen
     */
    private static final int HEIGHT_GAME_SCREEN = 800;

    /**
     * Slider representing the columns
     */
    @FXML
    private Slider sliderCols;

    /**
     * Slider representing the rows
     */
    @FXML
    private Slider sliderRows;

    /**
     * Slider representing the maximum amount of walls
     */
    @FXML
    private Slider sliderWalls;

    /**
     * Checkbox representing overflow to be activated
     */
    @FXML
    private CheckBox checkBoxOverflow;

    /**
     * Container for the contents of the SettingScreen
     */
    @FXML
    private BorderPane borderPn;

    /**
     * Method to start a new Game with the values provided in the SettingScreen
     *
     * @throws IOException Exception thrown, if the loading of the GameScreen did not work
     */
    public void startGame() throws IOException {
        Stage stage = (Stage) this.borderPn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root, WIDTH_GAME_SCREEN, HEIGHT_GAME_SCREEN));

        // Provide the settings to the controller
        GameController gameController = fxmlLoader.getController();
        Settings settings = new Settings((int) this.sliderCols.getValue(), (int) this.sliderRows.getValue(),
                (int) this.sliderWalls.getValue(), this.checkBoxOverflow.isSelected());
        gameController.initialize(settings);
        stage.show();
    }

    /**
     * Method used for providing the Controller with values to be set
     *
     * @param settings Values for Columns, Rows, Walls, and overflow
     */
    public void initializeValues(Settings settings) {

        initializeSlider(sliderCols, GameField.MIN_AMOUNT_COLS, GameField.MAX_AMOUNT_COLS, settings.cols());
        initializeSlider(sliderRows, GameField.MIN_AMOUNT_ROWS, GameField.MAX_AMOUNT_ROWS, settings.rows());
        initializeSlider(sliderWalls, GameField.MIN_AMOUNT_WALLS_PERCENT, GameField.MAX_AMOUNT_WALLS_PERCENT,
                settings.maxPercentageWalls());

        this.checkBoxOverflow.setSelected(settings.overflow());
    }

    /**
     * Initializes the values of the slider
     *
     * @param slider slider to be initialized
     * @param min    min value
     * @param max    max value
     * @param value  default value
     */
    private void initializeSlider(Slider slider, int min, int max, int value) {
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
    }
}
