/*
 * WireImage.java
 *
 * Created on February 25, 2005, 12:39 PM
 */

package gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import model.Wire;

/**
 * The gui part of a wire. Includes a collection of wire nodes.
 *
 * @author Daneil Stahl
 * @author Kevin Wellwood
 */
public class WireImage extends Displayable {
    
    /** 
     * Creates a new instance of WireImage.
     *
     * @param gui the gui
     */
    public WireImage(Gui gui) {
        super(gui, new Point(0, 0));
        nodes = new ArrayList();
        color = Color.black;
    }
    
    /**
     * Returns the coordinates of all the nodes and the starting and ending
     * points of the wire. Coordinates are in standard units.
     *
     * @return the list of coordinates from source to sink
     */
    public ArrayList getPoints() {
        ArrayList locations = new ArrayList();
        
        // add the source pin's location if the source is set
        if (wire.getSource() != null) {
            locations.add(wire.getSource().getComponentImage().
                    getOutputPinLocation(wire.getSourcePin()));
        }
        
        // add all the path nodes
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            locations.add(((WireNode)iter.next()).getStdCenter());
        }
        
        // add the sink pin's location if the sink is set
        if (wire.getSink() != null) {
            locations.add(wire.getSink().getComponentImage().
                    getInputPinLocation(wire.getSinkPin()));
        }
        
        return locations;
    }
    
    /**
     * Returns a list of the wire nodes, in order from sink to source.
     *
     * @return the path node list
     */
    public ArrayList getNodes() {
        return nodes;
    }
    
    /**
     * Sets the corresponding <code>wire</code> object in the model. If the
     * <code>wire</code> isn't already associated with this
     * <code>wireImage</code>, it will be associatesd with this.
     *
     * @param wire the wire to set
     */
    public void setWire(Wire wire) {
        this.wire = wire;
        if (wire.getWireImage() != this) {
            wire.setWireImage(this);
        }
    }
    
    /**
     * Returns the corresponding logical <code>Wire</code> object.
     *
     * @return the wire
     */
    public Wire getWire() {
        return wire;
    }
    
    /**
     * Returns the wire's color.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Adds a new node at the source end of the wire. Location is in standard
     * coordinates. If a node on this wire already exists at x, y no node
     * will be added and <code>null</code> well be returned.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the created node
     */
    public WireNode addNodeToFront(int x, int y) {
        if (nodeAt(x, y)) { return null; }
        
        WireNode node = new WireNode(gui, x, y, this);
        nodes.add(0, node);
        return node;
    }
    
    /**
     * Adds a new node at the sink end of the wire. Location is in standard
     * coordinates. If a node on this wire already exists at x, y then no node
     * will be added and <code>null</code> will be returned.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the created node
     */
    public WireNode addNodeToBack(int x, int y) {
        if (nodeAt(x, y)) { return null; }
        
        WireNode node = new WireNode(gui, x, y, this);
        nodes.add(node);
        return node;
    }
    
    /**
     * Inserts a new node, splitting a segment of the path. The location must
     * be in standard coordinates. If a node on this wire already exists at
     * x, y then no node will be added and <code>null</code> will be returned.
     *
     * @param segment the segment to split, base 0 starting from the source end
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the created node
     */
    public WireNode splitPathSegment(int segment, int x, int y) {
        WireNode node = new WireNode(gui, x, y, this);
        nodes.add(segment, node);
        return node;
    }

    /**
     * Inserts a node, splitting a segment of the path. This method differs
     * from <code>splitPathSegment</code> in that it takes an existing node
     * object and adds it to the path.
     *
     * @param node the node to add
     * @param position the slot to insert the node at, base 0 from the source
     */
    public void insertNodeAt(WireNode node, int position) {
        nodes.add(position, node);
    }

    /**
     * Returns the position of a wire node in the path. The positions are base 0
     * and start from the source end of the wire. This method is called by the
     * wire node that wants to know its position, so call
     * <code>wireNode.getPathPosition</code> instead.
     *
     * @param node the node to return the position of
     *
     * @return the node's position
     */
    public int getPathPosition(WireNode node) {
        return nodes.indexOf(node);
    }
    
    /**
     * Removes a node from the wire.
     *
     * @param node the node to remove
     */
    public void removeNode(WireNode node) {
        nodes.remove(node);
    }
    
    /**
     * Identifies <code>WireImage</code> objects as the
     * <code>WIRE</code> type of <code>Displayable</code>.
     *
     * @return <code>Displayable.WIRE</code>
     */
    public int getDisplayableType() {
        return Displayable.WIRE;
    }
    
    /**
     * Adds the wire and its nodes to the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void addToSandbox(Sandbox sandbox) {
        sandbox.add(this);
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            ((Displayable)iter.next()).addToSandbox(sandbox);
        }
    }
    
    /**
     * Removes the wire and its nodes from the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void removeFromSandbox(Sandbox sandbox) {
        sandbox.remove(this);
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            ((Displayable)iter.next()).removeFromSandbox(sandbox);
        }
    }
    
    /**
     * Returns the bounding rectangle for the wire that encompasses all points
     * on the wire path including end points. The rectangle returned is in
     * standard units.
     *
     * @return the bounding rectangle
     */
    public Rectangle getBounds() {
        ArrayList points = getPoints();
        
        Point p = (Point)points.get(0);
        int x1 = p.x; int x2 = p.x;
        int y1 = p.y; int y2 = p.y;
        
        for (int i = 1; i < points.size(); i++) {
            Point pp = (Point)points.get(i);
            if (pp.x < x1) x1 = pp.x; if (pp.x > x2) x2 = pp.x;
            if (pp.y < y1) y1 = pp.y; if (pp.y > y2) y2 = pp.y;
        }
        
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
    
    /**
     * Checks if any of the nodes in the path are at the given location. The
     * location is in standard coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return <code>true</code> if any path nodes are at <code>x</code>,
     * <code>y</code>
     */
    private boolean nodeAt(int x, int y) {
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            Point p = ((WireNode)iter.next()).getStdCenter();
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }
    
    /** the corresponding model <code>wire</code> object */
    private Wire wire;
    /** the list of nodes in the path from source to sink */
    private ArrayList nodes;
    /** the color of the wire */
    private Color color;
}
