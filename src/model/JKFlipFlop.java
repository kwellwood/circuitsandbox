/*
 * JKFlipFlop.java
 *
 * Created on January 24, 2005, 8:33 PM
 */

package model;



/**
 * <code>JKFlipFlopGate</code> extends the abstract class <code>LogicComponent</code>,
 * implementing the logic required to simulate a JK Flip-Flop.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class JKFlipFlop extends LogicComponent {
    
    /**
     * Constructs a solitary new <code>JKFlipFlopGate</code> object.
     *
     * @param model the model
     */
    public JKFlipFlop(Model model) {
        super(model, 5, 2);
    }
    
    /**
     * Computes the output of the AND gate based on the inputs. If the
     * calculated value differs from the current output value, a
     * <code>State</code> object is created and put on the <code>model's</code>
     * queue to change the output value later.
     */
    protected void compute() {
        byte j = getValueOfInput(0);
        byte c = getValueOfInput(1);
        byte k = getValueOfInput(2);
        byte ps = getValueOfInput(3);
        byte clr = getValueOfInput(4);
        byte q = UNDEFINED;
        byte notq = UNDEFINED;
        
        /* JK Flip-Flip Truth Table
         * _________________________________________
         *|______________INPUTS____________|_OUTPUTS|
         *|_PRESET_|_CLEAR_|_CLOCK_|_J_|_K_|_Q_|_!Q_|
         *|    0   |   0   |   X   | X | X | 1 | 1  |
         *|    0   |   1   |   X   | X | X | 1 | 0  |
         *|    1   |   0   |   X   | X | X | 0 | 1  |
         *|    1   |   1   |  down | 0 | 0 | 0 | !Q |
         *|    1   |   1   |  down | 1 | 0 | 1 | 0  |
         *|    1   |   1   |  down | 0 | 1 | 0 | 1  |
         *|    1   |   1   |  down | 1 | 1 | TOGGLE |
         *|____1___|___1___|___0___|_X_|_X_|_Q_|_!Q_|
         *
         * X = irrelevant
         *
         * Taken from: Electrical Publishing
         * http://www.tpub.com/neets/book13/55e.htm
         */
        
        // make sure all inputs are defined
        if (j != UNDEFINED && c != UNDEFINED && k != UNDEFINED &&
                ps != UNDEFINED && clr != UNDEFINED) {
                        
            if (c == TRUE && j != k) {
                q = j;
                qState = q;
            } else if (qState != UNDEFINED) {
                if (c == FALSE || (j == FALSE && k == FALSE)) {
                    q = qState;
                }
                else if (c == TRUE) {
                    // toggle the state
                    q = trinaryNot(qState);
                    qState = q;
                }                
            }
        
            notq = trinaryNot(qState);
            
            if (ps == FALSE) {
                // truth table row 1
                q = TRUE; notq = TRUE;
                if (clr == TRUE) {
                    // truth table row 2
                    notq = FALSE;
                }
                qState = q;
            } else if (ps == TRUE && clr == FALSE) {
                // truth table row 3
                q = FALSE; notq = TRUE;
                qState = q;
            }
        }
        
        if (q != newOutputValues[0] || notq != newOutputValues[1]) {
            // make state object and shove it on queue
            model.addStateChange(this, 0, q);
            model.addStateChange(this, 1, notq);
            newOutputValues[0] = q;
            newOutputValues[1] = notq;
        }
    }

    private byte trinaryNot(byte logicValue) {
        if (logicValue == TRUE) return FALSE;
        else if (logicValue == FALSE) return TRUE;
        else return UNDEFINED;
    }
    
    /**
     * Returns the identifying string for this type of 
     * <code>LogicComponent</code> object. All <code>JKFlipFlopGate</code> objects
     * return <code>JKFlipFlopGate.TYPE_STRING</code>.
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
    /** used to identify <code>JKFlipFlopGate</code> objects */
    static final public String TYPE_STRING = "jkflipflop";
    /** locally mirrors <code>LogicComponent.TRUE</code> */
    static final private byte TRUE = LogicComponent.TRUE;
    /** locally mirrors <code>LogicComponent.FALSE</code> */
    static final private byte FALSE = LogicComponent.FALSE;
    /** locally mirrors <code>LogicComponent.UNDEFINED</code> */
    static final private byte UNDEFINED = LogicComponent.UNDEFINED;
}
