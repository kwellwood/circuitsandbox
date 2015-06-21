/*
 * DFlipFlopImage.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.DFlipFlop;


/**
 * <code>DFlipFlopImage</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to DFlipFlop.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class DFlipFlopImage extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>DFlipFlopImage</code>, used by the toybox.
     */
    public DFlipFlopImage() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>DFlipFlopImage</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public DFlipFlopImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "DFlipFlop.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>DFlipFlopImage</code>
     * object as a D Flip-Flop.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return DFlipFlop.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the component, for use in the toybox.
     *
     * @return the string <code>"D FLIP-FLOP"</code>
     */
    public String toString() {
        return "D FLIP-FLOP";
    }
    
}
