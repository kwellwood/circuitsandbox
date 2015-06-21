/*
 * And2Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.And2Gate;


/**
 * <code>And2Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to And2Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see And3Image
 * @see And4Image
 */
public class And2Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>And2Image</code>, used by the toybox.
     */
    public And2Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>And2Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public And2Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "And2Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>And2Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return And2Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"AND (2)"</code>
     */
    public String toString() {
        return "AND (2)";
    }
    
}
