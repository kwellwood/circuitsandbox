/*
 * SwitchImage.java
 *
 * Created on March 12, 2005, 1:21 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.List;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Switch;

/**
 * The gui part of an ON/OFF switch.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class SwitchImage extends ComponentImage {
    
    /** Creates a new instance of SwitchImage */
    public SwitchImage() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>SwitchImage</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public SwitchImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Switch0.png");
        imageList.add(ComponentImage.IMAGE_PATH + "Switch1.png");
        loadImages(imageList);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                SwitchImage.this.mousePressed(evt);
            }
        });
    }
    
    /**
     * Toggles the switch's state. Called on mouse press.
     *
     * @param evt the mouse event
     */
    private void mousePressed(MouseEvent evt) {
        if (gui.isSimulating()) {
            if (Util.isLeftClick(evt)) {
                turnedOn = !turnedOn;
                frame = (turnedOn ? (byte)1 : (byte)0);
                logicComponent.setState((byte)frame);
                repaint();
            }
        }
    }

    /**
     * <p>Returns the state of the switch image.</p>
     * <ul>
     *     <li><code>0</code> - Off</li>
     *     <li><code>1</code> - On</li>
     * </ul>
     *
     * @return the button's state
     */
    public byte getState() {
        return ( turnedOn ? (byte)1 : (byte)0 );
    }
    
    /**
     * Returns a string that uniquely identifies each <code>SwithcImage</code>
     * object as a toggle switch.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Switch.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the switch, for use in the toybox.
     *
     * @return the string <code>"Toggle Switch"</code>
     */
    public String toString() {
        return "Toggle Switch";
    }
   
   /** flag indicating the switch is in the On position */
   private boolean turnedOn = false;
}
