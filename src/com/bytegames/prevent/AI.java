package com.bytegames.prevent;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Encapsulates the AI for the game.
 * 
 * @author byte
 *
 */
public class AI {
    
    private static Logger LOG = Logger.getLogger(AI.class);
    
    private Game _game;
    private Map<Sector, LinkedList<Point>> _optimalPathCache;
    
    /**
     * @param game The game object to manipulate.
     */
    public AI(Game game) {
        _game = game;
        _optimalPathCache = new HashMap<Sector, LinkedList<Point>>();
    }
    
    /**
     * Called when something about the current map has changed state, and optimal paths need recalculating.
     */
    public void invalidateCache() {
        LOG.debug("Game map state changed. AI cache invalidated.");
        _optimalPathCache.clear();
    }
    
    /**
     * @param startSector The sector to start from.
     * @param finishSector The sector to finish at.
     * @return A list of pixel points representing the optimal path from this sector to the finish, or null if no path exists.
     */
    public LinkedList<Point> getOptimalPath(Sector startSector, Sector finishSector) {

        if(_optimalPathCache.containsKey(startSector)) {
            return _optimalPathCache.get(startSector);
        }
        
        int scale = Game.getScale();
        LinkedList<Point> optimalPath = new LinkedList<Point>();
        
        if(startSector.equals(finishSector)) {
            optimalPath.add(new Point(finishSector.x * scale, finishSector.y * scale));
            return optimalPath;
        }
        
        Map<Sector, Integer> sectorMap = new HashMap<Sector, Integer>();
        sectorMap.put(finishSector, 0);
        
        Map<Sector, Integer> lastPass = new HashMap<Sector, Integer>();
        lastPass.put(finishSector, 0);
        
        boolean foundStart = false;
        while(foundStart == false) {

        	LOG.debug("Start not yet found. Last pass size: " + lastPass.size());
        	
            Map<Sector, Integer> toCheck = new HashMap<Sector, Integer>();
            toCheck.putAll(lastPass);
            lastPass.clear();

            for(Sector sector : toCheck.keySet()) {
                Map<Sector, Integer> newAdjacents = getNewAdjacents(sector, sectorMap);
                sectorMap.putAll(newAdjacents);
                lastPass.putAll(newAdjacents);
            }
            
            LOG.debug("After getting new adjacents: sectorMap size: " + sectorMap.size() + ", lastPass: " + lastPass.size());
            
            if(lastPass.size() == 0) {
            	return null;
            } else if(lastPass.containsKey(startSector)) {
                foundStart = true;
            }

        }

        Sector current = startSector;
        while(current.equals(finishSector) == false) {

            Sector left = AI.getLeft(current);
            Sector right = AI.getRight(current);
            Sector up = AI.getUp(current);
            Sector down = AI.getDown(current);
            
            int bestCounter = sectorMap.get(current);
            Sector best = null;

            if(sectorMap.containsKey(left)) {
                int counter = sectorMap.get(left);
                if(counter < bestCounter) {
                    bestCounter = counter;
                    best = left;
                }
            }
            
            if(sectorMap.containsKey(right)) {
                int counter = sectorMap.get(right);
                if(counter < bestCounter) {
                    bestCounter = counter;
                    best = right;
                }
            }
            
            if(sectorMap.containsKey(up)) {
                int counter = sectorMap.get(up);
                if(counter < bestCounter) {
                    bestCounter = counter;
                    best = up;
                }
            }
            
            if(sectorMap.containsKey(down)) {
                int counter = sectorMap.get(down);
                if(counter < bestCounter) {
                    bestCounter = counter;
                    best = down;
                }
            }
            
            int optimalPointX = best.x * scale;
            int optimalPointY = best.y * scale;
            Point optimalPoint = new Point(optimalPointX, optimalPointY);
            
            optimalPath.add(optimalPoint);
            
            current = best;
            
        }

        _optimalPathCache.put(startSector, optimalPath);
        
        return optimalPath;
        
    }
    
    private Map<Sector, Integer> getNewAdjacents(Sector sector, Map<Sector, Integer> sectorMap) {
        
        Map<Sector, Integer> adjacentSectorMap = new HashMap<Sector, Integer>();
        
        int sectorCounter = sectorMap.get(sector);
        int adjacentsCounter = sectorCounter + 1;
        
        Sector left = AI.getLeft(sector);
        Sector right = AI.getRight(sector);
        Sector up = AI.getUp(sector);
        Sector down = AI.getDown(sector);
        
        Map<Sector, GamePiece> pieces = _game.getData().getGamePieces();
        
        if(pieces.containsKey(left) && pieces.get(left).isTraversable()) {
            if((sectorMap.containsKey(left) == false) || (sectorMap.containsKey(left) && sectorMap.get(left) > adjacentsCounter)) {
                adjacentSectorMap.put(left, adjacentsCounter);
            }
        }
        
        if(pieces.containsKey(right) && pieces.get(right).isTraversable()) {
            if((sectorMap.containsKey(right) == false) || (sectorMap.containsKey(right) && sectorMap.get(right) > adjacentsCounter)) {
                adjacentSectorMap.put(right, adjacentsCounter);
            }
        }
        
        if(pieces.containsKey(up) && pieces.get(up).isTraversable()) {
            if((sectorMap.containsKey(up) == false) || (sectorMap.containsKey(up) && sectorMap.get(up) > adjacentsCounter)) {
                adjacentSectorMap.put(up, adjacentsCounter);
            }
        }
        
        if(pieces.containsKey(down) && pieces.get(down).isTraversable()) {
            if((sectorMap.containsKey(down) == false) || (sectorMap.containsKey(down) && sectorMap.get(down) > adjacentsCounter)) {
                adjacentSectorMap.put(down, adjacentsCounter);
            }
        }
        
        return adjacentSectorMap;
        
    }
    
    private static Sector getLeft(Sector sector) {
        return new Sector(sector.x - 1, sector.y);
    }
    
    private static Sector getRight(Sector sector) {
        return new Sector(sector.x + 1, sector.y);
    }
    
    private static Sector getUp(Sector sector) {
        return new Sector(sector.x, sector.y - 1);
    }
    
    private static Sector getDown(Sector sector) {
        return new Sector(sector.x, sector.y + 1);
    }



}
