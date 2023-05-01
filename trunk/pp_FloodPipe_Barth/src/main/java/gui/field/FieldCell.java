package gui.field;

import gui.GameController;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Represents an instance in the {@link Field} on the GraphicalUserInterface. It extends the {@link StackPane} class and
 * provides Getter and Setter methods for the image being displayed in the {@link ImageView} instance as well as methods
 * for rotating the ImageView, resizing and relocating it and a Getter method for the {@link ObjectProperty<Image>} of
 * the {@link #pipe}.
 *
 * @author Philip Barth
 */
public class FieldCell extends StackPane {
    /**
     * ImageView instance to represent the Pipe on the GameField
     */
    private final ImageView pipe;

    /**
     * Constructor for the FieldCell. It creates the {@link #pipe} and fits its height and width to this instance
     */
    FieldCell() {
        super();
        pipe = new ImageView();
        this.getChildren().add(pipe);
        this.setAlignment(Pos.CENTER);
        pipe.fitWidthProperty().bind(this.widthProperty());
        pipe.fitHeightProperty().bind(this.heightProperty());
    }

    /**
     * Creates an ImageView and Binds the Properties to the GridPane. It is static, because it is also used by the
     * {@link GameController}
     *
     * @param cellWidth  width of a cell in the gridPane
     * @param cellHeight height of a cell in the gridPane
     * @param colCount   amount of Columns in the gridPane
     * @param rowCount   amount of Rows in the gridPane
     * @param gridPane   GridPane where the {@link ImageView} should be placed in
     * @return ImageView with properties bound to the GridPane provided
     */
    public static ImageView createImageViewAndBindPropertiesToGrid(int cellWidth, int cellHeight, int colCount,
                                                                   int rowCount, GridPane gridPane) {
        ImageView imageView = new ImageView();
        //image has to fit a cell and mustn't preserve ratio
        imageView.setFitWidth(cellWidth);
        imageView.setFitHeight(cellHeight);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        //the image shall resize when the cell resizes
        imageView.fitWidthProperty().bind(gridPane.widthProperty().
                divide(colCount).subtract(gridPane.getHgap()));
        imageView.fitHeightProperty().bind(gridPane.heightProperty().
                divide(rowCount).subtract(gridPane.getVgap()));
        return imageView;
    }

    /**
     * Getter for the {@link Image} of the {@link #pipe}
     *
     * @return image
     */
    Image getPipeImage() {
        return this.pipe.getImage();
    }

    /**
     * Setter for the {@link Image} in the {@link #pipe}
     *
     * @param img Image to be displayed
     */
    void setPipeImage(Image img) {
        this.pipe.setImage(img);
    }

    /**
     * Sets the rotation of the {@link #pipe}, resizes and relocates it, if needed. Is needed for non-square fields
     *
     * @param val rotation value
     */
    void setRotation(double val) {
        this.pipe.setRotate(val);
        resizeAndRelocate();
    }

    /**
     * Turns the {@link #pipe} clockwise or counterclockwise based on the parameter clockwise
     *
     * @param clockwise true, if the image should be turned clockwise
     */
    void turn(boolean clockwise) {
        // Calculation for the rotation value. Needed for either negative values or values > 360
        int val = clockwise ? 90 : -90;
        val += 360;
        setRotation((pipe.getRotate() + val) % 360);
        //resize child to fit and correct movement caused by resizing if necessary
        resizeAndRelocate();
    }

    /**
     * Resizes and relocates the {@link #pipe}. Is used for non-square Fields, when the {@link ImageView} is rotated.
     * Also changes the width and height properties if needed, to fill out the FieldCell completely.
     */
    void resizeAndRelocate() {
        // If the Image is rotated to the right or left, the width should be bound to the height property and the other
        // way around.
        if ((pipe.getRotate() == 90) || (pipe.getRotate() == 270)) {

            pipe.fitWidthProperty().bind(this.heightProperty());
            pipe.fitHeightProperty().bind(this.widthProperty());

            // relocate to correct movement caused by resizing
            double delta = (getWidth() - getHeight()) / 2;
            pipe.relocate(delta, -delta);
        } else {
            pipe.fitWidthProperty().bind(this.widthProperty());
            pipe.fitHeightProperty().bind(this.heightProperty());

            //with 0° or 180° resize does no movement to be corrected
            pipe.relocate(0, 0);
        }
    }

    /**
     * Returns the ImageProperty of the {@link Image} being displayed
     *
     * @return ImageProperty of the PipeImage
     */
    ObjectProperty<Image> getPipeImageProperty() {
        return this.pipe.imageProperty();
    }
}
