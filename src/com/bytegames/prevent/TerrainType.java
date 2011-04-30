package com.bytegames.prevent;


/**
 * @author byte
 *
 * Represents the different types of terrain available.
 */
public enum TerrainType {
    /**
     * The entry to the map.
     */
    ENTRY,
    /**
     * The exit from the map.
     */
    EXIT,
    /**
     * Traversable land.
     */
    LAND,
    /**
     * Non-traversable water.
     */
    WATER,
    /**
     * Non-traversable obstruction.
     */
    ROCK
}
