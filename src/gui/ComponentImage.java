/*
 * ComponentImage.java
 *
 * Created on February 5, 2005, 3:26 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.List;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import model.LogicComponent;

/**
 * <p>Extends <code>Displayable</code> to create an abstract super class for
 * all component images. <code>ComponentImage</code>s have two collections of
 * <code>ComponentPin</code> objects which are drawn over their input and output
 * locations. Adding a <code>ComponentImage</code> to the sandbox or removing
 * it will add or remove its <code>ComponentPin</code>s.</p>
 *
 * <p>Each component image corresponds to 0 or 1 logic component in the model
 * that simulates the actual function of the component represented. Component
 * images are the visual manifestation of logic components.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public abstract class ComponentImage extends Displayable {
    
    /**
     * Creates new form ComponentImage
     * Note: Do not use this constructor, its for NetBeans only.
     */
    public ComponentImage() {
        this(null, null);
    }

    /**
     * Constructs a new component image with a location in the sandbox. The
     * location should be in standard coordinates.
     *
     * @param gui the gui
     * @param location the location in the sandbox (may be <code>null</code>)
     */
    public ComponentImage(Gui gui, Point location) {
        super(gui, location);
        initComponents();
        
        // This initializes the class collection pinLocations when
        // the first component image is created.
        if (inputPinLocations == null) { loadPinLocations(); }
        
        // only create component pins if the component has a location
        // (which means it is not in the toybox) and isnt a custom component
        // (which have their own pin locations stored in the .csc)
        if (!(this instanceof CustomImage) && location != null) {
            // get the pin locations for this type of component
            Point[] inputs = (Point[])inputPinLocations.get(getTypeString());
            Point[] outputs = (Point[])outputPinLocations.get(getTypeString());

            if (inputs == null || outputs == null) {
                System.out.println("Missing data in pinLocations.txt");
                System.exit(1);
            }
            
            // create the ComponentPin arrays
            inputPins = new ComponentPin[inputs.length];
            outputPins = new ComponentPin[outputs.length];
            
            // create the input ComponentPin objects
            for (int i = 0; i < inputs.length; i++) {
                inputPins[i] = new ComponentPin(gui, inputs[i], this,
                        ComponentPin.INPUT_PIN, i);
            }
            
            // create the output ComponentPin objects
            for (int i = 0; i < outputs.length; i++) {
                outputPins[i] = new ComponentPin(gui, outputs[i], this,
                        ComponentPin.OUTPUT_PIN, i);
            }
        }
    }
    
    /**
     * Sets the image component's corresponding logical
     * <code>LogicComponent</code> that is used by the model. This method should
     * only be called by a logic component's <code>setCustomImage</code>
     * method.
     *
     * @param logicComponent the logic component (not <code>null</code>)
     */
    public void setLogicComponent(LogicComponent logicComponent) {
        this.logicComponent = logicComponent;
        if (logicComponent.getComponentImage() != this) {
            logicComponent.setComponentImage(this);
        }
    }
    
    /**
     * Returns the component images's corresponding  <code>LogicComponent</code>
     * that is used in the model.
     *
     * @return the logic component object
     */
    public LogicComponent getLogicComponent() {
        return logicComponent;
    }
    
    /**
     * Sets the state of the component image. The state is used to choose the
     * number of the frame that will be displayed when the component is
     * painted. Each subclass that needs this method must implement it according
     * to the states it has.
     *
     * @param state the state number
     */
    public void setState(byte state) {
        /* override this method */
    }
    
    /**
     * Resets the frame back to 0 after exiting simulation mode. This method is
     * called by the <code>resetState</code> method of the component image's
     * corresponding logic component.
     */
    public void resetState() {
        frame = 0;
    }
    
    /**
     * Loads a list of image files representing the component. The first image
     * will be used in editing mode. All images have the same dimensions.
     *
     * @param imageList the list of <code>String</code> image file paths
     */
    protected void loadImages(List imageList) {
        images = new Image[imageList.getItemCount()];
        MediaTracker mediaTracker = new MediaTracker(this);
        
        // load all images in the list
        for (int i = 0; i < imageList.getItemCount(); i++) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            images[i] = toolkit.getImage(imageList.getItem(i));
            mediaTracker.addImage(images[i], i);
        }
        
        // wait for all the bitmaps to finish loading
        try { mediaTracker.waitForAll(); } catch (InterruptedException e) { }
        
        // set the size according to the first image. all images for a given
        // component should be the same size.
        setStdSize(images[0].getWidth(null), images[0].getHeight(null));
    }
    
    /**
     * Draws the component.
     *
     * @param g the graphics to draw with
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.scale(gui.getZoomFactor(), gui.getZoomFactor());
        g2.drawImage(images[frame], 0, 0, null);
        
        if (selected) {
            g2.setColor(Color.black);
            g2.setStroke(selectionStroke);
            g2.drawRect(0, 0, getStdWidth() - 1, getStdHeight() - 1);
        }
        
        if (customComponentPin > -1) {
            int numdigits = (int)Math.ceil((customComponentPin + 1) / 10f);
            int boxwidth = 19;
            if (numdigits > 1) { boxwidth *= numdigits; }
            
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(new Color(255, 255, 255, 192));
            g2.fillRect(0, getStdHeight() - 28, boxwidth, 27);
            g2.setColor(Color.black);
            g2.drawRect(0, getStdHeight() - 28, boxwidth, 27);
                    
            g2.setFont(g2.getFont().deriveFont(28f));
            if (getLogicComponent().getNumberOfInputs() == 0) {
                g2.setColor(new Color(255, 0, 0, 212));
            } else {
                g2.setColor(new Color(0, 0, 255, 212));
            }
            g2.drawString("" + customComponentPin, 2, getStdHeight() - 4);
        }
    }
    
    /**
     * Brings the component to the front of the Z-order in the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void bringToFront(Sandbox sandbox) {
        super.bringToFront(sandbox);
        for (int i = 0; i < inputPins.length; i++) {
            inputPins[i].bringToFront(sandbox);
        }
        for (int i = 0; i < outputPins.length; i++) {
            outputPins[i].bringToFront(sandbox);
        }
    }
    
    /**
     * Centers the <code>Displayable</code> at the given location. The location
     * must be in standard coordinates.
     *
     * @param location the location
     */
    public void centerAt(Point location) {
        Point oldLocation = getStdLocation();
        super.centerAt(location);
        Point newLocation = getStdLocation();
        
        int xShift = newLocation.x - oldLocation.x;
        int yShift = newLocation.y - oldLocation.y;
        
        for (int i = 0; i < inputPins.length; i++) {
            inputPins[i].shiftLocation(xShift, yShift);
        }
        for (int i = 0; i < outputPins.length; i++) {
            outputPins[i].shiftLocation(xShift, yShift);
        }
    }
    
    /**
     * Moves the <code>ComponentImage</code> and its pins by a relative amount.
     *
     * @param xShift the horizontal distance in standard coordinates
     * @param yShift the vertical distance in standard coordinates
     */
    public void shiftLocation(int xShift, int yShift) {
        super.shiftLocation(xShift, yShift);
        
        for (int i = 0; i < inputPins.length; i++) {
            inputPins[i].shiftLocation(xShift, yShift);
        }
        for (int i = 0; i < outputPins.length; i++) {
            outputPins[i].shiftLocation(xShift, yShift);
        }
    }
    
    /**
     * Adds the component to the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void addToSandbox(Sandbox sandbox) {
        sandbox.add(this, 0);
        zoom();
        
        for (int i = 0; i < inputPins.length; i++) {
            inputPins[i].addToSandbox(sandbox);
        }
        for (int i = 0; i < outputPins.length; i++) {
            outputPins[i].addToSandbox(sandbox);
        }
    }
    
    /**
     * Removes the component from the sandbox.
     *
     * @param sandbox the sandbox
     */
    public void removeFromSandbox(Sandbox sandbox) {
        sandbox.remove(this);
        
        for (int i = 0; i < inputPins.length; i++) {
            inputPins[i].removeFromSandbox(sandbox);
        }
        for (int i = 0; i < outputPins.length; i++) {
            outputPins[i].removeFromSandbox(sandbox);
        }
    }
    
    /**
     * Returns the center point of the specified output pin.
     *
     * @param pinNumber the number of the pin whose center is to be returned
     *
     * @return the center coordinates of the pin
     */
    public Point getOutputPinLocation(int pinNumber) {
        return outputPins[pinNumber].getStdCenter();
    }

    /**
     * Returns the gui pin object associated with the given output number.
     *
     * @param pinNumber the number of the output
     *
     * @return the component pin
     */
    public ComponentPin getOutputPin(int pinNumber) {
        return outputPins[pinNumber];
    }
    
    /**
     * Returns the center point of the specified input pin.
     *
     * @param pinNumber the number of the pin whose center is to be returned
     *
     * @return the center coordinates of the pin
     */
    public Point getInputPinLocation(int pinNumber) {
        return inputPins[pinNumber].getStdCenter();
    }

    /**
     * Returns the gui pin object associated with the given input number.
     *
     * @param pinNumber the number of the input
     *
     * @return the component pin
     */
    public ComponentPin getInputPin(int pinNumber) {
        return inputPins[pinNumber];
    }
    
    /**
     * Sets the pin number this component will be mapped to, should the model
     * be saved as a custom component. This is only relevant for strictly input
     * and output components.
     *
     * @param pinNumber the pin number
     */
    public void setCustomComponentPin(int pinNumber) {
        customComponentPin = pinNumber;
    }
    
    /**
     * Returns the pin number this component will be mapped to, should the model
     * be saved as a custom component. This is only relevant for strictly
     * input and output components.
     *
     * @return the pin number
     */
    public int getCustomComponentPin() {
        return customComponentPin;
    }
    
    /**
     * Identifies <code>ComponentImage</code> objects as the COMPONENT type of
     * <code>Displayable</code>.
     *
     * @return <code>Displayable.COMPONENT</code>
     */
    public int getDisplayableType() { return Displayable.COMPONENT; }
    
    /**
     * Returns a string that uniquely identifies each type of
     * <code>ComponentImage</code>. This method is analogous to
     * <code>LogicComponent.getTypeString()</code> and when implemented will
     * return this component image's <code>LogicComponent</code>'s type string.
     *
     * @return the identifying string
     */
    public abstract String getTypeString();
    
    /**
     * Returns a small 18x18 <code>Icon</code> of the component's first image
     * frame. The icon is used by the <code>Toybox</code> to display the
     * components. The <code>Icon</code> object is created the first time this
     * method is called, and cached for subsequent calls.
     *
     * @return the icon
     */
    public Icon getIcon() {
        if (imageIcon == null) {
            imageIcon = new ImageIcon(
                    images[0].getScaledInstance(18, 18, Image.SCALE_SMOOTH));
        }
        return imageIcon;
    }
    
    /**
     * Returns a 32x32 <code>Cursor</code> containing the component's first
     * image frame with a small arrow.
     *
     * @return the cursor
     */
    public Cursor getAddingCursor() {
        if (addingCursor == null) {
            BufferedImage bi = new BufferedImage(32, 32,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi.createGraphics();

            g2.drawImage(images[0], 5, 5, 31, 31, 0, 0,
                    images[0].getWidth(null),
                    images[0].getHeight(null), null, null);

            Polygon p = new Polygon();
            p.addPoint(1, 1);
            p.addPoint(10, 1);
            p.addPoint(1, 10);

            g2.setColor(Color.white); g2.fill(p);
            g2.setColor(Color.black); g2.draw(p);
            
            addingCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    bi, new Point(1, 1), toString());
        }
        return addingCursor;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(null);

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                ComponentImage.this.mouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ComponentImage.this.mousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ComponentImage.this.mouseReleased(evt);
            }
        });

    }//GEN-END:initComponents
    
    private void mouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseReleased
        if (!gui.isSimulating()) {
            displayableMouseReleased(evt);
        }
    }//GEN-LAST:event_mouseReleased
    
    private void mouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseDragged
        if (!gui.isSimulating()) {
            displayableMouseDragged(evt);
        }
    }//GEN-LAST:event_mouseDragged
    
    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        if (!gui.isSimulating()) {
            displayableMousePressed(evt);
        }
    }//GEN-LAST:event_mousePressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Loads the visual locations for all components' input and output pins from
     * a text file. If there is an error reading the file, an error is sent to
     * the console. The application must terminate.
     */
    protected static void loadPinLocations() {
        BufferedReader input = null;
        
        try {
            // open the file for reading
            input = new BufferedReader(
                    new FileReader(new File(System.getProperty("user.dir") +
                    "/pinLocations.txt")));
            
            // Create the collections for input pins and output pins
            inputPinLocations = new HashMap();
            outputPinLocations = new HashMap();
            
            // loop through each line until end of file
            String line;
            while ((line = input.readLine()) != null) {
                // split the string, format is: "type:numInputs:numOutputs"
                String[] str = line.split(":");
                if (str.length != 3) {
                    throw new Exception("Invalid format: pinLocations.txt");
                }
                
                String typeString = str[0];
                int numInputs = Integer.parseInt(str[1]);
                int numOutputs = Integer.parseInt(str[2]);
                
                // read all the input pin coordinates
                Point[] inputs = new Point[numInputs];
                for (int i=0; i < numInputs; i++) {
                    line = input.readLine();
                    // split the string, format is: "x y"
                    String[] coords = line.split(",");
                    if (coords.length != 2) {
                        throw new Exception("Invalid format");
                    }
                    
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    inputs[i] = new Point(x, y);
                }
                
                // push the input pin locations array for this component
                // into the hashmap
                inputPinLocations.put(typeString, inputs);
                
                // read all the output pin coordinates
                Point[] outputs = new Point[numOutputs];
                for (int i=0; i < numOutputs; i++) {
                    line = input.readLine();
                    // split the string, format is: "x y"
                    String[] coords = line.split(",");
                    if (coords.length != 2) {
                        throw new Exception("Invalid format");
                    }
                    
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    outputs[i] = new Point(x, y);
                }
                
                // push the output pin locations array for this component
                // into the hashmap
                outputPinLocations.put(typeString, outputs);
            }
            
        } catch (Exception e) {
            //System.out.println("Error in file 'pinLocations.txt'");
            //System.exit(1);
        } finally {
            try { input.close(); } catch (Exception e) { }
        }
    }
    
    /** the component's graphical wire attachment points (inputs) */
    protected ComponentPin[] inputPins;
    /** the component's graphical wire attachment points (outputs) */
    protected ComponentPin[] outputPins;
    
    /** the corresponding logic component in the model */
    protected LogicComponent logicComponent;
    /** the bitmaps of the component */
    private Image[] images;
    /** the current image number to draw */
    protected int frame = 0;
    /** an 18x18 icon of <code>images[0]</code> for the toybox */
    private ImageIcon imageIcon;
    /** a 32x32 image of <code>images[0]</code> with a small cursor arrow for
     * use when adding a new component to the sandbox */
    private Cursor addingCursor;
    /** the pin number this component (if its an input or output component) will
     * be mapped to, when the model is saved as a custom component. this number
     * is reassigned each time the save as custom component dialog is enetered */
    private int customComponentPin = -1;
    /** the relative path where all the component bitmaps are stored */
    public static final String IMAGE_PATH = "images/components/";
    /** the stroke to draw the selection box with when selected */
    private static final Stroke selectionStroke = new BasicStroke(1.0f);
    /** the collection of visual locations for component input pins. the key is
     * the typeString of a component and the value is an array of Points. */
    protected static HashMap inputPinLocations;
    /** the collection of visual locations for component output pins. the key is
     * the typeString of a component and the value is an array of Points. */
    protected static HashMap outputPinLocations;
}
