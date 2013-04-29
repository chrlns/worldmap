/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import me.lins.apps.worldmap.io.TileCacheManager;
import me.lins.apps.worldmap.util.Config;

import com.nokia.mid.ui.VirtualKeyboard;

/**
 * The main MIDlet of the application.
 * 
 * @author Christian Lins
 */
public class MapMIDlet extends MIDlet {

    private final Config      config;
    private final DebugDialog debugDialog;
    private final Map         map;

    public MapMIDlet() {
        if ("None".equals(System.getProperty("com.nokia.keyboard.type"))) {
            VirtualKeyboard.hideOpenKeypadCommand(true);
        }

        this.config = new Config(this);
        this.debugDialog = new DebugDialog(this);
        this.map = new Map(this);

        TileCacheManager.initialize(this);
    }

    public Config getConfig() {
        return this.config;
    }

    public DebugDialog getDebugDialog() {
        return this.debugDialog;
    }

    public Map getMap() {
        return this.map;
    }

    public void startApp() {
        Display display = Display.getDisplay(this);
        display.setCurrent(this.map);
    }

    /**
     * This method is called by the runtime when the MIDlet is paused. The
     * MIDlet should free as many resources as possible and return from this
     * method as soon as possible.
     */
    public void pauseApp() {
        TileCacheManager.clearVolatileCache();
    }

    public void destroyApp(boolean unconditional) {
        this.map.shutdown();
    }

}
