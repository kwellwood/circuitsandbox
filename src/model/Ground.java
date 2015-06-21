/*
 * Ground.java
 *
 * Created on March 13, 2005, 6:43 PM
 */

package model;

import gui.GroundImage;

/**
 * <code>Ground</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a grounded terminal.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Ground extends LogicComponent {

    /**
     * Constructs a solitary new <code>Ground</code> object.
     *
     * @param model the model
     */
    public Ground(Model model) {
        super(model, 0, 1);
    }

    /**
     * If the output value isn't <code>FALSE</code> yet, a <code>State</code>
     * object is created and put on the <code>model</code>'s queue to change
     * the output value to <code>FALSE</code> later.
     */
    protected void compute() {
        // make state object and shove it on queue
        if (FALSE != newOutputValues[0]) {
            model.addStateChange(this, 0, FALSE);
            newOutputValues[0] = FALSE;
        }
    }
    
    /**
     * Sends the initial signal from the grounded terminal. The state is not
     * applicable to this type of input component.
     *
     * @param s <i>unused</i>
     */
    public void setState(byte s) {
        model.addStateChange(this, 0, FALSE);
    }
    
    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>Ground</code> objects 
     * return <code>Switch.TYPE_STRING</code>.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING;
    }
    
    /** used to identify <code>Button</code> objects */
    static final public String TYPE_STRING = "ground";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
