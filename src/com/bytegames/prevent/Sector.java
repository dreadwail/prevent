package com.bytegames.prevent;

import java.awt.Point;


/**
 * Represents an x,y coordinate specifying one tile in a grid.
 * 
 * @author byte
 *
 */
@SuppressWarnings("serial")
public class Sector extends Point {
    /**
     * @param x Sector horizontal coordinate.
     * @param y Sector vertical coordinate.
     */
    public Sector(int x, int y) {
        this.x = x;
        this.y = y;
    }
}