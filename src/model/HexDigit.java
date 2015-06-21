/*
 * HexDigit.java
 *
 * Created on April 9, 2005, 6:15 PM
 */

package model;

import gui.HexDigitImage;


/**
 * <code>HexDigit</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a display capable of showing
 * a four bit number in hexadecimal.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class HexDigit extends LogicComponent {

    /**
     * Constructs a solitary new <code>HexDigit</code> object.
     *
     * @param model the model
     */
    public HexDigit(Model model) {
        super(model, 4, 0);
    }

    /**
     * Updates the component image's state based on the input values. No outputs
     * are computed because hex digits don't have any. Changing the state of the
     * HexDigit image will cause it to update its frame and repaint;
     */
    protected void compute() {
        if (getValueOfInput(0) == LogicComponent.UNDEFINED ||
                getValueOfInput(1) == LogicComponent.UNDEFINED ||
                getValueOfInput(2) == LogicComponent.UNDEFINED ||
                getValueOfInput(3) == LogicComponent.UNDEFINED) {
            // some of our inputs are not defined, the display is off
            componentImage.setState((byte)0);
        } else {
            // set the display to the correct digit
            componentImage.setState((byte)
                    (1 + getValueOfInput(0) + getValueOfInput(1) * 2 +
                    getValueOfInput(2) * 4 + getValueOfInput(3) * 8));
        }
    }

    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>Button</code> objects 
     * return <code>HexDigit.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }
    
    /** used to identify <code>HexDigit</code> objects */
    static final public String TYPE_STRING = "hexdigit";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
