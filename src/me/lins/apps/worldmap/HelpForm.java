/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Spacer;

/**
 * Help form.
 * 
 * @author Christian Lins
 */
public class HelpForm extends Form implements CommandListener {

    public static final Command BACK = new Command("back", Command.BACK, 0);
    private final MapMIDlet     midlet;

    public HelpForm(MapMIDlet midlet) {
        super("Help");
        setCommandListener(this);

        this.midlet = midlet;

        addCommand(BACK);

        append("DRAG the map to move the view.");
        append(new Spacer(getWidth(), 1));
        append("DOUBLE TAP to zoom in.");
        append(new Spacer(getWidth(), 1));
        append("Use PINCH gestures to zoom in and out.");
    }

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd.equals(BACK)) {
            Display.getDisplay(midlet).setCurrent(midlet.getMap());
        }
    }
}
