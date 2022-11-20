package main;

import fileio.Coordinates;

public final class Position {
    private int x, y;

    /**
     * Constructor for Position class
     * @param coordinates contains the coordinates information extracted from input
     */
    public Position(final Coordinates coordinates) {
        if (coordinates != null) {
            this.x = coordinates.getX();
            this.y = coordinates.getY();
        }
    }

    /**
     * Getter for x member
     * @return value of x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Setter for x member
     * @param x int number
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Getter for y member
     * @return value of y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for y member
     * @param y int number
     */
    public void setY(final int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{"
                + "x="
                + x
                + ", y="
                + y
                + '}';
    }
}
