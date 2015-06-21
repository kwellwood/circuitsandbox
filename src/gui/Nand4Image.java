/*
 * Nand4Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Nand4Gate;


/**
 * <code>Nand4Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Nand4Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nand2Image
 * @see Nand3Image
 */
public class Nand4Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>Nand4Image</code>, used by the toybox.
     */
    public Nand4Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>Nand4Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Nand4Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Nand4Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>Nand4Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Nand4Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"NAND (4)"</code>
     */
    public String toString() {
        return "NAND (4)";
    }
    
}
