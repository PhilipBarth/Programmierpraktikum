package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class used for loading the SettingScreen.fxml
 *
 * @author Philip Barth
 */
public class SettingScreenLoader {
    /**
     * Initial width of the setting screen
     */
    private static final int WIDTH_SETTING_SCREEN = 550;

    /**
     * Initial height of the setting screen
     */
    private static final int HEIGHT_SETTING_SCREEN = 725;

    /**
     * Method for loading the Setting screen and initializing the items based on the settings provided
     *
     * @param stage    stage, where the scene should be loaded in
     * @param settings Settings for the items (cols, rows, walls, overflow)
     * @throws IOException if the FXML could not be loaded
     */
    public void loadSettingScreen(Stage stage, Settings settings) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("SettingScreen.fxml"));
        Parent root = fxmlLoader.load();
        SettingController settingController = fxmlLoader.getController();
        // Initialize the sliders of the SettingScreen based on the state of the changes the user made
        settingController.initializeValues(settings);

        stage.setScene(new Scene(root, WIDTH_SETTING_SCREEN, HEIGHT_SETTING_SCREEN));
        stage.show();
    }
}
