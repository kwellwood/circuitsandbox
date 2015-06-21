/*
 * NotGate.java
 *
 * Created on January 24, 2005, 8:33 PM
 */

package model;

import gui.NotImage;
import java.awt.Point;

/**
 * <code>NotGate</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a NOT gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class NotGate extends LogicComponent {

    /**
     * Constructs a solitary new <code>NotGate</code> object.
     *
     * @param model the model
     */
    public NotGate(Model model) {
        super(model, 1, 1);
    }

    /**
     * Computes the output of the NOT gate based on the input. If the
     * calculated value differs from the current output value, a
     * <code>State</code> object is created and put on the <code>model's</code>
     * queue to change the output value later.
     */
    protected void compute() {
        byte a = getValueOfInput(0);
	byte result = LogicComponent.UNDEFINED;

        if (a == TRUE) result = FALSE;
        else if (a == FALSE) result = TRUE;

        if (result != newOutputValues[0]) {
            // make state object and shove it on queue
            model.addStateChange(this, 0, result);
            newOutputValues[0] = result;
       }
    }

    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>NotGate</code> objects 
     * return <code>NotGate.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }
    
    /** used to identify <code>And2Gate</code> objects */
    static final public String TYPE_STRING = "not";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
