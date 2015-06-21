/*
 * Wire.java
 *
 * Created on January 24, 2005, 7:14 PM
 */

package model;

import gui.WireImage;
import java.util.ArrayList;

/**
 * <p>A <code>Wire</code> object represents the logical aspect of a wire in
 * the model. It contains references to its end points and has a corresponding
 * <code>WireImage</code> gui object.</p>
 * 
 * <p><code>Wires</code> don't implement any logical functionality in terms of
 * simulating circuits. They are only for representing the connection between
 * two components in a way that can be loaded from disk and stored again.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 *
 * @see LogicComponent
 */
public class Wire {
    
    /**
     * Constructs a new <code>Wire</code> object, given its end points.
     *
     * @param source the source <code>LogicComponent</code>
     * @param sourcePin the output pin number on the <code>source</code>
     * @param sink the sink <code>LogicComponent</code>
     * @param sinkPin the output pin number on the <code>sink</code>
     */
    public Wire(LogicComponent source, int sourcePin, LogicComponent sink,
            int sinkPin) {
        this.source = source;
        this.sourcePin = sourcePin;
        this.sink = sink;
        this.sinkPin = sinkPin;
    }
    
    /**
     * Sets the id number of the <code>wire</code>.
     *
     * @param id the id number
     */
    public void setId(int id) { this.id = id; }
    
    /**
     * Returns the id number of the <code>wire</code>.
     *
     * @return the id number
     */
    public int getId() { return id; }
    
    /**
     * Returns the corresponding GUI wire.
     *
     * @return the <code>wireImage</code> object
     */
    public WireImage getWireImage() { return wireImage; }
    
    /**
     * Sets the corresponding <code>wireImage</code> object in the GUI. If the 
     * <code>wireImage</code> isn't already associated with this
     * <code>wire</code>, it will be associatesd with this.
     *
     * @param wireImage the wireImage to set
     */
    public void setWireImage(WireImage wireImage) {
        this.wireImage = wireImage;
        if (wireImage.getWire() != this) {
            wireImage.setWire(this);
        }
    }
    
    /**
     * Returns the source component attached to this <code>Wire</code>. If none
     * is currently attached, <code>null</code> is returned.
     *
     * @return the source component
     */
    public LogicComponent getSource() { return source; }
    
    /**
     * Returns the sink component attached to this <code>Wire</code>. If none is
     * currently attached, <code>null</code> is returned.
     *
     * @return the sink component
     */
    public LogicComponent getSink() { return sink; }
    
    /**
     * Returns the output pin number on the source component. If none is
     * currently attached, -1 is returned.
     *
     * @return the source output pin number
     */
    public int getSourcePin() { return sourcePin; }
    
    /**
     * Returns the input pin number on the sink component. If none is
     * currently attached, -1 is returned.
     *
     * @return the sink input pin number
     */
    public int getSinkPin() { return sinkPin; }
    
    /**
     * Sets the source component and output pin on the source.
     *
     * @param source the source logic component
     * @param sourcePin the output pin on the source component
     */
    public void setSource(LogicComponent source, int sourcePin) {
        this.source = source;
        this.sourcePin = sourcePin;
    }
    
    /**
     * Sets the sink component and input pin on the sink.
     *
     * @param sink the sink logic component
     * @param sinkPin the input pin on the sink component
     */
    public void setSink(LogicComponent sink, int sinkPin) {
        this.sink = sink;
        this.sinkPin = sinkPin;
    }
    
    /** the id number */
    private int id = -1;
    /** the corresponding GUI wire */
    private WireImage wireImage;
    /** the source <code>LogicComponent</code> */
    private LogicComponent source;
    /** the sink <code>LogicComponent</code> */
    private LogicComponent sink;
    /** the output pin number on the <code>source</code> */
    private int sourcePin = -1;
    /** the input pin number on the <code>source</code> */
    private int sinkPin = -1;
}
