package com.bytegames.prevent;

import java.awt.Graphics;
import java.awt.Point;


/**
 * @author byte
 *
 * Interface for varying game pieces.
 */
public interface GamePiece {

    /**
     * @return Whether or not the piece is traversable.
     */
    boolean isTraversable();
    /**
     * @param traversable Whether or not the piece is traversable.
     */
    void setTraversable(boolean traversable);
    /**
     * Draw the piece.
     * @param gfx The graphics object to draw with.
     * @param p The top left coordinate point to draw at.
     */
    void draw(Graphics gfx, Point p);
    
}
