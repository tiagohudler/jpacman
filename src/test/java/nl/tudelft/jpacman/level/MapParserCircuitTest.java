package nl.tudelft.jpacman.level;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.points.PointCalculator;
import nl.tudelft.jpacman.sprite.PacManSprites;

/** Tests map parsing for legacy maps and circuit definitions. */
class MapParserCircuitTest {
    /** Creates a parser with mocked sprite and game dependencies. */
    private MapParser parser() {
        PacManSprites sprites = mock(PacManSprites.class);
        LevelFactory levels = new LevelFactory(
            sprites, mock(GhostFactory.class), mock(PointCalculator.class));
        return new MapParser(levels, new BoardFactory(sprites));
    }

    /** Verifies that maps without gates remain supported. */
    @Test
    void legacyMapsRemainSupported() {
        assertNotNull(parser().parseMap(Arrays.asList("#####", "#P  #", "#####")));
    }

    /** Verifies that maps containing both complete circuits are accepted. */
    @Test
    void acceptsBothCompleteCircuits() {
        assertNotNull(parser().parseMap(
            Arrays.asList("########", "#P1a2b #", "########")));
    }

    /** Verifies that maps containing both phase pulses are accepted. */
    @Test
    void acceptsBothPhasePulses() {
        assertNotNull(parser().parseMap(
            Arrays.asList("########", "#P1a2b #", "########", "###!?###")));
    }
}
