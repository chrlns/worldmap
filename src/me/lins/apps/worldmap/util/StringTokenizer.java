/*
 *  WorldMap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.util;

import java.util.Vector;

public class StringTokenizer {

    public static Vector getVector(String str, char div) {
        Vector v = new Vector();

        String cur = "";
        for (int n = 0; n < str.length(); n++) {
            char c = str.charAt(n);
            if (c == div) {
                v.addElement(cur);
                cur = "";
            } else {
                cur = cur + c;
            }
        }

        return v;
    }
}
