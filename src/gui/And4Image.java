/*
 * And4Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.And4Gate;


/**
 * <code>And4Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to And4Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see And2Image
 * @see And3Image
 */
public class And4Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>And4Image</code>, used by the toybox.
     */
    public And4Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>And4Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public And4Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "And4Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>And4Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return And4Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"AND (4)"</code>
     */
    public String toString() {
        return "AND (4)";
    }
    
}
