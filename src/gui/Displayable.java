/*
 * Displayable.java
 *
 * Created on February 5, 2005, 2:37 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 * <code>Displayable</code> is the superclass of all logic components, input/
 * output pins, wires, and wire nodes.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public abstract class Displayable extends JComponent {
    
    /**
     * Constructs a new <code>Displayable</code> object.
     *
     * @param gui the gui
     * @param location the location of the <code>Displayable</code> in standard
     * coordinates
     */
    public Displayable(Gui gui, Point location) {
        this.gui = gui;
        
        setLayout(null);
        setOpaque(false);
        
        if (location != null) {
            setStdLocation(location);
        }
    }
    
    /**
     * Returns <code>true</code> if the <code>Displayable</code> is currently
     * selected.
     *
     * @return <code>true</code> if the object is selected, otherwise
     * <code>false</code>
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Selects or unselects the <code>Displayable</code>.
     *
     * @param selected the selection state
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * Updates the magnification level of the component to the gui's current
     * magnification level. This involves changing the location of the component
     * in screen coordinates, as well as resizing
     * the component in screen coordinates.
     */
    public void zoom() {
        setStdSize(getStdWidth(), getStdHeight());
        setStdLocation(getStdLocation());
    }
    
    /**
     * Brings the <code>Displayable</code> to the front of the Z-order in the
     * sandbox.
     *
     * @param sandbox the sandbox
     */
    public void bringToFront(Sandbox sandbox) {
        sandbox.remove(this);
        sandbox.add(this, 0);
    }
    
    /**
     * Centers the <code>Displayable</code> at the given location. The location
     * must be in standard coordinates.
     *
     * @param location the location
     */
    public void centerAt(Point location) {
        setStdLocation(location.x - getStdWidth() / 2, 
                location.y - getStdHeight() / 2);
    }
    
    /**
     * Returns the center point of the <code>displayable</code> in standard
     * coordinates.
     *
     * @return the center point
     */
    public Point getStdCenter() {
        return new Point(stdLocation.x + stdSize.width / 2,
                stdLocation.y + stdSize.height / 2);
    }
    
    /**
     * Moves the <code>Displayable</code> in its parent container by a relative
     * amount. The distances should be in standard coordinates
     *
     * @param xShift the horizontal distance
     * @param yShift the vertical distance
     */
    public void shiftLocation(int xShift, int yShift) {
        setStdLocation(getStdX() + xShift, getStdY() + yShift);
    }

    /**
     * Sets the location, using standard coordinates.
     *
     * @param location the new location
     */
    public void setStdLocation(Point location) {
        setStdLocation(location.x, location.y);
    }
    
    /**
     * Sets the location, using standard coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setStdLocation(int x, int y) {
        stdLocation = new Point(x, y);
        setLocation((int)(x * gui.getZoomFactor()),
                (int)(y * gui.getZoomFactor()));
    }
    
    /** 
     * Returns the location in standard coordinates.
     *
     * @return the location
     */
    public Point getStdLocation() {
        return stdLocation;
    }
    
    /**
     * Returns the x coordinate of the location in standard coordinates.
     *
     * @return the x coordinate
     */
    public int getStdX() {
        return stdLocation.x;
    }
    
    /**
     * Returns the y coordinate of the location in standard coordinates.
     *
     * @return the y coordinate
     */
    public int getStdY() {
        return stdLocation.y;
    }
    
    /**
     * Sets the size in standard units.
     *
     * @param size the new dimensions
     */
    public void setStdSize(Dimension size) {
        setStdSize(size.width, size.height);
    }
    
    /**
     * Sets the size in standard units.
     *
     * @param width the new width
     * @param height the new height
     */
    public void setStdSize(int width, int height) {
        stdSize = new Dimension(width, height);
        
        if (gui != null) {
            setSize((int)(width * gui.getZoomFactor()),
                    (int)(height * gui.getZoomFactor()));
        }
        else {
            setSize(width, height);
        }
    }
    
    /**
     * Returns the size in standard units.
     *
     * @return the size
     */
    public Dimension setStdSize() {
        return stdSize;
    }
    
    /**
     * Returns the width in standard units.
     *
     * @return the width
     */
    public int getStdWidth() {
        return stdSize.width;
    }
    
    /**
     * Returns the height in standard units.
     *
     * @return the height
     */
    public int getStdHeight() {
        return stdSize.height;
    }
        
    /**
     * Handles mousePressed events for the <code>Displayable</code>. This method
     * takes care of selecting/unselecting the object. It is called by
     * event handling methods of classes that extend <code>Displayable</code>.
     *
     * @param event the mouse event
     */
    protected void displayableMousePressed(MouseEvent event) {
        gui.bringToFront(this);

        // Record the mouse button that was initially pressed so user
        // can't switch between left mouse and right mouse before the
        // mouse release event
        if (Util.isLeftClick(event)) {
            mousePressLeft = true;
            mousePressRight = false;
        } else if (Util.isRightClick(event)) {
            mousePressLeft = false;
            mousePressRight = true;
        }
        
        // ---- LEFT MOUSE BUTTON ----
        if (mousePressLeft && !gui.isCreatingWire() &&
                !gui.isAddingComponent()) {

            prevLocation = getStdLocation();
            ignoreLeftMouse = false;
            leftClickedX = event.getX();
            leftClickedY = event.getY();

            if (!Util.isShiftClick(event)) {
                // Shift is not being held
                if (!selected) { gui.select(this); }
            } else {
                // shift is being held
                if (!selected) {
                    // its not selected, select it
                    gui.addToSelection(this);             
                } else {
                    // it is selected, unselect it
                    gui.removeFromSelection(this);
                    ignoreLeftMouse = true;
                }
            }
            gui.repaintSandbox();
        } else if (mousePressLeft) {
            // if it was a left click but the user is adding a component or
            // creating a wire then ignore the leftmouse drag and release
            // events
            ignoreLeftMouse = true;
        }

        // ---- RIGHT MOUSE BUTTON ----
        if (mousePressRight) {
            if (gui.isCreatingWire()) gui.cancelWire();
            if (gui.isAddingComponent()) gui.setComponentToAdd(null);
            gui.repaintSandbox();
        }
    }
    
    /**
     * Handles mouseDragged events for the <code>Displayable</code>. This method
     * takes care of moving the object. It is called by event handling methods
     * of classes that extend <code>Displayable</code>.
     *
     * @param event the mouse event
     */
    protected void displayableMouseDragged(MouseEvent event) {
        // ---- LEFT MOUSE BUTTON ----
        if (mousePressLeft && !ignoreLeftMouse) {
            // The distance the mouse moved since last event in standard
            // coordinates
            float distX = (event.getX() - leftClickedX) / gui.getZoomFactor();
            float distY = (event.getY() - leftClickedY) / gui.getZoomFactor();

            float gridsize = Sandbox.GRID_SIZE;

            int shiftX = (int)(Math.round(distX / gridsize) * gridsize);
            int shiftY = (int)(Math.round(distY / gridsize) * gridsize);

            if (shiftX != 0 || shiftY != 0) {
                gui.moveSelection(shiftX, shiftY);
                beingDragged = true;
            }
        }
    }
    
    /**
     * Handles mouseReleased events for the <code>Displayable</code>. This
     * method takes care of moving the object. It is called by event handling
     * methods of classes that extend <code>Displayable</code>.
     *
     * @param event the mouse event
     */
    protected void displayableMouseReleased(MouseEvent event) {
        // ---- LEFT MOUSE BUTTON ----
        if (mousePressLeft) {
            if (beingDragged) {
                gui.selectionMoved(getStdX() - prevLocation.x,
                        getStdY() - prevLocation.y);
                beingDragged = false;
            }
        }
    }
    
    /**
     * Extend this method to add the displayable to the given sandbox.
     *
     * @param sandbox the sandbox
     */
    public abstract void addToSandbox(Sandbox sandbox);

    /**
     * Extend this method to remove the displayable from the given sandbox.
     *
     * @param sandbox the sandbox
     */
    public abstract void removeFromSandbox(Sandbox sandbox);

    /**
     * Extend this method to return a value identifying the type of
     * displayable that the subclass is. Possible values to return are
     * <code>COMPONENT</code> for a component image, <code>COMPONENT_PIN</code>
     * for a component pin, <code>WIRE</code> for a wire, and 
     * <code>WIRE_NODE</code> for a wire path node.
     *
     * @return the type of <code>Displayable</code>
     */
    public abstract int getDisplayableType();
    
    /** the gui */
    protected Gui gui;
    /** the x coordinate of the mouse when the left button was pressed (in
     * screen units) */
    protected int leftClickedX;
    /** the y coordinate of the mouse when the left button was pressed (in
     * screen units) */
    protected int leftClickedY;
    /** flag indicating this Displayable is currently selected */
    protected boolean selected = false;
    /** flag indicating left mouse drag events should be ignored until mouse
     * button release */
    protected boolean ignoreLeftMouse = false;
    /** the location of the Displayable before it was dragged (in screen
      * coordinates */
    protected Point prevLocation;
    
    /** the location in standard coordinates */
    private Point stdLocation;
    /** the size in standard units */
    private Dimension stdSize;
    
    /** flag indicating the Displayable is being dragged by the mouse */
    private boolean beingDragged = false;
    
    /** flag indicating mouse press on component was a left click */
    private boolean mousePressLeft = false;
    /** flag indicating mouse press on component was a right click */
    private boolean mousePressRight = false;
    
    public static final int COMPONENT = 0;
    public static final int COMPONENT_PIN = 1;
    public static final int WIRE = 2;
    public static final int WIRE_NODE = 3;
}
