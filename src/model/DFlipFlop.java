/*
 * DFlipFlop.java
 *
 * Created on January 24, 2005, 8:33 PM
 */

package model;

import gui.DFlipFlopImage;
import java.awt.Point;

/**
 * <code>DFlipFlopGate</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a D Flip-Flop.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class DFlipFlop extends LogicComponent {
    
    /**
     * Constructs a solitary new <code>DFlipFlopGate</code> object.
     *
     * @param model the model
     */
    public DFlipFlop(Model model) {
        super(model, 2, 2);
    }
    
    /**
     * Computes the output of the AND gate based on the inputs. If the
     * calculated value differs from the current output value, a
     * <code>State</code> object is created and put on the <code>model's</code>
     * queue to change the output value later.
     */
    protected void compute() {
        byte d = getValueOfInput(0);
        byte c = getValueOfInput(1);

        if (c == TRUE) {
            qState = d;
        }
            
        if (qState != newOutputValues[0]) {
            // make state object and shove it on queue
            model.addStateChange(this, 0, qState);
            model.addStateChange(this, 1, trinaryNot(qState));
            newOutputValues[0] = qState;
            newOutputValues[1] = trinaryNot(qState);
        }
    }

    private byte trinaryNot(byte logicValue) {
        if (logicValue == TRUE) {
            return FALSE;
        } else if (logicValue == FALSE) {
            return TRUE;
        } else {
            return UNDEFINED;
        }
    }
    
    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>DFlipFlopGate</code> objects
     * return <code>DFlipFlopGate.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }
    
    /**
     * Resets the state of the Flip-Flop to undefined when the simulation is
     * stopped.
     */
    public void resetState()
    {
        super.resetState();
        qState = UNDEFINED;
    }
    
    /** the state of the Flip-Flop's Q output */
    private byte qState = UNDEFINED;
    /** used to identify <code>DFlipFlopGate</code> objects */
    static final public String TYPE_STRING = "dflipflop";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
