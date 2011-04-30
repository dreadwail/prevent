package com.bytegames.prevent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.bytegames.prevent.Menu.MenuFunction;

/**
 * The game engine.
 * 
 * @author byte
 *
 */
@SuppressWarnings("serial")
public class Game extends JFrame {

    private static final int DISPLAY_BUFFERS = 2;
    private static final String IMAGES_DIR = "images";
    private static final String AUDIO_DIR = "audio";
    //private static final String MAPS_DIR = "maps";
    private static final int SECTOR_CAPACITY_X = 40;
    private static final int SECTOR_CAPACITY_Y = 25;
    private static final int START_SCORE = 20;
    
    /**
     * The name of the game.
     */
    public static final String GAME_TITLE = "PREVENT";

    private static ResourceCache _cache;
    private static Logger LOG = Logger.getLogger(Game.class);
    /**
     * Configuration settings.
     */
    public static XMLConfiguration config;

    private Sector _hoverSector;
    private Color _hoverColor;
    private GraphicsDevice _graphicsDevice;

    private Data _data;
    private Input _input;
    private AI _ai;
    
    private Menu _menu;
    private Menu _levelMenu;
    private Menu _contextMenu;

    private GameState _state;
    private boolean _won;
    private boolean _lost;
    private boolean _exit;

    /**
     * Instantiates a new game engine.
     */
    public Game() {
        
        try {
            config = new XMLConfiguration("config/config.xml");
        } catch (ConfigurationException ex) {
            LOG.fatal("Fatal Error: Unable to read required configuration file.");
            System.exit(1);
        }
        
        _hoverSector = new Sector(0, 0);
        
        initDisplay();
        
        _cache = new ResourceCache(IMAGES_DIR, AUDIO_DIR);
        try {
            _cache.load();
        } catch(IOException ex) {
            LOG.fatal("Fatal Error: ResourceCache reports unreadable required files.");
            System.exit(1);
        }

        _cache.scaleImages();
        
        _input = new Input(this);
        
        addKeyListener(_input);
        addMouseListener(_input);
        addMouseMotionListener(_input);

        _menu = generateMainMenu();
        _levelMenu = generateLevelMenu();
        _contextMenu = generateContextMenu();

        _state = GameState.MENU;
        _exit = false;

        _ai = new AI(this);

    }
    
    private Menu generateContextMenu() {
        return new ContextMenu(this, new Font("SansSerif", Font.BOLD, getScale() / 2));
    }
    
    private Menu generateMainMenu() {
        
        Menu menu = new Menu(this, new Font("SansSerif", Font.BOLD, getScale()));
        
        menu.addMenuEntry(0, "New Game", new MenuFunction() {
            public void run(Game game) {
                game.setGameState(GameState.LEVELMENU);
            }
        });

        menu.addMenuEntry(1, "Credits", new MenuFunction() {
            public void run(Game game) {
                game.credits();
            }
        });

        menu.addMenuEntry(2, "Exit", new MenuFunction() {
            public void run(Game game) {
                game.exit();
            }
        });
        
        return menu;
        
    }
    
    private Menu generateLevelMenu() {
        
        Menu menu = new Menu(this, new Font("SansSerif", Font.BOLD, getScale()));
        
        menu.addMenuEntry(0, "Level 1", new MenuFunction() {
            public void run(Game game) {
                String map = "maps/level1.map";
                int difficulty = 1;
                game.newGame(map, difficulty);
            }
        });

        menu.addMenuEntry(1, "Level 2", new MenuFunction() {
            public void run(Game game) {
                String map = "maps/level2.map";
                int difficulty = 1;
                game.newGame(map, difficulty);
            }
        });
        
        menu.addMenuEntry(2, "Level 3", new MenuFunction() {
            public void run(Game game) {
                String map = "maps/level3.map";
                int difficulty = 1;
                game.newGame(map, difficulty);
            }
        });
        
        menu.addMenuEntry(3, "Main Menu", new MenuFunction() {
            public void run(Game game) {
                game.setGameState(GameState.MENU);
            }
        });
        
        return menu;
        
    }
    
    /**
     * @return The AI component for the game.
     */
    public AI getAI() {
        return _ai;
    }

    /**
     * Starts a new game.
     * @param map The name of the map to load.
     * @param difficulty The difficulty to use.
     */
    public void newGame(String map, int difficulty) {
    	
    	_data = new Data(map); //TODO: read from menu.
        _data.setScore(START_SCORE);
        try {
            _data.load();
        } catch (FileNotFoundException e) {
            LOG.fatal("Fatal Error: Unable to load map: " + map);
            System.exit(1);
        }
        
        _state = GameState.GAME;

    }
    
    /**
     * @return The current game state.
     */
    public GameState getGameState() {
    	return _state;
    }
    
    /**
     * @param state The game state to set.
     */
    public void setGameState(GameState state) {
    	_state = state;
    }
    
    /**
     * @param sector The sector the mouse is currently in.
     * @param color The color to set on the hover.
     */
    public void setHoverSector(Sector sector, Color color) {
        _hoverSector = sector;
        _hoverColor = color;
    }
    
    /**
     * Tells the game to display the credits state.
     */
    public void credits() {
        _state = GameState.CREDITS;
    }
    
    /**
     * @return The current data store.
     */
    public Data getData() {
        return _data;
    }

    /**
     * @return The resource cache.
     */
    public static ResourceCache getCache() {
        return _cache;
    }
    
    /**
     * @return The scale in pixels based on the screen resolution.
     */
    public static int getScale() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int xScale = screenSize.width / SECTOR_CAPACITY_X;
        int yScale = screenSize.height / SECTOR_CAPACITY_Y;
        int scale = (xScale < yScale ? xScale : yScale);

        return scale;

    }

    private void initDisplay() {
        
        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        _graphicsDevice = ge.getDefaultScreenDevice();
        _graphicsDevice.setFullScreenWindow(this);

        if (_graphicsDevice.isFullScreenSupported()) {
            createBufferStrategy(DISPLAY_BUFFERS);
        } else {
            LOG.fatal("Full-screen not supported.");
            System.exit(1);
        }

    }
    
    /**
     * The game loop.
     */
    public void run() {

        LOG.debug("Starting main game loop.");

        while(_exit == false) {
            
            BufferStrategy bufferStrategy = getBufferStrategy();
            Graphics gfx = bufferStrategy.getDrawGraphics();
            Graphics2D gfx2d = (Graphics2D)gfx;
            
            renderBackground(gfx);
            
            if(_state == GameState.MENU) {
                
                renderMenu(gfx2d);
            
            } else if(_state == GameState.LEVELMENU) {
                
                renderLevelMenu(gfx);
                
            } else if(_state == GameState.CREDITS) {
                
                renderCredits(gfx);
                
            } else {

                if(hasWon()) {

                    renderWin();
                    
                } else if(hasLost()) {

                    renderLoss();
                    
                } else {

                    _data.dispenseUnits();
                    moveUnits();
                    weaponsFire();
                
                    if(getData().unitsAllDone()) {
                        won();
                    }
                }
                
                renderGame(gfx);

            }

            gfx.dispose();
            bufferStrategy.show();
            
            try {
                Thread.sleep(10);
            } catch(InterruptedException ex) {
                LOG.debug("Warning: Another thread has interrupted the main game loop.");
            }
        }
        
        LOG.debug("Exit detected. Terminating game loop.");
    
    }
    
    /**
     * Win the game.
     */
    public void won() {
        _won = true;
        _lost = false;
    }
    
    /**
     * Lose the game.
     */
    public void lost() {
        _lost = true;
        _won = false;
    }
    
    private void renderWin() {
        
    }
    
    private void renderLoss() {
        
    }
    
    //TODO: weapons fire
    private void weaponsFire() {
    	
        //ideas...
    	//loop over all units. if adjacent sector to one currently occupied contains tower, make tower fire upon unit. 
        //make tower focus on one unit at a time adjacent
    	
    }

    private boolean hasWon() {
        return _won;
    }

    private boolean hasLost() {
        return _lost;
    }
    
    /**
     * Based on the current optimal path, set each units next destination point.
     */
    public void moveUnits() {

        for(Unit unit : getData().getDispensedUnits()) {
            
            if(unit.isDone())
                continue;
            
            if(_data.getFinishSector().equals(unit.getContainingSector())) {
                unit.setDone(true);
                int lives = _data.getLives();
                _data.setLives(lives - 1);
                if(_data.getLives() == 0) {
                    lost();
                    return;
                }
                continue;
            }
            
            //are we there yet?
            if(unit.getLocation().equals(unit.getDestination())) {
                LinkedList<Point> optimalPath = _ai.getOptimalPath(unit.getContainingSector(), getData().getFinishSector());
                
                if(optimalPath == null) {
                	LOG.warn("Warning: no optimal path found for unit.");
                } else {
                
                	Point newDestination = optimalPath.peek();
                
                	//do we need a new destination?
                	if(unit.getDestination().equals(newDestination) == false) {
                		unit.setDestination(newDestination);
                	}
                }
            }

            moveUnit(unit);
        }

    }

    private void moveUnit(Unit unit) {
        
        //move the unit towards its destination, based on speed (scale/speed), being careful not to overmove
        int speed = unit.getSpeed();
        
        Point current = unit.getLocation();
        Point destination = unit.getDestination();
        
        int movementRequiredX = destination.x - current.x;
        int movementRequiredY = destination.y - current.y;
        
        int proposedX = current.x;
        int proposedY = current.y;
        
        if(movementRequiredX < 0) {
            proposedX = current.x - speed;
            if(proposedX < destination.x) {
                proposedX = destination.x;
            }
        } else if(movementRequiredX > 0) {
            proposedX = current.x + speed;
            if(proposedX > destination.x) {
                proposedX = destination.x;
            }
        }
        
        if(movementRequiredY < 0) {
            proposedY = current.y - speed;
            if(proposedY < destination.y) {
                proposedY = destination.y;
            }
        } else if(movementRequiredY > 0) {
            proposedY = current.y + speed;
            if(proposedY > destination.y) {
                proposedY = destination.y;
            }
        }
        
        unit.setLocation(new Point(proposedX, proposedY));
        
    }
    
    private void renderMenu(Graphics2D gfx2d) {
        
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

        Font titleFont = new Font("SansSerif", Font.BOLD, Game.getScale() * 4);
        Point titleDrawPoint = new Point(screenDimension.width / 2, screenDimension.height / 4);
        FontRenderContext titleFontContext = gfx2d.getFontRenderContext();
        Rectangle2D titleBounds2D = titleFont.getStringBounds(Game.GAME_TITLE, titleFontContext);
        Rectangle titleBounds = titleBounds2D.getBounds();
        int horizontalOffset = (int)titleBounds.getWidth() / 2;
        Point titleOffsetPoint = new Point(titleDrawPoint.x - horizontalOffset, titleDrawPoint.y);

        gfx2d.setFont(titleFont);
        
        gfx2d.setColor(Color.BLACK);
        gfx2d.drawString(Game.GAME_TITLE, titleOffsetPoint.x + 5, titleOffsetPoint.y + 5); //TODO: get rid of these magic numbers
        gfx2d.setColor(Color.WHITE);
        gfx2d.drawString(Game.GAME_TITLE, titleOffsetPoint.x, titleOffsetPoint.y);

        Point center = new Point(screenDimension.width / 2, screenDimension.height / 2);
        _menu.setDrawPosition(center);
        _menu.setVisible(true);
        _menu.draw(gfx2d);
        
    }
    
    private void renderLevelMenu(Graphics gfx) {
        
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        Point center = new Point(screenDimension.width / 2, screenDimension.height / 2);
        _levelMenu.setDrawPosition(center);
        _levelMenu.setVisible(true);
        _levelMenu.draw(gfx);
        
    }
    
    private void renderBackground(Graphics gfx) {
        
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        gfx.setColor(Color.DARK_GRAY);
        gfx.fillRect(0, 0, screenDimension.width,screenDimension.height);
        
    }
    
    private void renderCredits(Graphics gfx) {

        String[] credits = StringEscapeUtils.unescapeJava(config.getString("strings.credits")).split("\n");

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        Point tempPoint = new Point(20, screenDimension.height / 2);
        
        Graphics2D gfx2d = (Graphics2D)gfx;
        
        Font font = new Font("SansSerif", Font.PLAIN, getScale());
        int fontHeight = gfx2d.getFontMetrics(font).getHeight();

        gfx2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gfx2d.setFont(font);
        gfx2d.setColor(Color.WHITE);
        
        for(String credit : credits) {
            gfx2d.drawString(credit, tempPoint.x, tempPoint.y);
            tempPoint.y += fontHeight;
        }
        
    }
    
    private void renderGame(Graphics gfx) {
        
        _data.draw(gfx);
        renderHover(gfx);
        _contextMenu.draw(gfx);

        renderStats((Graphics2D)gfx);
    }
    
    private void renderHover(Graphics gfx) {
        
        if(_contextMenu.isVisible() == false) {
            int scale = Game.getScale();
            int sectorX = scale * _hoverSector.x;
            int sectorY = scale * _hoverSector.y;
            int width = getScale();
            int height = getScale();
            gfx.setColor(_hoverColor);
            gfx.drawRect(sectorX, sectorY, width, height);
        }
        
    }
    
    private void renderStats(Graphics2D gfx2d) {
    
    	Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    	Point scorePoint = new Point((screenDimension.width / 2), screenDimension.height - (2 * getScale()));

        gfx2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gfx2d.setFont(new Font("SansSerif", Font.PLAIN, getScale()));
        gfx2d.setColor(Color.WHITE);
        
        String stats = "Score: " + _data.getScore() + " Lives: " + _data.getLives();
        
        gfx2d.drawString(stats, scorePoint.x, scorePoint.y);
        
    }

    /**
     * Terminate game.
     */
    public void exit() {
        _exit = true;
    }
    
    /**
     * @return The context menu object.
     */
    public Menu getContextMenu() {
        return _contextMenu;
    }
    
    /**
     * @return The menu object.
     */
    public Menu getMenu() {
    	return _menu;
    }
    
    /**
     * @return The level menu object.
     */
    public Menu getLevelMenu() {
        return _levelMenu;
    }
    
}
