/*
 * Or2Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Or2Gate;


/**
 * <code>Or2Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Or2Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Or3Image
 * @see Or4Image
 */
public class Or2Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>Or2Image</code>, used by the toybox.
     */
    public Or2Image() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>Or2Image</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */ 
    public Or2Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Or2Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>Or2Image</code>
     * object as a two-input or gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Or2Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the or gate, for use in the toybox.
     *
     * @return the string <code>"OR (2)"</code>
     */
    public String toString() {
        return "OR (2)";
    }
    
}
