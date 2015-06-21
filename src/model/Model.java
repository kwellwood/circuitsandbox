/*
 * Model.java
 *
 * Created on January 24, 2005, 6:37 PM
 */

package model;

import circuitsandbox.Util;
import controller.Controller;
import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * <p>A <code>Model</code> is the logical representation of a collection of
 * circuits. A model is composed of <code>LogicComponents</code> and
 * <code>Wires</code>.</p>
 *
 * <p><code>LogicComponent</code> objects are connected to each other in the
 * <code>model</code> with <code>wires</code> as <code>Observers</code> and
 * <code>Observables</code> to pass signals while the logic simulation is
 * being run.</p>
 *
 * <p><code>Model</code> objects can save and load their internal circuit
 * configuration in XML format for persistent data storage.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Model implements Runnable {
    
    /**
     * Constructs a new empty <code>Model</code> object.
     *
     * @param controller the model's <code>Controller</code>
     */
    public Model(Controller controller) {
        this.controller = controller;
        
        largestId = 0;
        
        inputComponents = new HashSet();
        outputComponents = new HashSet();
        components = new HashMap();
        wires = new HashMap();
        stateChangeQueue = Collections.synchronizedSortedSet(new TreeSet());
    }
    
    /**
     * Constructs a new <code>Model</code> object, loading the model data from
     * a file.
     *
     * @param modelFile the file to load
     * @param controller the model's <code>Controller</code>
     *
     * @throws Exception if the model file could not be loaded
     */
    public Model(Controller controller, File modelFile) throws Exception {
        this(controller);
        
        try { readFromXML(modelFile); }
        catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg == null) msg = "Invalid file format";
            throw new Exception("Model not opened: "+msg);
        }
    }
    
    /**
     * Starts the logic simulation. To set up the simulation, the thread must be
     * started, and all the input components notified so they will send out
     * their initial signals.
     */
    public void startSimulation() {
        // start all the input components
        Iterator iter = inputComponents.iterator();
        while (iter.hasNext()) {
            ((LogicComponent)iter.next()).setState((byte)0);
        }
        
        // start the simulation thread
        runSimulation = true;
        simThread = new Thread(this);
        simThread.start();
    }
    
    /**
     * Stops the logic simulation. To stop the simulation, the thread must be
     * stopped and all output values of all components must be reset to
     * <code>UNDEFINED</code>.
     */
    public void stopSimulation() {
        // terminate the simulation and wait for the thread to stop
        runSimulation = false;
        try { simThread.join(); } catch (Exception e) { }
        
        // clean up
        stateChangeQueue.clear();
        Iterator iter = getComponents();
        while (iter.hasNext()) {
            ((LogicComponent)iter.next()).resetState();
        }
    }
    
    /**
     * Executes the logic simulation. This is called by the <code>Thread</code>
     * created in the <code>startSimulation</code> method. It will return when
     * the instance variable <code>stopSimulation</code> is <code>true</code>.
     */
    public void run() {
        StateChange stateChange;
        
        while (runSimulation) {
            if (!stateChangeQueue.isEmpty()) {
                // -- execute all state changes that are (over)due --
                stateChange = (StateChange)stateChangeQueue.first();
                while (runSimulation &&
                        stateChange != null &&
                        stateChange.getTimeStamp() <=
                        System.currentTimeMillis()) {

                    // -- execute the most overdue state change --
                    stateChange.execute();
                    stateChangeQueue.remove(stateChange);
                    controller.repaintSandbox();
                    
                    // -- get the next most overdue state change --
                    if (!stateChangeQueue.isEmpty()) {
                        stateChange = (StateChange)stateChangeQueue.first();
                    } else {
                        stateChange = null;
                    }
                }
            }
            
            // Somehow taking the time to perform this operation keeps
            // the simulation from locking up
            try { simThread.sleep(1); } catch (Exception e) { }
        }
    }
    
    /**
     * Creates a new <code>StateChange</code> and adds it to the state change
     * queue. The time stamp is set by adding <code>simDelay</code> to the
     * current time.
     *
     * @param logicComponent the target logic component
     * @param outputNumber the output number on the component
     * @param newValue the value to set the output to
     */
    public synchronized void addStateChange(LogicComponent logicComponent,
            int outputNumber, byte newValue) {
        stateChangeQueue.add(new StateChange(logicComponent, outputNumber,
                newValue, System.currentTimeMillis() + getSimDelay()));
    }
    
    /**
     * Sets the propagation delay between components during the simulation
     * execution. Do not set this value too long or large models will not have
     * enough time to update.
     *
     * @param delay the delay in milliseconds
     */
    public void setSimDelay(long delay) {
        simDelay = delay;
    }
    
    /**
     * Returns the propagation delay between components during the simulation
     * execution.
     *
     * @return the delay in milliseconds
     */
    public long getSimDelay() {
        return simDelay;
    }
    
    /**
     * Checks if the model can be saved as a custom component. To be a custom
     * component, a model must have at least one input and one output.
     *
     * @return <code>true</code> if the model can be a custom component
     */
    public boolean canBeAComponent() throws Exception {
        // make sure we have an input component
        if (inputComponents.size() < 1) { return false; }
        
        // make sure we have an output component
        if (outputComponents.size() < 1) { return false; }
        
        Iterator iter = inputComponents.iterator();
        while (iter.hasNext()) {
            if (((LogicComponent)iter.next()).getNumberOfOutputs() > 1) {
                throw new Exception("Please only use single-pin input " +
                        "components for custom components");
            }
        }
        
        iter = outputComponents.iterator();
        while (iter.hasNext()) {
            if (((LogicComponent)iter.next()).getNumberOfInputs() > 1) {
                throw new Exception("Please only use single-pin output " +
                        "components for custom components");
            }

        }
        
        return true;
    }
    
    /**
     * Returns the input components in the model. This is used for creating a
     * new custom component.
     *
     * @return a set of logic components
     */
    public HashSet getInputComponents() {
        return inputComponents;
    }

    /**
     * Returns the output components in the model. This is used for creating a
     * new custom component.
     *
     * @return a set of logic components
     */
    public HashSet getOutputComponents() {
        return outputComponents;
    }
    
    /**
     * Reads the <code>model's</code> data from a file in XML format. Any
     * objects that were in the <code>model</code> before this method was called
     * will be cleared. <code>largestId</code> will be set to one greater than
     * the largest id number read from the file.
     *
     * @param file the file to read
     *
     * @throws Exception if the data could not be loaded
     */
    private void readFromXML(File file) throws Exception {
        Document document;      // xml document root node
        Node currentNode;       // pointer to current node
        
        if (! file.exists()) { throw new Exception("File not found"); }
        
        // create the parser and read the file
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);       // set to true for error detection
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            document = dbf.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            throw new Exception("Invalid model format");
        }
        
        // check for the model tag
        if (Util.findFirstNode(document, "model") == null) {
            throw new Exception("Invalid model format");
        }
        
        // components first pass: load the components
        LogicComponentFactory factory = LogicComponentFactory.newInstance();
        factory.setModel(this);
        currentNode = Util.findFirstNode(document, "component");
        while (currentNode != null) {
            
            // get the component's type, id, and location
            NamedNodeMap attributes = currentNode.getAttributes();
            String type = attributes.getNamedItem("type").getNodeValue();
            int id = toInt(attributes.getNamedItem("id").getNodeValue());
            int x = toInt(attributes.getNamedItem("x").getNodeValue());
            int y = toInt(attributes.getNamedItem("y").getNodeValue());
            
            // Make sure largestId is always larger than any id that we read
            if (id > largestId) {
                largestId = id;
            }
            
            // Create the new component and add it to the model
            // create the logic component and add it to the collection
            LogicComponent lc = factory.buildComponent(type, x, y);
            if (lc instanceof CustomComponent) {
                controller.loadToToybox(lc.getTypeString());
            }
            addComponent(id, lc);
                        
            currentNode = Util.findNextSameSibling(currentNode);
        }
        
        // components second pass: attach the components
        currentNode = Util.findFirstNode(document, "component");
        while (currentNode != null) {
            // Get the sink component object by its in the xml
            LogicComponent sink = getComponent(toInt(
                    currentNode.getAttributes().getNamedItem("id").
                    getNodeValue()));
            
            // Loop through all the child input nodes
            Node input = Util.findFirstNode(currentNode, "input");
            while (input != null) {
                
                // get the attributes of the input tag
                NamedNodeMap inputAttribs = input.getAttributes();
                int sinkPin = toInt(
                        inputAttribs.getNamedItem("number").getNodeValue());
                int sourceId = toInt(
                        inputAttribs.getNamedItem("sourceId").getNodeValue());
                int sourcePin = toInt(
                        inputAttribs.getNamedItem("sourcePin").getNodeValue());
                int wireId = toInt(
                        inputAttribs.getNamedItem("wireId").getNodeValue());
                LogicComponent source = getComponent(sourceId);
                
                // make sure largestId is always larger than any id that we read
                if (wireId > largestId) {
                    largestId = wireId;
                }
                
                // create the connecting wire object and add path nodes to it
                Wire wire = WireFactory.getDefaultFactory().buildWire(
                        source, sourcePin, sink, sinkPin);
                wire.setId(wireId);
                Node wireNode = Util.findFirstNode(input,  "wireNode");
                while (wireNode != null) {
                    
                    // add the location of the wire node to the wire's path
                    NamedNodeMap wireNodeAttribs = wireNode.getAttributes();
                    wire.getWireImage().addNodeToBack(toInt(
                            wireNodeAttribs.getNamedItem("x").getNodeValue()
                            ), toInt(
                            wireNodeAttribs.getNamedItem("y").getNodeValue())
                            );
                    
                    wireNode = Util.findNextSameSibling(wireNode);
                }
                
                // make the connection using the wire
                connectComponents(wire);
                
                input = Util.findNextSameSibling(input);
            }
            
            currentNode = Util.findNextSameSibling(currentNode);
        }
    }
    
    /**
     * Writes the <code>model's</code> data to a file in XML format. If the file
     * already exists, it will be overwritten.
     *
     * @param file the file to write
     *
     * @throws Exception if the file could not be written
     */
    public void writeToXML(File file) throws Exception {
        // open the file for output
        OutputStreamWriter out = new OutputStreamWriter(
                new BufferedOutputStream(new FileOutputStream(file)), "8859_1");
        
        // write out the internal dtd
        out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+endl);
        out.write(
                "<!DOCTYPE model [" + endl +
                "    <!ELEMENT model (component*)>" + endl +
                "    <!ELEMENT component (input*)>" + endl +
                "    <!ATTLIST component" + endl +
                "        type CDATA #REQUIRED" + endl +
                "        id CDATA #REQUIRED" + endl +
                "        x CDATA #REQUIRED" + endl +
                "        y CDATA #REQUIRED>" + endl +
                "    <!ELEMENT input (wireNode*)>" + endl +
                "    <!ATTLIST input" + endl +
                "        number CDATA #REQUIRED" + endl +
                "        sourceId CDATA #REQUIRED" + endl +
                "        sourcePin CDATA #REQUIRED" + endl +
                "        wireId CDATA #REQUIRED>" + endl +
                "    <!ELEMENT wireNode EMPTY>" + endl +
                "    <!ATTLIST wireNode" + endl +
                "        x CDATA #REQUIRED" + endl +
                "        y CDATA #REQUIRED>" + endl +
                "]>");
        out.write("<model>"+endl);
        
        // write each of the components to disk, looping through their ids
        Iterator iterator = components.values().iterator();
        while(iterator.hasNext()) {
            LogicComponent lc = (LogicComponent)iterator.next();
            
            out.write("    <component type=\"" + lc.getTypeString() +
                    "\" id=\"" + lc.getId() +
                    "\" x=\""  + lc.getComponentImage().getStdX() +
                    "\" y=\""  + lc.getComponentImage().getStdY() +
                    "\">" + endl);
            
            // loop through each input pin on this component
            LogicComponent.ConnectionPoint[] inputConnections;
            inputConnections = lc.getInputConnections();
            for (int i = 0; i < inputConnections.length; i++) {
                
                // if the input has a connection to it
                if (inputConnections[i] != null) {
                    
                    // get the wire that makes this connection. it holds the
                    // visual connection information that we want to store, not
                    // the logical connection in the logic component's
                    // ConnectionPoint.
                    Wire wire;
                    wire = getWire(inputConnections[i].wireId);
                    out.write("        " + 
                            "<input " + 
                            "number=\""    + i                        + "\" " +
                            "sourceId=\""  + wire.getSource().getId() + "\" " +
                            "sourcePin=\"" + wire.getSourcePin()      + "\" " +
                            "wireId=\""    + wire.getId()             + "\">" +
                            endl);
                    
                    // write the wire's visual path.
                    // the wire is located by its id which is stored in the
                    // current inputConnection slot.
                    ArrayList wirePath = getWire(inputConnections[i].wireId).
                            getWireImage().getPoints();
                    for (int j = 1; j < wirePath.size()-1; j++) {
                        out.write("            " +
                                "<wireNode " +
                                "x=\"" + ((Point)wirePath.get(j)).x + "\" "   +
                                "y=\"" + ((Point)wirePath.get(j)).y + "\" />" +
                                endl);
                    }
                    
                    out.write("        </input>"+endl);
                }
            }
            
            out.write("    </component>"+endl);
        }
        
        out.write("</model>"+endl);
        
        out.flush();
        out.close();
    }
    
    /**
     * Adds a <code>LogicComponent</code> object to the <code>model</code> and
     * assigns it a new unique id number.
     *
     * @param lc the component to add
     *
     * @return the new id number assigned to the component
     */
    public int addComponent(LogicComponent lc) {
        return addComponent(getUniqueId(), lc);
    }
    
    /**
     * Adds a <code>LogicComponent</code> object to the <code>model</code>,
     * given an existing unique id number.
     *
     * @param id the id number of the component
     * @param lc the component to add
     *
     * @return the id number of the component
     */
    public int addComponent(int id, LogicComponent lc) {
        lc.setId(id);
        components.put(new Integer(id), lc);
        
        if (lc.getFunction() == LogicComponent.INPUT) {
            inputComponents.add(lc);
        } else if (lc.getFunction() == LogicComponent.OUTPUT) {
            outputComponents.add(lc);
        }
        
        return id;
    }
    
    /**
     * Adds a <code>Wire</code> object to the <code>model</code>, given an
     * existing unique id number.
     *
     * @param id the id number of the wire
     * @param w the wire to add
     */
    public void addWire(int id, Wire w) {
        w.setId(id);
        wires.put(new Integer(id), w);
    }
    
    /**
     * Returns a <code>LogicComponent</code> object by id number. If the
     * component does not exist in the model, <code>null</code> is returned.
     *
     * @param id the id number of the component
     *
     * @return the component if it is found, otherwise <code>null</code>.
     */
    public LogicComponent getComponent(int id) {
        return (LogicComponent)components.get(new Integer(id));
    }
    
    /**
     * Returns an iterator for the collection of logic components in the model.
     *
     * @return the iterator
     */
    public Iterator getComponents() {
        return components.values().iterator();
    }
        
    /**
     * Removes a <code>LogicComponent</code> object from the model.
     *
     * @param id the id number of the component
     */
    public void removeComponent(int id) {
        LogicComponent lc = getComponent(id);
        if (lc.getFunction() == LogicComponent.INPUT) {
            inputComponents.remove(lc);
        } else if (lc.getFunction() == LogicComponent.OUTPUT) {
            outputComponents.remove(lc);
        }
        components.remove(new Integer(id));
    }
    
    /**
     * Removes a <code>Wire</code> object from the model.
     *
     * @param id the id number of the wire
     */
    public void removeWire(int id) {
        wires.remove(new Integer(id));
    }    
    
    /**
     * Returns a <code>Wire</code> object by id number. if the <code>wire</code>
     * does not exist in the model, <code>null</code> is returned.
     *
     * @param id the id number of the <code>wire</code>
     *
     * @return the <code>wire</code> if it is found, otherwise <code>null</code>
     */
    public Wire getWire(int id) {
        return (Wire)wires.get(new Integer(id));
    }

    /**
     * Returns an iterator for the collection of wires in the model.
     *
     * @return the iterator
     */
    public Iterator getWires() {
        return wires.values().iterator();
    }

    /**
     * Makes a visual connection between two components using a 
     * <code>Wire</code> object. The logical connection is also made.
     *
     * @param wire the wire that makes the connection
     */
    public void connectComponents(Wire wire) {
        // get the components being connected
        LogicComponent source = wire.getSource();
        LogicComponent sink = wire.getSink();
        int sourcePin = wire.getSourcePin();
        int sinkPin = wire.getSinkPin();
        
        // add the wire to the wires collection
        if (wire.getId() < 0) { wire.setId(getUniqueId()); }
        addWire(wire.getId(), wire);
        
        // Connect the components
        source.connectToOutput(sourcePin, sink, sinkPin, wire.getId());
        sink.connectToInput(sinkPin, source, sourcePin, wire.getId());
    }
    
    /**
     * Disconnects a visual connection between two components. The logical
     * connection is broken and the wire is removed from the model.
     *
     * @param wire the wire making the connection
     */
    public void disconnectComponents(Wire wire) {
        LogicComponent source = wire.getSource();
        LogicComponent sink = wire.getSink();
        int sourcePin = wire.getSourcePin();
        int sinkPin = wire.getSinkPin();
        
        source.disconnectFromOutput(sourcePin, sink, sinkPin);
        sink.disconnectFromInput(sinkPin);
        
        wires.remove(new Integer(wire.getId()));
    }
    
    /**
     * Returns a unique id number for either a <code>wire</code> or a component.
     *
     * @return a new id number
     */
    private int getUniqueId() {
        largestId++;
        return largestId;
    }
    
    /**
     * A shortcut for parsing strings to ints using Java's <code>Integer</code>
     * class.
     *
     * @param number the string to parse
     *
     * @return the string's integer value
     */
    private static int toInt(String number) {
        return Integer.parseInt(number);
    }
    
    /** the controller that instructs the model */
    private Controller controller;
    /** the next id number to be assigned */
    private int largestId;
    /** the collection of <code>LogicComponents</code> */
    private HashMap components;
    /** the collection of Wires */
    private HashMap wires;
    /** a subset of <code>components</code> that contains all the input
     * logic components */
    private HashSet inputComponents;
    /** a subset of <code>components</code> that contains all the output
     * logic components */
    private HashSet outputComponents;
    /** a thread for executing the logic simulation */
    private Thread simThread;
    /** flag indicating that the simulation is currently executing */
    private boolean runSimulation = false;
    /** the collection of <code>StateChange</code> objects waiting to be
     * executed during simulation */
    private SortedSet stateChangeQueue;
    /** the propagation delay in milliseconds during simulation */
    private long simDelay = 10;
    
    /** the endline character(s) used for writing a text file */
    private static final String endl = System.getProperty("line.separator");
}
