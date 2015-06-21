/*
 * LogicComponentFactory.java
 *
 * Created on February 26, 2005, 3:07 PM
 */

package model;

import gui.*;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides functionality for creating <code>LogicComponent</code> objects
 * from a given type string.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class LogicComponentFactory {
    
    /**
     * Constructs a new <code>LogicComponentFactory</code> object. Use
     * <code>LogicComponentFactory.getDefaultFactory()</code> to get a factory
     * instance instead of creating a new one.
     */
    private LogicComponentFactory(Model model, Gui gui) {
        this.model = model;
        this.gui = gui;
        buildComponentsCollection();
    }
    
    /**
     * Builds a collection of all the different classes of logic components.
     */
    private void buildComponentsCollection() {
        components = new HashMap();
        components.put(Button.TYPE_STRING, new Button(model));
        components.put(Switch.TYPE_STRING, new Switch(model));
        components.put(Ground.TYPE_STRING, new Ground(model));
        components.put(Vcc.TYPE_STRING, new Vcc(model));
        components.put(LED.TYPE_STRING, new LED(model));
        components.put(HexDigit.TYPE_STRING, new HexDigit(model));
        components.put(NotGate.TYPE_STRING, new NotGate(model));
        components.put(And2Gate.TYPE_STRING, new And2Gate(model));
        components.put(And3Gate.TYPE_STRING, new And3Gate(model));
        components.put(And4Gate.TYPE_STRING, new And4Gate(model));
        components.put(Or2Gate.TYPE_STRING, new Or2Gate(model));
        components.put(Or3Gate.TYPE_STRING, new Or3Gate(model));
        components.put(Or4Gate.TYPE_STRING, new Or4Gate(model));
        components.put(Nand2Gate.TYPE_STRING, new Nand2Gate(model));
        components.put(Nand3Gate.TYPE_STRING, new Nand3Gate(model));
        components.put(Nand4Gate.TYPE_STRING, new Nand4Gate(model));
        components.put(Nor2Gate.TYPE_STRING, new Nor2Gate(model));
        components.put(Nor3Gate.TYPE_STRING, new Nor3Gate(model));
        components.put(Nor4Gate.TYPE_STRING, new Nor4Gate(model));
        components.put(Xor2Gate.TYPE_STRING, new Xor2Gate(model));
        components.put(JKFlipFlop.TYPE_STRING, new JKFlipFlop(model));
        components.put(DFlipFlop.TYPE_STRING, new DFlipFlop(model));
    }
    
    /**
     * Sets the factory's gui reference.
     *
     * @param gui the gui
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }
    
    /**
     * Sets the factory's model reference.
     *
     * @param model the model
     */
    public void setModel(Model model) {
        this.model = model;
        buildComponentsCollection();
    }
    
    /**
     * Creates and returns a component of the type specified by a 
     * <code>TYPE_STRING</code>. The location must be in standard coordinates.
     *
     * @param type the component type
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public LogicComponent buildComponent(String type, int x, int y)
            throws Exception {
        
        if (type.startsWith(CustomComponent.TYPE_STRING+":")) {
            LogicComponent lc = new CustomComponent(model,
                    type.substring(CustomComponent.TYPE_STRING.length()+1));
            ComponentImage ci = buildComponentImage(type, x, y);
            lc.setComponentImage(ci);
            return lc;
        } else {
            LogicComponent lc = (LogicComponent)components.get(type);
            lc = lc.getClone();
            ComponentImage ci = buildComponentImage(type, x, y);
            lc.setComponentImage(ci);
            return lc;
        }
    }
    
    /**
     * Creates a returns a component image of the type specified by a
     * <code>TYPE_STRING</code>. The location must be in standard coordinates.
     *
     * @param type the component image type
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private ComponentImage buildComponentImage(String type, int x, int y)
            throws Exception {
        
        if (type.equals(Button.TYPE_STRING)) {
            return new ButtonImage(gui, new Point(x, y));
        } else if (type.equals(Switch.TYPE_STRING)) {
            return new SwitchImage(gui, new Point(x, y));
        } else if (type.equals(Ground.TYPE_STRING)) {
            return new GroundImage(gui, new Point(x, y));
        } else if (type.equals(Vcc.TYPE_STRING)) {
            return new VccImage(gui, new Point(x, y));
        } else if (type.equals(LED.TYPE_STRING)) {
            return new LEDImage(gui, new Point(x, y));
        } else if (type.equals(HexDigit.TYPE_STRING)) {
            return new HexDigitImage(gui, new Point(x, y));
        } else if (type.equals(NotGate.TYPE_STRING)) {
            return new NotImage(gui, new Point(x, y));
        } else if (type.equals(And2Gate.TYPE_STRING)) {
            return new And2Image(gui, new Point(x, y));
        } else if (type.equals(And3Gate.TYPE_STRING)) {
            return new And3Image(gui, new Point(x, y));
        } else if (type.equals(And4Gate.TYPE_STRING)) {
            return new And4Image(gui, new Point(x, y));
        } else if (type.equals(Or2Gate.TYPE_STRING)) {
            return new Or2Image(gui, new Point(x, y));
        } else if (type.equals(Or3Gate.TYPE_STRING)) {
            return new Or3Image(gui, new Point(x, y));
        } else if (type.equals(Or4Gate.TYPE_STRING)) {
            return new Or4Image(gui, new Point(x, y));
        } else if (type.equals(Nand2Gate.TYPE_STRING)) {
            return new Nand2Image(gui, new Point(x, y));
        } else if (type.equals(Nand3Gate.TYPE_STRING)) {
            return new Nand3Image(gui, new Point(x, y));
        } else if (type.equals(Nand4Gate.TYPE_STRING)) {
            return new Nand4Image(gui, new Point(x, y));
        } else if (type.equals(Nor2Gate.TYPE_STRING)) {
            return new Nor2Image(gui, new Point(x, y));
        } else if (type.equals(Nor3Gate.TYPE_STRING)) {
            return new Nor3Image(gui, new Point(x, y));
        } else if (type.equals(Nor4Gate.TYPE_STRING)) {
            return new Nor4Image(gui, new Point(x, y));
        } else if (type.equals(Xor2Gate.TYPE_STRING)) {
            return new Xor2Image(gui, new Point(x, y));
        } else if (type.equals(JKFlipFlop.TYPE_STRING)) {
            return new JKFlipFlopImage(gui, new Point(x, y));
         } else if (type.equals(DFlipFlop.TYPE_STRING)) {
            return new DFlipFlopImage(gui, new Point(x, y));
       } else if (type.startsWith(CustomComponent.TYPE_STRING+":")) {
            return new CustomImage(gui, new Point(x, y),
                    type.substring(CustomComponent.TYPE_STRING.length() + 1));
        } else {
            throw new Exception("Invalid component type");
        }
    }
    
    /**
     * Creates and returns a component of the type specified by a 
     * <code>TYPE_STRING</code>.
     *
     * @param type the component type
     */
    public LogicComponent buildComponent(String type) throws Exception {
        
        if (type.startsWith(CustomComponent.TYPE_STRING+":")) {
            return new CustomComponent(model,
                    type.substring(CustomComponent.TYPE_STRING.length() + 1));
        } else {
            LogicComponent lc = (LogicComponent)components.get(type);
            lc = lc.getClone();
            return lc;
        }
    }
    
    /** the model used for instantiating logic component objects */
    private Model model;
    /** the gui used for instantiating component image objects */
    private Gui gui;
    /** a collection of primitive pre-built logic components */
    private Map components;
        
    /**
     * Sets the gui for the default factory. As a side effect, the default
     * factory must be reinstantiated.
     *
     * @param gui the gui
     */
    public static void setDefaultGui(Gui gui) {
        LogicComponentFactory.defaultGui = gui;
        factory = new LogicComponentFactory(defaultModel, defaultGui);
    }
    
    /**
     * Sets the model for the default factory. As a side effect, the default
     * factory must be reinstantiated.
     *
     * @param model the model
     */
    public static void setDefaultModel(Model model) {
        LogicComponentFactory.defaultModel = model;
        factory = new LogicComponentFactory(defaultModel, defaultGui);
    }
    
    /**
     * Returns the default factory for building logic components.
     *
     * @return the factory
     */
    public static LogicComponentFactory getDefaultInstance() {
        if (factory == null) {
            factory = new LogicComponentFactory(defaultModel, defaultGui);
        }
        return factory;
    }
    
    /**
     * Returns a new factory for building logic components with the
     * defaults for model and gui preset.
     *
     * @return the new factory
     */
    public static LogicComponentFactory newInstance() {
        return new LogicComponentFactory(defaultModel, defaultGui);
    }
    
    /** the default model used by factories */
    private static Model defaultModel;
    /** the default gui used by factories */
    private static Gui defaultGui;
    /** the default factory for building logic components */
    private static LogicComponentFactory factory;
}
