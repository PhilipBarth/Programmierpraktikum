package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class that starts the application.
 *
 * @author Philip Barth
 */
public class ApplicationMain extends Application {

    /**
     * Initial width of the start screen
     */
    private static final int WIDTH_START_SCREEN = 500;
    /**
     * Initial width of the start screen
     */
    private static final int HEIGHT_START_SCREEN = 500;

    /**
     * Main method
     *
     * @param args launches the application with these arguments
     */
    public static void main(String... args) {
        launch(args);
    }

    /**
     * Creating the stage and showing it. This is where the initial size and the
     * title of the window are set.
     *
     * @param stage the stage to be shown
     * @throws IOException IO Exception thrown when we can not load the StartScreen
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loadStartScreen = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
        Scene startScene = new Scene(loadStartScreen.load(), WIDTH_START_SCREEN, HEIGHT_START_SCREEN);
        stage.setScene(startScene);
        stage.setTitle("FloodPipe");
        stage.show();
    }
}
