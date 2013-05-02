/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap;

import com.nokia.mid.ui.frameanimator.FrameAnimator;
import com.nokia.mid.ui.gestures.GestureEvent;
import com.nokia.mid.ui.gestures.GestureInteractiveZone;
import com.nokia.mid.ui.gestures.GestureListener;
import com.nokia.mid.ui.gestures.GestureRegistrationManager;

public class MapGestureListener implements GestureListener {

    public static final int     DOUBLE_TAP_TRESHOLD = 500;                // milliseconds

    private final Map           map;
    private long                lastTap             = 0;
    private final FrameAnimator frameAnimator       = new FrameAnimator();

    public MapGestureListener(Map map) {
        this.map = map;

        // Setup gesture interactive zone
        GestureInteractiveZone zone = new GestureInteractiveZone(GestureInteractiveZone.GESTURE_ALL);
        zone.setRectangle(0, 0, map.getWidth(), map.getHeight());
        GestureRegistrationManager.register(map, zone);

        frameAnimator.register(0, 0, (short) 0, (short) 0, this.map);
    }

    public void gestureAction(Object container, GestureInteractiveZone gestureInteractiveZone,
            GestureEvent gestureEvent) {
        int type = gestureEvent.getType();
        switch (type) {
            case GestureInteractiveZone.GESTURE_TAP:
                // Stop running animator, otherwise app will not look very
                // responsive
                frameAnimator.stop();

                if (System.currentTimeMillis() - lastTap < DOUBLE_TAP_TRESHOLD) {
                    System.out.println("Gesture Event: DOUBLE TAP");
                    this.map.zoomIn(gestureEvent.getStartX(), gestureEvent.getStartY());
                    lastTap = 0;
                } else {
                    System.out.println("Gesture Event: TAP");
                    lastTap = System.currentTimeMillis();
                }
                break;
            case GestureInteractiveZone.GESTURE_LONG_PRESS:
            case GestureInteractiveZone.GESTURE_DROP:
            case GestureInteractiveZone.GESTURE_RECOGNITION_START:
                frameAnimator.stop();
                break;
            case GestureInteractiveZone.GESTURE_DRAG:
                frameAnimator.stop();
                int dx = gestureEvent.getDragDistanceX();
                int dy = gestureEvent.getDragDistanceY();
                this.map.shiftPixel(dx, dy);
                break;
            case GestureInteractiveZone.GESTURE_PINCH:
                if (gestureEvent.getPinchDistanceChange() < 0) {
                    this.map.zoomOut(gestureEvent.getPinchCenterX(), gestureEvent.getPinchCenterY());
                } else {
                    this.map.zoomIn(gestureEvent.getPinchCenterX(), gestureEvent.getPinchCenterY());
                }
                break;
            case GestureInteractiveZone.GESTURE_FLICK:
                System.out.println("Gesture Event: FLICK");
                frameAnimator.kineticScroll(gestureEvent.getFlickSpeed(),
                        FrameAnimator.FRAME_ANIMATOR_FREE_ANGLE,
                        FrameAnimator.FRAME_ANIMATOR_FRICTION_MEDIUM,
                        gestureEvent.getFlickDirection());
                break;
        }
    }
}
