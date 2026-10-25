package nl.tudelft.jpacman;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.GateSquare;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Smoke test launching the full game and attempting typical moves.
 */
public class LauncherSmokeTest {

    private Launcher launcher;

    /**
     * Launch the user interface.
     */
    @BeforeEach
    void setUpPacman() {
        launcher = new Launcher();
        launcher.launch();
    }

    /**
     * Quit the user interface when we're done.
     */
    @AfterEach
    void tearDown() {
        launcher.dispose();
    }

    /**
     * Launch the game and verify that switches can open gates.
     *
     * @throws InterruptedException Since we're sleeping in this test.
     */
    @SuppressWarnings({
        "magicnumber",
        "methodlength",
        "PMD.JUnitTestContainsTooManyAsserts"
    })
    @Test
    void smokeTest() throws InterruptedException {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        Board board = game.getLevel().getBoard();

        GateSquare circuitTwoGate =
            (GateSquare) board.squareAt(17, 13);

        // The game starts cleanly, with circuit 2's gate closed.
        assertThat(game.isInProgress()).isFalse();
        assertThat(circuitTwoGate.isOpen()).isFalse();

        game.start();

        assertThat(game.isInProgress()).isTrue();
        assertThat(player.getScore()).isZero();

        // Collect pellets in the starting corridor.
        game.move(player, Direction.EAST);
        assertThat(player.getScore()).isEqualTo(10);

        game.move(player, Direction.WEST);
        assertThat(player.getScore()).isEqualTo(10);

        move(game, Direction.EAST, 7);
        assertThat(player.getScore()).isEqualTo(60);

        // Reach the circuit-2 switch in the lower-right corridor.
        move(game, Direction.SOUTH, 2);
        move(game, Direction.EAST, 4);
        move(game, Direction.SOUTH, 2);
        move(game, Direction.WEST, 2);

        assertThat(player.getSquare())
            .isSameAs(board.squareAt(19, 19));

        // The switch should open circuit 2's gate.
        assertThat(circuitTwoGate.isOpen()).isTrue();

        // Return to the gate and verify that Pac-Man can enter it.
        move(game, Direction.EAST, 2);
        move(game, Direction.NORTH, 2);
        move(game, Direction.WEST, 4);
        move(game, Direction.NORTH, 2);
        move(game, Direction.NORTH, 2);

        assertThat(player.getSquare())
            .isSameAs(board.squareAt(17, 13));

        game.stop();

        assertThat(game.isInProgress()).isFalse();
    }

    /**
     * Make a number of moves in a given direction.
     *
     * @param game The game being played.
     * @param dir The direction to move.
     * @param numSteps The number of steps to take.
     */
    public static void move(Game game, Direction dir, int numSteps) {
        Player player = game.getPlayers().get(0);

        for (int i = 0; i < numSteps; i++) {
            game.move(player, dir);
        }
    }
}