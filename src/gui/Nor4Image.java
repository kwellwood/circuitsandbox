/*
 * Nor4Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Nor4Gate;


/**
 * <code>Nor4Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Nor4Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nor2Image
 * @see Nor3Image
 */
public class Nor4Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>Nor4Image</code>, used by the toybox.
     */
    public Nor4Image() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>Nor4Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Nor4Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Nor4Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>Nor4Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Nor4Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"NOR (4)"</code>
     */
    public String toString() {
        return "NOR (4)";
    }
    
}
