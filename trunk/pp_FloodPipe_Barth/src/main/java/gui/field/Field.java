package gui.field;

import gui.GameController;
import gui.JavaFXGUI;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import logic.Position;

/**
 * The Field class extends the {@link HBox} class and represents a game field composed of a grid of {@link FieldCell}
 * objects. The class has several methods for initializing and manipulating the game field, including methods for
 * setting the source image, rotating cells, and retrieving image properties at specific positions. The class also
 * contains a FieldCell[][] object that stores the individual cells of the game field, as well as a {@link GridPane}
 * object that is used to display the cells in a grid layout.
 *
 * @author Philip Barth
 */
public class Field extends HBox {

    /**
     * Padding of the HBox
     */
    private static final int PADDING = 5;
    /**
     * Image displaying a source indicator for a FieldCell
     */
    private static final Image SOURCE_GAME = new Image("gui/graphics/sourceGame.png");
    /**
     * GridPane representing the GameField
     */
    private final GridPane grid;
    /**
     * Container for the FieldCells
     */
    private FieldCell[][] gameField;
    /**
     * ImageView element for the source that is being represented on the GameField
     */
    private ImageView source;

    /**
     * Constructor for the Field. Creates a new GridPane and adds it to this Instanz as a child and binds the width and
     * height to this instance too. Also uses the methode {@link #initNewFieldIfNeeded(int, int, GameController)}
     * to initialize the FieldCells and the GridPane
     *
     * @param cols       amount of cols in the GameField
     * @param rows       amount of rows in the GameField
     * @param controller used for providing dragAndDrop and onMouseClicked methods
     */
    public Field(int cols, int rows, GameController controller) {
        this.grid = new GridPane();
        this.getChildren().add(grid);
        grid.prefWidthProperty().bind(this.widthProperty());
        grid.prefHeightProperty().bind(this.heightProperty());
        initNewFieldIfNeeded(cols, rows, controller);
        this.setPadding(new Insets(PADDING));
    }

    /**
     * Initializes the {@link #gameField} if needed (change of rows or cols) by using the methods
     * {@link #initGridPane(int, int)} and {@link #initFields()} by initializing the Row- and ColumnConstraints of
     * the GridPane and binding the {@link FieldCell} instances to it. Also sets the onMouseClickedEvent and dragAndDrop
     * for each cell.
     *
     * @param cols       amount of cols of the GameField
     * @param rows       amount of rows of the GameField
     * @param controller Controller to define the methods called by the drag and drop and mouse clicks
     */
    public void initNewFieldIfNeeded(int cols, int rows, GameController controller) {
        if (this.gameField == null || cols != getCols() || rows != getRows()) {
            initGridPane(cols, rows);
            initFields();
            // Create source instance and bind it to the GridPane
            int cellWidth = (int) grid.getWidth() / cols;
            int cellHeight = (int) grid.getHeight() / rows;
            source = FieldCell.createImageViewAndBindPropertiesToGrid(cellWidth, cellHeight, cols, rows, grid);
            source.setImage(SOURCE_GAME);
            setOnMouseClicked(controller);
            setDragAndDrop(controller);
        }
    }

    /**
     * Creates the {@link #gameField} and binds the cells to the GridPane with the use of
     * {@link #createFieldCellAndBindPropertiesToGrid(DoubleBinding, DoubleBinding)}.
     */
    private void initFields() {
        int colCount = grid.getColumnCount();
        int rowCount = grid.getRowCount();
        this.gameField = new FieldCell[colCount][rowCount];
        // Get the width and height property of each GridPane cell
        DoubleBinding bindingWidth = grid.widthProperty().
                divide(colCount).subtract(grid.getHgap());
        DoubleBinding bindingHeight = grid.heightProperty().
                divide(rowCount).subtract(grid.getVgap());
        // Create cells
        for (int x = 0; x < colCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                gameField[x][y] = createFieldCellAndBindPropertiesToGrid(bindingWidth, bindingHeight);
                //add stackPane to the gridPane
                grid.add(gameField[x][y], x, y);
            }
        }
    }

    /**
     * Creates an instance of {@link  FieldCell} and binds the width and height properties to the properties of a single
     * {@link GridPane} cell.
     *
     * @param width  width of a single GridPane cell
     * @param height height of a single GridPane cell
     */
    private FieldCell createFieldCellAndBindPropertiesToGrid(DoubleBinding width, DoubleBinding height) {
        FieldCell cell = new FieldCell();
        //bind size properties of the stackPane to the size of the cell
        cell.prefWidthProperty().bind(width);
        cell.prefHeightProperty().bind(height);
        cell.maxWidthProperty().bind(width);
        cell.maxHeightProperty().bind(height);
        cell.minWidthProperty().bind(width);
        cell.minHeightProperty().bind(height);
        return cell;
    }

    /**
     * Getter for the {@link  #gameField}
     *
     * @return gameField-instance
     */
    public FieldCell[][] getField() {
        return this.gameField;
    }

    /**
     * Resets the GridPane by clearing the children, Row- and ColumnConstraints. Adds new Column- and RowConstraints
     * based on the cols and rows
     *
     * @param cols amount of ColumnConstraints to be added
     * @param rows amount of RowConstraints to be added
     */
    private void initGridPane(int cols, int rows) {
        // Clear the GridPane if it is not empty
        if (!grid.getChildren().isEmpty()) {
            this.grid.getRowConstraints().clear();
            this.grid.getColumnConstraints().clear();
            this.grid.getChildren().clear();
        }
        // add cols
        for (int x = 0; x < cols; x++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(2);
            this.grid.getColumnConstraints().add(col);
        }
        //add rows
        for (int y = 0; y < rows; y++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(2);
            this.grid.getRowConstraints().add(row);
        }

        this.grid.setHgap(2);
        this.grid.setVgap(2);
    }

    /**
     * Returns the amount of Columns in this field
     *
     * @return amount of Columns
     */
    public int getCols() {
        return this.gameField.length;
    }

    /**
     * Returns the amount of Rows in this field
     *
     * @return amount of Rows
     */
    public int getRows() {
        return this.gameField[0].length;
    }

    /**
     * Uses the method {@link FieldCell#turn(boolean)} to turn the graphical representation either clockwise or
     * counterclockwise
     *
     * @param position  Position to be turned
     * @param clockwise boolean-Value to indicate if the field should be turned clockwise
     */
    public void turn(Position position, boolean clockwise) {
        this.gameField[position.x()][position.y()].turn(clockwise);
    }

    /**
     * Getter-method for the Image of a {@link FieldCell} image
     *
     * @param position Position for the image to be determined
     * @return Image that is being displayed at that position or null, if the position is null
     */
    public Image getImageAt(Position position) {
        return position != null ? this.gameField[position.x()][position.y()].getPipeImage() : null;
    }

    /**
     * Returns the Image-Property of a {@link FieldCell} instance at the specified position or null, if the position is
     * null
     *
     * @param pos Position for the ImageProperty
     * @return ImageProperty at the specified position
     */
    public ObjectProperty<Image> getImagePropertyAt(Position pos) {
        return pos != null ? this.gameField[pos.x()][pos.y()].getPipeImageProperty() : null;
    }

    /**
     * Removes the source-Image from the cell, where it is currently displayed in and adds it to the cell, where it
     * should be displayed from now on. If the position is null, no source will be displayed on the field
     *
     * @param position Position, where the source should be displayed
     */
    public void setSourcePosition(Position position) {
        // Removes the source from the field, where it is displayed in currently
        for (FieldCell[] arr : this.gameField) {
            for (FieldCell cell : arr) {
                cell.getChildren().remove(source);
            }
        }

        // add source to the new Position on the field
        if (position != null) {
            this.gameField[position.x()][position.y()].getChildren().add(source);
        }
    }

    /**
     * Sets the Image of the {@link  FieldCell} at the position to a new one, if the position is not null
     *
     * @param position Position, where an image should be displayed
     * @param image    Image instance that should be displayed
     */
    public void setImageAt(Position position, Image image) {
        if (position != null) {
            this.gameField[position.x()][position.y()].setPipeImage(image);
        }
    }

    /**
     * Displays the rotation Value in the field, which is located on the provided position.
     *
     * @param pos      Position, where the rotation should be initiated
     * @param rotation value for the rotation
     */
    public void setRotationAt(Position pos, int rotation) {
        if (pos != null) {
            this.gameField[pos.x()][pos.y()].setRotation(rotation);
        }
    }

    /**
     * Sets the onMouseClicked Event for the field cells. The Cells should be turned and the logic will be changed
     * accordingly. The cells will be turned clockwise, if the secondary MouseButton was clicked and counterclockwise if
     * the primary MouseButton was clicked
     *
     * @param controller provides the method to call when clicked
     */
    private void setOnMouseClicked(GameController controller) {
        for (FieldCell[] fieldCell : gameField) {
            for (FieldCell cell : fieldCell) {
                cell.setOnMouseClicked((MouseEvent event) -> {
                    int clickedX = GridPane.getColumnIndex(cell);
                    int clickedY = GridPane.getRowIndex(cell);
                    MouseButton btn = event.getButton();
                    if (btn == MouseButton.PRIMARY || btn == MouseButton.SECONDARY) {
                        // Turn clockwise, if the secondary MouseButton was clicked
                        controller.turn(btn == MouseButton.SECONDARY, new Position(clickedX, clickedY));
                    }
                    event.consume();
                });
            }
        }
    }

    /**
     * Sets the drag and drop mechanic for the Cells which should be enabled for dropping. Sets effect on  the field on
     * drag over and handles the drop mechanic.
     *
     * @param controller provides the method to call when dropped
     */
    private void setDragAndDrop(GameController controller) {
        for (FieldCell[] fieldCell : gameField) {
            for (FieldCell cell : fieldCell) {
                // Effect on drag over.
                cell.setOnDragOver((DragEvent event) -> {
                    if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                        // only accept, if we are not trying to place a source and are over a wall image
                        if (!(event.getDragboard().getString().equals("SOURCE") &&
                                cell.getPipeImage().equals(JavaFXGUI.WALL))) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                            cell.setEffect(new InnerShadow(10.0, Color.BLUE));
                        }
                    }
                    event.consume();
                });

                cell.setOnDragDropped((DragEvent event) -> {

                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        int x = GridPane.getColumnIndex(cell);
                        int y = GridPane.getRowIndex(cell);
                        controller.onGameFieldCellDropped(new Position(x, y), db.getString());
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
                cell.setOnDragExited((DragEvent event) -> cell.setEffect(null));
            }
        }
    }
}
