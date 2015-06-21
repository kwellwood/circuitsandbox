/*
 * PlaceholderPin.java
 *
 * Created on March 13, 2005, 12:28 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JComponent;
import model.LogicComponent;

/**
 * <p>Represents the pin of a custom component before the component is created.
 * Each input pin of a custom component is represented by exactly one output
 * pin of an input component in the model, and each output pin of a custom
 * component is represented by exactly one input pin of an output component in
 * the model.</p>
 * 
 * <p>This class extends JComponent so it can be arranged visually by
 * ComponentDesignPane when designing the custom component.</p>
 *
 * <p>The relavent information stored in a PlaceHolderPin is:<br>
 * <ul>
 *    <li>the input/output component and pin number thats the place holder for
 *    the custom component's pin</li>
 *    <li>the location of the pin on the custom component's image (use
 *    <code>getCenter</code> to retrieve it</li>
 * </ul>
 * 
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class PlaceholderPin extends JComponent {

    /**
     * Constructs a new Pin.
     *
     * @param cdp the parent ComponentDesignPane
     * @param type the type of pin on the new custom component (input or
     * output) (used by <code>paint()</code>)
     * @param lc the logic component associated with it
     * @param x the x coordinate of the center
     * @param y the y coordinate of the center
     */
    public PlaceholderPin (ComponentDesignPane cdp, int type, LogicComponent lc,
            int x, int y) {
        this.cdp = cdp;
        this.type = type;
        this.component = lc;

        setLayout(null);
        setOpaque(false);
        setSize(RADIUS * 2 + 1, RADIUS * 2 + 1);
        centerAt(x, y);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                PlaceholderPin.this.mousePressed(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                PlaceholderPin.this.mouseEntered(evt);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                PlaceholderPin.this.mouseDragged(evt);
            }
        });
    }

    /**
     * Returns the location of the center of the pin, relative to the
     * pinBounds rectangle of the parent ComponentDesignPane. Use this to
     * get the location to be used for the custom component.
     *
     * @return the relative center point
     */
    public Point getCenter() {
        return new Point(
                getX() + RADIUS - cdp.getPinBounds().x,
                getY() + RADIUS - cdp.getPinBounds().y);
    }

    /**
     * Centers the <code>Pin</code> at the given location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void centerAt(int x, int y) {
        setLocation(x - RADIUS, y - RADIUS);
    }

    /**
     * Returns the logic component that is currently taking the place of the
     * custom component pin. A pin number is also needed.
     *
     * @return a logic component
     */
    public LogicComponent getLogicComponent() {
        return component;
    }
    
    /**
     * Draws the Pin.
     *
     * @param g the graphics to use
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(1.0f));
        if (type == INPUT) {
            g2.setColor(new Color(255, 0, 0, 128));
        } else {
            g2.setColor(new Color(0, 0, 255, 128));
        }
        g2.fillOval(0, 0, RADIUS * 2, RADIUS * 2);
        g2.setColor(Color.black);
        g2.drawOval(0, 0, RADIUS * 2, RADIUS * 2);

        g2.setColor(Color.white);
        // horizontal line
        g2.drawLine(getWidth()/2 - 2, getHeight()/2,
                getWidth()/2 + 2, getHeight()/2);

        // vertical line
        g2.drawLine(getWidth()/2, getHeight()/2 - 2,
                getWidth()/2, getHeight()/2 + 2);
    }

    private void mousePressed(MouseEvent evt) {
        cdp.remove(this);
        cdp.add(this, 0);

        if (Util.isLeftClick(evt)) {
            mousePressLeft = true;
        } else {
            mousePressLeft = false;
        }
        
        // ---- LEFT MOUSE BUTTON ----
        if (mousePressLeft) {
            leftClickedX = evt.getX();
            leftClickedY = evt.getY();
        }
    }

    private void mouseDragged(MouseEvent evt) {        
        // ---- LEFT MOUSE BUTTON ----
        if (mousePressLeft) {
            // move the pin
            int distX = (evt.getX() - leftClickedX);
            int distY = (evt.getY() - leftClickedY);
            setLocation(getX() + distX, getY() + distY);

            // clip the pin to its boundaries
            Rectangle bounds = cdp.getPinBounds();
            if (getX() + RADIUS < bounds.x) {
                setLocation(bounds.x - RADIUS, getY());
            } else if (getX() + RADIUS > bounds.x + bounds.width) {
                setLocation(bounds.x + bounds.width - RADIUS, getY());
            }
            if (getY() + RADIUS < bounds.y) {
                setLocation(getX(), bounds.y - RADIUS);
            } else if (getY() + RADIUS > bounds.y + bounds.height) {
                setLocation(getX(), bounds.y + bounds.height - RADIUS);
            }

            cdp.repaint();
        }
    }
    
    private void mouseEntered(MouseEvent evt) {
        cdp.setMouseOverPin(this);
    }

    /** the x coordinate of the mouse when the left button was pressed (in
    * screen units) */
    protected int leftClickedX;
    /** the y coordinate of the mouse when the left button was pressed (in
    * screen units) */
    protected int leftClickedY;
    /** flag indicating the mouse button pressed was initially the left */
    private boolean mousePressLeft = false;
    /** the parent ComponentDesignPane */
    private ComponentDesignPane cdp;
    /** the type of pin (input or output) */
    private int type;
    /** the logic component in the model */
    private LogicComponent component;

    /** the type of an input pin on the custom component */
    public static final int INPUT = 0;
    /** the type of an output pin on the custom component */
    static public final int OUTPUT = 1;
    /** the radius of the pin */
    static public final int RADIUS = ComponentPin.RADIUS;
}
