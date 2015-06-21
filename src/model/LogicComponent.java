/**
 * LogicComponent.java
 *
 * Created on January 24, 2005, 6:38 PM
 */

package model;

import gui.ComponentImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * <p><code>LogicComponent</code> is the abstract class that all components
 * extend. Components basically read values on their input pins,
 * compute their output values, and then notify the components connected to
 * theiroutputs that they need to recalculate as well.<p>
 * 
 * <p><code>LogicComponent</code> objects extend the Java
 * <code>Observable</code> class and implement the <code>Observer</code>
 * interface so that each component can observe other components and be
 * observed.</p>
 * 
 * <p>Values (<code>TRUE | FALSE | UNDEFINED</code>) propagate through connected
 * components using their observer/observable properties. When a component
 * is notified by one of its observables, it checks its inputs, computes its
 * outputs and notifies its observers.</p>
 * 
 * <p>To connect two <code>LogicComponents</code> (designated "source" and
 * "sink"), the <code>connectToOutput</code> method of the source component and
 * the <code>connectToInput</code> method of the sink component must both be
 * called. The order in which they are called doesn't matter.</p>
 * 
 * <p>There are two types of connections that can be made between components.
 * The first type of connection is referred to as a logical connection, simply
 * meaning the sink component becomes an observer of the source component and
 * they pass values. This type of connection is used inside a custom component
 * where the internal components and their connections are not visible to the
 * user. Use the <code>connectToInput(int, LogicComponent, int)</code>
 * and <code>connectToOutput(int, LogicComponent, int)</code> methods to do
 * this.</p>
 * 
 * <p>The second type of connection is referred to as a visual connection.
 * This type of connection implies a logical connection, but a <code>Wire</code>
 * object is used in the connection also. Users can see visual connections on
 * the screen in the form of wires drawn between two component images. Use
 * the <code>connectToInput(int, LogicComponent, int, int)</code> and
 * <code>connectToOutput(int, LogicComponent, int, int)</code> methods to do
 * this. Note that the <code>Wire</code> object must already exist in the model
 * before creating the visual connection.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see Wire
 */
public abstract class LogicComponent extends Observable implements Observer,
        Cloneable {
    
    /**
     * Empty constructor used only when a component doesn't know how many
     * inputs and outputs it will have. <code>CustomComponent</code> objects use
     * this constructor.
     *
     * @see CustomComponent
     */
    public LogicComponent() { }

    /**
     * Initializes a new <code>LogicComponent</code> object.
     *
     * @param model the model
     * @param numberOfInputs the number of input pins
     * @param numberOfOutputs the number of output pins
     */
    public LogicComponent(Model model,
            int numberOfInputs, int numberOfOutputs) {
        this.model = model;
        
        inputs = new ConnectionPoint[numberOfInputs];
        outputs = new ArrayList[numberOfOutputs];
        for (int i=0; i<numberOfOutputs; i++) {
            outputs[i] = new ArrayList();
        }
        
        outputValues = new byte[numberOfOutputs];
        newOutputValues = new byte[numberOfOutputs];
        for (int i=0; i<numberOfOutputs; i++) {
            outputValues[i] = UNDEFINED;
            newOutputValues[i] = UNDEFINED;
        }
    }
    
    /**
     * Sets the id number of this component.
     *
     * @param id the id number
     */
    public void setId(int id) { this.id = id; }
    
    /**
     * Returns the id number of this component.
     *
     * @return the id number
     */
    public int getId() { return id; }
    
    /**
     * Returns the number of inputs this component has.
     *
     * @return the number of inputs
     */
    public int getNumberOfInputs() {
        return inputs.length;
    }
    
    /**
     * Returns the number of outputs this component has.
     *
     * @return the number of outputs
     */
    public int getNumberOfOutputs() {
        return outputs.length;
    }
    
    /**
     * Sets the logic component's corresponding visual
     * <code>ComponentImage</code> that is used by the gui. This method will
     * also call the <code>setLogicComponent</code> method of the component
     * image so that only one calls needs to be made to create the association.
     *
     * @param componentImage the component image
     */
    public void setComponentImage(ComponentImage componentImage) {
        this.componentImage = componentImage;
        if (componentImage.getLogicComponent() != this) {
            componentImage.setLogicComponent(this);
        }
    }
    
    /**
     * Returns the logic component's corresponding visual
     * <code>ComponentImage</code> that is used by the gui.
     *
     * @return the component image object
     */
    public ComponentImage getComponentImage() {
        return componentImage;
    }
    
    /**
     * <p>Makes a connection from an input pin on this component to the given
     * output pin on another <code>LogicComponent</code>. The connection is
     * logical only and does not have a wire visually representing it.</p>
     * 
     * <p>The connection created by calling this method is unidirectional, so
     * the <code>connectToOutput(int, LogicComponent, int)</code> method of the
     * source component must also be called to connect this component to it. The
     * order in which these two methods are called to create a connect doesn't
     * matter.</p>
     *
     * @param inputNumber the input number on this component
     * @param source the source component
     * @param sourcePin the output pin number on the source component
     */
    public void connectToInput(int inputNumber, LogicComponent source,
            int sourcePin) {
        ConnectionPoint cp = source.getConnectionPointOut(sourcePin);
        inputs[inputNumber] = cp;
    }
    
    /**
     * <p>Makes a connection from an input pin on this component to the given
     * output pin on another <code>LogicComponent</code>. The <code>Wire</code>
     * object with the given <code>wireId</code> will visually represent the
     * connection.</p>
     * 
     * <p>The connection created by calling this method is unidirectional, so
     * the <code>connectToOutput(int, LogicComponent, int, int)</code> method
     * of the source component must also be called to connect this component to
     * it. The order in which these two methods are called to create a connect
     * doesn't matter.</p>
     *
     * @param inputNumber the input number on this component
     * @param source the source component
     * @param sourcePin the output pin number on the source component
     * @param wireId the id number of the <code>wire</code> representing this
     * connection
     */
    public void connectToInput(int inputNumber, LogicComponent source,
            int sourcePin, int wireId) {
        ConnectionPoint cp = source.getConnectionPointOut(sourcePin);
        cp.wireId = wireId;
        inputs[inputNumber] = cp;
    }
    
    /**
     * Breaks the connection from a given input pin on this component to another
     * <code>LogicComponent</code>. The <code>disconnectFromOutput</code> method
     * of the source component must also be called to break the other end of the
     * connection.
     *
     * @param inputNumber the input pin number to disconnect
     */
    public void disconnectFromInput(int inputNumber) {
        inputs[inputNumber] = null;
    }
    
    /**
     * <p>Makes a connection from an output pin on this component to the given
     * input pin on another <code>LogicComponent</code>. The connection is
     * logical only and does not have a wire visually representing it.</p>
     * 
     * <p>The connection created by calling this method is unidirectional, so
     * the <code>connectToInput(int, LogicComponent, int)</code> method of the
     * sink component must also be called to connect this component to it. The
     * order in which these two methods are called to create a connect doesn't
     * matter.</p>
     *
     * @param outputNumber the output number on this component
     * @param sink the sink component
     * @param sinkPin the input pin number on the sink component
     */
    public void connectToOutput(int outputNumber, LogicComponent sink,
            int sinkPin) {
        ConnectionPoint cp = sink.getConnectionPointIn(sinkPin);
        if (!isASink(cp.component)) { addObserver((Observer)cp.component); }
        outputs[outputNumber].add(cp);
    }

    /**
     * <p>Makes a connection from an output pin on this component to the given
     * input pin on another <code>LogicComponent</code>. The <code>Wire</code>
     * object with the given <code>wireId</code> will visually represent the
     * connection.</p>
     * 
     * <p>The connection created by calling this method is unidirectional, so
     * the <code>connectToInput(int, LogicComponent, int, int)</code> method of
     * the sink component must also be called to connect this component to it.
     * The order in which these two methods are called to create a connect
     * doesn't matter.</p>
     *
     * @param outputNumber the output number on this component
     * @param sink the sink component
     * @param sinkPin the input pin number on the sink component
     * @param wireId the id number of the <code>wire</code> representing this
     * connection
     */
    public void connectToOutput(int outputNumber, LogicComponent sink,
            int sinkPin, int wireId) {
        ConnectionPoint cp = sink.getConnectionPointIn(sinkPin);
        if (!isASink(cp.component)) { addObserver((Observer)cp.component); }
        cp.wireId = wireId;
        outputs[outputNumber].add(cp);
    }

    /**
     * Breaks the connection from a given output pin on this component to
     * another <code>LogicComponent</code>. The <code>disconnectFromInput</code>
     * method of the sink component must also be called to break the other end
     * of the connection.
     *
     * @param outputNumber the output pin number to disconnect
     * @param sink the sink <code>LogicComponent</code> being disconnected from
     * @param sinkPin the input pin number on the sink
     * <code>LogicComponent</code>
     */
    public void disconnectFromOutput(int outputNumber, LogicComponent sink,
            int sinkPin) {
        ConnectionPoint cp = sink.getConnectionPointIn(sinkPin);
        ArrayList outputsFromPin = outputs[outputNumber];
        
        for (int i = 0; i < outputsFromPin.size(); i++) {
            ConnectionPoint cp2 = (ConnectionPoint)outputsFromPin.get(i);
            if (cp2.component.equals(cp.component) &&
                    cp2.pinNumber == cp.pinNumber) {
                outputsFromPin.remove(i);
            }
        }
        
        if (!isASink(cp.component)) { deleteObserver(cp.component); }
    }
    
    /**
     * Returns a <code>ConnectionPoint</code> object containing the information
     * necessary to make a connection to the desired input pin.
     *
     * @param inputNumber the input pin number to connect to
     *
     * @return the <code>ConnectionPoint</code>
     */
    protected ConnectionPoint getConnectionPointIn(int inputNumber) {
        return new ConnectionPoint(this, inputNumber);
    }
    
    /**
     * Returns a <code>ConnectionPoint</code> object containing the information
     * necessary to make a connection to the desired output pin.
     *
     * @param outputNumber the output pin number to connect to
     *
     * @return the <code>ConnectionPoint</code>
     */
    protected ConnectionPoint getConnectionPointOut(int outputNumber) {
        return new ConnectionPoint(this, outputNumber);
    }
    
    /**
     * Called when a <code>LogicComponent</code> that this component was
     * observing has updated its output values.
     *
     * @param observerable the source component <code>LogicComponent</code>
     *
     * @param o unused; should be <code>null</code>
     */
    public void update(Observable observerable, Object o) {
        compute();
    }
    
    /**
     * <p>Sets the state of the component. The state is used to re-compute all
     * the outputs and notify its observers. Each subclass that needs this
     * method must implement it according to the states it has.</p>
     * 
     * <p>Call this method to initialize the component if its an input
     * component, or change its state if the user interacts with its component
     * image during simulation.
     *
     * @param s the state number
     */
    public void setState(byte s) {
        /* override this method */
    }
    
    /**
     * Returns the value of a given input by checked the connected source
     * component's output pin. If no component is connected to the input pin,
     * <code>UNDEFINED</code> is returned.
     *
     * @param inputNumber the input number
     *
     * @return the value of the input
     */
    protected byte getValueOfInput(int inputNumber) {
        if (inputs[inputNumber] != null) {
            LogicComponent source = inputs[inputNumber].component;
            int sourcePin = inputs[inputNumber].pinNumber;
            return source.getValueOfOutput(sourcePin);
        }
        return UNDEFINED;
    }
    
    /**
     * Returns the computed value of an output, given an output pin number.
     *
     * @param outputNumber the output pin number
     *
     * @return the value on the output pin
     */
    public byte getValueOfOutput(int outputNumber) {
        return outputValues[outputNumber];
    }
    
    /**
     * Sets the value of a given output pin and notifies all observing
     * <code>LogicComponents</code>. The value may be <code>TRUE</code>,
     * <code>FALSE</code> or <code>UNDEFINED</code>.
     *
     * @param outputNumber the output pin number
     * @param value the new value
     */
    public void setValueOfOutput(int outputNumber, byte value) {
        outputValues[outputNumber] = value;
        setChanged();
        notifyObservers();
    }
    
    /**
     * Sets the values of all the output pins to <code>UNDEFINED</code>. Call
     * this method to reset the state of each component after running the
     * simulation.
     */
    public void resetState() {
        for (int i = 0; i < outputValues.length; i++) {
            outputValues[i] = UNDEFINED;
            newOutputValues[i] = UNDEFINED;
        }
        if (componentImage != null) {
            // reset the frame displayed in the component image
            componentImage.resetState();
        }
    }
    
    /**
     * Returns an array of the component's input <code>ConnectionPoints</code>.
     *
     * @return the input connections
     */
    public ConnectionPoint[] getInputConnections() {
        return inputs;
    }
    
    /**
     * Returns <code>true</code> if the specified input pin has not connected
     * to anything.
     *
     * @param pinNumber the pin number
     *
     * @return <code>true</code> if the pin has no connections
     */
    public boolean isInputAvailable(int pinNumber) {
        return inputs[pinNumber] == null;
    }
    
    /**
     * Returns <code>true</code> if a given <code>LogicComponent</code> is
     * attached to an output on this component.
     *
     * @param lc the possible sink component
     *
     * @return <code>true</code> if <code>lc</code> is connected, otherwise
     * <code>false</code>
     */
    public boolean isASink(LogicComponent lc) {
        // Loop through all components connected to output pins and see if
        // lc is one of them. If so then it is connected to an output.
        for (int i=0; i<outputs.length; i++) {
            for (int j=0; j<outputs[i].size(); j++) {
                if (((ConnectionPoint)outputs[i].get(j)).component.equals(lc)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns a collection of all the IDs of the wires attached to the
     * componenent.
     * // TODO return a set of Wires instead
     *
     * @return the collection of IDs as <code>Integer</code>s
     */
    public HashSet getWires() {
        HashSet wires = new HashSet();
        
        // add the inputs
        for (int i = 0; i < inputs.length; i++)
            if (inputs[i] != null) wires.add(new Integer(inputs[i].wireId));
        
        // add the outputs
        for (int i = 0; i < outputs.length; i++)
            for (int j = 0; j < outputs[i].size(); j++)
                wires.add(new Integer(((ConnectionPoint)outputs[i].
                        get(j)).wireId));
        
        return wires;
    }
    
    /**
     * Returns a copy of this logic component. If it has a corresponding
     * component image, it will be stripped from the copy.
     *
     * @return a copy of the logic component
     */
    public LogicComponent getClone() {
        LogicComponent clone = null;
        try {
            clone = (LogicComponent)this.clone();
            
            clone.componentImage = null;
            
            inputs = new ConnectionPoint[inputs.length];
            outputs = new ArrayList[outputs.length];
            for (int i=0; i<outputs.length; i++) {
                outputs[i] = new ArrayList();
            }
            outputValues = new byte[outputs.length];
            newOutputValues = new byte[outputs.length];
            for (int i=0; i<outputValues.length; i++) {
                outputValues[i] = UNDEFINED;
                newOutputValues[i] = UNDEFINED;
            }
       } catch(Exception e) {
            /* do nothing */
        }
        return clone;
    }
    
    /**
     * This method is called when the <code>LogicComponent</code> is notified
     * that one of its input values has changed. Implement this method to
     * compute the new <code>outputValues</code>.
     */
    abstract protected void compute();
    
    /**
     * Returns a string that uniquely identifies each type of
     * <code>LogicComponent</code>.
     *
     * @return the identifying string
     */
    abstract public String getTypeString();

    /**
     * Returns the function of the component in a circuit. It may be an input
     * component (with no outputs), an output component (with no inputs), or
     * a logic component. The three values returned respectively are 
     * <code>INPUT</code>, <code>OUTPUT</code>, and <code>LOGIC</code>.
     *
     * @return the component's function in a circuit
     */
    public int getFunction() {
        if (inputs.length == 0) { return INPUT; }
        if (outputs.length == 0) { return OUTPUT; }
        return LOGIC;
    }
    
    /** the model */
    protected Model model;
    /** the unique id of the component in the <code>model</code> */
    protected int id;
    /**
     * an array of input <code>ConnectionPoints</code> representing incoming
     * connections from other components
     */
    protected ConnectionPoint[] inputs;
    /**
     * an array of collections that contain output
     * <code>ConnectionPoints</code> representing outgoing connections to
     * other components
     */
    protected ArrayList[] outputs;
    /** an array of the currently computed output values */
    protected byte[] outputValues;
    /** an array with the ouput value used in the most
     * recently created state change object for each output */
    protected byte[] newOutputValues;
    /** the corresponding visual image of the component */
    protected ComponentImage componentImage;

    /**
     * A <code>ConnectionPoint</code> object encapsulates all the information
     * necessary to represent the endpoint of a connection made to a
     * <code>LogicComponent</code>: the component, the pin number on the
     * component and the id number of the wire attached to the pin on the
     * component, if it exists (otherwise <code>null</code>).
     *
     * @author Daniel Stahl
     * @author Kevin Wellwood
     */
    protected class ConnectionPoint {
        ConnectionPoint(LogicComponent lc, int pinNumber, int wireId) {
            this.component = lc;
            this.pinNumber = pinNumber;
            this.wireId = wireId;
        }
        
        ConnectionPoint(LogicComponent lc, int pinNumber) {
            this.component = lc;
            this.pinNumber = pinNumber;
        }
        
        /**
         *  id number of the <code>wire</code> attached to
         *  <code>pinNumber</code> on <code>component</code>
         */
        public int wireId;
        /** the pin number (input or output) on <code>component</code> */
        public int pinNumber;
        /** the <code>LogicComponent</code> that this is for */
        public LogicComponent component;
    }
    
    /** a <code>LogicComponent</code>'s boolean False value */
    public static final byte FALSE = 0;
    /** a <code>LogicComponent</code>'s boolean True value */
    public static final byte TRUE = 1;
    /** a <code>LogicComponent</code>'s undefined boolean value */
    public static final byte UNDEFINED = -1;
    /** a component's function if it has no outputs */
    public static final int INPUT = 1;
    /** a component's function if it has no inputs */
    public static final int OUTPUT = 2;
    /** a component's function if it has inputs and outputs */
    public static final int LOGIC = 3;
}
