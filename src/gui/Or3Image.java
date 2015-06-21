/*
 * Or3Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Or3Gate;


/**
 * <code>Or3Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Or3Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Or2Image
 * @see Or4Image
 */
public class Or3Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>Or3Image</code>, used by the toybox.
     */
    public Or3Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>Or3Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Or3Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Or3Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>Or3Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Or3Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"OR (3)"</code>
     */
    public String toString() {
        return "OR (3)";
    }
    
}
