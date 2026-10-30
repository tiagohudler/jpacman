package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.Sprite;

import java.awt.Color;
import java.awt.Graphics;

/** Represents a phase pulse consummable, which switches the phase of a circuit for 5 seconds. */
public class PhasePulse extends Unit {
    private final Sprite sprite;
    private final GateCircuit circuit;

    /**
     * Creates a switch associated with a circuit.
     * @param circuit the owning circuit
     * @param keySprite the sprite used to represent the switch
     */
    public PhasePulse(GateCircuit circuit, Sprite keySprite) {
        if (circuit == null) {
            throw new IllegalArgumentException(
                "Circuit cannot be null");
        }
        this.circuit = circuit;
        this.sprite = new MarkedSprite(keySprite, String.valueOf(circuit.getId()));
    }

    /** Activates a pulse on the circuit wired to this phase pulse unit. */
    public void pulse() {
        this.circuit.pulse();
    }

    @Override 
    public Sprite getSprite() {
        return sprite;
    }

    /** Decorates the key sprite with the circuit identifier. */
    private static final class MarkedSprite implements Sprite {
        private final Sprite base;
        private final String mark;

        MarkedSprite(Sprite base, String mark) {
            this.base = base;
            this.mark = mark;
        }

        @Override
        public void draw(Graphics graphics, int x, int y, int width, int height) {
            base.draw(graphics, x, y, width, height);
            Color oldColor = graphics.getColor();
            graphics.setColor(Color.WHITE);
            graphics.drawString(mark, x + width / 2, y + height - 2);
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
