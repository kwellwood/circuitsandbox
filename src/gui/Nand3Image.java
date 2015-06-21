/*
 * Nand3Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Nand3Gate;


/**
 * <code>Nand3Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Nand3Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nand2Image
 * @see Nand4Image
 */
public class Nand3Image extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>Nand3Image</code>, used by the toybox.
     */
    public Nand3Image() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>Nand3Image</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Nand3Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Nand3Gate.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>Nand3Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Nand3Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the and gate, for use in the toybox.
     *
     * @return the string <code>"NAND (3)"</code>
     */
    public String toString() {
        return "NAND (3)";
    }
    
}
