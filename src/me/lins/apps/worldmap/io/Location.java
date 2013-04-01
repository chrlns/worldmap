/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import me.lins.apps.worldmap.MapMIDlet;

/**
 * A position on planet earth.
 * 
 * @author Christian Lins
 */
public class Location {

    private float     x, y;
    private Timer     timer      = null;
    private final int satellites = 3;
    private MapMIDlet midlet;

    public Location(MapMIDlet midlet) {
        this.y = 52.0f; // y
        this.x = 8.0f; // x
        this.midlet = midlet;
        updateLocation();
    }

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void enableUpdateTimer(int secInterval) {
        secInterval = secInterval * 1000;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {

            public void run() {
                if (updateLocation()) {
                    midlet.getMap().locationUpdated();
                }
            }
        }, secInterval, secInterval);
    }

    public int getSatellites() {
        return this.satellites;
    }

    public boolean hasLocationProvider() {
        try {
            if (LocationProvider.getInstance(null) != null) {
                return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
     * Updates the coordinates of this instance using a LocationProvider or an
     * attached Bluetooth GPS device.
     * 
     * @return
     */
    public boolean updateLocation() {
        boolean updated = false;

        try {
            javax.microedition.location.Location location = LocationProvider.getLastKnownLocation();
            if (location != null) {
                QualifiedCoordinates coord = location.getQualifiedCoordinates();
                this.y = (float) coord.getLatitude();
                this.x = (float) coord.getLongitude();
                updated = true;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
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
