/*
 * AddWire.java
 *
 * Created on March 3, 2005, 4:30 PM
 */

package controller;

import gui.Gui;
import model.Model;
import model.Wire;

/**
 * Implements the action for connecting components with a wire. Handles both the
 * gui and model aspects.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class AddWire extends Command {
    
    /**
     * Constructs a new <code>AddWire</code> command. The wire must have both
     * ends set.
     *
     * @param gui the gui
     * @param model the model
     * @param wire the wire to be added
     */
    public AddWire(Gui gui, Model model, Wire wire) {
        super(gui, model, "create wire");
        this.wire = wire;
    }
    
    /**
     * Executes the command.
     */
    public void execute() {
        model.connectComponents(wire);
        gui.addToSandbox(wire.getWireImage());
        gui.resizeSandbox();
        gui.repaintSandbox();
    }

    /**
     * Unexecutes the command.
     */
    public void unexecute() {
        gui.removeFromSandbox(wire.getWireImage());
        model.disconnectComponents(wire);
        gui.resizeSandbox();
        gui.repaintSandbox();
    }
    
    /** the wire to add */
    private Wire wire;
}
