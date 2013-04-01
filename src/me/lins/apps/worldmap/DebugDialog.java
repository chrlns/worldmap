/*
 *  WorldMap - J2ME OpenStreetMap Client
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
import javax.microedition.lcdui.StringItem;

/**
 * 
 * @author Christian Lins
 */
public class DebugDialog extends Form implements CommandListener {

    private final Command           cmdBack = new Command("Back", Command.BACK, 0);
    private final MapMIDlet midlet;

    public DebugDialog(MapMIDlet midlet) {
        super("Debug");
        this.midlet = midlet;
        setCommandListener(this);
        addCommand(cmdBack);
    }

    public void addMessage(String label, String msg) {
        this.insert(0, new StringItem(label + ": ", msg));
        while (this.size() > 50) {
            delete(50);
        }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command.equals(this.cmdBack)) {
            Display.getDisplay(midlet).setCurrent(midlet.getMap());
        }
    }

    public void show() {
        Display.getDisplay(midlet).setCurrent(this);
    }

}
