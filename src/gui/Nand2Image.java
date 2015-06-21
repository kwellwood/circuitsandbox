/*
 * Nand2Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Nand2Gate;


/**
 * <code>Nand2Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Nand2Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nand3Image
 * @see Nand4Image
 *
 * @author Daniel Stahl, Kevin Wellwood
 */
public class Nand2Image extends ComponentImage {
    
    /** Creates a new instance of Nand2Image, used by Toybox */
    public Nand2Image() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>Nand2Image</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public Nand2Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Nand2Gate.png");
        loadImages(imageList);
    }

    /*
     * Returns a string that uniquely identifies each <code>Nand2Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */    
    public String getTypeString() {
        return Nand2Gate.TYPE_STRING;
    }
    
     /**
     * Returns the plain english name of the or gate, for use in the toybox.
     *
     * @return the string <code>"NAND (2)"</code>
     */
   public String toString() {
        return "NAND (2)";
    }
    
}
