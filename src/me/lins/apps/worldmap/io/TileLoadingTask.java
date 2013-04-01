/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import javax.microedition.lcdui.Image;

/**
 * Thread that loads a tile asynchronously.
 * 
 * @author Christian Lins
 */
public class TileLoadingTask implements Runnable {

    public String                     URL;

    private final TileCache           cache;
    private final TileLoadingObserver observer;
    private final int                 x, y, zoom, mapSource;
    private final long                creationTime = System.currentTimeMillis();

    public TileLoadingTask(int zoom, int x, int y, int mapSource, TileCache cache,
            TileLoadingObserver observer) {
        if (cache == null || observer == null) {
            throw new IllegalArgumentException();
        }

        this.URL = mapSource + "/" + zoom + "/" + x + "/" + y;
        this.cache = cache;
        this.observer = observer;
        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.mapSource = mapSource;
    }

    public long creationTime() {
        return this.creationTime;
    }

    public void run() {
        try {
            Image img = this.cache.loadImage(zoom, x, y, mapSource, true, null);
            this.observer.tileLoaded(img, zoom, x, y, mapSource, null);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
