package logic;

import logic.enums.FieldError;
import logic.enums.PipeType;
import logic.field.Pipe;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fake-Gui, die zu Testzwecken genutzt wird, um etwaige Gui-Aenderungen durch die Logik zu unterbinden.
 *
 * @author Philip Barth
 */
public class FakeGUI implements GUIConnector {


    @Override
    public void turn(Position position, boolean clockwise) {

    }

    @Override
    public void changeSourcePosition(Position position) {

    }

    @Override
    public void changeField(Position pos, PipeType type) {

    }

    @Override
    public void displayFieldWithAnimation(Pipe[][] gameField, Map<Integer, List<Position>> connectedPositions,
                                          Set<Position> unconnectedPositions, Integer counter) {

    }

    @Override
    public void displayFieldWithoutAnimation(Pipe[][] gameField, Position sourcePosition, Set<Position> reachablePositions, boolean solved) {

    }


    @Override
    public void showLoadingErrorAlert(FieldError error) {

    }
}
