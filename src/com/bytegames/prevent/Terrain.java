package com.bytegames.prevent;

import java.awt.Graphics;
import java.awt.Point;


/**
 * @author byte
 *
 */
public class Terrain implements GamePiece {
    
    private Sprite _sprite;
    private boolean _traversable;
    
    /**
     * @param sprite The sprite for the terrain.
     * @param traversable Whether this terrain is traversable or not.
     */
    public Terrain(String sprite, boolean traversable) {
        _sprite = Game.getCache().getSprite(sprite);
        _traversable = traversable;
    }
    
    /**
     * @return The sprite for this terrain.
     */
    public Sprite getSprite() {
        return _sprite;
    }

    /**
     * @param sprite The sprite to assign to this terrain.
     */
    public void setSprite(Sprite sprite) {
        _sprite = sprite;
    }

    @Override
    public boolean isTraversable() {
        return _traversable;
    }

    @Override
    public void setTraversable(boolean traversable) {
        _traversable = traversable;
    }

    @Override
    public void draw(Graphics gfx, Point p) {
        _sprite.draw(gfx, p);
    }

}
