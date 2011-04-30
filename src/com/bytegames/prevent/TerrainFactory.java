package com.bytegames.prevent;

import org.apache.log4j.Logger;


/**
 * @author byte
 *
 * Constructs and returns requested terrain.
 */
public class TerrainFactory {
    
    private static Logger LOG = Logger.getLogger(TerrainFactory.class);

    /**
     * @param t The type of terrain requested.
     * @return The terrain.
     */
    public static Terrain getTerrain(TerrainType t) {

        switch(t) {
            case ENTRY:
                return new Terrain("opening.png", true);
            case EXIT:
                return new Terrain("opening.png", true);
            case LAND:
                return new Terrain("empty.png", true);
            case WATER:
                return new Terrain("water.png", false);
            case ROCK:
                return new Terrain("wall.png", false);
            default:
                LOG.error("Unable to instantiate requested terrain.");
                return null;
        }
        
    }

}
