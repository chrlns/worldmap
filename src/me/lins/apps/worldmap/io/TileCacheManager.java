/*
 *  WorldMap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import javax.microedition.lcdui.Image;

import me.lins.apps.worldmap.MapMIDlet;

/**
 * Controls the caching. This class has only static methods for performance
 * reasons.
 * 
 * @author Christian Lins
 */
public final class TileCacheManager {

    private static MemoryTileCache memoryTileCache;
    private static TileLoader      loader;

    public static void clearVolatileCache() {
        memoryTileCache.freeCache();
    }

    public static void initialize(MapMIDlet midlet) {
        memoryTileCache = new MemoryTileCache(new JarTileCache(/*
                                                                * new
                                                                * RMSTileCache
                                                                * (midlet,
                                                                */
        new OnlineFileSource(midlet)));
        TileCacheManager.memoryTileCache.initialize();
        loader = new TileLoader();
        loader.start();
    }

    public static Image loadImage(int zoom, int x, int y, int mapSource, TileLoadingObserver obs) {
        Image img = memoryTileCache.loadImage(zoom, x, y, mapSource, false, null);
        if (img == null && obs != null) {
            TileLoadingTask task = new TileLoadingTask(zoom, x, y, mapSource, memoryTileCache, obs);
            loader.addTask(task);
            return null;
        } else {
            return img;
        }
    }

    public static void shutdown() {
        try {
            loader.interrupt();
            loader = null;
            memoryTileCache.shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private TileCacheManager() {
        // Is never called
    }

}
