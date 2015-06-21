/*
 * WireFactory.java
 *
 * Created on March 3, 2005, 2:01 PM
 */

package model;

import gui.Gui;
import gui.WireImage;


/**
 * Provides functionality for creating <code>Wire</code> objects.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class WireFactory {
    
    /**
     * Constructs a new <code>WireFactory</code> object. Use
     * <code>WireFactory.getDefaultFactory()</code> to get a factory
     * instance instead of creating a new one.
     */
    public WireFactory() { }

    /**
     * Sets the gui for this factory. The gui is used for instantiating
     * wire images corresponding to logical wire objects.
     *
     * @param gui the gui
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }
    
    /**
     * Creates and returns a wire with the given parameters. The wire will have
     * a corresponding GUI <code>WireImage</code> object.
     *
     * @param source the source component
     * @param sourcePin the output pin on the source component
     * @param sink the sink component
     * @param sinkPin the input pin on the sink component
     *
     * @return the newly constructed wire
     */
    public Wire buildWire(LogicComponent source, int sourcePin,
            LogicComponent sink, int sinkPin) {
        Wire wire = new Wire(source, sourcePin, sink, sinkPin);
        WireImage wi = new WireImage(gui);
        
        wire.setWireImage(wi);
        return wire;
    }
    
    /** the gui used for instantiating wire image objects */
    private Gui gui;
    
    /**
     * Returns the default factory for building wires.
     *
     * @return the factory
     */
    public static WireFactory getDefaultFactory() {
        return factory;
    }
    
    /** the default factory for building wires */
    private static final WireFactory factory = new WireFactory();
}
