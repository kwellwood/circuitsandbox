/*
 * ButtonImage.java
 *
 * Created on March 9, 2005, 2:02 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.List;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Button;

/**
 * The gui part of a button.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class ButtonImage extends ComponentImage {
    
    /** Creates a new instance of ButtonImage */
    public ButtonImage() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>ButtonImage</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public ButtonImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Button0.png");
        imageList.add(ComponentImage.IMAGE_PATH + "Button1.png");
        loadImages(imageList);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                ButtonImage.this.mousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                ButtonImage.this.mouseReleased(evt);
            }
        });
    }
    
    /**
     * Sets the button's state to On. Called on mouse press.
     *
     * @param evt the mouse event
     */
    private void mousePressed(MouseEvent evt) {
        if (gui.isSimulating()) {
            if (Util.isLeftClick(evt)) {
                logicComponent.setState((byte)1);
                frame = 1;
                repaint();
            }
        }
    }
 
    /**
     * Sets the button's state to Off. Called on mouse release.
     *
     * @param evt the mouse event
     */
    private void mouseReleased(MouseEvent evt) {
        if (gui.isSimulating()) {
            if (Util.isLeftClick(evt)) {
                logicComponent.setState((byte)0);
                frame = 0;
                repaint();
            }
        }
    }

    /**
     * <p>Returns the state of the button image.</p>
     * <ul>
     *     <li><code>0</code> - Off</li>
     *     <li><code>1</code> - On</li>
     * </ul>
     *
     * @return the button's state
     */
    public byte getState() {
        return ( beingPressed ? (byte)1 : (byte)0 );
    }
    
    /**
     * Returns a string that uniquely identifies each <code>ButtonImage</code>
     * object as a push button.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Button.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the button, for use in the toybox.
     *
     * @return the string <code>"Button"</code>
     */
    public String toString() {
        return "Button";
    }
   
   /** flag indicating the button is being pressed */
   private boolean beingPressed = false;
}
