/*
 * Button.java
 *
 * Created on March 9, 2005, 2:00 PM
 */

package model;

import gui.ButtonImage;



/**
 * <code>Button</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a push button.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Button extends LogicComponent {

    /**
     * Constructs a solitary new <code>Button</code> object.
     *
     * @param model the model
     */
    public Button(Model model) {
        super(model, 0, 1);
    }

    /**
     * Computes the output of the button based on the state of its
     * corresponding gui object, <code>ButtonImage</code>. If the value differs
     * from the current output value, a <code>State</code> object is created
     * and put on the <code>model's</code> queue to change the output value
     * later.
     */
    protected void compute() {
	byte result = ((ButtonImage)componentImage).getState();

        if (result != newOutputValues[0]) {
            // make state object and shove it on queue
            model.addStateChange(this, 0, result);
            newOutputValues[0] = result;
       }
    }

    /**
     * <p>Switches the button off or on. Possible states are:</p>
     * <ul>
     *     <li><code>0</code> - Off</li>
     *     <li><code>1</code> - On</li>
     * </ul>
     *
     * @param s the state
     */
    public void setState(byte s) {
        model.addStateChange(this, 0, s);
    }
    
    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>Button</code> objects 
     * return <code>Button.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }
    
    /** used to identify <code>Button</code> objects */
    static final public String TYPE_STRING = "button";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
