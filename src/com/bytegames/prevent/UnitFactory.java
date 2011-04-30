package com.bytegames.prevent;

import org.apache.log4j.Logger;


/**
 * @author byte
 *
 * Constructs units on demand.
 */
public class UnitFactory {

    private static Logger LOG = Logger.getLogger(UnitFactory.class);
    
    /**
     * @param type The type of unit to retrieve.
     * @return A unit.
     */
    public static Unit getUnit(UnitType type) {
        
        switch(type) {
            case SOLDIER:
                return getSoldier();
            case HUMVEE:
                return getHumvee();
            case TANK:
                return getTank();
            default:
                LOG.error("Unable to instantiate requested unit.");
                return null;
        }
        
    }
    
    private static Unit getSoldier() {
        
        String spriteFile = Game.config.getString("units.soldier[@sprite]");
        int health = Game.config.getInt("units.soldier[@health]");
        int speed = Game.config.getInt("units.soldier[@speed]");

        LOG.debug("Soldier unit instantiated and returned.");
        
        return new Unit("Soldier", spriteFile, health, speed);
    }
    
    private static Unit getHumvee() {

        String spriteFile = Game.config.getString("units.humvee[@sprite]");
        int health = Game.config.getInt("units.humvee[@health]");
        int speed = Game.config.getInt("units.humvee[@speed]");
        
        LOG.debug("Humvee unit instantiated and returned.");

        return new Unit("Humvee", spriteFile, health, speed);
    }
    
    private static Unit getTank() {

        String spriteFile = Game.config.getString("units.tank[@sprite]");
        int health = Game.config.getInt("units.tank[@health]");
        int speed = Game.config.getInt("units.tank[@speed]");
        
        LOG.debug("Tank unit instantiated and returned.");

        return new Unit("Tank", spriteFile, health, speed);
    }
    
}
