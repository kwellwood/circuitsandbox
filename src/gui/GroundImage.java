/*
 * GroundImage.java
 *
 * Created on March 13, 2005, 6:47 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.List;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Ground;

/**
 * The gui part of a grounded terminal.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class GroundImage extends ComponentImage {
    
    /** Creates a new instance of GroundImage */
    public GroundImage() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>GroundImage</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public GroundImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Ground.png");
        loadImages(imageList);
    }
    
    /**
     * Returns a string that uniquely identifies each <code>GroundImage</code>
     * object as a grounded terminal.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Ground.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the switch, for use in the toybox.
     *
     * @return the string <code>"Button"</code>
     */
    public String toString() {
        return "Grounded Terminal";
    }
}
