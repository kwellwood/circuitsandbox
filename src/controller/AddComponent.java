/*
 * AddComponent.java
 *
 * Created on February 26, 2005, 2:57 PM
 */

package controller;

import gui.Gui;
import model.LogicComponent;
import model.LogicComponentFactory;
import model.Model;

/**
 * Implements the action for adding a component to the sandbox.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class AddComponent extends Command {
    
    /**
     * Constructs a new <code>AddComponent</code> object. The component location
     * is the upper left of the new component in standard coordinates.
     *
     * @param gui the gui
     * @param model the model
     * @param type the <code>TYPE_STRING</code> of the component to add
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public AddComponent(Gui gui, Model model, String type, int x, int y) {
        super(gui, model, "create component");

        try {
            component = LogicComponentFactory.getDefaultInstance().
                    buildComponent(type, x, y);
        } catch(Exception e) { }
    }
    
    /**
     * Executes the command.
     */
    public void execute() {
        if (componentId < 0) {
            componentId = model.addComponent(component);
        } else {
            model.addComponent(componentId, component);
        }
        gui.addToSandbox(component.getComponentImage());
        gui.resizeSandbox();
        gui.repaintSandbox();
    }

    /**
     * Unexecutes the command.
     */
    public void unexecute() {
        model.removeComponent(componentId);
        gui.removeFromSelection(component.getComponentImage());
        gui.removeFromSandbox(component.getComponentImage());
        gui.resizeSandbox();
        gui.repaintSandbox();
    }
    
    /** the component that is created */
    private LogicComponent component;
    /** the component's id, set when it has been added to the model */
    private int componentId = -1;
}
