package nl.tudelft.jpacman.level;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import nl.tudelft.jpacman.board.GateSquare;
import java.util.concurrent.atomic.AtomicInteger;

/** Coordinates a single circuit's command across all of its gates. */
public final class GateCircuit {
    private final int id;
    private final List<GateSquare> gates;
    private final Timer pulseTimer;
    // counts how many pulses are queued
    private AtomicInteger remainingPulses;
    private boolean open;

    /**
     * Creates a circuit and initially closes all its gates.
     * @param id circuit identifier (1 or 2)
     */
    public GateCircuit(int id) {
        if (id != 1 && id != 2) {
            throw new IllegalArgumentException("Circuit id must be 1 or 2");
        }
        this.id = id;
        this.gates = new ArrayList<GateSquare>();
        this.open = false;
        for (GateSquare gate : this.gates) {
            if (gate.getCircuitId() != id) {
                throw new IllegalArgumentException(
                    "Gate belongs to a different circuit");
            }
            gate.close();
        }
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                toggle();
            }
        };
        pulseTimer = new Timer(5000, taskPerformer);
        pulseTimer.setRepeats(false);
        remainingPulses = new AtomicInteger(0);
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

    /** adds a gate to the circuit.
     * @param gate the gate to be added to the circuit
     */
    public void addGate(GateSquare gate) {
        if (gate == null) {
            throw new IllegalArgumentException("Gate cannot be null");
        }
        this.gates.add(gate);
    }

    /** Reverses the circuit's commanded state. */
    public void toggle() {
        if (remainingPulses.get() > 0) {
            remainingPulses.decrementAndGet();
            this.pulseTimer.start();
            return;
        }
        setOpen(!open);
    }

    /** Reverses the circuit's commanded state. */
    public void pulse() {
        if (this.pulseTimer.isRunning()) {
            this.remainingPulses.addAndGet(1);
            return;
        }

        toggle();
        this.pulseTimer.start();
    }

    /** Pauses the pulse in a circuit. */
    public void pause() {
        if (this.pulseTimer.isRunning()) {
            this.pulseTimer.stop();
        }
    }

    /** Restarts the pulse in a circuit. */
    public void restart() {
        if (this.pulseTimer.isRunning()) {
            this.pulseTimer.start();
        }
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
