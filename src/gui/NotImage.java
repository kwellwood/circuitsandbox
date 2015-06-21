/*
 * NotImage.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.NotGate;

/**
 * The gui part of a NOT gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class NotImage extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>NotImage</code>, used by the toybox.
     */
    public NotImage() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>NotImage</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public NotImage(Gui gui, Point location) {
        super(gui, location);
        
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "NotGate.png");
        loadImages(imageList);
    }

    /**
     * Returns a string that uniquely identifies each <code>NotImage</code>
     * object as a not gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return NotGate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the not gate, for use in the toybox.
     *
     * @return the string <code>"NOT"</code>
     */
    public String toString() {
        return "NOT";
    }
    
}
