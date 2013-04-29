/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

/**
 * A position on planet earth.
 * 
 * @author Christian Lins
 */
public class Location {

    private boolean   disabled                   = true;
    private boolean   hasLocationProviderChecked = false;
    private float     x, y;
    private Timer     timer                      = null;
    private final int satellites                 = 3;
    private MapMIDlet midlet;

    public Location(MapMIDlet midlet) {
        this();
        this.midlet = midlet;

        if (hasLocationProvider()) {
            enableUpdateTimer(5);
        }
    }

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Location() {
        this(52.0f, 8.0f);
    }

    protected void enableUpdateTimer(int secInterval) {
        secInterval = secInterval * 1000;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {

            public void run() {
                try {
                    if (!disabled && updateLocation()) {
                        midlet.getMap().locationUpdated();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    cancel();
                    disabled = true;
                }
            }
        }, secInterval, secInterval);
    }

    public int getSatellites() {
        return this.satellites;
    }

    public synchronized boolean hasLocationProvider() {
        if (hasLocationProviderChecked) {
            return !this.disabled;
        }

        try {
            if (LocationProvider.getInstance(null) != null) {
                this.disabled = false;
                return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        hasLocationProviderChecked = true;
        return false;
    }

    public void shift(double dlon, double dlat) {
        this.y += dlat;
        this.x += dlon;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    /**
     * Updates the coordinates of this instance using a LocationProvider.
     * 
     * @return
     */
    public boolean updateLocation() {
        if (disabled) {
            return false;
        }

        boolean updated = false;

        try {
            javax.microedition.location.Location location = LocationProvider.getLastKnownLocation();
            if (location != null) {
                QualifiedCoordinates coord = location.getQualifiedCoordinates();
                this.y = (float) coord.getLatitude();
                this.x = (float) coord.getLongitude();
                updated = true;
            }

        } catch (SecurityException ex) {
            disabled = true;
        }

        return updated;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Location@");
        buf.append(hashCode());
        buf.append(' ');
        buf.append(this.x);
        buf.append(' ');
        buf.append(this.y);
        buf.append('\n');
        return buf.toString();
    }

}
