/*
 * Nor2Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Nor2Gate;


/**
 * <code>Nor2Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Nor2Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nor3Image
 * @see Nor4Image
 */
public class Nor2Image extends ComponentImage {
    
    /** Creates a new instance of Nor2Image, used by Toybox */
    public Nor2Image() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>Nor2Image</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Nor2Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Nor2Gate.png");
        loadImages(imageList);
    }

    /*
     * Returns a string that uniquely identifies each <code>Nor2Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */    
    public String getTypeString() {
        return Nor2Gate.TYPE_STRING;
    }
    
     /**
     * Returns the plain english name of the or gate, for use in the toybox.
     *
     * @return the string <code>"NOR (2)"</code>
     */
   public String toString() {
        return "NOR (2)";
    }
    
}
