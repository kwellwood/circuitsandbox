/*
 * CustomImage.java
 *
 * Created on February 10, 2005, 7:12 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.List;
import java.awt.Point;
import model.CustomComponent;

import model.LogicComponent;

/**
 * The gui part of a <code>ComponentImage</code>.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class CustomImage extends ComponentImage {
    
    /** Creates a new instance of CustomImage */
    public CustomImage(String filePath) throws Exception {
        this(null, null, filePath);
    }

    public CustomImage(Gui gui, Point location, String filePath)
            throws Exception {
        super(gui, location);
        
        CustomComponent cc = new CustomComponent(null, filePath);
        componentPath = cc.getPath();
        componentName = cc.getName();
        
        List imageList = new List();
        imageList.add(Util.getPathWithoutExtension(componentPath) + ".png");
        loadImages(imageList);
    
        // only create component pins if the component has a location
        // (which means it is not in the toybox)
        if (location != null) {
            // get the pin locations from the temporary custom component object
            Point[] inputLocations = cc.getInputLocations();
            Point[] outputLocations = cc.getOutputLocations();
            
            // create input pins
            inputPins = new ComponentPin[inputLocations.length];
            for (int i=0; i<inputLocations.length; i++) {
                inputPins[i] = new ComponentPin(gui, inputLocations[i], this,
                        ComponentPin.INPUT_PIN, i);
            }

            // create output pins
            outputPins = new ComponentPin[outputLocations.length];
            for (int i=0; i<outputLocations.length; i++) {
                outputPins[i] = new ComponentPin(gui, outputLocations[i], this,
                        ComponentPin.OUTPUT_PIN, i);
            }
        }
    }
    
    public String getTypeString() {
        return CustomComponent.TYPE_STRING + ":" + componentPath;
    }
    
    public String toString() {
        return componentName;
    }
    
    /** the component's relative path */
    private String componentPath;
    /** the english readable name of the component */
    private String componentName;
    
    
    /**
     * the locations of the centers of the input pins in standard coordintes,
     * relative to the top left of the image
     */
    private Point[] inputLocations;
    /**
     * the locations of the centers of the output pins in standard coordintes,
     * relative to the top left of the image
     */
    private Point[] outputLocations;
}

