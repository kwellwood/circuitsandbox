/*
 * LED.java
 *
 * Created on March 9, 2005, 6:15 PM
 */

package model;



/**
 * <code>LED</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a light-emitting diode.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class LED extends LogicComponent {

    /**
     * Constructs a solitary new <code>LED</code> object.
     *
     * @param model the model
     */
    public LED(Model model) {
        super(model, 1, 0);
    }

    /**
     * Updates the component image's state based on the input value. No outputs
     * are computed because an LED doesn't have any. Changing the state of the
     * LED image will cause it to update its frame and repaint;
     */
    protected void compute() {
        componentImage.setState(getValueOfInput(0));
    }

    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>LED</code> objects 
     * return <code>LED.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }
    
    /** used to identify <code>LED</code> objects */
    static final public String TYPE_STRING = "LED";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
