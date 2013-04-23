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
import javax.microedition.lcdui.TextField;

import me.lins.apps.worldmap.osmbugs.OpenStreetBugs;

/**
 * 
 * @author Christian Lins
 */
public class ReportMapErrorDialog extends Form implements CommandListener {

    private final Command           cmdCancel    = new Command("Cancel", "Cancel", Command.CANCEL,
                                                         1);
    private final Command           cmdSubmitBug = new Command("Submit", "Submit Map Error",
                                                         Command.ITEM, 1);
    private final Location          location;
    private final Map               map;
    private final TextField         txtUsername  = new TextField("User name:", "NoName", 64,
                                                         TextField.ANY);
    private final TextField         txtProblem   = new TextField("Describe the Problem:", "", 255,
                                                         TextField.ANY);
    private final MapMIDlet midlet;

    public ReportMapErrorDialog(MapMIDlet midlet, Location location, Map map) {
        super("Report Map Error");
        setCommandListener(this);

        addCommand(cmdCancel);
        addCommand(cmdSubmitBug);

        this.midlet = midlet;
        this.location = location;
        this.map = map;
        this.append("Position: " + location.getX() + " " + location.getY());

        this.append(txtProblem);
        this.append(txtUsername);
    }

    public void commandAction(Command cmd, Displayable displayable) {
        if (cmd.equals(this.cmdCancel)) {
            Display.getDisplay(midlet).setCurrent(this.map);
        } else if (cmd.equals(this.cmdSubmitBug)) {
            OpenStreetBugs.submitBug(location, txtProblem.getString(), txtUsername.getString());
            Display.getDisplay(midlet).setCurrent(this.map);
        }
    }

}
