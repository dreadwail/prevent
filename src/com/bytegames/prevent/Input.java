package com.bytegames.prevent;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.*;

import org.apache.log4j.Logger;

/**
 * Supports user input.
 * 
 * @author byte
 *
 */
public class Input extends MouseAdapter implements KeyListener {

    private static Logger LOG = Logger.getLogger(Input.class);

    private Game _game;
    
    /**
     * Instantiates a new input.
     * @param game The game to manipulate with controls.
     */
    public Input(Game game) {
        _game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        LOG.debug("Key " + e.getKeyChar() + " pressed.");
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        	if(_game.getGameState() == GameState.GAME) {
        		_game.setGameState(GameState.MENU);
        	} else if(_game.getGameState() == GameState.MENU) {
        	    if(_game.getData() != null) {
        	        _game.setGameState(GameState.GAME);
        	    }
        	} else if(_game.getGameState() == GameState.LEVELMENU) {
        	    _game.setGameState(GameState.MENU);
        	} else if(_game.getGameState() == GameState.CREDITS) {
        	    _game.setGameState(GameState.MENU);
        	}
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Invoked when the mouse button has been clicked (pressed and released) on a component.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //Invoked when the mouse enters a component.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //Invoked when the mouse exits a component.
    }

    @Override
    public void mousePressed(MouseEvent e) {

        LOG.debug("Mouse " + e.getButton() + " pressed.");
        
    	if(_game.getGameState() == GameState.GAME) {
    		mousePressedGame(e);
    	} else if(_game.getGameState() == GameState.MENU) {
    		mousePressedMenu(e);
    	} else if(_game.getGameState() == GameState.LEVELMENU) {
    	    mousePressedLevelMenu(e);
    	}

    }
    
    private void mousePressedMenu(MouseEvent e) {
    	
    	if(e.getButton() == MouseEvent.BUTTON1) {
            _game.getMenu().execute();
    	}
    }
    
    private void mousePressedLevelMenu(MouseEvent e) {
        
        if(e.getButton() == MouseEvent.BUTTON1) {
            _game.getLevelMenu().execute();
        }
    }

    private void mousePressedGame(MouseEvent e) {
        
        if(e.getButton() == MouseEvent.BUTTON1) {
            
            Point pressPoint = e.getPoint();
            
            int scale = Game.getScale();
            int potentialSectorX = pressPoint.x / scale;
            int potentialSectorY = pressPoint.y / scale;
            Sector potentialSector = new Sector(potentialSectorX, potentialSectorY);
            
            ContextMenu contextMenu = (ContextMenu)_game.getContextMenu();
            
            if(contextMenu.isVisible()) {
                LOG.debug("Context menu is already visible.");
                if(contextMenu.containsPoint(pressPoint)) {
                    LOG.debug("Context menu contains the current mouse point. Executing menu function.");
                    contextMenu.execute();
                } else {
                    LOG.debug("Hiding context menu.");
                    contextMenu.setVisible(false);
                }
            } else {
                LOG.debug("Context menu is not yet visible.");
                if(_game.getData().getGamePieces().containsKey(potentialSector)) {
                    LOG.debug("There is a game piece at the sector where the mouse is.");
                
                    contextMenu.setContext(potentialSector);
                    contextMenu.setDrawPosition(pressPoint);
                    
                    LOG.debug("Context and draw position set.");
                
                    if(contextMenu.entryCount() > 0) {
                        LOG.debug("There are menu items in the context menu. Showing context menu.");
                        contextMenu.setVisible(true);
                    }
                }
            }

        }
    }
    
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    
    public void mouseMoved(MouseEvent e) {
        
        if(_game.getGameState() == GameState.GAME) {
        	mouseMovedGame(e);
        } else if(_game.getGameState() == GameState.MENU) {
        	mouseMovedMenu(e);
        } else if(_game.getGameState() == GameState.LEVELMENU) {
            mouseMovedLevelMenu(e);
        }

    }
    
    private void mouseMovedMenu(MouseEvent e) {
    	_game.getMenu().mouseInteract(e.getPoint());
    }
    
    private void mouseMovedLevelMenu(MouseEvent e) {
        _game.getLevelMenu().mouseInteract(e.getPoint());
    }
    
    private void mouseMovedGame(MouseEvent e) {
    	
    	Point mousePoint = e.getPoint();
        
        if(_game.getContextMenu().isVisible()) {
        	_game.getContextMenu().mouseInteract(mousePoint);
        }
        
        int scale = Game.getScale();
        int potentialSectorX = mousePoint.x / scale;
        int potentialSectorY = mousePoint.y / scale;
        Sector potentialSector = new Sector(potentialSectorX, potentialSectorY);
        
        if(_game.getData().getGamePieces().containsKey(potentialSector)) {
            GamePiece piece = _game.getData().getGamePieces().get(potentialSector);
            if(piece.isTraversable() || piece instanceof Tower) {
                _game.setHoverSector(potentialSector, Color.WHITE);
            }
        }
    }
    
    /**
     * @return The current position of the mouse.
     */
    public static Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}
