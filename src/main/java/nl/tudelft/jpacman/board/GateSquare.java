package nl.tudelft.jpacman.board;

import java.awt.Color;
import java.awt.Graphics;
import nl.tudelft.jpacman.sprite.Sprite;

/** A circuit-controlled passage with an independent physical state. */
public final class GateSquare extends Square {
    /** The physical states a gate can occupy. */
    public enum State {
        /** The gate allows entry and exit. */
        OPEN,
        /** The gate is closed and unoccupied. */
        CLOSED,
        /** The gate is occupied and will close when vacated. */
        PENDING_CLOSE
    }

    private static final int MIN_MARK_OFFSET = 2;
    private static final int MIN_VERTICAL_OFFSET = 10;
    private static final int MARK_X_DIVISOR = 3;
    private static final int MARK_Y_DIVISOR = 3;

    private final int circuitId;
    private final Sprite floor;
    private final Sprite wall;
    private State state = State.CLOSED;

    /**
     * Creates a gate for the given circuit.
     * @param circuitId the owning circuit identifier (1 or 2)
     * @param floor the sprite used while the gate is open
     * @param wall the sprite used while the gate is closed
     */
    public GateSquare(int circuitId, Sprite floor, Sprite wall) {
        if (circuitId != 1 && circuitId != 2) {
            throw new IllegalArgumentException(
                "Circuit id must be 1 or 2: " + circuitId);
        }
        this.circuitId = circuitId;
        this.floor = new MarkedSprite(floor, String.valueOf(circuitId), false);
        this.wall = new MarkedSprite(wall, String.valueOf(circuitId), true);
    }

    /** @return the identifier of the circuit controlling this gate */
    public int getCircuitId() {
        return circuitId;
    }

    /** @return the current physical state of this gate */
    public State getState() {
        return state;
    }

    /** @return whether the gate currently permits entry */
    public boolean isOpen() {
        return state == State.OPEN;
    }

    /** @return whether closure is waiting for occupants to leave */
    public boolean isPendingClose() {
        return state == State.PENDING_CLOSE;
    }

    /** Opens the gate and cancels any pending closure. */
    public void open() {
        state = State.OPEN;
    }

    /** Closes the gate immediately if empty, or marks closure as pending. */
    public void close() {
        if (getOccupants().isEmpty()) {
            state = State.CLOSED;
        } else {
            state = State.PENDING_CLOSE;
        }
    }

    @Override
    void remove(Unit occupant) {
        super.remove(occupant);
        if (state == State.PENDING_CLOSE && getOccupants().isEmpty()) {
            state = State.CLOSED;
        }
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return state == State.OPEN;
    }

    @Override
    public Sprite getSprite() {
        if (state == State.OPEN) {
            return floor;
        }
        if (state == State.PENDING_CLOSE) {
            return new MarkedSprite(wall, "!", true);
        }
        return wall;
    }

    /** Draws the circuit identifier and, when needed, a pending marker. */
    private static final class MarkedSprite implements Sprite {
        private final Sprite base;
        private final String mark;
        private final boolean closed;

        MarkedSprite(Sprite base, String mark, boolean closed) {
            this.base = base;
            this.mark = mark;
            this.closed = closed;
        }

        @Override
        public void draw(Graphics graphics, int x, int y, int width, int height) {
            base.draw(graphics, x, y, width, height);
            Color oldColor = graphics.getColor();
            if (closed) {
                graphics.setColor(Color.WHITE);
            } else {
                graphics.setColor(Color.BLACK);
            }
            int markX = x + Math.max(MIN_MARK_OFFSET, width / MARK_X_DIVISOR);
            int markY = y + Math.max(MIN_VERTICAL_OFFSET,
                height * MARK_Y_DIVISOR / (MARK_Y_DIVISOR + 1));
            graphics.drawString(mark, markX, markY);
            graphics.setColor(oldColor);
        }

        @Override
        public Sprite split(int x, int y, int width, int height) {
            return base.split(x, y, width, height);
        }

        @Override
        public int getWidth() {
            return base.getWidth();
        }

        @Override
        public int getHeight() {
            return base.getHeight();
        }
    }
}
