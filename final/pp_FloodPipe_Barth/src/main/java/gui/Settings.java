package gui;

import logic.field.GameField;

/**
 * Class for providing {@link GameController} or {@link SettingController} classes with Settings
 *
 * @param cols               amount of Columns
 * @param rows               amount of rows
 * @param maxPercentageWalls amount of walls in percent
 * @param overflow           overflow enabled
 * @author Philip Barth
 */
public record Settings(int cols, int rows, int maxPercentageWalls, boolean overflow) {

    /**
     * Constructor for the default settings used by the {@link StartScreenController}
     */
    public Settings() {
        this(GameField.DEFAULT_AMOUNT_COLS, GameField.DEFAULT_AMOUNT_ROWS, GameField.DEFAULT_AMOUNT_WALLS_PERCENT,
                false);
    }
}
