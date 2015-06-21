/*
 * InsertWireNode.java
 *
 * Created on March 7, 2005, 11:28 PM
 */

package controller;

import gui.Gui;
import gui.WireImage;
import gui.WireNode;

/**
 * Implements the actions for adding a new node to a wire, splitting a segment.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class InsertWireNode extends Command {
    
    /**
     * Constructs a new <code>InsertWireNode</code> command object.
     *
     * @param gui the gui
     * @param wireImage the wire image object involved
     * @param segment the segment to split, base 0 starting from the source
     * @param x the x coordinate of the node
     * @param y the y coordinate of the node
     */
    public InsertWireNode(Gui gui, WireImage wireImage, int segment,
            int x, int y) {
        super(gui, null, "insert node");
        
        this.gui = gui;
        this.wireImage = wireImage;
        this.segment = segment;
        this.nodeX = x;
        this.nodeY = y;
    }
    
    /**
     * Executes the command.
     */
    public void execute() {
        boolean firstTimeExecuted = (node == null);
        
        node = wireImage.splitPathSegment(segment, nodeX, nodeY);
        gui.addToSandbox(node);
        if (firstTimeExecuted) { gui.select(node); }
        gui.resizeSandbox();
        gui.repaintSandbox();
    }

    /**
     * Unexecutes the command.
     */
    public void unexecute() {
        gui.removeFromSelection(node);
        gui.removeFromSandbox(node);
        wireImage.removeNode(node);
        gui.resizeSandbox();
        gui.repaintSandbox();
    }
    
    /** the wire image into whose path the node is being inserted */
    private WireImage wireImage;
    /** the wire node to be added */
    private WireNode node;
    /** the segment to split; (base 0 starting from the source end) */
    private int segment;
    /** the x coordinate of the node */
    private int nodeX;
    /** the y coordinate of the node */
    private int nodeY;
}
