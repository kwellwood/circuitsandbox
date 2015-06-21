/*
 * Controller.java
 *
 * Created on February 20, 2005, 9:48 AM
 */

package controller;

import circuitsandbox.Util;
import gui.ComponentImage;
import gui.ComponentPin;
import gui.Displayable;
import gui.Gui;
import gui.WireImage;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import model.CustomComponent;
import model.LogicComponent;
import model.LogicComponentFactory;
import model.Model;
import model.Wire;
import model.WireFactory;

/**
 * Communicates between the model and gui.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Controller {

    /**
     * Start the Circuit Sandbox application by creating an instance of this
     * class.
     *
     * @param args command line parameters (unused)
     */
    public static void main(String[] args) {
        Controller controller = new Controller();
    }
    
    /**
     * Creates a new <code>Controller</code> object, then creates and
     * registers itself with a new <code>Gui</code> and <code>Model</code>.
     */
    public Controller() {
        undoCommands = new Stack();
        redoCommands = new Stack();
        
        model = new Model(this);
        gui = new Gui(this);
        
        LogicComponentFactory.setDefaultModel(model);
        LogicComponentFactory.setDefaultGui(gui);
        WireFactory.getDefaultFactory().setGui(gui);
        
        gui.setVisible(true);
        gui.repaint();
    }
    
    /**
     * Returns the current working model.
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Creates a new model, discarding the old model object.
     */
    public void newModel() {
        model = new Model(this);
        LogicComponentFactory.setDefaultModel(model);
        undoCommands.clear();
        redoCommands.clear();
        setModified(false);
    }
    
    /**
     * Loads a new model, discarding the old model object. If an error occurs,
     * it will be passed to <code>showError</code> and the old model will be
     * left untouched.
     *
     * @param file the file to load
     *
     * @return <code>true</code> if the file was loaded successfully
     */
    public void loadModel(File file) throws Exception {
        Model m = new Model(this, file);        // may throw an exception
        model = m;
        LogicComponentFactory.setDefaultModel(model);
        undoCommands.clear();
        redoCommands.clear();
        setModified(false);
    }
    
    /**
     * Saves the current model to disk. If an error occurs, it will be thrown
     * as an exception.
     *
     * @param file the file to write
     *
     * @throws Exception if the file can't be written to disk
     */
    public void saveModel(File file) throws Exception {
        model.writeToXML(file);                 // may throw an exception
        setModified(false);
    }

    /**
     * Saves the current model to disk as a custom component. If an error
     * occurs, it will be thrown as an exception.
     *
     * @param file the custom component file to write
     * @param name the english name of the component
     * @param image the image of the component
     * @param inputPins array of input pins (PlaceholderPin objects)
     * @param outputPins array of output pins (PlaceholderPin objects)
     *
     * @throws Exception if the file can't be written to disk
     */
    public void saveAsCustomComponent(File file, String name, Image image,
            ArrayList inputPins, ArrayList outputPins) throws Exception {
        // write the custom component
        CustomComponent.saveComponent(model, file, name, image, inputPins,
                outputPins);
        // load the new component into the toybox
        gui.loadToToybox(CustomComponent.TYPE_STRING + ":" +
                Util.getRelativePath(file));
    }
    
    /**
     * Checks if the current model can be saved as a custom component. Called
     * by the gui before showing the new custom component dialog.
     *
     * @return <code>true</code> if the model can be a custom component
     */
    public boolean canModelBeAComponent() throws Exception {
        return model.canBeAComponent();
    }
    
    /**
     * Returns the input components in the model. This is used for the new
     * custom component dialog in the gui.
     *
     * @return a set of logic components
     */
    public HashSet getInputComponents() {
        return model.getInputComponents();
    }

    /**
     * Returns the output components in the model. This is used for the new
     * custom component dialog in the gui.
     *
     * @return a set of logic components
     */
    public HashSet getOutputComponents() {
        return model.getOutputComponents();
    }
    
    /**
     * Instructs the toybox to load the specified component into its custom
     * components category.
     *
     * @param type the <code>typeString</code> of the component to load
     */
    public void loadToToybox(String type) {
        gui.loadToToybox(type);
    }
    
    /**
     * Instructs the gui to repaint its sandbox component. This is called to
     * update the gui from the model during simultion mode.
     */
    public void repaintSandbox() {
        gui.repaintSandbox();
    }
    
    /**
     * Returns <code>null</code> if the model has been modified since it was
     * created or loaded. Always test this method before creating a new model
     * or loading a saved model.
     *
     * @return the model's status
     */
    public boolean isModified() {
        return modelModified;
    }

    /**
     * Sets whether the model has been modified or not.
     *
     * @param modified if the model has been modified
     */
    public void setModified(boolean modified) { 
        modelModified = modified;
    }
    
    /**
     * Returns <code>true</code> if commands are on the <code>undoStack</code>.
     *
     * @return <code>true</code> if a command can be undone
     */
    public boolean canUndo() {
        return !undoCommands.isEmpty();
    }

    /**
     * Returns <code>true</code> if commands are on the <code>redoStack</code>.
     *
     * @return <code>true</code> if a command can be redone
     */
    public boolean canRedo() {
        return !redoCommands.isEmpty();
    }

    /**
     * Undoes the most recent command.
     */
    public void undo() {
        if (canUndo()) {
            Command command = (Command)undoCommands.pop();
            command.unexecute();
            redoCommands.push(command);
            setModified(true);
        }
    }
    
    /**
     * Redoes the most recently undone command.
     */
    public void redo() {
        if (canRedo()) {
            Command command = (Command)redoCommands.pop();
            command.execute();
            undoCommands.push(command);
            setModified(true);
        }
    }
    
    /**
     * Starts the execution of the simulation.
     */
    public void startSimulation() {
        model.startSimulation();
    }
    
    /**
     * Stops the execution of the simulation.
     */
    public void stopSimulation() {
        model.stopSimulation();
    }
    
    /**
     * Returns the model's signal propagation delay in milliseconds.
     *
     * @return the delay
     */
    public long getSimDelay() {
        return model.getSimDelay();
    }
    
    /**
     * Sets the model's signal propagation delay in milliseconds.
     *
     * @param delay the delay
     */
    public void setSimDelay(long delay) {
        model.setSimDelay(delay);
    }
    
    /**
     * Creates a <code>MoveSelection</code> object and executes it. Distances
     * are given in standard coordinates.
     *
     * @param selection the selection that was moved
     * @param xShift the horizontal distance moved
     * @param yShift the vertical distance moved
     */
    public void selectionMoved(ArrayList selection, int xShift, int yShift) {
       MoveSelection command = new MoveSelection(
               gui, selection, xShift, yShift); 
       executeNewCommand(command);
    }

    /**
     * Creates a <code>DeleteSelection</code> object and executes it.
     *
     * @param selection the selection that will be deleted
     */
    public void deleteSelection(ArrayList selection) {
        DeleteSelection command = new DeleteSelection(gui, model, selection);
        executeNewCommand(command);
    }
    
    /**
     * Creates a new <code>AddComponent</code> command object and executes it.
     * The component location is the upper left of the new component in
     * standard coordinates.
     *
     * @param type the <code>TYPE_STRING</code> of the component to add
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void addComponent(String type, int x, int y) {
        AddComponent command = new AddComponent(gui, model, type, x, y);
        executeNewCommand(command);
    }
    
    /**
     * Creates a new <code>AddWire</code> command object and executes it.
     *
     * @param wire the wire to be created
     */
    public void addWire(Wire wire) {
        AddWire command = new AddWire(gui, model, wire);
        executeNewCommand(command);
    }

    /**
     * Inserts a wire node into a segment of an existing wire's path. The
     * location must be in standard coordinates.
     *
     * @param wireImage the wire image receiving the new node
     * @param segment the segment to split, base 0 starting from the source
     * @param x the x coordinate for the new node
     * @param y the y coordinate for the new node
     */
    public void insertWireNode(WireImage wireImage, int segment, int x, int y) {
        InsertWireNode command = new InsertWireNode(
                gui, wireImage, segment, x, y);
        executeNewCommand(command);
    }
    
    /**
     * Executes the specified <code>command</code> and adds it to the
     * <code>undoStack</code> stack.
     *
     * @param command the command to be executed
     */
    private void executeNewCommand(Command command) {
        command.execute();
        undoCommands.push(command);
        redoCommands.clear();
        setModified(true);
    }
        
    /** the gui frame */
    private Gui gui;
    /** the model currently being used */
    private Model model;
    /** the stack of commands that have already been executed */
    private Stack undoCommands;
    /** the stack of commands that have been unexecuted */
    private Stack redoCommands;
    /** flag for if the model has been changed or not */
    private boolean modelModified = false;
}
