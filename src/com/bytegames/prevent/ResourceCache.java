package com.bytegames.prevent;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;


/**
 * A cache of resources, which can be preloaded when the game starts up.
 * 
 * @author byte
 *
 */
public class ResourceCache {

    private static Logger LOG = Logger.getLogger(ResourceCache.class);

    private String _spriteDir;
    //private String _audioDir;
    private HashMap<String, Sprite> _spriteCache;
    private HashMap<String, File> _audioCache;

    /**
     * Instantiates a new resource cache of images and audio.
     * 
     * @param imageDir The directory containing the image files.
     * @param audioDir The directory containing the sound files.
     */
    public ResourceCache(String imageDir, String audioDir) {

        _spriteDir = imageDir;
        //_audioDir = audioDir;
        _spriteCache = new HashMap<String, Sprite>();
        _audioCache = new HashMap<String, File>();

    }

    /**
     * @param resourceName The name of the image to retrieve from the cache.
     * @return The image.
     */
    public Sprite getSprite(String resourceName) {
        return _spriteCache.get(resourceName);
    }
    
    /**
     * @param resourceName The name of the audio to retrieve from the cache.
     * @return The audio.
     */
    public File getAudio(String resourceName) {
        return _audioCache.get(resourceName);
    }
    
    /**
     * Processes the resources in the image and audio directories, and loads them up into the cache.
     * @return Whether or not all resources were properly loaded.
     * @throws IOException Exception thrown if the loader was unable to load a required file.
     */
    public boolean load() throws IOException {
        
        LOG.debug("Starting resource cache load.");
        
        loadSprites();
        loadAudio();
        
        LOG.debug("Resource cache load complete.");
        
        return true;

    }

    private void loadSprites() throws IOException {
        
        URL resourceDirURL = Main.class.getClassLoader().getResource(_spriteDir);
        File resourceDirFile = new File(resourceDirURL.getFile());
        String[] resourcePaths = resourceDirFile.list(new OnlyImages());

        for(String resourcePath : resourcePaths) {
            URL url = this.getClass().getClassLoader().getResource(_spriteDir + "/" + resourcePath);
            BufferedImage img = null;
            try {
                img = ImageIO.read(url);
            } catch(IOException ex) {
                LOG.error("Unable to load required image file: " + url.getPath());
                throw ex;
            }
            Sprite spr = new Sprite(img);
            _spriteCache.put(resourcePath, spr);
        }

    }

    //TODO: make audio
    private void loadAudio() {

    }
    
    /**
     * Set the scale of images stored in the resource cache.
     */
    public void scaleImages() {
        
        for(String imageName : _spriteCache.keySet()) {
            BufferedImage preScale = _spriteCache.get(imageName).getImage();
            int scale = Game.getScale();
            BufferedImage postScale = getScaledImage(preScale, scale, scale, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
            _spriteCache.get(imageName).setImage(postScale);
        }

    }

    private BufferedImage getScaledImage(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
        
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            w = img.getWidth();
            h = img.getHeight();
        } else {
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;

    }

}