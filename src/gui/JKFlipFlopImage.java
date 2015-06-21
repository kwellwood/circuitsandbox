/*
 * JKFlipFlopImage.java
 *
 * Created on February 5, 2005, 5:06 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;
import model.LogicComponent;
import model.JKFlipFlop;


/**
 * <code>JKFlipFlopImage</code> extends the abstract class <code>ComponentImage</code>,
 * and is the gui counterpart to JKFlipFlop.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class JKFlipFlopImage extends ComponentImage {
    
    /**
     * Constructs a new instance of <code>JKFlipFlopImage</code>, used by the toybox.
     */
    public JKFlipFlopImage() {
        this(null, null);
    }

    /**
     * Constructs a new instance of <code>JKFlipFlopImage</code> with a given location
     * in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public JKFlipFlopImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "JKFlipFlop.png");
        loadImages(imageList);
    }
    
    /*
     * Returns a string that uniquely identifies each <code>JKFlipFlopImage</code>
     * object as a JK Flip-Flop.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return JKFlipFlop.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the component, for use in the toybox.
     *
     * @return the string <code>"JK FLIP-FLOP"</code>
     */
    public String toString() {
        return "JK FLIP-FLOP";
    }
    
}
