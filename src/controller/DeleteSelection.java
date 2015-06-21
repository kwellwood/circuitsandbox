/*
 * DeleteSelection.java
 *
 * Created on March 8, 2005, 1:07 AM
 */

package controller;

import gui.ComponentImage;
import gui.Displayable;
import gui.Gui;
import gui.WireImage;
import gui.WireNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import model.LogicComponent;
import model.Model;
import model.Wire;

/**
 * Implements the actions for deleting all objects selected in the gui.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class DeleteSelection extends Command {
    
    /**
     * Constructs a new <code>DeleteSelection</code> command.
     *
     * @param gui the gui
     * @param model the model
     * @param selection the collection of gui objects to delete
     */
    public DeleteSelection(Gui gui, Model model, ArrayList selection) {
        super(gui, model, "delete");
        
        this.selection = (ArrayList)selection.clone();
        
        // take all of the gui image components from the selection and put their
        // corresponding logic components into the components set
        components = new HashSet();
        for (int i=0; i<selection.size(); i++) {
            Displayable displayable = (Displayable)selection.get(i);
            if (displayable.getDisplayableType() == Displayable.COMPONENT) {
                LogicComponent lc = ((ComponentImage)selection.get(i)).
                        getLogicComponent();
                components.add(lc);
            }
        }

        // look through the components set and create a set of all the id
        // numbers of the wires attached to these components
        wires = new HashSet();
        Iterator iter = components.iterator();
        while (iter.hasNext())
        {   
            // Get a set of the ids of the wires attached to the current
            // component
            HashSet wireIds = ((LogicComponent)iter.next()).getWires();
            
            // For each id of the wires attached to the current component, get
            // its wire object and put it in the wires set
            Iterator iter2 = wireIds.iterator();
            while (iter2.hasNext()) {
                wires.add(model.getWire(((Integer)iter2.next()).intValue()));
            }
        }
        
        // look through the selection set again for wires and add all them to
        // the set of wires to be deleted
        for (int i=0; i<selection.size(); i++) {
            Displayable displayable = (Displayable)selection.get(i);
            if (displayable.getDisplayableType() == Displayable.WIRE) {
                wires.add(((WireImage)displayable).getWire());
            }
        }
        
        // look through the selection set again for wire nodes and add them to
        // the wireNodes list if they dont belong to any wires which will be
        // deleted. these nodes will be deleted individually. they must be
        // undeleted in the reverse order of how they were deleted.
        wireNodes = new ArrayList();
        for (int i=0; i<selection.size(); i++) {
            Displayable displayable = (Displayable)selection.get(i);
            if (displayable.getDisplayableType() == Displayable.WIRE_NODE) {
                
                // get the selected wire node
                WireNode wn = (WireNode)selection.get(i);
                
                boolean included = false;
                Iterator iter2 = wires.iterator();
                while (iter2.hasNext()) {
                    // get the nodes along the current wire
                    ArrayList nodes = ((Wire)iter2.next()).getWireImage().
                            getNodes();
                    
                    // is the selected node one of them? If so, its included in
                    // the wires that will be deleted
                    if (nodes.contains(wn)) { included = true; }
                }
                
                // if the selected node is not on any wires that will be deleted
                // then it must be added to the wireNodes list to be deleted
                // individually
                if (!included) { wireNodes.add(wn); }
            }
        }
    }
    
    /**
     * Executes the command.
     */
    public void execute() {
        // unselect everything
        gui.removeFromSelection(selection);
        
        // remove the wires
        Iterator iter = wires.iterator();
        while (iter.hasNext()) {
            Wire wire = (Wire)iter.next();
            
            model.disconnectComponents(wire);
            gui.removeFromSandbox((Displayable)wire.getWireImage());
        }

        // remove the individual wire nodes
        if (wireNodePositions == null) {
            
            // we havent calculated the nodes' positions yet. do this only the
            // first time the command is executed.
            wireNodePositions = new ArrayList();
            for (int i = 0; i < wireNodes.size(); i++) {
                WireNode wn = (WireNode)wireNodes.get(i);
                
                // record the path position of this node just before removing it
                int pathPosition = wn.getPathPosition();
                wireNodePositions.add(new Integer(pathPosition));
                
                // remove the node from its wire
                wn.getOwner().removeNode(wn);
                gui.removeFromSandbox(wn);
            }
        } else {

            for (int i = 0; i < wireNodes.size(); i++) {
                WireNode wn = (WireNode)wireNodes.get(i);
                
                // remove the node from its wire
                wn.getOwner().removeNode(wn);
                gui.removeFromSandbox(wn);
            }
        }

        // remove the components
        iter = components.iterator();
        while (iter.hasNext()) {
            LogicComponent lc = (LogicComponent)iter.next();
            
            model.removeComponent(lc.getId());
            gui.removeFromSandbox(lc.getComponentImage());
        }
        
        gui.resizeSandbox();
        gui.repaintSandbox();
    }
    
    /**
     * Unexecutes the command.
     */
    public void unexecute() {
        
        // put the components back
        Iterator iter = components.iterator();
        while (iter.hasNext()) {
            LogicComponent lc = (LogicComponent)iter.next();
            
            model.addComponent(lc.getId(), lc);
            gui.addToSandbox(lc.getComponentImage());
        }

        // insert the individual wire nodes back into their wire's path
        for (int i = wireNodes.size() - 1; i >= 0; i--) {
            WireNode wn = (WireNode)wireNodes.get(i);
            int pathPosition = ((Integer)wireNodePositions.get(i)).intValue();
            
            // put the node back into its wire's path
            wn.getOwner().insertNodeAt(wn, pathPosition);
            gui.addToSandbox(wn);
        }

        // replace the wires
        iter = wires.iterator();
        while (iter.hasNext()) {   
            Wire wire = (Wire)iter.next();
            
            model.connectComponents(wire);
            gui.addToSandbox(wire.getWireImage());
        }

        gui.resizeSandbox();
        gui.repaintSandbox();
    }
    
    /** the selection as pass in the constructor */
    private ArrayList selection;
    /** the set of selected logic components */
    private HashSet components;
    /** the set of wires attached to the selected components */
    private HashSet wires;
    /** the list of independent wire nodes to be deleted */
    private ArrayList wireNodes;
    /** the list of path positions of the corresponding wire node entries in
     * wireNodes */
    private ArrayList wireNodePositions;
}
