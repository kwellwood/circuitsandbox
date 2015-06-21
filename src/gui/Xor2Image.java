/*
 * Xor2Image.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.Xor2Gate;


/**
 * <code>Xor2Image</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to Xor2Gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 */
public class Xor2Image extends ComponentImage {
    
    /** Creates a new instance of Xor2Image, used by Toybox */
    public Xor2Image() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>Xor2Image</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */ 
    public Xor2Image(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Xor2Gate.png");
        loadImages(imageList);
    }

    /*
     * Returns a string that uniquely identifies each <code>Xor2Image</code>
     * object as a two-input and gate.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Xor2Gate.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the or gate, for use in the toybox.
     *
     * @return the string <code>"XOR (2)"</code>
     */
    public String toString() {
        return "XOR (2)";
    }
    
}
