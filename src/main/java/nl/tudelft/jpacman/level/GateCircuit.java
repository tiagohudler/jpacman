package nl.tudelft.jpacman.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.tudelft.jpacman.board.GateSquare;

/** Coordinates a single circuit's command across all of its gates. */
public final class GateCircuit {
    private final int id;
    private final List<GateSquare> gates;
    private boolean open;

    /**
     * Creates a circuit and initially closes all its gates.
     * @param id circuit identifier (1 or 2)
     * @param gates gates controlled by this circuit
     */
    public GateCircuit(int id, List<GateSquare> gates) {
        if (id != 1 && id != 2) {
            throw new IllegalArgumentException("Circuit id must be 1 or 2");
        }
        if (gates == null || gates.isEmpty()) {
            throw new IllegalArgumentException(
                "Circuit " + id + " needs at least one gate");
        }
        this.id = id;
        this.gates = Collections.unmodifiableList(new ArrayList<>(gates));
        this.open = false;
        for (GateSquare gate : this.gates) {
            if (gate.getCircuitId() != id) {
                throw new IllegalArgumentException(
                    "Gate belongs to a different circuit");
            }
            gate.close();
        }
    }

    /** @return this circuit's identifier */
    public int getId() {
        return id;
    }

    /** @return the last command issued to this circuit */
    public boolean isOpen() {
        return open;
    }

    /** @return an unmodifiable list of gates controlled by this circuit */
    public List<GateSquare> getGates() {
        return gates;
    }

    /** Reverses the circuit's commanded state. */
    public void toggle() {
        setOpen(!open);
    }

    /**
     * Commands every gate in the circuit to open or close.
     * @param value true to open all gates, false to close them
     */
    public void setOpen(boolean value) {
        open = value;
        for (GateSquare gate : gates) {
            if (value) {
                gate.open();
            } else {
                gate.close();
            }
        }
    }
}
