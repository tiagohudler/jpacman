package nl.tudelft.jpacman.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.GateSquare;
import nl.tudelft.jpacman.board.PhasePulseSquare;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Creates new {@link Level}s from text representations.
 *
 * @author Jeroen Roosen
 */
public class MapParser {

    /**
     * The factory that creates the levels.
     */
    private final LevelFactory levelCreator;

    /**
     * The factory that creates the squares and board.
     */
    private final BoardFactory boardCreator;

    /**
     * Creates a new map parser.
     *
     * @param levelFactory
     *            The factory providing the NPC objects and the level.
     * @param boardFactory
     *            The factory providing the Square objects and the board.
     */
    public MapParser(LevelFactory levelFactory, BoardFactory boardFactory) {
        this.levelCreator = levelFactory;
        this.boardCreator = boardFactory;
    }

    /**
     * Parses the text representation of the board into an actual level.
     *
     * <ul>
     * <li>Supported characters:
     * <li>' ' (space) an empty square.
     * <li>'#' (bracket) a wall.
     * <li>'.' (period) a square with a pellet.
     * <li>'P' (capital P) a starting square for players.
     * <li>'G' (capital G) a square with a ghost.
     * </ul>
     *
     * @param map
     *            The text representation of the board, with map[x][y]
     *            representing the square at position x,y.
     * @return The level as represented by this text.
     */
    public Level parseMap(char[][] map) {
        int width = map.length;
        int height = map[0].length;
        ParseState state = new ParseState(width, height);
        makeGrid(map, state);

        Board board = boardCreator.createBoard(state.grid);
        
        return levelCreator.createLevel(
            board, state.ghosts, state.startPositions);
    }

    /** Holds the mutable data collected while parsing one map. */
    private static final class ParseState {
        private final Square[][] grid;
        private final List<Ghost> ghosts = new ArrayList<>();
        private final List<Square> startPositions = new ArrayList<>();
        private final Map<Integer, GateCircuit> circuits = new HashMap<>();

        ParseState(int width, int height) {
            grid = new Square[width][height];
        }
    }

    private void makeGrid(char[][] map, ParseState state) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                addSquare(state, x, y, map[x][y]);
            }
        }
    }

    /**
     * Adds a square to the grid based on a map character.
     * @param grid grid receiving the new square
     * @param ghosts ghosts already present in the map
     * @param startPositions player start squares already present
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param c map character describing the square
     */
    protected void addSquare(Square[][] grid, List<Ghost> ghosts,
                             List<Square> startPositions, int x, int y, char c) {
        ParseState state = new ParseState(grid.length, grid[0].length);
        for (int column = 0; column < grid.length; column++) {
            System.arraycopy(grid[column], 0, state.grid[column], 0, grid[column].length);
        }
        state.ghosts.addAll(ghosts);
        state.startPositions.addAll(startPositions);
        addSquare(state, x, y, c);
        grid[x][y] = state.grid[x][y];
        ghosts.clear();
        ghosts.addAll(state.ghosts);
        startPositions.clear();
        startPositions.addAll(state.startPositions);
    }

    private void addSquare(ParseState state, int x, int y, char c) {
        switch (c) {
            case ' ':
                state.grid[x][y] = boardCreator.createGround();
                break;
            case '#':
                state.grid[x][y] = boardCreator.createWall();
                break;
            case '.':
                addPelletSquare(state, x, y);
                break;
            case 'G':
                state.grid[x][y] = makeGhostSquare(
                    state.ghosts, levelCreator.createGhost());
                break;
            case 'P':
                addPlayerStart(state, x, y);
                break;
            case '1':
            case '2':
                addSwitchSquare(state, x, y, c - '0');
                break;
            case 'a':
            case 'b':
                addGateSquare(state, x, y, gateCircuitId(c));
                break;
            case '!':
                addPhasePulseSquare(state, x, y, 1);
            case '?':
                addPhasePulseSquare(state, x, y, 2);
                break;
            default:
                throw new PacmanConfigurationException("Invalid symbol " + c + " at " + x + y);
        }
    }

    private void addPelletSquare(ParseState state, int x, int y) {
        Square square = boardCreator.createGround();
        state.grid[x][y] = square;
        levelCreator.createPellet().occupy(square);
    }

    private void addPlayerStart(ParseState state, int x, int y) {
        Square square = boardCreator.createGround();
        state.grid[x][y] = square;
        state.startPositions.add(square);
    }

    private void addSwitchSquare(ParseState state, int x, int y, int circuitId) {
        if (state.circuits.get(circuitId) == null) {
            state.circuits.put(circuitId, new GateCircuit(circuitId));
        }
        state.grid[x][y] = boardCreator.createSwitch(state.circuits.get(circuitId));
    }

    private void addGateSquare(ParseState state, int x, int y, int circuitId) {
        if (state.circuits.get(circuitId) == null) {
            state.circuits.put(circuitId, new GateCircuit(circuitId));
        }
        GateSquare gate = boardCreator.createGate(circuitId);
        state.grid[x][y] = gate;
        state.circuits.get(circuitId).addGate(gate);
    }

    private void addPhasePulseSquare(ParseState state, int x, int y, int circuitId) {
        if (state.circuits.get(circuitId) == null) {
            state.circuits.put(circuitId, new GateCircuit(circuitId));
        }
        PhasePulseSquare pulse = boardCreator.createPhasePulse(state.circuits.get(circuitId));
        state.grid[x][y] = pulse;
        // state.circuits.get(circuitId).addGate(gate);
    }

    private int gateCircuitId(char symbol) {
        if (symbol == 'a') {
            return 1;
        }
        return 2;
    }

    /**
     * creates a Square with the specified ghost on it
     * and appends the placed ghost into the ghost list.
     *
     * @param ghosts all the ghosts in the level so far, the new ghost will be appended
     * @param ghost the newly created ghost to be placed
     * @return a square with the ghost on it.
     */
    protected Square makeGhostSquare(List<Ghost> ghosts, Ghost ghost) {
        Square ghostSquare = boardCreator.createGround();
        ghosts.add(ghost);
        ghost.occupy(ghostSquare);
        return ghostSquare;
    }

    /**
     * Parses the list of strings into a 2-dimensional character array and
     * passes it on to {@link #parseMap(char[][])}.
     *
     * @param text
     *            The plain text, with every entry in the list being a equally
     *            sized row of squares on the board and the first element being
     *            the top row.
     * @return The level as represented by the text.
     * @throws PacmanConfigurationException If text lines are not properly formatted.
     */
    public Level parseMap(List<String> text) {

        checkMapFormat(text);

        int height = text.size();
        int width = text.get(0).length();

        char[][] map = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = text.get(y).charAt(x);
            }
        }
        return parseMap(map);
    }

    /**
     * Check the correctness of the map lines in the text.
     * @param text Map to be checked
     * @throws PacmanConfigurationException if map is not OK.
     */
    private void checkMapFormat(List<String> text) {
        if (text == null) {
            throw new PacmanConfigurationException(
                "Input text cannot be null.");
        }

        if (text.isEmpty()) {
            throw new PacmanConfigurationException(
                "Input text must consist of at least 1 row.");
        }

        int width = text.get(0).length();

        if (width == 0) {
            throw new PacmanConfigurationException(
                "Input text lines cannot be empty.");
        }

        for (String line : text) {
            if (line.length() != width) {
                throw new PacmanConfigurationException(
                    "Input text lines are not of equal width.");
            }
        }
    }

    /**
     * Parses the provided input stream as a character stream and passes it
     * result to {@link #parseMap(List)}.
     *
     * @param source
     *            The input stream that will be read.
     * @return The parsed level as represented by the text on the input stream.
     * @throws IOException
     *             when the source could not be read.
     */
    public Level parseMap(InputStream source) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            source, "UTF-8"))) {
            List<String> lines = new ArrayList<>();
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            return parseMap(lines);
        }
    }

    /**
     * Parses the provided input stream as a character stream and passes it
     * result to {@link #parseMap(List)}.
     *
     * @param mapName
     *            Name of a resource that will be read.
     * @return The parsed level as represented by the text on the input stream.
     * @throws IOException
     *             when the resource could not be read.
     */
    @SuppressFBWarnings(
        value = {"OBL_UNSATISFIED_OBLIGATION", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
        justification = "try with resources always cleans up / false positive in java 11"
    )
    public Level parseMap(String mapName) throws IOException {
        try (InputStream boardStream = MapParser.class.getResourceAsStream(mapName)) {
            if (boardStream == null) {
                throw new PacmanConfigurationException("Could not get resource for: " + mapName);
            }
            return parseMap(boardStream);
        }
    }

    /**
     * @return the BoardCreator
     */
    protected BoardFactory getBoardCreator() {
        return boardCreator;
    }
}
