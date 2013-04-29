/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import me.lins.apps.worldmap.io.TileCache;
import me.lins.apps.worldmap.io.TileCacheManager;
import me.lins.apps.worldmap.io.TileLoadingObserver;
import me.lins.apps.worldmap.osmbugs.Bug;
import me.lins.apps.worldmap.osmbugs.BugReceiver;
import me.lins.apps.worldmap.osmbugs.OpenStreetBugs;
import me.lins.apps.worldmap.util.Config;
import me.lins.apps.worldmap.util.Math2;

import com.nokia.mid.ui.gestures.GestureRegistrationManager;

/**
 * Main draw canvas (Map).
 * 
 * @author Christian Lins
 */
public class Map extends Canvas implements CommandListener, BugReceiver, TileLoadingObserver {

    public static final int MAXBUGS            = 32;
    private final Command   cmdHelp            = new Command("help", Command.ITEM, 0);
    private final Command   cmdExit            = new Command("exit", Command.EXIT, 1);
    private final Command   cmdBugreport       = new Command("Map Error", "Report Map Error",
                                                       Command.ITEM, 1);
    private final Command   cmdShowBugs        = new Command("Show Bugs", "Show Map Bugs",
                                                       Command.ITEM, 1);
    private final Command   cmdSwitchCycleMap  = new Command("Cycle Map", "Switch to OpenCycleMap",
                                                       Command.ITEM, 1);
    private final Command   cmdSwitchStreetMap = new Command("Street Map",
                                                       "Switch to OpenStreetMap", Command.ITEM, 1);
    private final Command   cmdFollow          = new Command("follow", "follow position",
                                                       Command.ITEM, 1);
    private final Command   cmdUnfollow        = new Command("Unfollow", "Unfollow position",
                                                       Command.ITEM, 1);
    private final Command   cmdDebug           = new Command("debug", Command.ITEM, 10);
    private final Command   cmdAbout           = new Command("about", Command.HELP, 1);
    private boolean         follow             = false;
    private int             zoom               = 1;
    private int[]           centerTileNumbers;
    private final Location  gpsPos             = null;
    private Location        scrollPos;
    private final Bug[]     bugs               = new Bug[MAXBUGS];
    private int             bugPnt             = 0;
    private int             mapSource          = TileCache.SOURCE_OPENSTREETMAP;
    private final MapMIDlet midlet;
    private Image           loadingImg;

    /**
     * Map constructor.
     */
    public Map(MapMIDlet midlet) {
        this.midlet = midlet;

        try {
            setCommandListener(this);

            addCommand(cmdExit);

            // addCommand(cmdBugreport);
            // addCommand(cmdShowBugs);
            // addCommand(cmdSwitchCycleMap);
            addCommand(cmdFollow);

            // addCommand(cmdDebug);
            addCommand(cmdAbout);
            addCommand(cmdHelp);

            GestureRegistrationManager.setListener(this, new MapGestureListener(this));

            float x = midlet.getConfig().get(Config.POS_X, 60.1f);
            float y = midlet.getConfig().get(Config.POS_Y, 24.9f);
            scrollPos = new Location(x, y);
            this.zoom = midlet.getConfig().get(Config.ZOOM, zoom);
            String lastMapType = midlet.getConfig().get(Config.LASTMAPTYPE, "osm");
            if (lastMapType.equals("ocm")) { // OpenCycleMap
                removeCommand(this.cmdSwitchCycleMap);
                addCommand(this.cmdSwitchStreetMap);
                mapSource = TileCache.SOURCE_OPENCYCLEMAP;
            }

            centerTileNumbers = Math2.tileNumbers(scrollPos.getX(), scrollPos.getY(), zoom);
            loadingImg = Image.createImage("/resources/loading.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws a tile image.
     * 
     * @param g
     * @param x
     * @param y
     * @param offX
     * @param offY
     * @throws IOException
     */
    private void drawImage(Graphics g, int x, int y, int offX, int offY) throws IOException {
        Image img = TileCacheManager.loadImage(zoom, x, y, mapSource, null);
        if (img != null) {
            g.drawImage(img, offX, offY, Graphics.TOP | Graphics.LEFT);
        } else {
            // Draw placeholder image
            g.drawImage(loadingImg, offX, offY, Graphics.TOP | Graphics.LEFT);

            TileCacheManager.loadImage(zoom, x, y, mapSource, this);
        }
    }

    /**
     * Draws the current GPS position if available.
     * 
     * @param g
     * @param x
     * @param y
     */
    private void drawGPSPosition(Graphics g, int x, int y) {
        if (this.gpsPos != null && this.gpsPos.hasLocationProvider()) {
            for (int n = 2; n < zoom * 8 / this.gpsPos.getSatellites(); n += 6) {
                g.setColor(0, 25, 255);
                g.drawArc(x - (n >> 1), y - (n >> 1), n, n, 0, 360);
            }
        }
    }

    /**
     * Draw the map.
     */
    public void paint(Graphics g) {
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        try {
            int[] tileNumbers = centerTileNumbers;
            int offX = -tileNumbers[2] + getWidth() / 2; // We want to transform
                                                         // the origin to the
                                                         // center
            int offY = -tileNumbers[3] + getHeight() / 2; // of the screen

            System.out.println(offX + " " + offY);

            // Draw center image
            drawImage(g, tileNumbers[0], tileNumbers[1], offX, offY);

            // Draw image above
            drawImage(g, tileNumbers[0], tileNumbers[1] - 1, offX, offY - 256);

            // Draw image below
            drawImage(g, tileNumbers[0], tileNumbers[1] + 1, offX, offY + 256);

            // Draw left center image
            drawImage(g, tileNumbers[0] - 1, tileNumbers[1], offX - 256, offY);

            // Draw left image above
            drawImage(g, tileNumbers[0] - 1, tileNumbers[1] - 1, offX - 256, offY - 256);

            // Draw left image below
            drawImage(g, tileNumbers[0] - 1, tileNumbers[1] + 1, offX - 256, offY + 256);

            // Draw right center image
            drawImage(g, tileNumbers[0] + 1, tileNumbers[1], offX + 256, offY);

            // Draw right image above
            drawImage(g, tileNumbers[0] + 1, tileNumbers[1] - 1, offX + 256, offY - 256);

            // Draw right image below
            drawImage(g, tileNumbers[0] + 1, tileNumbers[1] + 1, offX + 256, offY + 256);

            // Draw bugs
            g.setColor(255, 0, 0);
            for (int n = 0; n < MAXBUGS; n++) {
                Bug bug = this.bugs[n];
                if (bug != null) {
                    // Draw it! Now!
                    int[] pos = posOnScreen(bug.getX(), bug.getY());
                    g.fillArc(pos[0] - 3, pos[1] - 3, 6, 6, 0, 360);
                }
            }

            // Draw GPS position
            if (this.gpsPos != null) {
                this.gpsPos.updateLocation();
                int[] pos = posOnScreen(gpsPos.getX(), gpsPos.getY());
                drawGPSPosition(g, pos[0], pos[1]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            midlet.getDebugDialog().addMessage("Exception", ex.getMessage());
            g.setColor(255, 0, 0);
            g.drawString("An error occurred", 0, 30, Graphics.TOP | Graphics.LEFT);
        }
    }

    private int[] posOnScreen(float lon, float lat) {
        int[] tileNumbers = centerTileNumbers;
        int offX = -tileNumbers[2] + getWidth() / 2; // We want to transform the
                                                     // origin to the center
        int offY = -tileNumbers[3] + getHeight() / 2; // of the screen
        int[] pos = Math2.tileNumbers(lon, lat, zoom);
        int x = (pos[0] - tileNumbers[0]) * 256 + (pos[2] + offX);
        int y = (pos[1] - tileNumbers[1]) * 256 + (pos[3] + offY);
        return new int[] { x, y };
    }

    public void receiveBug(Bug bug) {
        System.out.println("Bug: " + bug.getText());
        this.bugs[this.bugPnt] = bug;
        this.bugPnt = (this.bugPnt + 1) % MAXBUGS;
        repaint();
    }

    public void shutdown() {
        midlet.getConfig().set(Config.POS_X, scrollPos.getX());
        midlet.getConfig().set(Config.POS_Y, scrollPos.getY());
        midlet.getConfig().set(Config.ZOOM, zoom);
        TileCacheManager.shutdown();
    }

    /**
     * Called when action should be handled
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command.equals(this.cmdExit)) {
            midlet.destroyApp(false);
            midlet.notifyDestroyed();
        } else if (command.equals(this.cmdBugreport)) {
            Display.getDisplay(midlet).setCurrent(
                    new ReportMapErrorDialog(midlet, this.scrollPos, this));
        } else if (command.equals(this.cmdShowBugs)) {
            float[] rpp = Math2.radPerPixel(zoom);
            float xmin = (scrollPos.getX() - rpp[0] * getWidth() / 2);
            float xmax = (scrollPos.getX() + rpp[0] * getWidth() / 2);
            float ymin = (scrollPos.getY() - rpp[1] * getHeight() / 2);
            float ymax = (scrollPos.getY() + rpp[1] * getHeight() / 2);
            OpenStreetBugs.getBugs(xmin, xmax, ymin, ymax, this);
        } else if (command.equals(this.cmdSwitchStreetMap)) {
            removeCommand(this.cmdSwitchStreetMap);
            addCommand(this.cmdSwitchCycleMap);
            mapSource = TileCache.SOURCE_OPENSTREETMAP;
            repaint();
            midlet.getConfig().set(Config.LASTMAPTYPE, "osm");
        } else if (command.equals(this.cmdSwitchCycleMap)) {
            removeCommand(this.cmdSwitchCycleMap);
            addCommand(this.cmdSwitchStreetMap);
            mapSource = TileCache.SOURCE_OPENCYCLEMAP;
            repaint();
            midlet.getConfig().set(Config.LASTMAPTYPE, "ocm");
        } else if (command.equals(this.cmdFollow)) {
            removeCommand(cmdFollow);
            addCommand(cmdUnfollow);
            follow = true;
        } else if (command.equals(this.cmdUnfollow)) {
            removeCommand(cmdUnfollow);
            addCommand(cmdFollow);
            follow = false;
        } else if (command.equals(this.cmdDebug)) {
            midlet.getDebugDialog().show();
        } else if (command.equals(this.cmdAbout)) {
            Display.getDisplay(midlet).setCurrent(new AboutForm(midlet));
        } else if (command.equals(cmdHelp)) {
            Display.getDisplay(midlet).setCurrent(new HelpForm(midlet));
        }
    }

    public void tileLoaded(Image img, int zoom, int x, int y, int mapSource, byte[] raw) {
        if (img != null) {
            repaint();
        }
    }

    /**
     * Called by a timer in Location class when the location changes. Redraws
     * this Map.
     */
    public void locationUpdated() {
        if (this.follow) {
            keyPressed(49); // Press '1'
        }
        repaint();
    }

    public void zoomIn(int x, int y) {
        int dx = getWidth() / 2 - x;
        int dy = getHeight() / 2 - y;

        if (zoom < 18) {
            shiftPixel(dx, dy, false);

            zoom++;
            centerTileNumbers = Math2.tileNumbers(scrollPos.getX(), scrollPos.getY(), zoom);
            repaint();
        } else if (dx != 0 || dy != 0) {
            shiftPixel(dx, dy);
        }
    }

    public void zoomOut(int x, int y) {
        int dx = getWidth() / 2 - x;
        int dy = getHeight() / 2 - y;

        if (zoom > 1) {
            shiftPixel(dx, dy, false);

            zoom--;
            centerTileNumbers = Math2.tileNumbers(scrollPos.getX(), scrollPos.getY(), zoom);
            repaint();
        } else if (dx != 0 || dy != 0) {
            shiftPixel(dx, dy);
        }
    }

    public void shiftPixel(int dx, int dy) {
        shiftPixel(dx, dy, true);
    }

    /**
     * Shift scroll position with given pixel values.
     * 
     * @param dx
     * @param dy
     */
    protected void shiftPixel(int dx, int dy, boolean repaint) {
        if (dx == 0 && dy == 0) {
            return;
        }

        float[] rpp = Math2.radPerPixel(zoom);
        this.scrollPos.shift(rpp[0] * -dx, rpp[1] * dy);
        centerTileNumbers = Math2.tileNumbers(scrollPos.getX(), scrollPos.getY(), zoom);
        if (repaint) {
            repaint();
        }
    }
}
