package com.bytegames.prevent;

import org.apache.log4j.Logger;

/**
 * Constructs requested towers.
 * 
 * @author byte
 *
 */
public class TowerFactory {

    private static Logger LOG = Logger.getLogger(TowerFactory.class);
    
    /**
     * @param t The type of tower requested.
     * @return The tower.
     */
    public static Tower getTower(TowerType t) {

        switch(t) {
            case SMALLTURRET:
                return getLightTurretTower();
            case MEDIUMTURRET:
                return getMediumTurretTower();
            case HEAVYTURRET:
                return getHeavyTurretTower();
            default:
                LOG.error("Unable to instantiate requested tower.");
                return null;
        }
        
    }
    
    private static Tower getLightTurretTower() {
        
        String spriteFile = Game.config.getString("towers.light[@sprite]");
        int dps = Game.config.getInt("towers.light[@dps]");
        int cost = Game.config.getInt("towers.light[@cost]");
        boolean traversable = Game.config.getBoolean("towers.light[@traversable]");

        Tower smallTurretTower = new Tower("Small Turret", spriteFile, dps, cost, traversable);
        smallTurretTower.getUpgradeTowerTypes().add(TowerType.MEDIUMTURRET);
        
        LOG.debug("Light tower instantiated and returned.");
        
        return smallTurretTower;
    }
    
    private static Tower getMediumTurretTower() {
        
        String spriteFile = Game.config.getString("towers.medium[@sprite]");
        int dps = Game.config.getInt("towers.medium[@dps]");
        int cost = Game.config.getInt("towers.medium[@cost]");
        boolean traversable = Game.config.getBoolean("towers.medium[@traversable]");

        Tower mediumTurretTower = new Tower("Medium Turret", spriteFile, dps, cost, traversable);
        mediumTurretTower.getUpgradeTowerTypes().add(TowerType.HEAVYTURRET);
        
        LOG.debug("Medium tower instantiated and returned.");
        
        return mediumTurretTower;
    }
    
    private static Tower getHeavyTurretTower() {
        
        String spriteFile = Game.config.getString("towers.heavy[@sprite]");
        int dps = Game.config.getInt("towers.heavy[@dps]");
        int cost = Game.config.getInt("towers.heavy[@cost]");
        boolean traversable = Game.config.getBoolean("towers.heavy[@traversable]");

        Tower heavyTurretTower = new Tower("Heavy Turret", spriteFile, dps, cost, traversable);
        
        LOG.debug("Heavy tower instantiated and returned.");
        
        return heavyTurretTower;
    }

}
