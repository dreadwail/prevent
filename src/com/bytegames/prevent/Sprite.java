package com.bytegames.prevent;

import com.bytegames.prevent.MathHelper;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * A 2D entity to display.
 * 
 * @author byte
 */
public class Sprite {

    private BufferedImage _image;
    private double _angle;

    /**
     * Instantiates a new sprite.
     * 
     * @param image The image this sprite has.
     */
    public Sprite(BufferedImage image) {

        _image = image;
        _angle = 0;

    }
    
    /**
     * @return The image stored in this sprite.
     */
    public BufferedImage getImage() {
        return _image;
    }
    
    /**
     * @param image The image to store in this sprite.
     */
    public void setImage(BufferedImage image) {
        _image = image;
    }

    /**
     * Rotate the sprite counter-clockwise.
     * 
     * @param byAngle The angle to rotate the sprite left by.
     */
    public void rotateLeft(double byAngle) {
        _angle = MathHelper.clampAngle(_angle - byAngle);
    }

    /**
     * Rotate the sprite clockwise.
     * 
     * @param byAngle The angle to rotate the sprite right by.
     */
    public void rotateRight(double byAngle) {
        _angle = MathHelper.clampAngle(_angle + byAngle);
    }
    
    /**
     * @return The angle the sprite is currently at.
     */
    public double getAngle() {
        return _angle;
    }

    /**
     * @param angle The angle to set the sprite at.
     */
    public void setAngle(double angle) {
        _angle = MathHelper.clampAngle(angle);
    }

    /**
     * Renders the sprite to the display.
     * 
     * @param gfx The graphics object to draw with.
     * @param point The place to draw at.
     */
    public void draw(Graphics gfx, Point point) {
        
        Graphics2D gfx2d = (Graphics2D)gfx;

        //set transform based on angle
        AffineTransform xform = gfx2d.getTransform();
        AffineTransform xformOriginal = (AffineTransform)(xform.clone());

        xform.rotate(Math.toRadians(_angle), point.x, point.y);
        gfx2d.setTransform(xform);

        //draw it
        gfx2d.drawImage(_image, point.x, point.y, null);

        //reset transform back
        gfx2d.setTransform(xformOriginal);

    }

}
