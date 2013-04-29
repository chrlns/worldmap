/*
 *  WorldMap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Image;

public class JarTileCache implements TileCache {

    protected TileCache successor;

    public JarTileCache(TileCache successor) {
        this.successor = successor;
    }

    /**
     * No initialization needed.
     * 
     * @return Returns the initialization return code of successor.initialize()
     */
    public boolean initialize() {
        return this.successor.initialize();
    }

    /**
     * @return Always true
     */
    public boolean isEnabled() {
        return true;
    }

    public Image loadImage(int zoom, int x, int y, int mapSource, boolean goDown, Vector observer) {
        if (y < 0) {
            return null;
        }

        String uri = "/resources/" + mapSource + "/" + zoom + "/" + x + "/" + y + ".png";

        try {
            System.out.println("Retrieving " + uri + " from JAR cache");
            return Image.createImage(uri);
        } catch (IOException e) {
            System.err.println("IOException in JarTileCache.loadImage(): " + e.getMessage());
            if (goDown) {
                return this.successor.loadImage(zoom, x, y, mapSource, goDown, observer);
            }
            return null;
        }
    }

    public void shutdown() {
        // Do nothing
    }

}
