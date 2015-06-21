/*
 * WireNode.java
 *
 * Created on March 3, 2005, 10:59 AM
 */

package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A node on a wire (away from input/output pins) that can be dragged to change
 * the shape of a the wire.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class WireNode extends Displayable {
    
    /**
     * Constructs a new <code>WireNode</code> object for the path of a wire.
     * The owner of a wire node is its wire image.
     *
     * @param gui the gui
     * @param x the x coordinate of the center
     * @param y the y coordinate of the center
     * @param owner the wire to which this node belongs
     */
    public WireNode(Gui gui, int x, int y, WireImage owner) {
        super(gui, new Point(x, y));
        this.owner = owner;
        
        setLayout(null);
        setStdSize(RADIUS * 2 + 1, RADIUS * 2 + 1);
        centerAt(new Point(x, y));
    
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                WireNode.this.mouseEntered(event);
            }
            public void mouseExited(MouseEvent event) {
                WireNode.this.mouseExited(event);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                WireNode.this.mousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                WireNode.this.mouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                WireNode.this.mouseDragged(evt);
            }
        });
    }
    
    /**
     * Identifies <code>ComponentPin</code> objects as the
     * <code>COMPONENT_PIN</code> type of <code>Displayable</code>.
     * 
     * @return <code>Displayable.COMPONENT_PIN</code>
     */
    public int getDisplayableType() {
        return Displayable.WIRE_NODE;
    }

    /**
     * Returns the <code>WireImage</code> object that owns this node.
     * 
     * @return the node's owner
     */
    public WireImage getOwner() {
        return owner;
    }
    
    /**
     * Returns the position of the in its owner's path. The positions are base 0
     * and start from the source end of the wire. 
     *
     * @return the node's position
     */
    public int getPathPosition() {
        return owner.getPathPosition(this);
    }
    
    /**
     * Adds the node to the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void addToSandbox(Sandbox sandbox) {
        sandbox.add(this, 0);
        zoom();
    }
    
    /**
     * Removes the node from the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void removeFromSandbox(Sandbox sandbox) {
        sandbox.remove(this);
        highlighted = false;
    }
    
    /**
     * Sets highlight flag to true when the cursor is above the node.
     *
     * @param event the <code>MouseEvent</code>
     */
    private void mouseEntered(MouseEvent event) {
        if (gui.isNodeClickable() && !gui.isSimulating()) {
            highlighted = true;
            repaint();
        }
    }

    /**
     * Sets highlight flag to false when the cursor moves off the node.
     *
     * @param event the <code>MouseEvent</code>
     */
    private void mouseExited(MouseEvent event) {
        highlighted = false;
        repaint();
    }
    
    private void mouseReleased(MouseEvent evt) {
        if (!gui.isSimulating()) {
            displayableMouseReleased(evt);
        }
    }      
    
    private void mouseDragged(MouseEvent evt) {
        if (!gui.isSimulating()) {
            displayableMouseDragged(evt);
        }
    }
    
    private void mousePressed(MouseEvent evt) {
        if (!gui.isSimulating()) {
            displayableMousePressed(evt);
        }
    }
    
    /**
     * Paints the wire node.
     *
     * @param g the <code>Graphics</code> to be used
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(gui.getZoomFactor(), gui.getZoomFactor());

        if (selected) {
            g2.setStroke(lineStroke);
            g2.setColor(selectedColor);
            g2.fillOval(0, 0, RADIUS * 2, RADIUS * 2);
            g2.setColor(edgeColor);
            g2.drawOval(0, 0, RADIUS * 2, RADIUS * 2);
        } else if (highlighted) {
            g2.setStroke(lineStroke);
            g2.setColor(highlightedColor);
            g2.fillOval(0, 0, RADIUS * 2, RADIUS * 2);
            g2.setColor(edgeColor);
            g2.drawOval(0, 0, RADIUS * 2, RADIUS * 2);
        }
    }
    
    /** the wire image whose path contains this node */
    private WireImage owner;
    /** flag indicating the mouse is over the node */
    private boolean highlighted = false;
    /** the radius of a wire node circle */
    private static final int RADIUS = 5;
    /** the stroke drawn around the circle */
    private static final Stroke lineStroke = new BasicStroke(1.0f);
    /** the color of the edge of the circle */
    private static final Color edgeColor = Color.black;
    /** the color of the circle when it is highlighted */
    private static final Color highlightedColor = new Color(96, 96, 96, 128);
    /** the color of the circle when it is selected */
    private static final Color selectedColor = new Color(255, 32, 32, 212);
}
