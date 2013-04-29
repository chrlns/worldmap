/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.osmbugs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Loads Bug information from OpenStreetBugs.
 * 
 * @author Christian Lins
 */
public class BugLoader extends Thread {

    private final float       xmin, xmax, ymin, ymax;
    private final BugReceiver rec;

    public BugLoader(float xmin, float xmax, float ymin, float ymax, BugReceiver rec) {
        setPriority(MIN_PRIORITY);
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.rec = rec;
    }

    public void run() {
        // Call to
        // /api/0.1/getBugs?b=36.17496&t=61.03797&l=-9.9793&r=31.54902&ucid=1
        // Reply is something like: putAJAXMarker(552542, 6.971592, 50.810296,
        // 'Straßensystem auf Friedhof fehlt [TobiR, 2010-08-09 23:30:37
        // CEST]', 0);
        try {
            String url = OpenStreetBugs.API_URL + "getBugs?b=" + ymin + "&t=" + ymax + "&l=" + xmin
                    + "&r=" + xmax;
            HttpConnection httpConn = (HttpConnection) Connector.open(url);
            DataInputStream in = httpConn.openDataInputStream();
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            int openbrackets = 0;
            for (int b = in.read(); b != -1; b = in.read()) {
                char c = (char) b;
                if (c == ')') {
                    openbrackets--;
                    if (openbrackets == 0) {
                        Bug bug = Bug.parse(buf.toString());
                        buf.reset();
                        if (bug != null) {
                            rec.receiveBug(bug);
                        }
                    }
                } else if (c == '(') {
                    openbrackets++;
                } else if (openbrackets > 0) { // only store bytes between '('
                                               // ')'
                    buf.write(b);
                }
            }

            in.close();
            httpConn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
