/*
 * CustomComponent.java
 *
 * Created on February 1, 2005, 6:08 PM
 */

package model;

import circuitsandbox.Util;
import gui.PlaceholderPin;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * <p><code>CustomComponent</code> extends the abstract class
 * <code>LogicComponent</code> and provides customizable functionality. The
 * <code>CustomComponent</code> class is basically a container component with
 * no logic functionality of its own. Each <code>CustomComponent</code> contains
 * a collection of components (including other <code>CustomComponents</code>)
 * that perform the logic displayed by their parent
 * <code>CustomComponent</code>.
 * 
 * <p>When an external component passes its output values to a
 * <code>CustomComponent</code>, the values are transferred via special input
 * <code>Pin</code> components to inputs of components inside the
 * <code>CustomComponent</code>'s collection. When the last components inside
 * the container update their outputs, the values are again transferred by
 * output <code>Pin</code> components to the inputs of other components outside
 * the <code>CustomComponent</code>.</p>
 *
 * <p>Visually, external components are connected directly to the inputs and
 * outputs of the <code>CustomComponent</code> object, but logically external
 * source components are connected to internal input <code>Pin</code> components
 * and internal output <code>Pin</code> components are connected to the external
 * sink components.</p>
 *
 * <p>Below is a diagram of a simple custom component. The blue dashed lines are
 * logical connections and the solid orange lines are visual connections made
 * with wires.</p>
 *
 * <img src="customcomponent.png" width="393" height="191"></img>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class CustomComponent extends LogicComponent {
    
    /**
     * Constructs a solitary new <code>CustomComponent</code> object.
     *
     * @param model the model
     * @param filePath relative path of the file to be loaded
     *
     * @throws java.lang.Exception if the component file could not be loaded
     */
    public CustomComponent(Model model, String filePath) throws Exception {
        this.model = model;
        components = new HashMap();
        // replace all backslashes with the more compatible forward slash
        componentPath = filePath.replaceAll("\\\\", "/");
        
        try {
            readFromXML(componentPath);
        }
        catch (Exception e) {
            throw new Exception("Component not loaded: " + e.getMessage());
        }
    }
    
    /**
     * Reads the component's data from a file in XML format. 
     * <code>largestId</code> will be set to one greater than the largest id
     * number read from the file.
     *
     * @param filePath relative path of the file to be loaded
     *
     * @throws java.lang.Exception if the data could not be loaded
     */
    private void readFromXML(String filePath) throws Exception {
        Document document;          // xml document root node
        Node currentNode;           // pointer to current node
        int numberOfInputs = 0;
        int numberOfOutputs = 0;
        File file = new File(filePath);
        
        // Check if the file exists
        if (!file.exists()) { throw new Exception("File not found"); }
        
        // Create the parser and read the file
        try {
            DocumentBuilderFactory dbf;
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);       // set to true for error detection
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            document = dbf.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            throw new Exception("Invalid file format");
        }
        
        // check for the customComponent tag
        currentNode = Util.findFirstNode(document, "customComponent");
        if (currentNode == null) { throw new Exception("Invalid file format"); }
        
        // get the component's real name
        componentName = currentNode.getAttributes().
                getNamedItem("name").getNodeValue();
        
        // inputs first pass: count the number of inputs
        currentNode = Util.findFirstNode(document, "input");
        while (currentNode != null) {
            numberOfInputs++;
            currentNode = Util.findNextSameSibling(currentNode);
        }
        
        // initialize the inputs for this custom component
        inputs = new ConnectionPoint[numberOfInputs];
        inputPins = new Pin[numberOfInputs];
        for (int i = 0; i < numberOfInputs; i++) {
            inputPins[i] = new Pin(model);
        }
        inputLocations = new Point[numberOfInputs];
        
        // inputs second pass: read the input pin visual locations (in pixels)
        currentNode = Util.findFirstNode(document, "input");
        for (int i = 0; i < numberOfInputs; i++) {
            NamedNodeMap attributes = currentNode.getAttributes();
            int x = toInt(attributes.getNamedItem("x").getNodeValue());
            int y = toInt(attributes.getNamedItem("y").getNodeValue());
            inputLocations[i] = new Point(x, y);
            currentNode = Util.findNextSameSibling(currentNode);
        }
        
        // components first pass: create all logic component objects
        LogicComponentFactory factory = LogicComponentFactory.newInstance();
        factory.setModel(model);
        currentNode = Util.findFirstNode(document, "component");
        while (currentNode != null) {
            
            // get the component's id and type
            NamedNodeMap attributes = currentNode.getAttributes();
            String type = attributes.getNamedItem("type").getNodeValue();
            int id = toInt(attributes.getNamedItem("id").getNodeValue());
            
            // create the logic component and add it to the collection
            addComponent(id, factory.buildComponent(type));
                                    
            currentNode = Util.findNextSameSibling(currentNode);
        }
        
        // components second pass: connect all components to each other or to
        // this CustomComponent's input Pins
        currentNode = Util.findFirstNode(document, "component");
        while (currentNode != null) {
            int sinkId = toInt(currentNode.
                    getAttributes().getNamedItem("id").getNodeValue());
            LogicComponent sink = getComponent(sinkId);
            Node connection = Util.findFirstNode(currentNode, "connection");
            
            // loop through the component's used input pins
            while (connection != null) {
                
                // get the input tag's attributes
                NamedNodeMap attributes = connection.getAttributes();
                int sinkPin = toInt(
                        attributes.getNamedItem("number").getNodeValue());
                int sourceId = toInt(
                        attributes.getNamedItem("sourceId").getNodeValue());
                int sourcePin = toInt(
                        attributes.getNamedItem("sourcePin").getNodeValue());
                
                if (sourceId < 0) {
                    // the component is connected to the output of one of this
                    // CustomComponent's input Pin objects
                    connectToInternalInput(sourcePin, sink, sinkPin);
                    sink.connectToInput(sinkPin, inputPins[sourcePin], 0);
                } else {
                    // the component is connected to the output of some other
                    // internal logic component
                    LogicComponent source = getComponent(sourceId);
                    sink.connectToInput(sinkPin, source, sourcePin);
                    source.connectToOutput(sourcePin, sink, sinkPin);
                }
                
                connection = Util.findNextSameSibling(connection);
            }
            
            currentNode = Util.findNextSameSibling(currentNode);
        }
        
        // outputs first pass: count the number of outputs
        currentNode = Util.findFirstNode(document, "output");
        while (currentNode != null) {
            numberOfOutputs++;
            currentNode = Util.findNextSameSibling(currentNode);
        }
        
        // initialize the outputs for this custom component
        outputs = new ArrayList[numberOfOutputs];
        outputPins = new Pin[numberOfOutputs];
        for (int i = 0; i < numberOfOutputs; i++) {
            outputs[i] = new ArrayList();
            outputPins[i] = new Pin(model);
        }
        outputLocations = new Point[numberOfOutputs];
        
        // outputs second pass: connect all of this CustomComponent's output
        // Pins to internal components or input Pins
        currentNode = Util.findFirstNode(document, "output");
        for (int i = 0; i < numberOfOutputs; i++) {
            NamedNodeMap attributes = currentNode.getAttributes();
            int sourceId = toInt(
                    attributes.getNamedItem("sourceId").getNodeValue());
            int sourcePin = toInt(
                    attributes.getNamedItem("sourcePin").getNodeValue());
            
            // read the output pin visual location (in pixels)
            int x = toInt(attributes.getNamedItem("x").getNodeValue());
            int y = toInt(attributes.getNamedItem("y").getNodeValue());
            outputLocations[i] = new Point(x, y);
            
            if (sourceId < 0) {
                // the output Pin is connected directly to an input Pin
                connectToInternalInput(sourcePin, outputPins[i], 0);
                connectToInternalOutput(i, inputPins[sourcePin], 0);
            } else {
                // the output Pin is connected to an internal logic component
                LogicComponent source = getComponent(sourceId);
                source.connectToOutput(sourcePin, outputPins[i], 0);
                connectToInternalOutput(i, source, sourcePin);
            }
            
            currentNode = Util.findNextSameSibling(currentNode);
        }
    }
    
    /**
     * Overrides <code>LogicComponent.connectToInput(int, LogicComponent,
     * int)</code>. The <code>CustomComponent</code> first connects the source
     * component to itself, then connects the source to its internal input
     * <code>Pin</code> component corresponding to the input number.
     *
     * @param inputNumber the input number on this component
     * @param source the source component
     * @param sourcePin the output pin number on the source component
     */
    public void connectToInput(int inputNumber, LogicComponent source,
            int sourcePin) {
        
        // connect self to source
        ConnectionPoint cp = source.getConnectionPointOut(sourcePin);
        inputs[inputNumber] = cp;
        
        // connect internal input to source
        inputPins[inputNumber].connectToInput(0, source, sourcePin);
    }

    /**
     * Overrides <code>LogicComponent.connectToInput(int, LogicComponent,
     * int, int)</code>. The <code>CustomComponent</code> first connects the
     * source component to itself visually, then makes a logical connection from
     * the source to its internal input <code>Pin</code> component corresponding
     * to the input number.
     *
     * @param inputNumber the input number on this component
     * @param source the source component
     * @param sourcePin the output pin number on the source component
     * @param wireId the id number of the <code>wire</code> representing this
     * connection
     */
    public void connectToInput(int inputNumber, LogicComponent source,
            int sourcePin, int wireId) {
        
        // connect self to source
        ConnectionPoint cp = source.getConnectionPointOut(sourcePin);
        cp.wireId = wireId;
        inputs[inputNumber] = cp;
        
        // connect internal input to source
        inputPins[inputNumber].connectToInput(0, source, sourcePin);
    }
    
    /**
     * Overrides <code>LogicComponent.disconnectFromInput(int)</code>.
     * The <code>CustomComponent</code> first disconnects from its own
     * internal input <code>Pin</code> component corresponding to the input
     * number,  then disconnects from itself.
     *
     * @param inputNumber the input pin number to disconnect
     */
    public void disconnectFromInput(int inputNumber) {
        // disconnect internal input Pin
        inputPins[inputNumber].disconnectFromInput(0);
        
        // disconnect self
        inputs[inputNumber] = null;
    }

    /**
     * Overrides <code>LogicComponent.connectToOutput(int, LogicComponent,
     * int)</code>. The <code>CustomComponent</code> first connects the sink
     * component to itself, then connects the sink to its internal output
     * <code>Pin</code> component corresponding to the output number.
     *
     * @param outputNumber the output number on this component
     * @param sink the sink component
     * @param sinkPin the input pin number on the sink component
     */
    public void connectToOutput(int outputNumber, LogicComponent sink,
            int sinkPin) {
        
        // connect self to sink
        ConnectionPoint cp = sink.getConnectionPointIn(sinkPin);
        outputs[outputNumber].add(cp);
        
        // connect internal output Pin to sink
        outputPins[outputNumber].connectToOutput(0, sink, sinkPin);
    }

    /**
     * Overrides <code>LogicComponent.connectToOutput(int, LogicComponent,
     * int, int)</code>. The <code>CustomComponent</code> first connects the
     * sink component to itself visually, then makes a logical connection from
     * the sink to its internal output <code>Pin</code> component corresponding
     * to the output number.
     *
     * @param outputNumber the output number on this component
     * @param sink the sink component
     * @param sinkPin the input pin number on the sink component
     * @param wireId the id number of the <code>wire</code> representing this
     * connection
     */
    public void connectToOutput(int outputNumber, LogicComponent sink,
            int sinkPin, int wireId) {
        
        // connect self to sink
        ConnectionPoint cp = sink.getConnectionPointIn(sinkPin);
        cp.wireId = wireId;
        outputs[outputNumber].add(cp);
        
        // connect internal output Pin to sink
        outputPins[outputNumber].connectToOutput(0, sink, sinkPin);
    }

    /**
     * Overrides <code>LogicComponent.disconnectFromOutput(int, LogicComponent,
     * int)</code>. The <code>CustomComponent</code> first
     * disconnects from its own internal output <code>Pin</code> component
     * corresponding to the output number, then disconnects from itself.
     *
     * @param outputNumber the output pin number to disconnect
     * @param sink the sink <code>LogicComponent</code> being disconnected from
     * @param sinkPin the input pin number on the sink
     * <code>LogicComponent</code>
     */
    public void disconnectFromOutput(int outputNumber, LogicComponent sink,
            int sinkPin) {
        
        // disconnect internal output Pin
        outputPins[outputNumber].disconnectFromOutput(0, sink, sinkPin);
        
        // disconnect self
        ConnectionPoint cp = sink.getConnectionPointIn(sinkPin);
        ArrayList outputsFromPin = outputs[outputNumber];
        for (int i = 0; i < outputsFromPin.size(); i++) {
            ConnectionPoint cp2 = (ConnectionPoint) outputsFromPin.get(i);
            if (cp2.component.equals(cp.component) &&
                    cp2.pinNumber == cp.pinNumber) {
                /* if this ConnectionPoint points to the same sink and pin
                   number that we are trying to disconnect from, then this is
                   the one we need to remove */
                outputsFromPin.remove(i);
            }
        }
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
        return new ConnectionPoint(inputPins[inputNumber], 0);
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
        return new ConnectionPoint(outputPins[outputNumber], 0);
    }
    
    /**
     * <p>Makes a connection to the output of one of the the
     * <code>CustomComponent</code>'s input <code>Pin</code> components.
     * This is how source components outside the <code>CustomComponent</code>
     * transmit values to sink components inside the
     * <code>CustomComponent</code>.</p>
     * 
     * <p>The <code>connectToInput(int, LogicComponent, int)</code> method of
     * the internal sink component must also be called to complete the
     * connection.</p>
     *
     * <p>Connections can also be made from an input <code>Pin</code>
     * directly to an output <code>Pin</code>. If this is the case, this method
     * should be passed the output <code>Pin</code> object it is being
     * connected to, and its <code>connectToInternalOutput</code> method should
     * be passed the input <code>Pin</code> that it will be connected to.</p>
     *
     * @param pinNumber the number of the <code>CustomComponent</code>'s input
     * pin
     * @param sink the sink component
     * @param sinkPin the input pin number on the sink component
     */
    private void connectToInternalInput(int pinNumber, LogicComponent sink,
            int sinkPin) {
        inputPins[pinNumber].connectToOutput(0, sink, sinkPin);
    }

    /**
     * <p>Makes a connection to the input of one of the
     * <code>CustomComponent</code>'s output <code>Pin</code> components.
     * This is how sink components outside the <code>CustomComponent</code>
     * recieve values from components inside the <code>CustomComponent</code>.
     * </p>
     * 
     * <p>The <code>connectToOuput(int, LogicComponent, int)</code> method of
     * the internal source component must also be called to complete the
     * connection.</p>
     *
     * <p>Connections can also be made from an output <code>Pin</code> component
     * directly to an input <code>Pin</code> component. If this is the case,
     * this method should be passed the input <code>Pin</code> object it is
     * being connected to, and the <code>connectToInternalInput</code> method
     * should be passed the output <code>Pin</code> that it will be connected
     * to.</p>
     *
     * @param pinNumber the number of the <code>CustomComponent</code>'s output
     * pin
     * @param source the source component
     * @param sourcePin the output pin number on the source component
     */
    private void connectToInternalOutput(int pinNumber, LogicComponent source,
            int sourcePin) {
        outputPins[pinNumber].connectToInput(0, source, sourcePin);
    }
    
    /**
     * Returns the output value of the custom component's pin object associated
     * with the output number, since the custom component itself doesn't have
     * output values.
     *
     * @param outputNumber the output number to get the value from
     *
     * @return the output value (<code>TRUE</code> | <code>FALSE</code> |
     * <code>UNDEFINED</code>)
     */
    public byte getValueOfOutput(int outputNumber) {
        return outputPins[outputNumber].getValueOfOutput(0);
    }
    
    /**
     * Returns the identifying string for this type of
     * <code>LogicComponent</code> and <code>CustomComponent</code> object.
     * The string preceding the colon character identifies this object as a
     * <code>CustomComponent</code>, and the string following the colon
     * identifies the file that this CustomComponent was loaded from.
     *
     * @return the object's type-identifying string
     */
    public String getTypeString() {
        return TYPE_STRING + ":" + componentPath;
    }

    /**
     * Adds a <code>LogicComponent</code> object to the <code>components</code>
     * collection, given a unique id number.
     *
     * @param id the id number of the component
     * @param lc the component to add
     */
    private void addComponent(int id, LogicComponent lc) {
        lc.setId(id);
        components.put(new Integer(id), lc);
    }

    /**
     * Returns a <code>LogicComponent</code> object by id number. If the
     * component does not exist in the <code>components</code> collection,
     * <code>null</code> is returned.
     *
     * @param id the id number of the component
     *
     * @return the component if it is found, otherwise <code>null</code>
     */
    private LogicComponent getComponent(int id) {
        return (LogicComponent)components.get(new Integer(id));
    }
    
    /**
     * Returns the plain english name of the component.
     *
     * @return the component name
     */
    public String getName() {
        return componentName;
    }
    
    /**
     * Returns the component's file path as a string relative to the working
     * directory.
     *
     * @return the path string
     */
    public String getPath() {
        return componentPath;
    }
    
    /**
     * Returns the array of input pin locations. Used by the gui custom
     * component images.
     *
     * @return the array of input pin locations
     */
    public Point[] getInputLocations() {
        return inputLocations;
    }
    
    /**
     * Returns the array of output pin locations. Used by the gui custom
     * component images.
     *
     * @return the array of output pin locations
     */
    public Point[] getOutputLocations() {
        return outputLocations;
    }
    
    /**
     * Does nothing because custom components do not directly compute their
     * output values. This method should never be called.
     */
    public void compute() { }
    
    /**
     * Sets the values of all the output pins on the internal components to
     * <code>UNDEFINED</code>. Call this method to reset the state of each
     * component after running the simulation.
     */
    public void resetState() {
        if (componentImage != null) {
            // reset the frame displayed in the component image
            componentImage.resetState();
        }
        
        // reset the internal components
        Iterator iter = components.values().iterator();
        while (iter.hasNext()) {
            ((LogicComponent)iter.next()).resetState();
        }
        
        // reset the Pins
        for (int i = 0; i < inputPins.length; i++) {
            inputPins[i].resetState();
        }
        for (int i = 0; i < outputPins.length; i++) {
            outputPins[i].resetState();
        }
    }
    
    /** the collection of internal <code>LogicComponent</code> objects */
    private HashMap components;
    /** the input <code>Pin</code> components corresponding to the
     * <code>CustomComponent</code> inputs */
    private Pin[] inputPins;
    /** the output <code>Pin</code> components corresponding to the
     * <code>CustomComponent</code> outputs */
    private Pin[] outputPins;
    /** the visual locations of the input pins in pixels, relative to the
     * top left of the component image */
    private Point[] inputLocations;
    /** the visual locations of the output pins in pixels, relative to the
     * top left of the component image */
    private Point[] outputLocations;
    /** the relative path of the file that this component was loaded from */
    private String componentPath;
    /** the name of this component in plain english */
    private String componentName;
    
    // -----------------------------------------------------------------------
    
    /**
     * <code>Pin</code> objects are <code>LogicComponents</code> that simply
     * observe other components and notify their own observers. They are used by
     * <code>CustomComponents</code> to ferry signals in and out, instead of
     * making the <code>CustomComponent</code> object itself observe its
     * sources. When making a connection to an input pin number on a
     * <code>CustomComponent</code>, the connection is translated to input
     * number 0 on an input <code>Pin</code> object belonging to the
     * <code>CustomComponent</code>.
     *
     * @author Daniel Stahl
     * @author Kevin Wellwood
     */
    private class Pin extends LogicComponent {
        
        /**
         * Constructs a new <code>Pin</code> object.
         *
         * @param model the model
         */
        public Pin(Model model) { super(model, 1, 1); }
        
        /**
         * Sets the value of the output to the value of the input and notifies
         * all observers. No actual computation is done because <code>Pin</code>
         * objects just transfer signals across <code>CustomComponent</code>
         * object boundaries.
         */
        public void compute() { setValueOfOutput(0, getValueOfInput(0)); }
        
        /**
         * This method must be implemented, but it will never be called for this
         * type of <code>LogicComponent</code>. It will always return
         * <code>null</code>.
         *
         * @return <code>null</code>
         */
        public String getTypeString() { return null; }
        
        /**
         * This method must be implemented, but it will never be called for this
         * type of <code>LogicComponent</code>.
         */
        public void setLocation(int x, int y) { }

        /**
         * Returns the value of the pin's input so that its internal array
         * of output values is never accessed.
         *
         * @param pin the output number (unused)
         *
         * @return the value of the output pin connected to this pin's input
         */
        public byte getValueOfOutput(int pin) { return getValueOfInput(0); }
    }
    
    // -----------------------------------------------------------------------
    
    /**
     * Saves all of the data necessary for a custom component to disk. An XML
     * file containing the model data will be written, as well as a .png image
     * file of the specified image for the component.
     *
     * @param model the model
     * @param file the custom component file to write (.csc)
     * @param name the english name of the component
     * @param image the image representing the component
     * @param inputPins the array of Pin objects that will be custom component
     * input pins
     * @param outputPins the array of Pin objects that will be custom component
     * output pins
     *
     * @throws Exception if the file could not be written
     */
    public static void saveComponent(Model model, File file, String name,
            Image image, ArrayList inputPins, ArrayList outputPins)
            throws Exception {
        
        // write the image file
        File imgFile = Util.replaceExtension(file, "png");
        ImageIO.write((BufferedImage)image, "png", imgFile);

        // write the xml
        writeToXML(model, file, name, inputPins, outputPins);
    }
    
    /**
     * Writes the given component data to a file in XML format. If the file
     * already exists, it will be overwritten.
     *
     * @param model the model
     * @param file the file to write
     * @param name the english name of the component
     * @param inputPins the array of PlaceholderPin input pins
     * @param outputPins the array of PlaceholderPin output pins
     *
     * @throws Exception if the file could not be written
     */
    private static void writeToXML(Model model, File file, String name,
            ArrayList inputPins, ArrayList outputPins) throws Exception {
        // open the file for output
        OutputStreamWriter out = new OutputStreamWriter(
                new BufferedOutputStream(new FileOutputStream(file)), "8859_1");
        
        // write out the internal dtd
        out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+endl);
        out.write(
                "<!DOCTYPE customComponent [" + endl +
                "   <!ELEMENT customComponent " +
                                    "(input+, component*, output+)>" + endl +
                "   <!ATTLIST customComponent" + endl +
                "       name CDATA #REQUIRED>" + endl +
                "   <!ELEMENT input EMPTY>" + endl +
                "   <!ATTLIST input" + endl +
                "       x CDATA #REQUIRED" + endl +
                "       y CDATA #REQUIRED>" + endl +
                "   <!ELEMENT component (connection*)>" + endl +
                "   <!ATTLIST component" + endl +
                "       type CDATA #REQUIRED" + endl +
                "       id CDATA #REQUIRED>" + endl +
                "   <!ELEMENT connection EMPTY>" + endl +
                "   <!ATTLIST connection" + endl +
                "       number CDATA #REQUIRED" + endl +
                "       sourceId CDATA #REQUIRED" + endl +
                "       sourcePin CDATA #REQUIRED>" + endl +
                "   <!ELEMENT output EMPTY>" + endl +
                "   <!ATTLIST output" + endl +
                "       x CDATA #REQUIRED" + endl +
                "       y CDATA #REQUIRED" + endl +
                "       sourceId CDATA #REQUIRED" + endl +
                "       sourcePin CDATA #REQUIRED>" + endl +
                "]>");
        out.write("<customComponent name=\"" + name + "\">" + endl);

        // write the input pins
        Iterator iter = inputPins.iterator();
        while (iter.hasNext()) {
            PlaceholderPin pin = (PlaceholderPin)iter.next();
            out.write("    <input " +
                    "x=\"" + pin.getCenter().x + "\" " +
                    "y=\"" + pin.getCenter().y + "\" />" + endl);
        }
        
        // write all the components internal to the custom component
        iter = model.getComponents();
        while (iter.hasNext()) {
            LogicComponent lc = (LogicComponent)iter.next();
            if (lc.getFunction() == LogicComponent.LOGIC) {
                
                out.write("    " +
                        "<component type=\"" + lc.getTypeString() + "\" " +
                        "id=\"" + lc.getId() + "\">" + endl);
            
                // loop through each input pin on this component
                LogicComponent.ConnectionPoint[] inputConnections;
                inputConnections = lc.getInputConnections();
                for (int i = 0; i < inputConnections.length; i++) {

                    // if the input has a connection to it
                    if (inputConnections[i] != null) {

                        // get the wire that makes this connection. it holds the
                        // visual connection information that we want to store,
                        // not the logical connection in the logic component's
                        // ConnectionPoint.
                        Wire wire = model.getWire(inputConnections[i].wireId);

                        int sourceId = wire.getSource().getId();
                        int sourcePin = wire.getSourcePin();
                        if (model.getComponent(sourceId).getFunction() !=
                                LogicComponent.LOGIC) {
                            // this component is internally connected to an
                            // input pin on the custom component
                            sourcePin = findPlaceholderPin(inputPins,
                                    model.getComponent(sourceId));
                            sourceId = -1;
                        }

                        // write connection on input pin 'i' of component 'lc'
                        out.write("        " + 
                                "<connection " + 
                                "number=\""    + i         + "\" " +
                                "sourceId=\""  + sourceId  + "\" " +
                                "sourcePin=\"" + sourcePin + "\" />" + endl);
                    }
                }

                out.write("    </component>"+endl);
            }
        }
        
        // write the output pins
        iter = outputPins.iterator();
        while (iter.hasNext()) {
            // get the output pin object
            PlaceholderPin pin = (PlaceholderPin)iter.next();
            // get the output component that is representing the output pin
            // on the custom component
            LogicComponent lc = pin.getLogicComponent();
            // get the wire attached to the input of the output component
            Wire wire = model.getWire(lc.getInputConnections()[0].wireId);
            // get the source component and output pin number for the connection
            int sourceId = wire.getSource().getId();
            int sourcePin = wire.getSourcePin();
            
            if (model.getComponent(sourceId).getFunction() !=
                    LogicComponent.LOGIC) {
                // this component is internally connected directly to one of the
                // custom component input pins
                sourcePin = findPlaceholderPin(inputPins,
                        model.getComponent(sourceId));
                sourceId = -1;
            }
            
            out.write("    " +
                    "<output " + 
                    "x=\"" + pin.getCenter().x + "\" " +
                    "y=\"" + pin.getCenter().y + "\" " +
                    "sourceId=\"" + sourceId + "\" " +
                    "sourcePin=\"" + sourcePin + "\" />" + endl);
        }
        
        out.write("</customComponent>" + endl);
        
        out.flush();
        out.close();
    }
    
    /**
     * Searches through an array list of PlaceHolderPin objects and returns the
     * array index of the Pin that is representing the given pin number on the
     * given logic component.
     *
     * @return the array index, or <code>-1</code> if not found
     */
    private static int findPlaceholderPin(ArrayList pins, LogicComponent lc) {
        for (int i = 0; i < pins.size(); i++) {
            PlaceholderPin pin = (PlaceholderPin)pins.get(i);
            if (pin.getLogicComponent().equals(lc)) { return i; }
        }
        
        return -1;
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
    
    /** used to identify <code>CustomComponent</code> objects */
    public final static String TYPE_STRING = "custom";
    
    /** the endline character(s) used for writing a text file */
    private static final String endl = System.getProperty("line.separator");
}
