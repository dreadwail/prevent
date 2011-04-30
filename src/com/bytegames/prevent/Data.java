package com.bytegames.prevent;

import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * The in-memory data store for current game objects.
 * 
 * @author byte
 *
 */
public class Data {
    
    private static final long DISPENSE_DELAY = 1000;
    private static final int MAX_LIVES = 20;
    
    private static Logger LOG = Logger.getLogger(Data.class);

    private int _score;
    private int _lives;
    private String _mapFile;
    private Map<Sector, GamePiece> _gamePieces;
    private Sector _start;
    private Sector _finish;

    private int _waveNumber;
    private Map<Integer, Stack<Unit>> _waves;
    private LinkedList<Unit> _dispensedUnits;
    private long _lastDispensedTime;

    /**
     * Instantiates a new data store.
     * 
     * @param mapFile The map file to load.
     */
    public Data(String mapFile) {

        _score = 0;
        _lives = MAX_LIVES;
        _mapFile = mapFile;
        _gamePieces = new HashMap<Sector, GamePiece>();
        _waves = new HashMap<Integer, Stack<Unit>>();
        _dispensedUnits = new LinkedList<Unit>();
        _lastDispensedTime = System.currentTimeMillis();
        _waveNumber = 0;

    }
    
    /**
     * Identify if at this pass any units need to be sent out for play, and do so.
     */
    public void dispenseUnits() {
        
        int scale = Game.getScale();
        
        //determine if delay between waves has passed
        if(_lastDispensedTime + DISPENSE_DELAY < System.currentTimeMillis()) {

            _lastDispensedTime = System.currentTimeMillis();

            if(_waves.containsKey(_waveNumber) == false) {
                return;
            } else if(_waves.get(_waveNumber).size() > 0) {
                //there are still units in this wave to dispense
                Unit unit = _waves.get(_waveNumber).pop();
                Point start = new Point(getStartSector().x * scale, getStartSector().y * scale);
                unit.setLocation(start);
                unit.setDestination(start);
                _dispensedUnits.add(unit);
            } else if(_dispensedUnits.size() == 0) {
                //this wave is empty, and the units dispensed are finished. increment _waveNumber and put a delay until next wave
                _waveNumber++;
                _lastDispensedTime = System.currentTimeMillis() + (4 * DISPENSE_DELAY);
            }
            
        }

    }
    
    /**
     * @return Whether the loaded data has been run through completely.
     */
    public boolean unitsAllDone() {
        
        if(_waves.size() == 0 && getDispensedUnits().size() == 0) {
            return true;
        }
        
        return false;
        
    }
    
    /**
     * @return The number of lives remaining.
     */
    public int getLives() {
    	return _lives;
    }
    
    /**
     * @param lives The number of lives to set.
     */
    public void setLives(int lives) {
    	_lives = lives;
    }
    
    /**
     * @return A list of the units that have been put into play.
     */
    public LinkedList<Unit> getDispensedUnits() {
        return _dispensedUnits;
    }
    
    /**
     * @return The current mapping of sectors to the towers.
     */
    public Map<Sector, GamePiece> getGamePieces() {
        return _gamePieces;
    }
    
    /**
     * @return The sector marking the starting place for units.
     */
    public Sector getStartSector() {
        return _start;
    }
    
    /**
     * @return The sector marking the finish place for units.
     */
    public Sector getFinishSector() {
        return _finish;
    }
    
    /**
     * @return The current level.
     */
    public int getWaveNumber() {
        return _waveNumber;
    }

    /**
     * @param score The score to set.
     */
    public void setScore(int score) {
        _score = score;
        LOG.debug("Score is now " + _score);
    }
    
    /**
     * @return The game score.
     */
    public int getScore() {
        return _score;
    }
    
    /**
     * Loads a map from file.
     * @throws FileNotFoundException Exception thrown when map file is unavailable.
     */
    public void load() throws FileNotFoundException {
        
        LOG.debug("Loading data.");
        
        URL mapsDirURL = Main.class.getClassLoader().getResource(_mapFile);
        File mapsDirFile = new File(mapsDirURL.getFile());
        
        Scanner fin = new Scanner(mapsDirFile);

        parseWaves(fin);
        parseMapObjects(fin);

        LOG.debug("Finished loading data.");

    }
    
    private void parseMapObjects(Scanner fin) {
        
        int y = 0;

        while(fin.hasNextLine()) {
            
            String line = fin.nextLine();
            
            int x = 0;
            for(x = 0; x < line.length(); x++) {
                Sector sector = new Sector(x, y);
                char lineChar = line.charAt(x);
                switch(lineChar) {
                    case '1':
                        _gamePieces.put(sector, TowerFactory.getTower(TowerType.SMALLTURRET));
                        break;
                    case '2':
                        _gamePieces.put(sector, TowerFactory.getTower(TowerType.MEDIUMTURRET));
                        break;
                    case '3':
                        _gamePieces.put(sector, TowerFactory.getTower(TowerType.HEAVYTURRET));
                        break;
                    case 'X':
                        _gamePieces.put(sector, TerrainFactory.getTerrain(TerrainType.ROCK));
                        break;
                    case 'S':
                        _gamePieces.put(sector, TerrainFactory.getTerrain(TerrainType.ENTRY));
                        _start = sector;
                        break;
                    case 'F':
                        _gamePieces.put(sector, TerrainFactory.getTerrain(TerrainType.EXIT));
                        _finish = sector;
                        break;
                    case '.':
                        _gamePieces.put(sector, TerrainFactory.getTerrain(TerrainType.LAND));
                        break;
                }
            }
            y++;

        }
        
    }
    
    private void parseWaves(Scanner fin) {

        String wavesStringLine = fin.nextLine();
        String[] wavesString = wavesStringLine.split(":");
        
        int wave = 0;

        for(int i = 0; i < wavesString.length; i++) {
            
            Stack<Unit> thisWave = new Stack<Unit>();
            
            char[] waveUnitChars = wavesString[i].toCharArray();
            for(int j = 0; j < waveUnitChars.length; j++) {
                
            	Character waveUnitChar = waveUnitChars[j];
            	
            	int waveUnitLevel = Integer.parseInt(waveUnitChar.toString());
            	
            	switch(waveUnitLevel) {
            		default:
            		case 1:
            		    thisWave.push(UnitFactory.getUnit(UnitType.SOLDIER));
            			break;
            		case 2:
            		    thisWave.push(UnitFactory.getUnit(UnitType.HUMVEE));
            		    break;
            		case 3:
                        thisWave.push(UnitFactory.getUnit(UnitType.HUMVEE));
                        break;
            	}
            }
            
            _waves.put(wave, thisWave);

            wave++;

        }
        
    }

    /**
     * Draws the towers and units.
     * 
     * @param gfx Graphics object to use to draw with.
     */
    public void draw(Graphics gfx) {

        for(Sector sector : _gamePieces.keySet()) {
            int scale = Game.getScale();
            _gamePieces.get(sector).draw(gfx, new Point(sector.x * scale, sector.y * scale));
        }

        for (Iterator<Unit> it = _dispensedUnits.iterator(); it.hasNext(); ) {
            
            Unit unit = it.next();
            
            if (unit.isDone()) {
                it.remove();
            } else {
                unit.draw(gfx);
            }
        }
        
        
    }

}
