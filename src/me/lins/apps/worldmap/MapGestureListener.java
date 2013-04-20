/*
 *  WorldMap - J2ME OpenStreetMap Client
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap;

import com.nokia.mid.ui.gestures.GestureEvent;
import com.nokia.mid.ui.gestures.GestureInteractiveZone;
import com.nokia.mid.ui.gestures.GestureListener;
import com.nokia.mid.ui.gestures.GestureRegistrationManager;

public class MapGestureListener implements GestureListener {

    public static final int DOUBLE_TAP_TRESHOLD = 500; // milliseconds

    private final Map       map;
    private long            lastTap             = 0;

    public MapGestureListener(Map map) {
        this.map = map;

        // Setup gesture interactive zone
        GestureInteractiveZone zone = new GestureInteractiveZone(GestureInteractiveZone.GESTURE_ALL);
        zone.setRectangle(0, 0, map.getWidth(), map.getHeight());
        GestureRegistrationManager.register(map, zone);
    }

    public void gestureAction(Object container, GestureInteractiveZone gestureInteractiveZone,
            GestureEvent gestureEvent) {
        int type = gestureEvent.getType();
        switch (type) {
            case GestureInteractiveZone.GESTURE_TAP:
                if (System.currentTimeMillis() - lastTap < DOUBLE_TAP_TRESHOLD) {
                    System.out.println("Gesture Event: DOUBLE TAP");
                    this.map.zoomIn();
                    lastTap = 0;
                } else {
                    System.out.println("Gesture Event: TAP");
                    lastTap = System.currentTimeMillis();
                }
                break;
        }
    }
}
