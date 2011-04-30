package com.bytegames.prevent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Main driver to bootstrap the game.
 * 
 * @author byte
 * 
 */
public class Main {

    private static Logger LOG = Logger.getLogger(Main.class);
    
    /**
     * Entry point for the game.
     * 
     * @param args Any command line arguments.
     */
    public static void main(String[] args) {
        
        //basic logging
        BasicConfigurator.configure();

        LOG.debug("Instantiating game.");

        Game game = new Game();
        game.run();
        
        LOG.debug("Game has finished. Terminating main.");
        
        game.dispose();
    }

}

//TODO: fix bug where placing a tower in same sector as unit causes freeze
//TODO: more levels
//TODO: prevent from spending into negatives