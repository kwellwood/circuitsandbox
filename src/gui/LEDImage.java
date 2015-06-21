/*
 * LEDImage.java
 *
 * Created on March 9, 2005, 6:12 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;

import model.LED;
import model.LogicComponent;

/**
 * The gui part of an LED.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class LEDImage extends ComponentImage {
    
    /** Creates a new instance of LEDImage */
    public LEDImage() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>LEDImage</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public LEDImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "LED0.png");
        imageList.add(ComponentImage.IMAGE_PATH + "LED1.png");
        loadImages(imageList);
    }
    
    /**
     * <p>Sets the state of the LED image, called by the <code>compute</code>
     * method of <code>LED</code>.</p>
     * <ul>
     *     <li><code>LogicComponent.TRUE</code> - On</li>
     *     <li><code>LogicComponent.FALSE</code> - Off</li>
     *     <li><code>LogicComponent.UNDEFINED</code> - Off</li>
     * </ul>
     *
     * @param s the state
     *
     * @see model.LED
     */
    public void setState(byte s) {
        switch (s) {
            case LogicComponent.TRUE: frame = 1; break;
            case LogicComponent.FALSE: frame = 0; break;
            default: frame = 0; break;
        }
    }
    
    /**
     * Returns a string that uniquely identifies each <code>LEDImage</code>
     * object as a push button.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return LED.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the LED, for use in the toybox.
     *
     * @return the string <code>"LED"</code>
     */
    public String toString() {
        return "LED";
    }
}
