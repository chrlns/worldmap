/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import java.util.Vector;

import javax.microedition.lcdui.Image;

/**
 * 
 * @author Christian Lins
 */
public interface TileCache {

    public static final int SOURCE_OPENSTREETMAP = 1;
    public static final int SOURCE_OPENCYCLEMAP  = 2;

    boolean initialize();

    boolean isEnabled();

    /**
     * Loads the tile identified through the given parameter. If obs is null the
     * image is loaded synchronously.
     * 
     * @param zoom
     * @param x
     * @param y
     * @param mapSource
     * @param goDown
     */
    Image loadImage(int zoom, int x, int y, int mapSource, boolean goDown, Vector observer);

    void shutdown();
}
