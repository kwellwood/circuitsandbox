/*
 * Nand3Gate.java
 *
 * Created on January 24, 2005, 8:33 PM
 */

package model;

import java.awt.Point;

/**
 * <code>Nand3Gate</code> extends the abstract class
 * <code>LogicComponent</code>, implementing the logic required to simulate a
 * three-input NAND gate.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Nand2Gate
 * @see Nand4Gate
 */
public class Nand3Gate extends LogicComponent {
    
    /**
     * Constructs a solitary new <code>Nand3Gate</code> object.
     *
     * @param model the model
     */
    public Nand3Gate(Model model) {
        super(model, 3, 1);
    }

    /**
     * Computes the output of the NAND gate based on the inputs. If the
     * calculated value differs from the current output value, a
     * <code>State</code> object is created and put on the <code>model's</code>
     * queue to change the output value later.
     */
    protected void compute() {
        byte a = getValueOfInput(0);
        byte b = getValueOfInput(1);
        byte c = getValueOfInput(2);
        byte result = LogicComponent.UNDEFINED;
        
        if (a == FALSE || b == FALSE || c == FALSE) {
            result = TRUE;
        } else if (a == TRUE && b == TRUE && c == TRUE) {
            result = FALSE;
        }
        
        if (result != newOutputValues[0]) {
            // make state object and shove it on queue
            model.addStateChange(this, 0, result);
            newOutputValues[0] = result;
       }
    }
    
    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>Nand3Gate</code> objects 
     * return <code>Nand3Gate.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }

    /** used to identify <code>And2Gate</code> objects */
    static final public String TYPE_STRING = "nand3";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
