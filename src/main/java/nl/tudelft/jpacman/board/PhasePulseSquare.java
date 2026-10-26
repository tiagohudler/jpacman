package nl.tudelft.jpacman.board;

import java.awt.Color;
import java.awt.Graphics;

import nl.tudelft.jpacman.level.GateCircuit;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.sprite.Sprite;

public class PhasePulseSquare extends Square {
    private final Sprite sprite;
    private final GateCircuit circuit;

    /**
     * Creates a switch associated with a circuit.
     * @param circuit the owning circuit
     * @param keySprite the sprite used to represent the switch
     */
    public PhasePulseSquare(GateCircuit circuit, Sprite keySprite) {
        if (circuit == null) {
            throw new IllegalArgumentException(
                "Circuit cannot be null");
        }
        this.circuit = circuit;
        this.sprite = new MarkedSprite(keySprite, String.valueOf(circuit.getId()));
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return true;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    void put(Unit occupant) {
        super.put(occupant);

        if (occupant instanceof Player) {
            this.circuit.pulse();
        }
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
