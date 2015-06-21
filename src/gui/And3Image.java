/*
 * And3Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.And3Gate;


/**
 * <code>And3Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to And3Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see And2Image
 * @see And4Image
 */
public class And3Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>And3Image</code>, used by the toybox.
     */
    public And3Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>And3Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public And3Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "And3Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>And3Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return And3Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"AND (3)"</code>
     */
    public String toString() {
        return "AND (3)";
    }
    
}
