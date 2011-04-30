package com.bytegames.prevent;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Encapsulates the menu system.
 * 
 * @author byte 
 *
 */
public class Menu {
    
    /**
     * @author byte
     * Encapsulates an action to take for a menu entry selection.
     */
    public interface MenuFunction {
        /**
         * @param g The game object to manipulate.
         */
        void run(Game g);
    }
    
    private static final int DRAW_PADDING = 8;
    
    private static class MenuItem {
        
        private Game _game;
        private String _text;
        private MenuFunction _function;
        private Rectangle _region;
        
        public MenuItem(Game g, String t, MenuFunction f) {
            this._game = g;
            this._text = t;
            this._function = f;
            this._region = new Rectangle();
        }
        
        public void run() {
            _function.run(this._game);
        }
        
        public Rectangle getRegion() {
            return this._region;
        }
        
        public void setRegion(Rectangle r) {
            this._region = r;
        }
        
        public String getText() {
            return this._text;
        }
        
    }
    
    private Game _game;
    private Map<Integer, MenuItem> _menuItems;
    private int _hoverIdx;
    private Font _font;
    private String _widestString;
    private boolean _regionsSet;
    private boolean _visible;
    private Point _drawPosition;
    private Color _foreColor;
    private Color _backColor;
    
    private static Logger LOG = Logger.getLogger(Menu.class);

    /**
     * Instantiates a new menu.
     * @param game The game object to manipulate.
     * @param font The font to use with this menu.
     */
    public Menu(Game game, Font font) {        

        _game = game;
        _font = font;
        _regionsSet = false;
        _widestString = "";
        _hoverIdx = 0;
        _visible = false;
        
        _menuItems = new HashMap<Integer, MenuItem>();
        
        _drawPosition = new Point(0,0);
        
        _foreColor = Color.BLACK;
        _backColor = Color.LIGHT_GRAY;
        
    }
    
    /**
     * @param color The foreground color to use.
     */
    public void setForeColor(Color color) {
        _foreColor = color;
    }
    
    /**
     * @param color The background color to use.
     */
    public void setBackColor(Color color) {
        _backColor = color;
    }
    
    /**
     * @return Whether or not the menu is visible.
     */
    public boolean isVisible() {
        return _visible;
    }
    
    /**
     * @param visible Whether to draw the menu or not.
     */
    public void setVisible(boolean visible) {
        _visible = visible;
    }

    /**
     * @param drawPosition The place to draw the menu.
     */
    public void setDrawPosition(Point drawPosition) {
        _drawPosition = drawPosition;
    }
    
    /**
     * @return The point where the menu will be drawn.
     */
    public Point getDrawPosition() {
        return _drawPosition;
    }
    
    protected Game getGame() {
        return _game;
    }
    
    private void setWidestString() {
        
        _widestString = "";
        
        for(Integer idx : _menuItems.keySet()) {
            if(_menuItems.get(idx).getText().length() >= _widestString.length()) {
                _widestString = _menuItems.get(idx).getText();
            }
        }

    }
    
    /**
     * @param weight The position of the entry in the menu. Lower comes first.
     * @param name The name of the item being added.
     * @param function What action to perform when this entry is chosen.
     */
    public void addMenuEntry(int weight, String name, MenuFunction function) {
        
        _regionsSet = false;
        _menuItems.put(weight, new MenuItem(_game, name, function));
        setWidestString();
    }
    
    /**
     * Clear existing menu entries.
     */
    public void clearEntries() {
        _menuItems.clear();
        _regionsSet = false;
    }
    
    /**
     * @return The number of entries in the menu.
     */
    public int entryCount() {
        return _menuItems.size();
    }
    
    /**
     * Run the command for the currently selected menu item.
     */
    public void execute() {
        MenuItem item = _menuItems.get(_hoverIdx);
        LOG.debug("Executing menu function for '" + item.getText() + "'");
        item.run();
    }

    /**
     * @param point The current mouse point.
     */
    public void mouseInteract(Point point) {

        for(Integer idx : _menuItems.keySet()) {
            if(_menuItems.get(idx).getRegion().contains(point)) {
                _hoverIdx = idx;
                //LOG.debug("Menu hover index now: " + _hoverIdx);
                return;
            }
        }

    }
    
    private void setRegions(Graphics2D gfx2d, Point basis) {

        FontRenderContext context = gfx2d.getFontRenderContext();
        Rectangle2D bounds2D = _font.getStringBounds(_widestString, context);
        Rectangle bounds = bounds2D.getBounds();
        int regionWidth = (int)bounds.getWidth();
        int regionHeight = (int)bounds.getHeight();
        int horizontalOffset = (int)bounds.getWidth() / 2;
        Point offsetCenterPoint = new Point(basis.x - horizontalOffset, basis.y);
        
        for(Integer idx : _menuItems.keySet()) {
            Rectangle region = new Rectangle(offsetCenterPoint.x - DRAW_PADDING, offsetCenterPoint.y - DRAW_PADDING, regionWidth + (2 * DRAW_PADDING), regionHeight + DRAW_PADDING);
            _menuItems.get(idx).setRegion(region);
            offsetCenterPoint.y += regionHeight + (2 * DRAW_PADDING);
        }

    }
    
    /**
     * @param p The point to check against the menu.
     * @return Whether or not the specified point is in the menu.
     */
    public boolean containsPoint(Point p) {
        
        for(Integer idx : _menuItems.keySet()) {
            MenuItem item = _menuItems.get(idx);
            if(item.getRegion().contains(p)) {
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * @param gfx The graphics object to draw with.
     */
    public void draw(Graphics gfx) {

        if(_visible == false)
            return;
        
        Graphics2D gfx2d = (Graphics2D)gfx;
        gfx2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(_regionsSet == false) {
            setRegions(gfx2d, _drawPosition);
            _regionsSet = true;
        }

        for(Integer idx : _menuItems.keySet()) {
            
            MenuItem item = _menuItems.get(idx);
            Rectangle region = item.getRegion();

            gfx2d.setFont(_font);
            
            if(idx == _hoverIdx) {
                gfx2d.setColor(Color.WHITE);
                gfx2d.fillRoundRect(region.x, region.y, region.width, region.height, 10, 10);
                gfx2d.setColor(Color.BLACK);
                gfx2d.drawRoundRect(region.x, region.y, region.width, region.height, 10, 10);
                gfx2d.setColor(Color.RED);
            } else {
                gfx2d.setColor(_backColor);
                gfx2d.fillRoundRect(region.x, region.y, region.width, region.height, 10, 10);
                gfx2d.setColor(Color.BLACK);
                gfx2d.drawRoundRect(region.x, region.y, region.width, region.height, 10, 10);
                gfx2d.setColor(_foreColor);
            }
            
            gfx2d.drawString(item.getText(), region.x + DRAW_PADDING, region.y + region.height - DRAW_PADDING);

        }
        
    }
    
}
