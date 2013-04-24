/*
 *  WorldMap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;

import me.lins.apps.worldmap.MapMIDlet;

/**
 * Load tiles from a tile source.
 * 
 * @author Christian Lins
 */
class OnlineFileSource implements TileCache {

    public static final String OSM_URL = "http://tile.openstreetmap.org/";
    public static final String OCM_URL = "http://tile.opencyclemap.org/cycle/";

    private final MapMIDlet    midlet;

    public OnlineFileSource(MapMIDlet midlet) {
        this.midlet = midlet;
    }

    public boolean initialize() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    /**
     * Inefficient way to read the image data from the InputStream but necessary
     * for some servers.
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    private Image loadImage(InputStream in, ByteArrayOutputStream out) throws IOException {
        int b;
        while ((b = in.read()) != -1) {
            out.write(b);
        }
        byte[] buf = out.toByteArray();
        return Image.createImage(buf, 0, buf.length);
    }

    public Image loadImage(int zoom, int x, int y, int mapSource, boolean goDown, Vector obs) {
        if (y < 0) {
            return null;
        }

        String url;
        if (mapSource == TileCache.SOURCE_OPENCYCLEMAP) {
            url = OCM_URL;
        } else {
            url = OSM_URL;
        }
        url += zoom + "/" + x + "/" + y + ".png";
        midlet.getDebugDialog().addMessage("Note", "Loading " + url);

        HttpConnection conn = null;
        DataInputStream ins = null;
        try {
            conn = (HttpConnection) Connector.open(url);
            conn.setRequestMethod(HttpConnection.GET);
            if (conn.getResponseCode() != HttpConnection.HTTP_OK) {
                System.out.println(url + " returned " + conn.getResponseCode());
                return null;
            }

            ins = conn.openDataInputStream();
            int slen = (int) conn.getLength();

            Image img;
            byte[] raw;
            if (slen == -1) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                img = loadImage(ins, buf);
                raw = buf.toByteArray();
            } else {
                raw = new byte[slen];
                ins.readFully(raw);
                img = Image.createImage(raw, 0, raw.length);
            }

            // Notify observer
            if (obs != null) {
                for (int n = 0, os = obs.size(); n < os; n++) {
                    TileLoadingObserver observer = (TileLoadingObserver) obs.elementAt(n);
                    observer.tileLoaded(img, zoom, x, y, mapSource, raw);
                }
            }

            return img;
        } catch (Exception ex) {
            midlet.getDebugDialog().addMessage("Excp", ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                if (ins != null) {
                    ins.close();
                    ins = null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void shutdown() {
    }

}
