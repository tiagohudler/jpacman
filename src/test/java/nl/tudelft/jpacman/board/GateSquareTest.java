package nl.tudelft.jpacman.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import nl.tudelft.jpacman.sprite.Sprite;

/** Tests for gate accessibility and state transitions. */
class GateSquareTest {
    private final Sprite unused = null;

    /** Verifies that closed gates block entry and open gates permit it. */
    @Test
    void closedGateBlocksEntryAndOpenGateAllowsIt() {
        GateSquare gate = new GateSquare(1, unused, unused);
        BasicUnit unit = new BasicUnit();
        assertFalse(gate.isAccessibleTo(unit));
        gate.open();
        assertTrue(gate.isAccessibleTo(unit));
    }

    /** Verifies that an occupied gate closes after its last occupant leaves. */
    @Test
    void occupiedGateRemainsPendingUntilLastOccupantLeaves() {
        GateSquare gate = new GateSquare(1, unused, unused);
        BasicUnit first = new BasicUnit();
        BasicUnit second = new BasicUnit();
        TestGround ground = new TestGround();
        gate.open();
        first.occupy(gate);
        second.occupy(gate);
        gate.close();
        assertEquals(GateSquare.State.PENDING_CLOSE, gate.getState());
        assertFalse(gate.isAccessibleTo(new BasicUnit()));
        first.occupy(ground);
        assertEquals(GateSquare.State.PENDING_CLOSE, gate.getState());
        second.occupy(ground);
        assertEquals(GateSquare.State.CLOSED, gate.getState());
    }

    /** Verifies that reopening a gate cancels its pending closure. */
    @Test
    void openingCancelsPendingClosure() {
        GateSquare gate = new GateSquare(2, unused, unused);
        BasicUnit unit = new BasicUnit();
        gate.open();
        unit.occupy(gate);
        gate.close();
        gate.open();
        assertEquals(GateSquare.State.OPEN, gate.getState());
        assertTrue(gate.isAccessibleTo(new BasicUnit()));
    }

    /** A traversable square used as the destination in occupancy tests. */
    private static final class TestGround extends Square {
        @Override
        public boolean isAccessibleTo(Unit unit) {
            return true;
        }

        @Override
        public Sprite getSprite() {
            return null;
        }
    }
}
