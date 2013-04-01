/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import javax.microedition.lcdui.Image;

/**
 * 
 * @author Christian Lins
 */
public interface TileLoadingObserver {

    /**
     * Called after the image is completely loaded.
     * 
     * @param img
     * @param zoom
     * @param x
     * @param y
     * @param mapSource
     */
    void tileLoaded(Image img, int zoom, int x, int y, int mapSource, byte[] raw);

}
