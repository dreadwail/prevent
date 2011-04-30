package com.bytegames.prevent;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;


/**
 * Represents a stationary tower.
 * 
 * @author byte
 *
 */
public class Tower implements GamePiece {

    private String _name;
    private Sprite _sprite;
    private int _dps;
    private int _cost;
    private boolean _traversable;
    private LinkedList<TowerType> _upgradeTypes;
    
    /**
     * @param name The name of this tower.
     * @param sprite The sprite for the tower.
     * @param dps How much damage the tower does per second.
     * @param cost The cost to obtain this tower.
     * @param traversable Whether this tower is traversable or not.
     */
    public Tower(String name, String sprite, int dps, int cost, boolean traversable) {

        _name = name;
        _sprite = Game.getCache().getSprite(sprite);
        _dps = dps;
        _cost = cost;
        _traversable = traversable;
        
        _upgradeTypes = new LinkedList<TowerType>();

    }
    
    /**
     * @return The sprite for this tower.
     */
    public Sprite getSprite() {
        return _sprite;
    }
    
    /**
     * @param upgrade The tower to upgrade this one to.
     */
    public void upgrade(Tower upgrade) {
        //TODO: this method is stupid... its just a copy.
        _name = upgrade.getName();
        _sprite = upgrade.getSprite();
        _dps = upgrade.getDps();
        _cost = upgrade.getCost();
        _traversable = upgrade.isTraversable();
        _upgradeTypes = upgrade.getUpgradeTowerTypes();
    }
    
    /**
     * @param t Adds a tower to the list of possible upgrades for this tower.
     */
    public void addUpgradeTowerType(TowerType t) {
        _upgradeTypes.add(t);
    }
    
    /**
     * @return A list of all towers upgradeable from this one.
     */
    public LinkedList<TowerType> getUpgradeTowerTypes() {
        return _upgradeTypes;
    }
    
    /**
     * @param name The name of this tower.
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * @param cost The cost of this tower.
     */
    public void setCost(int cost) {
        _cost = cost;
    }
    
    /**
     * @param dps The amount of damage per time that this tower does.
     */
    public void setDps(int dps) {
        _dps = dps;
    }

    /**
     * @param sprite The sprite to assign to this tower.
     */
    public void setSprite(Sprite sprite) {
        _sprite = sprite;
    }
    
    /**
     * @return The name of this tower.
     */
    public String getName() {
        return _name;
    }
    
    /**
     * @return The cost to obtain this tower.
     */
    public int getCost() {
        return _cost;
    }
    
    /**
     * @return How much damage the tower does per second.
     */
    public int getDps() {
        return _dps;
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
