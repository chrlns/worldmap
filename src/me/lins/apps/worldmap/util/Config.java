/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import me.lins.apps.worldmap.MapMIDlet;

/**
 * Central app config. Stores data in "config" RecordStore.
 * 
 * @author Christian Lins
 */
public class Config {

    public static final String POS_X       = "PosX";
    public static final String POS_Y       = "PosY";
    public static final String LASTMAPTYPE = "LastMapType";
    public static final String ZOOM        = "Zoom";

    private final Hashtable    keys        = new Hashtable();

    public Config(MapMIDlet midlet) {
        try {
            RecordStore config = RecordStore.openRecordStore("config", true);

            RecordEnumeration configKeys = config.enumerateRecords(null, null, false);
            while (configKeys.hasNextElement()) {
                String kv = new String(configKeys.nextRecord());
                Vector vkv = StringTokenizer.getVector(kv, '=');
                keys.put(vkv.elementAt(0), vkv.elementAt(1));
            }

            configKeys.destroy();
            config.closeRecordStore();
        } catch (RecordStoreException ex) {
            midlet.getDebugDialog().addMessage("Exception", ex.getMessage());
        }
    }

    public String get(String key, String def) {
        if (this.keys.containsKey(key)) {
            Object obj = this.keys.get(key);
            return (String) obj;
        } else {
            return def;
        }
    }

    public float get(String key, float def) {
        String f = get(key, null);
        if (f != null) {
            return Float.parseFloat(f);
        } else {
            return def;
        }
    }

    public int get(String key, int def) {
        try {
            String f = get(key, null);
            if (f != null) {
                return Integer.parseInt(f);
            } else {
                return def;
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return def;
        }
    }

    public void set(String key, String value) {
        this.keys.put(key, value);
        store();
    }

    public void set(String key, float value) {
        set(key, Float.toString(value));
    }

    public void set(String key, int value) {
        set(key, Integer.toString(value));
    }

    private void store() {
        try {
            RecordStore.deleteRecordStore("config");
            RecordStore config = RecordStore.openRecordStore("config", true);

            Enumeration k = this.keys.keys();
            while (k.hasMoreElements()) {
                String key = (String) k.nextElement();
                byte[] buf = (key + "=" + (String) this.keys.get(key)).getBytes();
                config.addRecord(buf, 0, buf.length);
            }

            config.closeRecordStore();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

}
