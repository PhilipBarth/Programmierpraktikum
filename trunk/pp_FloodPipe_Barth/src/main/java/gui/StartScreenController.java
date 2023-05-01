package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the StartScreen. Sets an {@link ImageView} which can be clicked to load the SettingScreen
 *
 * @author Philip Barth
 */
public class StartScreenController implements Initializable {

    /**
     * Container for the elements of this Screen
     */
    @FXML
    private AnchorPane anchor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the background of this Scene and initialize the onMouseClicked of the background
        ImageView imageView = new ImageView(new Image("gui/graphics/background_start_screen.png"));
        imageView.fitHeightProperty().bind(anchor.heightProperty());
        imageView.fitWidthProperty().bind(anchor.widthProperty());
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setOnMouseClicked(mouseEvent -> {
            try {
                // load SettingScreen with default settings
                new SettingScreenLoader().loadSettingScreen((Stage) this.anchor.getScene().getWindow(), new Settings());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        anchor.getChildren().add(imageView);
    }
}
