package nl.tudelft.jpacman.level;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import nl.tudelft.jpacman.board.GateSquare;

/** Tests for circuit commands and independence. */
class GateCircuitTest {
    /** Verifies that a circuit toggles all its gates without affecting another circuit. */
    @Test
    void togglesAllGatesAndKeepsCircuitsIndependent() {
        GateSquare a1 = new GateSquare(1, null, null);
        GateSquare a2 = new GateSquare(1, null, null);
        GateSquare b1 = new GateSquare(2, null, null);
        GateCircuit one = new GateCircuit(1, Arrays.asList(a1, a2));
        GateCircuit two = new GateCircuit(2, Arrays.asList(b1));
        one.toggle();
        assertTrue(a1.isOpen() && a2.isOpen() && !b1.isOpen());
        one.toggle();
        assertFalse(a1.isOpen() && a2.isOpen());
        two.toggle();
        assertTrue(b1.isOpen());
    }
}
