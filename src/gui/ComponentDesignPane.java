/*
 * ComponentDesignPane.java
 *
 * Created on March 11, 2005, 11:43 PM
 */

package gui;

import circuitsandbox.Util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JComponent;
import model.LogicComponent;

/**
 * Displays the design of the new custom component as it is created in the
 * <code>NewComponentDialog</code>.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class ComponentDesignPane extends JComponent {
    
    /** For compatibility with netbeans. Do not use this constructor. */
    public ComponentDesignPane() {
        this(new HashSet(), new HashSet());
    }
    
    /**
     * Constructs a new <code>ComponentDesignPane</code> object.
     *
     * @param inputs the set of inputs for the new component
     * @param outputs the set of outputs for the new component
     */
    public ComponentDesignPane(HashSet inputs, HashSet outputs) {
        initComponents();
        
        inputPins = sortComponents(inputs);
        for (int i = 0; i < inputPins.size(); i++) {
            // replace the logic components in the inputPins list with their
            // placeholderpin objects, in order
            LogicComponent lc = (LogicComponent)inputPins.remove(i);
            inputPins.add(i, new PlaceholderPin(
                    this, PlaceholderPin.INPUT, lc, 0, 0));
            add((PlaceholderPin)inputPins.get(i));
        }
        
        outputPins = sortComponents(outputs);
        for (int i = 0; i < outputPins.size(); i++) {
            LogicComponent lc = (LogicComponent)outputPins.remove(i);
            outputPins.add(i, new PlaceholderPin(
                    this, PlaceholderPin.OUTPUT, lc, 0, 0));
            add((PlaceholderPin)outputPins.get(i));
        }
        
        setImage(null);
    }
    
    /**
     * Takes an unordered collection of <code>LogicComponent</code>s and moves
     * them into an <code>ArrayList</code>, sorted by y location and x location
     * of the logic components' images in the sandbox. The collection passed to
     * this method will be empty upon returning.
     *
     * @param components the set of logic components
     *
     * @return the sorted list of logic components
     */
    private ArrayList sortComponents(HashSet components) {
        ArrayList sortedComponents = new ArrayList(components.size());
        
        // loop through components collection, adding one component to
        // the sorted components collection on each loop
        for (int i = 0; i < components.size(); i++) {
            
            Iterator iter = components.iterator();
            LogicComponent nextLowestLC = null;
            while (iter.hasNext()) {
                LogicComponent lc = (LogicComponent)iter.next();

                // if lc is the "lowest" logic component found on this loop
                // through the components collection and it isnt already in the
                // sorted components collection...
                if (!sortedComponents.contains(lc) &&
                        (nextLowestLC == null ||
                        compareComponents(lc, nextLowestLC) < 0)) {
                    nextLowestLC = lc;
                }
            }
            
            // add the lowest logic component in components that hasnt already
            // been sorted
            sortedComponents.add(nextLowestLC);
        }
        
        return sortedComponents;
    }
    
    /**
     * Compares the two logic component objects by location. Components with
     * images with a lesser y are considered the lesser of the two. If the y's
     * are equal, the tie is broken by the x in the same way.
     *
     * @param lc1 the left hand component of the comparison
     * @param lc2 the right hand component of the comparison
     *
     * @return -1 if the left component is lesser, 0 if they are equal or 1 if
     * the left component greater
     */
    private int compareComponents(LogicComponent lc1, LogicComponent lc2) {
        if (lc1 == null && lc2 == null) return 0;
        if (lc1 == null) return -1;
        if (lc2 == null) return 1;
        
        int y1 = lc1.getComponentImage().getStdY();
        int x1 = lc1.getComponentImage().getStdX();
        int y2 = lc2.getComponentImage().getStdY();
        int x2 = lc2.getComponentImage().getStdX();        
        
        if (y1 < y2) {
            return -1;
        } else if (y1 == y2) {
            if (x1 < x2) return -1;
            else if (x1 == x2) return 0;
            else return 1;
        } else {
            return 1;
        }
    }
    
    /**
     * Sets the image that will represent the new component. If
     * <code>null</code> is passed, a default image will be generated.
     *
     * @param image the image
     */
    public void setImage(Image image) {
        if (image == null) { image = createDefaultImage(); }

        // convert the image to a bufferedimage
        BufferedImage bi = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics big = bi.getGraphics();
        big.drawImage(image, 0, 0, null);
        componentImage = bi;
        
        // Generate the pin boundaries
        pinBounds = new Rectangle(
                (getPreferredSize().width / 2) -
                    (componentImage.getWidth(null) / 2),
                (getPreferredSize().height / 2) -
                    (componentImage.getHeight(null) / 2),
                componentImage.getWidth(null),
                componentImage.getHeight(null));

        repaint();
    }
    
    /**
     * Returns the image that represents the new component.
     *
     * @return the new component's image (instances of Pin)
     *
     * @see PlaceholderPin
     */
    public Image getImage() {
        return componentImage;
    }

    /**
     * Returns an array of the custom component's input pins.
     *
     * @return the array of input pins (instances of Pin)
     *
     * @see PlaceholderPin
     */
    public ArrayList getInputPins() {
        return inputPins;
    }
    
    /**
     * Returns an array of the custom component's output pins.
     *
     * @return the array of output pins
     */
    public ArrayList getOutputPins() {
        return outputPins;
    }
    
    /**
     * Returns the bounding box around the component image. The pins must stay
     * within this area.
     *
     * @return the rectangle around the component
     */
    public Rectangle getPinBounds() {
        return pinBounds;
    }
    
    /**
     * Sets the pin the mouse is currently over so that the pin's identification
     * information can be displayed.
     *
     * @param pin the pin, or <code>null</code> if mouse isnt over a pin
     */
    public void setMouseOverPin(PlaceholderPin pin) {
        mouseOverPin = pin;
        repaint();
    }
    
    /**
     * Creates and returns a default image representing the new component. The
     * image is a gray square with the inputs evenly spaced on the left and the
     * outputs evenly spaced on the right. A side effect is that the
     * <code>inputPins</code> and <code>outputPins</code> arrays will be reset
     * with the correct locations of the pins drawn on the default image.
     *
     * @return the component's default image
     */
    private Image createDefaultImage() {
        
        // --- calculate dimensions ---
        int pinRadius = ComponentPin.RADIUS + 3;
        int size = (int)Math.max(inputPins.size(), outputPins.size()) * 10;
        int inset = 5;

        // --- create buffered image ---
        BufferedImage image = new BufferedImage(size + inset * 2,
                size + inset * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        
        // --- draw gray body ---
        g2.setBackground(Color.white);
        g2.setColor(Color.lightGray);
        g2.fillRect(inset, inset, size - 1, size - 1);
        g2.setColor(Color.black);
        g2.drawRect(inset, inset, size - 1, size - 1);

        //Rectangle bounds = new Rectangle(
        Rectangle bounds = new Rectangle(
                (getPreferredSize().width / 2) - (image.getWidth(null) / 2),
                (getPreferredSize().height / 2) - (image.getHeight(null) / 2),
                image.getWidth(null),
                image.getHeight(null));

        // --- draw wire stubs ---
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));
        for (int i = 0;  i < inputPins.size(); i++) {
            int y = (i * 10) + 5 + inset;
            g2.drawLine(0, y, inset, y);
            g2.drawLine(0, y - 1, inset, y - 1);
            
            ((PlaceholderPin)inputPins.get(i)).centerAt(
                    bounds.x, bounds.y + y);
        }
        for (int i = 0; i < outputPins.size(); i++) {
            int y = (i * 10) + 5 + inset;
            g2.drawLine(size + inset, y, size + (inset * 2), y);
            g2.drawLine(size + inset, y - 1, size + (inset * 2), y - 1);
                        
            ((PlaceholderPin)outputPins.get(i)).centerAt(
                    bounds.x + size + inset*2 - 1, bounds.y + y);
        }

        return (Image)image;
    }
    
    /**
     * Draws the component design pane.
     *
     * @param g the graphics to use
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // --- clear the background ---
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, getWidth(), getHeight());

        // --- draw the legend ---
        drawPin(g2, 4, getHeight() - (10 + PlaceholderPin.RADIUS * 4),
                ComponentPin.RADIUS * 2, PlaceholderPin.RADIUS * 2,
                new Color(255, 0, 0, 160));
        g2.drawString("Input Pin", 9 + PlaceholderPin.RADIUS * 2,
                getHeight() - (10 + PlaceholderPin.RADIUS * 2));
        drawPin(g2, 4, getHeight() - (5 + PlaceholderPin.RADIUS * 2),
                ComponentPin.RADIUS * 2, PlaceholderPin.RADIUS * 2,
                new Color(0, 0, 255, 160));
        g2.drawString("Output Pin", 9 + ComponentPin.RADIUS * 2,
                getHeight() - 5);
        
        // --- draw the type/id of the pin currently positioned over
        if (mouseOverPin != null) {
            String str;
            LogicComponent lc = mouseOverPin.getLogicComponent();
            if (lc.getFunction() == LogicComponent.INPUT) {
                str = "Input "+lc.getComponentImage().getCustomComponentPin();
            } else {
                str = "Output "+lc.getComponentImage().getCustomComponentPin();
            }
            g2.drawString(str, 5, 15);
        }
        
        // --- center the image ---
        g2.drawImage(componentImage, pinBounds.x, pinBounds.y,
                null);

        // --- draw a dashed border around it ---
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 4.0f, 4.0f }, 0));
        g2.drawRect(
                getWidth() / 2 - componentImage.getWidth(null) / 2 - 1, 
                getHeight() / 2 - componentImage.getHeight(null) / 2 - 1, 
                componentImage.getWidth(null) + 1,
                componentImage.getHeight(null) + 1);
        g2.setStroke(new BasicStroke(1.0f));
        
        // --- draw the pins ---
        paintChildren(g);
        
        paintBorder(g);
    }
    
    /**
     * Draws on one of the input or output pins on the component as a colored
     * circle. This method is called only by <code>paint</code>.
     *
     * @param g2 the graphics to use
     * @param x the x coordinate of the upper left of the pin circle in pixels
     * @param y the y coordinate of the upper left of the pin circle in pixels
     * @param w the width of the circle in pixels
     * @param h the height of the circle in pixels
     * @param color the color of the circle
     */
    private void drawPin(Graphics2D g2,
            int x, int y, int w, int h, Color color) {
        Stroke olds = g2.getStroke();
        Color oldc = g2.getColor();
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(color); g2.fillOval(x, y, w, h);            // inside fill
        g2.setColor(Color.black); g2.drawOval(x, y, w, h);      // outside edge
        g2.setColor(Color.white);
        g2.drawLine(x + w/2 - 2,  y + h/2, x + w/2 + 2, y + h/2);  // horizontal
        g2.drawLine(x + w/2, y + h/2 - 2, x + w/2, y + h/2 + 2);   // vertical
        g2.setStroke(olds);
        g2.setColor(oldc);
    }
    
    /**
     * Puts the component image on the clipboard.
     */
    private void copyToClipboard () {
        ImageSelection imgSel = new ImageSelection(componentImage);
        Toolkit.getDefaultToolkit().getSystemClipboard().
                setContents(imgSel, null);
    }
    
    /**
     * Gets the component image from the clipboard. If there isn't an image on
     * the clipboard, it is left untouched.
     */
    private void pasteFromClipboard() {
        // get the clipboard contents
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().
                getContents(null);
    
        try {
            // copy the clipboard image to the component image
            if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                setImage((Image)t.getTransferData(DataFlavor.imageFlavor));
                
                /*
                BufferedImage bi;
                bi = (BufferedImage)t.getTransferData(DataFlavor.imageFlavor);
                int colorToRemove = Color.white.getRGB() | 0xFF000000;
                
                // convert white to transparent before setting the component
                // image to the pasted image
                for (int y = 0; y < bi.getHeight(); y++) {
                    for (int x = 0; x < bi.getWidth(); x++) {
                        int rgb = bi.getRGB(x, y);
                        if ((rgb | 0xFF000000) == colorToRemove) {
                            // if this is opaque white, set its alpha bits to 0
                            bi.setRGB(x, y, 0x00FFFFFF & rgb);
                        }
                    }
                }*/
                
                componentImage =
                        (Image)t.getTransferData(DataFlavor.imageFlavor);
            }
        }
        catch (UnsupportedFlavorException e) { }
        catch (IOException e) { }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        javax.swing.JMenuItem copyMenuItem;
        javax.swing.JMenuItem pasteMenuItem;

        popupMenu = new javax.swing.JPopupMenu();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();

        copyMenuItem.setText("Copy to Clipboard");
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });

        popupMenu.add(copyMenuItem);

        pasteMenuItem.setText("Paste from Clipboard");
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });

        popupMenu.add(pasteMenuItem);

        setLayout(null);

        setMinimumSize(new java.awt.Dimension(274, 174));
        setPreferredSize(new java.awt.Dimension(275, 190));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ComponentDesignPane.this.mouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ComponentDesignPane.this.mousePressed(evt);
            }
        });

    }//GEN-END:initComponents

    private void mouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseEntered
        setMouseOverPin(null);
    }//GEN-LAST:event_mouseEntered

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        pasteFromClipboard();
    }//GEN-LAST:event_pasteMenuItemActionPerformed

    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        copyToClipboard();
    }//GEN-LAST:event_copyMenuItemActionPerformed

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        // display the pop up menu
        if (Util.isRightClick(evt)) {
            popupMenu.show(this, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_mousePressed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu popupMenu;
    // End of variables declaration//GEN-END:variables

    /** the array of input pins for the custom component */
    private ArrayList inputPins;
    /** the array of output pins for the custom component */
    private ArrayList outputPins;
    /** the image to display as the component */
    private Image componentImage;
    /** the bounds of the area around the component image where the pins must
     * stay */
    private Rectangle pinBounds;
    /** the PlaceholderPin object that the mouse is currently over */
    private PlaceholderPin mouseOverPin;
    
    // -----------------------------------------------------------------------    
    
    /** ImageSelection is a wrapper class for transferring an Image. */
    private class ImageSelection implements Transferable {
        public ImageSelection (Image image) {
            this.image = image;
        }
    
        /**
         * Returns supported flavors.
         *
         * @param return the array of data flavors available
         */
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }
    
        /**
         * Returns <code>true</code> if a <code>DataFlavor</code> is supported,
         * otherwise <code>false</code>.
         *
         * @param the data flavor
         *
         * @return <code>true</code> if the data flavor is supported
         */
        public boolean isDataFlavorSupported (DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }
    
        /**
         * Returns the image from inside the ImageSelection.
         *
         * @param DataFlavor the flavor of the data to retrieve
         *
         * @return the Image
         *
         * @throws UnsupportedFlavorException if the image selection doesn't
         * contain the requested data flavor
         * @throws IOException if there's an IO problem
         */
        public Object getTransferData(DataFlavor flavor) throws
                UnsupportedFlavorException, IOException {
            
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            
            return image;
        }

        private Image image;
    }
    
    // -----------------------------------------------------------------------
}
