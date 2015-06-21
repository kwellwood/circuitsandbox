/*
 * ComponentPin.java
 *
 * Created on February 25, 2005, 12:45 PM
 */

package gui;

import circuitsandbox.Util;
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
 * <code>ComponentPin</code> extends <code>Displayable</code> and is the
 * gui representation of an input/output pin.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class ComponentPin extends Displayable {
    
    /**
     * Constructs a new <code>ComponentPin</code> object, given a component
     * image that owns it, a pin type, pin number and location in the sandbox.
     * The pin type must be either <code>INPUT_PIN</code> or
     * <code>OUTPUT_PIN</code>. The location must be in standard coordinates.
     *
     * @param gui the gui
     * @param location the center of the pin, relative to the owner
     * @param owner the component image to which this pin belongs
     * @param pinType the type of pin (an input or an output)
     * @param pinNumber the pin number on the owner component
     */
    public ComponentPin(Gui gui, Point location, ComponentImage owner,
            int pinType, int pinNumber) {
        super(gui, location);
        this.owner = owner;
        this.pinType = pinType;
        this.pinNumber = pinNumber;
        
        setLayout(null);
        setStdSize(RADIUS * 2 + 1, RADIUS * 2 + 1);
        centerAt(new Point(
                owner.getStdX() + location.x,
                owner.getStdY() + location.y));
    
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                ComponentPin.this.mouseEntered(event);
            }
            public void mouseExited(MouseEvent event) {
                ComponentPin.this.mouseExited(event);
            }
            public void mouseClicked(MouseEvent event) {
                ComponentPin.this.mouseClicked(event);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ComponentPin.this.mousePressed(evt);
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
        return Displayable.COMPONENT_PIN;
    }

    /**
     * Returns the <code>ComponentImage</code> object that owns this pin.
     * 
     * @return the pin's owner
     */
    public ComponentImage getOwner() {
        return owner;
    }
    
    /**
     * Adds the pin to the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void addToSandbox(Sandbox sandbox) {
        sandbox.add(this, 0);
        zoom();
    }
    
    /**
     * Removes the pin from the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void removeFromSandbox(Sandbox sandbox) {
        sandbox.remove(this);
        highlighted = false;
    }
    
    /**
     * Sets highlight flag to true when the cursor is above the pin.
     *
     * @param event the <code>MouseEvent</code>
     */
    private void mouseEntered(MouseEvent event) {
        if (!gui.isSimulating()) {
            if (gui.isPinClickable(pinType) && (pinType == OUTPUT_PIN ||
                    owner.getLogicComponent().isInputAvailable(pinNumber))) {
                highlighted = true;
                repaint();
            }            
        }
    }

    /**
     * Sets highlight flag to false when the cursor moves off the pin.
     *
     * @param event the <code>MouseEvent</code>
     */
    private void mouseExited(MouseEvent event) {
        highlighted = false;
        repaint();
    }

    /**
     * Tells the GUI to start creating a wire from this pin.
     *
     * @param event the <code>MouseEvent</code>
     */
    private void mouseClicked(MouseEvent event) {
        if (!gui.isSimulating()) {

        }
    }
    
    /**
     * Cancels creating a wire or a component if the pin is right-clicked on.
     *
     * @param event the <code>MouseEvent</code>
     */
    private void mousePressed(MouseEvent event) {
        if (!gui.isSimulating()) {
            // ---- LEFT CLICK ----
            if (Util.isLeftClick(event) && highlighted) {
                if (!gui.isCreatingWire()) {
                    // begin creating a new wire
                    gui.placeWireLead(owner, pinType, pinNumber);
                    setSelected(true);
                    highlighted = false;
                    repaint();
                } else {
                    // complete the creation of a wire
                    gui.placeWireTail(owner, pinType, pinNumber);
                    highlighted = false;
                    repaint();
                }
            }
            
            // ---- RIGHT CLICK ----
            if (Util.isRightClick(event)) {
                if (gui.isAddingComponent()) gui.setComponentToAdd(null);
                if (gui.isCreatingWire()) gui.cancelWire();
            }
        }
    }
    
    /**
     * Paints the component pin.
     *
     * @param g the <code>Graphics</code> to be used
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(gui.getZoomFactor(), gui.getZoomFactor());

        if (highlighted) {
            // draw highlighted because user's mouse is
            // currently over the pin
            g2.setStroke(lineStroke);
            g2.setColor(highlightedColor);
            g2.fillOval(0, 0, RADIUS * 2, RADIUS * 2);
            g2.setColor(edgeColor);
            g2.drawOval(0, 0, RADIUS * 2, RADIUS * 2);
        } else if (selected) {
            // draw highlighted differently because user is
            // currently creating a wire attached to this pin
            g2.setStroke(lineStroke);
            g2.setColor(selectedColor);
            g2.fillOval(0, 0, RADIUS * 2, RADIUS * 2);
            g2.setColor(edgeColor);
            g2.drawOval(0, 0, RADIUS * 2, RADIUS * 2);
        }
    }
    
    /** the component image containing this pin */
    private ComponentImage owner;
    /** the type of pin (input or output) */
    private int pinType;
    /** the pin number on the owner component */
    private int pinNumber;
    /** the coordinates (in std coords) of the pin */
    private Point location;
    /** flag indicating whether mouse if over the pin */
    private boolean highlighted = false;

    /** the pin type for neither pin type */
    public static final int NO_PINS = 0;
    /** the pin type for an input pin */
    public static final int INPUT_PIN = 1;
    /** the pin type for an output pin */
    public static final int OUTPUT_PIN = 2;
    /** the pin type for both pin types */
    public static final int ALL_PINS = INPUT_PIN | OUTPUT_PIN;
    /** the radius of circle representing the component pin */
    public static final int RADIUS = 5;
    /** the stroke drawn around the circle */
    private static final Stroke lineStroke = new BasicStroke(1.0f);
    /** the color of the edge of the circle */
    private static final Color edgeColor = Color.black;
    /** the color of the circle when it is highlighted */
    private static final Color highlightedColor = new Color(255, 153, 51, 212);
    /** the color of the circle when it is selected */
    private static final Color selectedColor = new Color(255, 102, 0, 212);
}
