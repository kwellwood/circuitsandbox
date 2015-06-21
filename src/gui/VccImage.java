/*
 * VccImage.java
 *
 * Created on March 13, 2005, 6:47 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.List;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Vcc;

/**
 * The gui part of a grounded terminal.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class VccImage extends ComponentImage {
    
    /** Creates a new instance of VccImage */
    public VccImage() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>VccImage</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public VccImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "Vcc.png");
        loadImages(imageList);
    }
    
    /**
     * Returns a string that uniquely identifies each <code>VccImage</code>
     * object as a grounded terminal.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return Vcc.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the switch, for use in the toybox.
     *
     * @return the string <code>"Button"</code>
     */
    public String toString() {
        return "Vcc Terminal";
    }
}
