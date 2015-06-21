/*
 * Command.java
 *
 * Created on February 20, 2005, 9:49 AM
 */

package controller;

import gui.Gui;
import model.Model;

/**
 * Performs operations on the data model. This class is the abstract super class
 * for the concrete command classes, as specified by the Command design pattern.
 * 
 * The <CODE>execute</CODE> method must be implemented to perform the subclass'
 * operation, and the <CODE>unexecute</CODE> method must be implemented to undo
 * whatever changes are made to the model.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
abstract public class Command {
    
    /**
     * Creates a new <code>Command</code> object.
     *
     * @param gui the gui
     * @param model the model
     * @param description a brief description of the command
     */
    public Command(Gui gui, Model model, String description) {
        this.gui = gui;
        this.model = model;
        this.description = description;
    }

    /**
     * Gets a description of the command
     * @return a brief description of the command
     */
    public String getDescription() {
        return description;
    }
 
    /**
     * Executes the command. Implement this method to perform the necessary
     * actions.
     */
    abstract void execute();
    
    /**
     * Unexecutes the command. Implement this method to perform the opposite
     * actions done in the <code>execute</code> method. It may be assumed that
     * the program state is exactly as it would be just after calling
     * <code>execute</code>.
     */
    abstract void unexecute();
    
    /** the gui */
    protected Gui gui;
    /** the model */
    protected Model model;
    /** a brief description of the command */
    protected String description;
}
