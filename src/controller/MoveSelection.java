/*
 * MoveSelection.java
 *
 * Created on February 24, 2005, 7:04 PM
 */

package controller;

import gui.Displayable;
import gui.Gui;
import java.util.ArrayList;

/**
 * Implements the action for a moving a selection.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class MoveSelection extends Command {
    
    /** Creates a new instance of MoveSelection. */
    public MoveSelection(Gui gui, ArrayList selection, int xShift, int yShift) {
        super(gui, null, "move");
        
        this.selection = (ArrayList)selection.clone();
        this.xShift = xShift;
        this.yShift = yShift;
        executed = true;
    }
    
    /**
     * Executes the command.
     */
    public void execute() {
        if (!executed) {
            for (int i = 0; i < selection.size(); i++) {
                ((Displayable)selection.get(i)).shiftLocation(xShift, yShift);
            }
            gui.resizeSandbox();
        }
        executed = true;
    }

    /**
     * Unexecutes the command.
     */
    public void unexecute() {
        for (int i = 0; i < selection.size(); i++) {
            ((Displayable)selection.get(i)).shiftLocation(-xShift, -yShift);
        }
        gui.resizeSandbox();
        executed = false;
    }
    
    /** the collection of components to move */
    private ArrayList selection;
    /** the horizontal distance (in standard coordinates) */
    private int xShift;
    /** the vertical distance (in standard coordinates) */
    private int yShift;
    /** <code>true</code> if command has been executed */
    private boolean executed;
}
