/*
 * Nor3Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Nor3Gate;


/**
 * <code>Nor3Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Nor3Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nor2Image
 * @see Nor4Image
 */
public class Nor3Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>Nor3Image</code>, used by the toybox.
     */
    public Nor3Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>Nor3Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Nor3Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Nor3Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>Nor3Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Nor3Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"NOR (3)"</code>
     */
    public String toString() {
        return "NOR (3)";
    }
    
}
