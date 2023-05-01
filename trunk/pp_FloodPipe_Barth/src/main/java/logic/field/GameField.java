package logic.field;

import logic.Position;
import logic.enums.Direction;
import logic.enums.PipeType;

import java.util.*;


/**
 * Class representing the GameField. Contains of a 2d-Array of {@link Pipe} instances, a {@link Position} for the source,
 * and a boolean Value for the overflow. Provides methods for generating a gameField, getters and setters, methods for
 * changing the rows and cols as well as different methods to get statuses of the GameField.
 *
 * @author Philip Barth
 */
public class GameField {

    /**
     * minimum amount of cols to be valid
     */
    public static final int MIN_AMOUNT_COLS = 2;

    /**
     * maximum amount of cols to be valid
     */
    public static final int MAX_AMOUNT_COLS = 15;

    /**
     * Default amount of Columns
     */
    public static final int DEFAULT_AMOUNT_COLS = 10;

    /**
     * minimum amount of rows to be valid
     */
    public static final int MIN_AMOUNT_ROWS = 2;

    /**
     * maximum amount of rows to be valid
     */
    public static final int MAX_AMOUNT_ROWS = 15;

    /**
     * Default amount of rows
     */
    public static final int DEFAULT_AMOUNT_ROWS = 10;

    /**
     * Default amount of walls in percent
     */

    public static final int DEFAULT_AMOUNT_WALLS_PERCENT = 10;

    /**
     * Min amount of walls in percent
     */
    public static final int MIN_AMOUNT_WALLS_PERCENT = 0;

    /**
     * Max amount of walls in percent
     */
    public static final int MAX_AMOUNT_WALLS_PERCENT = 100;

    /**
     * Array of Pipes for the GameField representation
     */
    private Pipe[][] field;

    /**
     * Position for the source
     */
    private Position source;

    /**
     * overflow selection
     */
    private boolean overflow;

    /**
     * Constructor used to create a GameField from different conditional values
     *
     * @param cols               amount of columns
     * @param rows               amount of rows
     * @param maxPercentageWalls max amount of walls in percentage
     * @param overflow           overflow enabled?
     * @throws IllegalArgumentException if cols, rows or maxPercentageWalls is out of bounds
     */
    public GameField(int cols, int rows, int maxPercentageWalls, boolean overflow) throws IllegalArgumentException {
        if (cols < MIN_AMOUNT_COLS || cols > MAX_AMOUNT_COLS || rows < MIN_AMOUNT_ROWS || rows > MAX_AMOUNT_ROWS
                || maxPercentageWalls < MIN_AMOUNT_WALLS_PERCENT || maxPercentageWalls > MAX_AMOUNT_WALLS_PERCENT) {
            throw new IllegalArgumentException();
        }
        this.overflow = overflow;
        createSolvedGameField(cols, rows, maxPercentageWalls, overflow);
        this.source = provideRandomSourcePosition();

    }


    /**
     * Constructor for creating a GameField from provided data
     *
     * @param data GameFieldData provided
     */
    public GameField(GameFieldData data) {
        int[][] fieldData = data.getBoard();
        int cols = fieldData.length;
        int rows = fieldData[0].length;

        this.field = new Pipe[cols][rows];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                this.field[x][y] = new Pipe(fieldData[x][y]);
            }
        }
        this.overflow = data.isOverflow();
        this.source = data.getSource();
    }

    /**
     * Erstellt ein Spielfeld aus BoxDrawingCharacters. Wird nur zu Testzwecken verwendet
     *
     * @param boxDrawingCharacters String mit BoxDrawingCharacters
     * @param sourcePosition       Position der Quelle
     * @param overflow             Überlaufmodus an/aus
     */
    public GameField(String boxDrawingCharacters, Position sourcePosition, boolean overflow) {
        String[] rows = boxDrawingCharacters.split("\n");
        int amountRows = rows.length;
        int amountCols = rows[0].length();
        this.field = new Pipe[amountCols][amountRows];

        for (int x = 0; x < amountCols; x++) {
            for (int y = 0; y < amountRows; y++) {
                field[x][y] = new Pipe(String.valueOf(rows[y].charAt(x)));
            }
        }
        this.source = sourcePosition;
        this.overflow = overflow;
    }


    /**
     * Returns a copy of the current GameField
     *
     * @return copy of the current GameField
     */
    public Pipe[][] getField() {
        int cols = getCols();
        int rows = getRows();
        Pipe[][] newField = new Pipe[cols][rows];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                newField[x][y] = field[x][y].copy();
            }
        }
        return newField;
    }

    /**
     * Method to create a solved gameField. Is being used by the constructor
     *
     * @param cols               amount of Columns
     * @param rows               amount of Rows
     * @param maxPercentageWalls max Amount of Walls in Percent
     * @param overflow           overflow set
     */
    private void createSolvedGameField(int cols, int rows, int maxPercentageWalls, boolean overflow) {
        // Generate a gameField until we get a suitable one (no empty fields, not too much walls)
        do {
            generateGameField(cols, rows, overflow);
        } while (!wallsInBounds(maxPercentageWalls) || !allOpeningsConnected());
    }


    /**
     * Provides a random Source Position for a solved GameField
     *
     * @return Position, where a source can be set
     */
    private Position provideRandomSourcePosition() {
        Random rnd = new Random();
        int cols = field.length;
        int rows = field[0].length;
        int x;
        int y;

        // Look for a random position without a wall
        do {
            x = rnd.nextInt(cols);
            y = rnd.nextInt(rows);
        } while (field[x][y].getType() == PipeType.WALL);
        return new Position(x, y);
    }

    /**
     * Generates a random GameField
     *
     * @param cols     amount of Columns
     * @param rows     amount of Rows
     * @param overflow overflow enabled?
     */
    private void generateGameField(int cols, int rows, boolean overflow) {

        Random rnd = new Random();
        Position startPosition = new Position(rnd.nextInt(cols), rnd.nextInt(rows));
        this.field = new Pipe[cols][rows];
        // Generate the GameField recursively
        generateGameField(startPosition, overflow);

        // Set walls on empty fields
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[x].length; y++) {
                if (field[x][y] == null) {
                    field[x][y] = new Pipe(PipeType.WALL);
                }
            }
        }
    }

    /**
     * Generates the GameField recursively from the position provided by filling the neighbors with fitting Pipes
     *
     * @param position current Position
     * @param overflow overflow selected?
     */
    private void generateGameField(Position position, boolean overflow) {
        // Get a pipe which would fit for the current position
        Pipe pipe = getRandomPipeForPosition(position);
        int cols = getCols();
        int rows = getRows();

        // Fitting pipe found
        if (pipe != null) {
            field[position.x()][position.y()] = pipe;
            // get Empty Neighbors, that need to be connected to this field
            EnumSet<Direction> emptyNeighbors = getEmptyNeighborsToFill(position);
            for (Direction direction : emptyNeighbors) {
                // generate the GameField recursively for each neighbor
                generateGameField(position.getNeighborPosition(cols, rows, direction, overflow), overflow);
            }
        }
    }

    /**
     * Provides a random Pipe that fits at the provided positions
     *
     * @param position position, where a pipe should be set
     * @return fitting Pipe or null, if no pipe fits
     */
    private Pipe getRandomPipeForPosition(Position position) {
        // Neighbors that have Openings to this position
        EnumSet<Direction> mandatoryOpenings = getNeighborsToConnect(position);

        // Empty neighbors
        EnumSet<Direction> optionalOpenings = getEmptyNeighbors(position);

        List<PipeType> possibleTypes = getPossiblePipeTypes(mandatoryOpenings, optionalOpenings);

        // DeadEnds can only be set, if no other pipe fits and
        if (possibleTypes.isEmpty() && (mandatoryOpenings.size() + optionalOpenings.size()) == 1) {
            possibleTypes.add(PipeType.DEAD_END);
        }

        // no type possible
        if (possibleTypes.isEmpty()) {
            return null;
            //get a random Pipe from the suitable ones and rotate it correctly
        } else {
            Random rnd = new Random();
            PipeType chosen = possibleTypes.get(rnd.nextInt(possibleTypes.size()));

            Pipe pipe = new Pipe(chosen);

            while (!rotationCorrect(pipe, mandatoryOpenings, optionalOpenings)) {
                pipe.turn(true);
            }
            return pipe;
        }
    }

    /**
     * Provides a List of possible PipeTypes from provided EnumSets with Openings in directions that are either
     * mandatory or optional. DeadEnds are only provided, if no other PipeType is suitable
     *
     * @param mandatory Directions of openings which are needed in the possible pipe type
     * @param optional  Directions of openings which are optional in the possible pipe type
     * @return List of suitable pipeTypes
     */
    List<PipeType> getPossiblePipeTypes(EnumSet<Direction> mandatory, EnumSet<Direction> optional) {
        int sizeMandatory = mandatory.size();
        int sizeOptional = optional.size();

        EnumSet<Direction> mandatoryAndOptional = EnumSet.copyOf(mandatory);
        mandatoryAndOptional.addAll(optional);

        List<PipeType> possibleTypes = new ArrayList<>();

        if (sizeMandatory + sizeOptional == 1) {
            possibleTypes.add(PipeType.DEAD_END);
        }

        // maximum of 2 mandatory Openings and minimum of 2 total possible openings -> Curve or Line possible
        if (sizeMandatory <= 2 && sizeMandatory + sizeOptional >= 2) {
            // cover mandatory openings first and then all possible openings
            if (hasAdjacentOpenings(mandatory)) {
                possibleTypes.add(PipeType.CURVE);
            } else if (hasOpposingOpenings(mandatory)) {
                possibleTypes.add(PipeType.LINE);
            } else if (hasAdjacentOpenings(mandatoryAndOptional)) {
                possibleTypes.add(PipeType.CURVE);
            } else if (hasOpposingOpenings(mandatoryAndOptional)) {
                possibleTypes.add(PipeType.LINE);
            }
        }

        if (sizeMandatory <= 3 && sizeMandatory + sizeOptional >= 3) {
            possibleTypes.add(PipeType.T_PIPE);
        }
        return possibleTypes;
    }

    /**
     * Checks whether the provided set contains adjacent openings (f.e. top and right)
     *
     * @param openings Set with the openings, that should be checked
     * @return true, if the set contains adjacent openings
     */
    private boolean hasAdjacentOpenings(EnumSet<Direction> openings) {
        if (openings.contains(Direction.TOP) || openings.contains(Direction.BOTTOM)) {
            return openings.contains(Direction.RIGHT) || openings.contains(Direction.LEFT);
        }
        return false;
    }

    /**
     * Checks whether the provided set contains opposing openings (f.e. top and bottom)
     *
     * @param openings Set with the openings, that should be checked
     * @return true, if the set contains opposing openings
     */
    private boolean hasOpposingOpenings(EnumSet<Direction> openings) {
        return openings.containsAll(Set.of(Direction.TOP, Direction.BOTTOM)) || openings.containsAll(Set.of(Direction.RIGHT, Direction.LEFT));
    }

    /**
     * Checks whether the provided Pipe is correctly rotated by comparing it with the mandatory and optionalOpenings
     *
     * @param pipe              Pipe to check rotation on
     * @param mandatoryOpenings mandatory Openings to be covered by the Pipe
     * @param optionalOpenings  optionalOpenings that can be covered by the pipe
     * @return true, if the pipe is correctly rotated
     */
    private boolean rotationCorrect(Pipe pipe, EnumSet<Direction> mandatoryOpenings, EnumSet<Direction> optionalOpenings) {
        EnumSet<Direction> pipeOpenings = pipe.getOpenings();
        EnumSet<Direction> mandatoryAndOptional = EnumSet.copyOf(mandatoryOpenings);
        mandatoryAndOptional.addAll(optionalOpenings);
        mandatoryAndOptional.addAll(optionalOpenings);
        return pipeOpenings.containsAll(mandatoryOpenings) && mandatoryAndOptional.containsAll(pipeOpenings);
    }

    /**
     * Initializes a new field with only WALL values
     */
    public void initNewField() {
        int cols = getCols();
        int rows = getRows();
        this.field = new Pipe[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                field[x][y] = new Pipe(PipeType.WALL);
            }
        }
        this.source = null;
    }


    /**
     * Getter for the SourcePosition
     *
     * @return SourcePosition
     */
    public Position getSource() {
        return source;
    }

    /**
     * Setter for the SourcePosition
     *
     * @param source SourcePosition to be set
     */
    public void setSource(Position source) {
        this.source = source;
    }

    /**
     * Getter for the overflow
     *
     * @return overflow
     */
    public boolean isOverflow() {
        return overflow;
    }

    /**
     * Setter for the overflow
     *
     * @param overflow overflow to be set
     */
    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }

    /**
     * Returns a copy of the Pipe at the Position provided
     *
     * @param pos Position of the pipe
     * @return Pipe on the position provided or null, if the position is null
     */
    public Pipe getAt(Position pos) {
        return pos != null ? field[pos.x()][pos.y()].copy() : null;
    }

    /**
     * Setter for a {@link Pipe} Instanz on the Position provided with the pipeType also provided and
     * {@link logic.enums.Rotation#NORMAL}
     *
     * @param pos  Position, where a new Pipe should be set
     * @param type Type of the Pipe
     */
    public void setAt(Position pos, PipeType type) {
        if (pos != null) {
            field[pos.x()][pos.y()] = new Pipe(type);
        }

    }

    /**
     * Turns the Pipe on the Position provided clockwise or counterclockwise
     *
     * @param pos       Position of the pipe
     * @param clockwise indicator for clockwise or counterclockwise
     */
    public void turn(Position pos, boolean clockwise) {
        if (pos != null) {
            field[pos.x()][pos.y()].turn(clockwise);
        }
    }

    /**
     * Getter for the amount of Columns in the field
     *
     * @return amount of Columns
     */
    public int getCols() {
        return field.length;
    }

    /**
     * Getter for the amount of Rows in the field
     *
     * @return amount of Rows
     */
    public int getRows() {
        return field[0].length;
    }

    /**
     * Returns the PipeType at the Position provided, or null, if the position is null
     *
     * @param position position, where the pipeType is demanded
     * @return PipeType or null, if position is null
     */
    public PipeType getPipeTypeAt(Position position) {
        return position != null ? this.field[position.x()][position.y()].getType() : null;
    }


    /**
     * Method to add or remove Rows on the Field and removes the source, if it is no longer on the field.
     *
     * @param newAmountOfRows new Amount of Rows
     */
    public void changeAmountOfRows(int newAmountOfRows) {
        if (newAmountOfRows < MIN_AMOUNT_ROWS || newAmountOfRows > MAX_AMOUNT_ROWS) {
            throw new IllegalArgumentException("Anzahl Reihen ist nicht valide");
        }
        // Create a new field and copy the rows that remain
        Pipe[][] newField = new Pipe[field.length][newAmountOfRows];
        int rowsToCopy = Math.min(newAmountOfRows, field[0].length);
        for (int i = 0; i < field.length; i++) {
            System.arraycopy(field[i], 0, newField[i], 0, rowsToCopy);
        }
        // Add new rows to the field if needed and provide Walls
        for (int i = 0; i < field.length; i++) {
            for (int j = rowsToCopy; j < newAmountOfRows; j++) {
                newField[i][j] = new Pipe(PipeType.WALL);
            }
        }
        field = newField;
        // Clear source if it is no longer in the field
        if (source != null && source.y() >= newAmountOfRows) {
            source = null;
        }
    }

    /**
     * Method to add or remove Cols on the Field and removes the source, if it is no longer on the field.
     *
     * @param newAmountOfCols new Amount of cols
     */
    public void changeCols(int newAmountOfCols) {
        if (newAmountOfCols < MIN_AMOUNT_COLS || newAmountOfCols > MAX_AMOUNT_COLS) {
            throw new IllegalArgumentException("Anzahl Spalten ist nicht valide");
        }
        // Create a new field and copy the cols that remain
        Pipe[][] newField = new Pipe[newAmountOfCols][field[0].length];
        int colsToCopy = Math.min(newAmountOfCols, field.length);
        System.arraycopy(field, 0, newField, 0, colsToCopy);

        // Add new cols to the field if needed and provide Walls
        for (int x = colsToCopy; x < newAmountOfCols; x++) {
            for (int y = 0; y < field[0].length; y++) {
                newField[x][y] = new Pipe(PipeType.WALL);
            }
        }
        this.field = newField;
        // Clear source if it is no longer in the field
        if (source != null && source.x() >= newAmountOfCols) {
            source = null;
        }

    }

    /**
     * Method to create an int-representation of the GameField
     *
     * @return Array of int-values representing the openings of the fields
     */
    int[][] toInt() {
        int cols = getCols();
        int rows = getRows();
        int[][] arr = new int[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                arr[i][j] = field[i][j].toInt();
            }
        }
        return arr;
    }

    /**
     * Method to rotate every field randomly
     */
    public void rotateRandomly() {
        for (Pipe[] row : field) {
            for (Pipe pipe : row) {
                pipe.rotateRandomly();
            }
        }
    }

    /**
     * This method returns a map of all the positions that are connected to the source position,
     * along with the distance (in number of moves) from the source position to each of those positions.
     *
     * @return a map where the keys are the distances from the source position, and the values are lists of positions
     * that are that distance away from the source
     */
    public Map<Integer, List<Position>> getConnectedPositionsWithDistances() {
        // If the source position is not set, return an empty map
        if (source == null) {
            return new HashMap<>();
        }
        int cols = getCols();
        int rows = getRows();

        Map<Integer, List<Position>> reachablePositions = new HashMap<>();

        // Initialize a queue to use for breadth-first search
        Queue<Position> queue = new LinkedList<>();

        // Initialize a set to keep track of which positions have been visited
        Set<Position> visited = new HashSet<>();

        queue.offer(source);
        visited.add(source);
        int distance = 0;

        // Add the source position to the map with a distance of 0
        reachablePositions.put(distance, new ArrayList<>());
        reachablePositions.get(distance).add(source);

        while (!queue.isEmpty()) {

            // Get the number of positions at the current distance
            int size = queue.size();
            distance++;
            //Add new List for the current distance
            reachablePositions.put(distance, new ArrayList<>());

            for (int i = 0; i < size; i++) {
                Position curr = queue.poll();
                if (curr != null) {

                    // Check all possible directions to find the connected neighbor positions
                    for (Direction direction : Direction.values()) {
                        Position neighbor = curr.getNeighborPosition(cols, rows, direction, overflow);
                        // add neighbor to reachablePositions if it is not visited yet but connected to this field
                        if (isConnectedToNeighbor(direction, curr) && !visited.contains(neighbor)) {
                            queue.offer(neighbor);
                            visited.add(neighbor);
                            // Add position to the list at the current distance
                            reachablePositions.get(distance).add(neighbor);
                        }
                    }
                }
            }
        }
        return reachablePositions;
    }


    /**
     * Returns all connected Positions as a Set. Uses {@link #getConnectedPositionsWithDistances}
     *
     * @return Set with every Position that is connected to the sourcePosition
     */
    public Set<Position> getConnectedPositionsAsSet() {
        Map<Integer, List<Position>> reachablePositions = getConnectedPositionsWithDistances();
        Set<Position> connectedPositions = new HashSet<>();
        for (List<Position> positions : reachablePositions.values()) {
            connectedPositions.addAll(positions);
        }
        return connectedPositions;
    }


    /**
     * This method returns a Set of Position objects that are not connected to the source
     * position.
     *
     * @return a set of unconnected positions (except Walls)
     */
    public Set<Position> getUnconnectedPositions() {
        Set<Position> unreachablePositions = new HashSet<>();
        int cols = getCols();
        int rows = getRows();
        Set<Position> visited = new HashSet<>();

        // Queue for breadth-first search algorithm
        Queue<Position> queue = new LinkedList<>();
        queue.offer(source);
        visited.add(source);
        if (source != null) {
            while (!queue.isEmpty()) {
                Position curr = queue.poll();
                // Check every direction and add the Position to visited
                for (Direction direction : Direction.values()) {
                    Position neighbor = curr.getNeighborPosition(cols, rows, direction, overflow);
                    if (isConnectedToNeighbor(direction, curr) && !visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }

        // Check every position and determine if it is in the visited set, meaning that it is connected to the source.
        // If it is not, add it to unreachable Positions, if it is not a wall
        for (int i = 0; i < getCols(); i++) {
            for (int j = 0; j < getRows(); j++) {
                Position curr = new Position(i, j);

                if (!visited.contains(curr) && !(getPipeTypeAt(curr) == PipeType.WALL)) {
                    unreachablePositions.add(curr);
                }
            }
        }
        return unreachablePositions;
    }

    /**
     * Checks if there are no unconnected Positions in the set anymore
     *
     * @return true, if all fields are connected (or walls)
     */
    public boolean noUnconnectedFields() {
        return getUnconnectedPositions().size() == 0;
    }

    /**
     * Check for a position, if all neighbors of that position are connected
     *
     * @param pos Position to be checked
     * @return true, if all the Neighbors are connected
     */
    private boolean allNeighborsConnected(Position pos) {
        Pipe pipe = field[pos.x()][pos.y()];

        if (pipe == null) {
            return false;
        }

        int cols = getCols();
        int rows = getRows();

        boolean allConnected = true;

        Iterator<Direction> it = pipe.getOpenings().iterator();
        Position neighbor;
        Direction dir;
        // Check every direction, if it should be connected and if it is connected
        while (it.hasNext() && allConnected) {
            dir = it.next();
            neighbor = pos.getNeighborPosition(cols, rows, dir, overflow);
            allConnected = neighbor != null && field[neighbor.x()][neighbor.y()].getOpenings().contains(dir.getOpposite());
        }
        return allConnected;
    }

    /**
     * Checks if the Neighbor field needs to be connected to this field by taking the direction of the neighbor and
     * checking if the neighbor has the opposite Opening
     *
     * @param neighborPosition Position to be checked
     * @param dir              direction in which the neighbor is
     * @return true, if the neighbor needs to be connected to this position
     */
    private boolean needsConnection(Position neighborPosition, Direction dir) {
        if (neighborPosition == null) {
            return false;
        }
        Pipe pipe = field[neighborPosition.x()][neighborPosition.y()];
        if (pipe == null) {
            return false;
        }
        return pipe.getOpenings().contains(dir.getOpposite());
    }

    /**
     * Checks, if the neighbor is connected to the field on the current Position.
     *
     * @param dir             Direction of the neighbor
     * @param currentPosition Position of the current field
     * @return true, if the neighbor is connected
     */
    private boolean isConnectedToNeighbor(Direction dir, Position currentPosition) {
        int cols = getCols();
        int rows = getRows();
        if (currentPosition.isInvalidPosition(cols, rows)) {
            return false;
        }

        if (!field[currentPosition.x()][currentPosition.y()].getOpenings().contains(dir)) {
            return false;
        }

        Position neighborPosition = currentPosition.getNeighborPosition(cols, rows, dir, overflow);
        if (neighborPosition == null) {
            return false;
        }
        Pipe neighbor = field[neighborPosition.x()][neighborPosition.y()];

        return neighbor != null && neighbor.getOpenings().contains(dir.getOpposite());
    }

    /**
     * Check for every field, if every neighbor is connected. Uses the {@link #allNeighborsConnected(Position)} method
     * to achieve it
     *
     * @return true, if all openings are connected
     */
    public boolean allOpeningsConnected() {
        boolean isDone = true;
        for (int x = 0; x < getCols() && isDone; x++) {
            for (int y = 0; y < getRows() && isDone; y++) {
                isDone = allNeighborsConnected(new Position(x, y));
            }
        }
        return isDone;
    }

    /**
     * Returns a Set of Directions containing Empty Neighbors from the position provided where connections need to be
     * made.
     *
     * @param pos Position to be checked
     * @return Set of Empty neighbors to fill
     */
    private EnumSet<Direction> getEmptyNeighborsToFill(Position pos) {

        EnumSet<Direction> emptyNeighbors = getEmptyNeighbors(pos);
        EnumSet<Direction> emptyNeighborsToFill = EnumSet.noneOf(Direction.class);
        for (Direction direction : field[pos.x()][pos.y()].getOpenings()) {
            if (emptyNeighbors.contains(direction)) {
                emptyNeighborsToFill.add(direction);
            }
        }
        return emptyNeighborsToFill;
    }

    /**
     * Checks, if the field is empty
     *
     * @param pos Position
     * @return true, if the position is not null and the field is null
     */
    private boolean isEmptyField(Position pos) {
        return pos != null && field[pos.x()][pos.y()] == null;
    }


    /**
     * Returns a Set with directions of every empty neighbor field from the provided position
     *
     * @param pos position where empty neighbors should be provided
     * @return Set of Directions, where empty neighbor fields are located
     */
    private EnumSet<Direction> getEmptyNeighbors(Position pos) {
        EnumSet<Direction> emptyNeighbors = EnumSet.noneOf(Direction.class);
        int cols = getCols();
        int rows = getRows();
        for (Direction direction : Direction.values()) {
            Position neighborPos = pos.getNeighborPosition(cols, rows, direction, overflow);
            if (isEmptyField(neighborPos)) {
                emptyNeighbors.add(direction);
            }
        }
        return emptyNeighbors;
    }

    /**
     * Method to retrieve neighbors, that need to be connected to the provided Position on the field
     *
     * @param pos position where neighbors, that need a connection, should be provided
     * @return Set of Directions, where neighbor fields need to be connected
     */
    private EnumSet<Direction> getNeighborsToConnect(Position pos) {
        EnumSet<Direction> neighborsToConnect = EnumSet.noneOf(Direction.class);
        int cols = getCols();
        int rows = getRows();
        for (Direction direction : Direction.values()) {
            Position neighborPos = pos.getNeighborPosition(cols, rows, direction, overflow);
            if (needsConnection(neighborPos, direction)) {
                neighborsToConnect.add(direction);
            }
        }
        return neighborsToConnect;
    }

    /**
     * Checks, if this field has less than or equal amount of walls to the specified settings.
     *
     * @param maxPercentageWalls max amount of walls (in percent)
     * @return true, if less than or equal walls are present in the current field
     */
    private boolean wallsInBounds(int maxPercentageWalls) {
        return getAmountOfWalls() <= getCols() * getRows() * maxPercentageWalls / 100;
    }

    /**
     * Checks how many Walls are placed on the GameField
     *
     * @return amount of walls
     */
    int getAmountOfWalls() {
        int counter = 0;
        for (Pipe[] pipes : field) {
            for (Pipe pipe : pipes) {
                if (pipe.getType() == PipeType.WALL) {
                    counter++;
                }
            }
        }
        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameField gameField = (GameField) o;
        return overflow == gameField.overflow && Arrays.deepEquals(field, gameField.field)
                && Objects.equals(source, gameField.source);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(source, overflow);
        result = 31 * result + Arrays.deepHashCode(field);
        return result;
    }
}
