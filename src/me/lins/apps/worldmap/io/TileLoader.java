/*
 *  worldmap
 *  
 *  Copyright (C) 2010-2013 by Christian Lins <christian@lins.me>
 *  All rights reserved.
 */

package me.lins.apps.worldmap.io;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Tile loading thread.
 * 
 * @author Christian Lins
 */
public class TileLoader extends Thread {

    private final Hashtable tasks = new Hashtable();

    public TileLoader() {
        super("TileLoader");
        setPriority(MIN_PRIORITY);
    }

    public void addTask(TileLoadingTask task) {
        synchronized (this.tasks) {
            tasks.put(task.URL, task);
            tasks.notify();
        }
    }

    public void run() {
        try {
            TileLoadingTask task = null;
            for (;;) {
                synchronized (this.tasks) {
                    Enumeration keys = tasks.keys();
                    if (keys.hasMoreElements()) {
                        task = (TileLoadingTask) this.tasks.remove(keys.nextElement());
                        if (System.currentTimeMillis() - task.creationTime() > 30000) {
                            // Skip this task as it is probably too old (> 30
                            // seconds)
                            continue;
                        }
                    }
                }

                if (task != null) {
                    System.out.println("Running task " + task.URL);
                    task.run();
                    task = null;
                } else {
                    synchronized (tasks) {
                        tasks.wait();
                    }
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("TileLoader thread interrupted.");
        }
        System.out.println("TileLoader thread ended.");
    }

}
