package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.Job2dDriver;

/**
 * A decorator that wraps any {@link Job2dDriver} and counts distance travelled
 * (all moves) and distance drawn (ink/filament usage).
 */
public class UsageTrackingDriverDecorator implements Job2dDriver {

    private final Job2dDriver delegate;
    private final String label;

    private int lastX = 0;
    private int lastY = 0;
    private double travelDistance = 0.0;
    private double drawingDistance = 0.0;

    /**
     * Wraps a driver to track its usage.
     *
     * @param delegate The driver to wrap and track.
     * @param label    A name to identify this driver.
     */
    public UsageTrackingDriverDecorator(Job2dDriver delegate, String label) {
        this.delegate = delegate;
        this.label = label;
    }

    /**
     * Repositions without drawing. Counts as travel distance.
     *
     * @param x The target X coordinate.
     * @param y The target Y coordinate.
     */
    @Override
    public void setPosition(int x, int y) {
        registerMovement(x, y, false);
        delegate.setPosition(x, y);
        updatePosition(x, y);
    }

    /**
     * Draws a line to the target position. Counts as both travel and drawing distance.
     *
     * @param x The target X coordinate.
     * @param y The target Y coordinate.
     */
    @Override
    public void operateTo(int x, int y) {
        registerMovement(x, y, true);
        delegate.operateTo(x, y);
        updatePosition(x, y);
    }

    /**
     * Accumulates distance from the last position.
     *
     * @param x       The target X coordinate.
     * @param y       The target Y coordinate.
     * @param drawing {@code true} if this is a drawing operation; {@code false} for repositioning.
     */
    private void registerMovement(int x, int y, boolean drawing) {
        double segment = Math.hypot(x - lastX, y - lastY);
        travelDistance += segment;
        if (drawing) {
            drawingDistance += segment;
        }
    }

    /**
     * Updates the current position for the next distance calculation.
     *
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     */
    private void updatePosition(int x, int y) {
        this.lastX = x;
        this.lastY = y;
    }

    /**
     * Returns the total distance travelled (all moves, including draws).
     *
     * @return Travel distance in units.
     */
    public double getTravelDistance() {
        return travelDistance;
    }

    /**
     * Returns the total distance drawn (drawing operations only).
     *
     * @return Drawing distance in units (ink/filament usage).
     */
    public double getDrawingDistance() {
        return drawingDistance;
    }

    /**
     * Returns the label assigned to this decorated driver.
     *
     * @return The label string.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Resets both counters to zero.
     */
    public void reset() {
        travelDistance = 0.0;
        drawingDistance = 0.0;
        lastX = 0;
        lastY = 0;
    }

    @Override
    public String toString() {
        return String.format("%s [tracked]", delegate.toString());
    }
}
