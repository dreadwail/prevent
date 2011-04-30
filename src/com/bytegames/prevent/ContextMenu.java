package com.bytegames.prevent;

import java.awt.Font;
import java.awt.Graphics;
import org.apache.log4j.Logger;

/**
 * @author byte
 * 
 * When clicking on a piece, the user is presented with a context menu.
 */
public class ContextMenu extends Menu {
    
    private static Logger LOG = Logger.getLogger(ContextMenu.class);
    
    private Sector _context;

    /**
     * Instantiates a context menu.
     * @param game The game object that this context menu will operate on.
     * @param font The font to use with this menu.
     */
    public ContextMenu(Game game, Font font) {
        super(game, font);
        setVisible(false);
    }

    /**
     * @param sector The context sector of this menu.
     */
    public void setContext(Sector sector) {
        
        clearEntries();
        
        _context = sector;
        GamePiece piece = getGame().getData().getGamePieces().get(sector);
        
        int menuIdx = 0;
        
        if(piece instanceof Terrain && piece.isTraversable()) {

            LOG.debug("Detected a terrain at the mouse press for context menu.");
            
            //add menu option for light tower placement
            addMenuEntry(menuIdx++, "Light Tower", new MenuFunction() {
                public void run(Game game) {
                    Tower tower = TowerFactory.getTower(TowerType.SMALLTURRET);
                    game.getData().getGamePieces().put(_context, tower);
                    game.getData().setScore(game.getData().getScore() - tower.getCost());
                    LOG.debug("Added light tower via context menu press.");
                    game.getContextMenu().setVisible(false);
                    game.getAI().invalidateCache();
                }
            });

            //add menu option for cancel
            addMenuEntry(menuIdx++, "Cancel", new MenuFunction() {
                public void run(Game game) {
                    game.getContextMenu().setVisible(false);
                    LOG.debug("Context menu cancelled.");
                }
            });

        } else if(piece instanceof Tower) {
            
            LOG.debug("Detected a tower at the mouse press for context menu.");
            
            final Tower contextTower = (Tower)piece;
            
            for(TowerType type : contextTower.getUpgradeTowerTypes()) {
                
                final Tower tower = TowerFactory.getTower(type);
                
                addMenuEntry(menuIdx++, tower.getName(), new MenuFunction() {
                    public void run(Game game) {
                        game.getData().getGamePieces().put(_context, tower);
                        game.getData().setScore(game.getData().getScore() - tower.getCost());
                        LOG.debug("Added " + tower.getName() + " via context menu press.");
                        game.getContextMenu().setVisible(false);
                        game.getAI().invalidateCache();
                    }
                });
                
            }
            
            final int sellPrice = (contextTower.getCost() / 2) == 0 ? 1 :(contextTower.getCost() / 2);
            
            //add menu option for sell
            addMenuEntry(menuIdx++, "Sell for " + sellPrice, new MenuFunction() {
                public void run(Game game) {
                    Terrain terrain = TerrainFactory.getTerrain(TerrainType.LAND);
                    game.getData().setScore(game.getData().getScore() + sellPrice);
                    LOG.debug("Sold " + contextTower.getName() + " via context menu press.");
                    game.getData().getGamePieces().put(_context, terrain);
                    game.getContextMenu().setVisible(false);
                    game.getAI().invalidateCache();
                }
            });
            
            //add menu option for cancel
            addMenuEntry(menuIdx++, "Cancel", new MenuFunction() {
                public void run(Game game) {
                    game.getContextMenu().setVisible(false);
                    LOG.debug("Context menu cancelled.");
                }
            });
        }

        
        LOG.debug("Context of context menu setup.");
    }
    
    /**
     * @return The piece whose context this menu represents.
     */
    public Sector getContext() {
        return _context;
    }

    /**
     * @param gfx The graphics object to draw with.
     */
    public void draw(Graphics gfx) {
        
        if(_context == null || isVisible() == false) {
            LOG.debug("Context is null, or the menu is not visible. No drawing will occur.");
            return;
        } else {
            super.draw(gfx);
        }
        
    }

}
