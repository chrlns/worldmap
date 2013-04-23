/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;

import me.lins.apps.worldmap.MapMIDlet;
import me.lins.apps.worldmap.util.StringTokenizer;

/**
 * Cache that resides in the application's Record Store.
 * 
 * @author Christian Lins
 */
class RMSTileCache implements TileCache, TileLoadingObserver {

    private final TileCache successor;
    private final Hashtable keys      = new Hashtable();
    private boolean         isEnabled = true;
    private RecordStore     rsIndex;
    private RecordStore     rsImages;
    private final MapMIDlet midlet;

    public RMSTileCache(MapMIDlet midlet, TileCache successor) {
        this.midlet = midlet;
        this.successor = successor;
    }

    public boolean initialize() {
        try {
            this.rsIndex = RecordStore.openRecordStore("index", true);
            RecordEnumeration indices = rsIndex.enumerateRecords(null, null, false);
            this.keys.clear();

            while (indices.hasNextElement()) {
                byte[] buf = indices.nextRecord();
                Vector indexChunks = StringTokenizer.getVector(new String(buf), '=');
                keys.put(indexChunks.elementAt(0), indexChunks.elementAt(1));
            }

            indices.destroy();

            rsImages = RecordStore.openRecordStore("images", true);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            this.isEnabled = false;
        } catch (Exception ex) {
            lowMemAction(); // Resetting the RMS cache
        }
        this.successor.initialize();
        return true;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public Image loadImage(int zoom, int x, int y, int mapSource, boolean goDown, Vector obs) {
        String key = mapSource + "/" + zoom + "/" + x + "/" + y;
        synchronized (this.keys) {
            if (this.isEnabled && this.keys.containsKey(key)) {
                int id = Integer.parseInt((String) this.keys.get(key));
                try {
                    byte[] buf = rsImages.getRecord(id);
                    Image img = Image.createImage(buf, 0, buf.length);
                    return img;
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                    return this.successor.loadImage(zoom, x, y, mapSource, goDown, obs);
                }
            } else if (goDown) {
                if (obs == null) {
                    obs = new Vector();
                }
                obs.addElement(this);
                return this.successor.loadImage(zoom, x, y, mapSource, goDown, obs);
            } else {
                return null;
            }
        }
    }

    /**
     * Completely frees the cache contents.
     */
    private void lowMemAction() {
        try {
            if (rsImages != null) {
                this.rsImages.closeRecordStore();
            }
            if (rsIndex != null) {
                this.rsIndex.closeRecordStore();
            }
            RecordStore.deleteRecordStore("images");
            RecordStore.deleteRecordStore("index");
            initialize(); // Reinitialize
        } catch (RecordStoreException ex) {
            midlet.getDebugDialog().addMessage("Excp", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void tileLoaded(Image img, int zoom, int x, int y, int mapSource, byte[] raw) {
        if (raw == null) {
            return;
        }

        try {
            int id = rsImages.addRecord(raw, 0, raw.length);

            String key = mapSource + "/" + zoom + "/" + x + "/" + y + "=" + id;
            rsIndex.addRecord(key.getBytes(), 0, key.length());

            this.keys.put(mapSource + "/" + zoom + "/" + x + "/" + y, new Integer(id));
        } catch (RecordStoreFullException ex) {
            System.out.println(ex.getMessage() + ": truncate cache");
            lowMemAction();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.keys.clear();
            this.rsImages.closeRecordStore();
            this.rsIndex.closeRecordStore();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

}
