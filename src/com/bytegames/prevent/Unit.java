package com.bytegames.prevent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

//import org.apache.log4j.Logger;

/**
 * Represents a unit that attempts to navigate to a specified destination.
 * 
 * @author byte
 *
 */
public class Unit {
    
    private static final int HEALTH_BAR_HEIGHT_DIVISOR = 15;

    private String _name;
    private Sprite _sprite;
    private int _speed;
    private int _startingHealth;
    private int _health;
    private Point _location;
    private Point _destination;
    private boolean _done;
    
    //private static Logger LOG = Logger.getLogger(Unit.class);
    
    /**
     * Instantiate a new unit.
     * 
     * @param name The name of the unit.
     * @param spriteResourceName The resource used to represent this unit.
     * @param health The initial health level of this unit.
     * @param speed The initial speed level of this unit. Lower number means faster.
     */
    public Unit(String name, String spriteResourceName, int health, int speed) {

        _name = name;
        _sprite = Game.getCache().getSprite(spriteResourceName);
        _health = _startingHealth = health;
        setSpeed(speed); //clamp speed
        _location = new Point(0,0);
        _destination = new Point(0,0);
        _done = false;
    }
    
    /**
     * @return Whether or not this unit is still in play.
     */
    public boolean isDone() {
        return _done;
    }
    
    /**
     * @param done Whether or not this unit is still in play.
     */
    public void setDone(boolean done) {
        _done = done;
    }

    /**
     * @return The name of this unit.
     */
    public String getName() {
        return _name;
    }
    
    /**
     * @return This units current location.
     */
    public Point getLocation() {
        return _location;
    }
    
    /**
     * @return The sector this unit is currently in, based on the current game scale.
     */
    public Sector getContainingSector() {
        int scale = Game.getScale();
        int sectorX = _location.x / scale;
        int sectorY = _location.y / scale;
        return new Sector(sectorX, sectorY);
    }
    
    /**
     * @param point The location to place this unit.
     */
    public void setLocation(Point point) {
        _location = point;
    }
    
    /**
     * @return The destination point of this unit.
     */
    public Point getDestination() {
        return _destination;
    }
    
    /**
     * @param point The new destination point of this unit.
     */
    public void setDestination(Point point) {
        _destination = point;
    }
    
    /**
     * @param speed The speed for the unit. 
     */
    public void setSpeed(int speed) {
        if(speed < 1)
            speed = 1;
        if(speed > Game.getScale()) 
            speed = Game.getScale();
        _speed = speed;
    }
    
    /**
     * @return The current speed of the unit. 
     */
    public int getSpeed() {
        return _speed;
    }
    
    /**
     * @return The current health level of the unit.
     */
    public int getHealth() {
        return _health;
    }
    
    /**
     * @param gfx The graphics object used to draw with.
     */
    public void draw(Graphics gfx) {
        
        _sprite.draw(gfx, _location);

        int scale = Game.getScale();
        
        int healthBarHeight = scale / HEALTH_BAR_HEIGHT_DIVISOR;
        healthBarHeight = (healthBarHeight == 0 ? 1 : healthBarHeight);
        
        double healthBarWidth = (_health / _startingHealth) * scale;
        
        Point barDrawLocation = new Point(_location.x, _location.y - healthBarHeight);
        
        gfx.setColor(Color.RED);        
        gfx.fillRect(barDrawLocation.x, barDrawLocation.y, (int)healthBarWidth, healthBarHeight);

    }
    
}
