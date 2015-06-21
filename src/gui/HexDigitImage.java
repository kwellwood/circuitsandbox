/*
 * HexDigitImage.java
 *
 * Created on April 9, 2005, 7:58 PM
 */

package gui;

import java.awt.List;
import java.awt.Point;

import model.HexDigit;
import model.LogicComponent;

/**
 * The gui part of a HexDigit.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class HexDigitImage extends ComponentImage {
    
    /** Creates a new instance of HexDigitImage */
    public HexDigitImage() {
        this(null, null);
    }
    
    /**
     * Constructs a new instance of <code>HexDigitImage</code> with a given
     * location in the sandbox.
     *
     * @param gui the gui
     * @param location the location, in standard coordinates
     */
    public HexDigitImage(Gui gui, Point location) {
        super(gui, location);
        List imageList = new List();
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit0.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit1.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit2.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit3.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit4.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit5.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit6.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit7.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit8.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigit9.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigitA.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigitB.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigitC.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigitD.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigitE.png");
        imageList.add(ComponentImage.IMAGE_PATH + "HexDigitF.png");
        loadImages(imageList);
    }
    
    /**
     * <p>Sets the state of the  HexDigit image, called by the
     * <code>compute</code> method of <code>HexDigit</code>.</p>
     * <ul>
     *     <li><code>0</code> - Off/Undefined</li>
     *     <li><code>&gt;0 - Digit to display plus 1 (ex: 11 -> A)</code></li>
     * </ul>
     *
     * @param s the state
     *
     * @see model.HexDigit
     */
    public void setState(byte s) {
        frame = s;
    }
    
    /**
     * Returns a string that uniquely identifies each <code>HexDigitImage</code>
     * object as a push button.
     *
     * @return the identifying string
     */
    public String getTypeString() {
        return HexDigit.TYPE_STRING;
    }
    
    /**
     * Returns the plain english name of the HexDigit, for use in the toybox.
     *
     * @return the string <code>"Hex Digit"</code>
     */
    public String toString() {
        return "Hex Digit";
    }
}
