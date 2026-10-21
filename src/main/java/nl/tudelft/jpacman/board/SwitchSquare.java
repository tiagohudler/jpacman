package nl.tudelft.jpacman.board;

import java.awt.Color;
import java.awt.Graphics;
import nl.tudelft.jpacman.sprite.Sprite;

/** A persistent floor switch; Level is responsible for activating it. */
public final class SwitchSquare extends Square {
    private final int circuitId;
    private final Sprite sprite;

    /**
     * Creates a switch associated with a circuit.
     * @param circuitId the owning circuit identifier (1 or 2)
     * @param keySprite the sprite used to represent the switch
     */
    public SwitchSquare(int circuitId, Sprite keySprite) {
        if (circuitId != 1 && circuitId != 2) {
            throw new IllegalArgumentException(
                "Circuit id must be 1 or 2: " + circuitId);
        }
        this.circuitId = circuitId;
        this.sprite = new MarkedSprite(keySprite, String.valueOf(circuitId));
    }

    /** @return the identifier of the circuit controlled by this switch */
    public int getCircuitId() {
        return circuitId;
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return true;
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
            graphics.setColor(Color.BLACK);
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
