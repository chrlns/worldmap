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

        append("Press '5' to zoom in.");
        append(new Spacer(getWidth(), 1));
        append("Press '0' to zoom out.");
        append(new Spacer(getWidth(), 1));
        append("Press '4' and '6' to move map west and east.");
        append(new Spacer(getWidth(), 1));
        append("Press '2' and '8' to move map north and south.");
        append(new Spacer(getWidth(), 1));
        append("Press '1' to center the map to your current location if available.");
    }

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd.equals(BACK)) {
            Display.getDisplay(midlet).setCurrent(midlet.getMap());
        }
    }
}
