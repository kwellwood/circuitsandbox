/*
 * Gui.java
 *
 * Created on February 5, 2005, 10:56 AM
 */

package gui;

import circuitsandbox.ComponentFileFilter;
import circuitsandbox.ModelFileFilter;
import circuitsandbox.PNGFileFilter;
import circuitsandbox.Util;
import controller.Controller;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.LogicComponent;
import model.Model;
import model.Wire;
import model.WireFactory;


/**
 * <p>Contains the application's main window. The <code>Gui</code> class is
 * responsible for responding to the user and interacting with the controller
 * to perform commands.</p>
 *
 * <p>The basic layout of the frame is simple. A <code>JToolbar</code>
 * stretches left to right at the top of the frame. The rest is filled with a
 * <code>JSplitFrame</code>, with the <code>toybox</code> on the left and the
 * <code>sandbox</code> on the right.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Gui extends JFrame {
    
    /**
     * Do not use, for NetBeans compatibility only.
     */
    public Gui() {
        selection = new ArrayList();
        initComponents();
    }
    
    /**
     * Constructs a new <code>Gui</code> frame.
     *
     * @param controller the gui's controller
     */
    public Gui(Controller controller) {
        this.controller = controller;
        selection = new ArrayList();
        setWorkingFile(null);
        initComponents();
    
        // center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-getWidth()) / 2,
                (screenSize.height-getHeight()) / 2,
                getWidth(), getHeight());
    }
    
    /**
     * Exits the Add Wire mode. <code>wireBeingCreated</code> will be cleared.
     */
    public void cancelWire() {
        if (isCreatingWire()) {
            Wire wire = wireBeingCreated.getWire();
            if (wire.getSource() != null) {
                // unselect the component pin at the source end of the wire
                wire.getSource().getComponentImage().getOutputPin(
                    wire.getSourcePin()).setSelected(false);
            } else {
                // unselect the component pin at the sink end of the wire
                wire.getSink().getComponentImage().getInputPin(
                    wire.getSinkPin()).setSelected(false);
            }
            
            // the wire isnt actually in the sandbox yet but its nodes
            // are and this will remove them
            wireBeingCreated.removeFromSandbox(sandbox);
            
            wireBeingCreated = null;
            clickablePinTypes = ComponentPin.ALL_PINS;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            deleteItem.setEnabled(!selection.isEmpty());
            repaintSandbox();
        }
    }
    
    /**
     * Returns <code>true</code> if the gui is currently in the creating a new
     * wire state.
     *
     * @return <code>true</code> if the user is creating a wire, otherwise
     * <code>false</code>
     */
    public boolean isCreatingWire() {
        return wireBeingCreated != null;
    }
    
    /**
     * Begins creating a wire from the specified pin of a component.
     *
     * @param component the <code>ComponentImage</code> the wire is being
     * attached to
     * @param pinType the pin type (input or output)
     * @param pinNumber the number of the pin on the component
     */
    public void placeWireLead(ComponentImage component, int pinType,
            int pinNumber) {
        if (isAddingComponent()) setComponentToAdd(null);
        
        Wire wire;
        if (pinType == ComponentPin.INPUT_PIN) {
            // create the wire
            wire = WireFactory.getDefaultFactory().buildWire(
                    null, -1, component.getLogicComponent(), pinNumber);
            // set the lead component pin as selected so it draws differently
            // until the wire is completed or cancelled
            wire.getSink().getComponentImage().getInputPin(
                    wire.getSinkPin()).setSelected(true);
            // only allow output pins to be clicked now
            clickablePinTypes = ComponentPin.OUTPUT_PIN;
        } else {
            wire = WireFactory.getDefaultFactory().buildWire(
                    component.getLogicComponent(), pinNumber, null, -1);
            wire.getSource().getComponentImage().getOutputPin(
                    wire.getSourcePin()).setSelected(true);
            clickablePinTypes = ComponentPin.INPUT_PIN;
        }
        
        wireBeingCreated = wire.getWireImage();
        setCursor(getWireCursor());
        deleteItem.setEnabled(true);
        repaintSandbox();
    }

    /**
     * Finishes creating a wire with the specified pin of a component.
     *
     * @param component the <code>ComponentImage</code> the wire is being
     * attached to
     * @param pinType the pin type (input or output)
     * @param pinNumber the number of the pin on the component
     */
    public void placeWireTail(ComponentImage component, int pinType,
            int pinNumber) {
        if (pinType == ComponentPin.INPUT_PIN) {
            Wire wire = wireBeingCreated.getWire();
            // set the sink end of the wire to the component just clicked
            wire.setSink(component.getLogicComponent(), pinNumber);
            // unselect the component pin at the source end of the wire so
            // it draws normally again
            wire.getSource().getComponentImage().getOutputPin(
                    wire.getSourcePin()).setSelected(false);
        } else {
            Wire wire = wireBeingCreated.getWire();
            wire.setSource(component.getLogicComponent(), pinNumber);
            wire.getSink().getComponentImage().getInputPin(
                    wire.getSinkPin()).setSelected(false);
        }
        controller.addWire(wireBeingCreated.getWire());
        undoItem.setEnabled(true);
        undoButton.setEnabled(true);
        redoItem.setEnabled(false);
        redoButton.setEnabled(false);
        if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
        
        wireBeingCreated = null;
        clickablePinTypes = ComponentPin.ALL_PINS;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        deleteItem.setEnabled(!selection.isEmpty());
        repaintSandbox();
    }
    
    /**
     * Places a new node along the path of the wire currently being created.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void placeNewWireNode(int x, int y) {
        if (!isCreatingWire()) return;
        
        WireNode node;
        if (wireBeingCreated.getWire().getSource() == null) {
            // add the node on the source end of the wire
            node = wireBeingCreated.addNodeToFront(x, y);
        } else {
            // add the node on the sink end of the wire
            node = wireBeingCreated.addNodeToBack(x, y);
        }
        
        if (node != null) {
            node.addToSandbox(sandbox);
            repaintSandbox();
        }
    }
    
    /**
     * Returns the wire currently being created. If a wire is not being created
     * at this time, <code>null</code> is returned.
     *
     * @return the wire image being created
     */
    public WireImage getNewWire() {
        return wireBeingCreated;
    }
    
    /**
     * Inserts a wire node into a segment of an existing wire's path. The
     * location must be in standard coordinates.
     *
     * @param wireImage the wire image receiving the new node
     * @param segment the segment to split, base 0 starting from the source
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void insertWireNode(WireImage wireImage, int segment, int x, int y) {
        controller.insertWireNode(wireImage, segment, x, y);
        undoItem.setEnabled(true);
        undoButton.setEnabled(true);
        redoItem.setEnabled(false);
        redoButton.setEnabled(false);
        if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
    }
    
    /**
     * Returns <code>true</code> if the pin can be highlighted and clicked on.
     *
     * @param pinType the code for the pinType
     *
     * @return <code>true</code> if the pin can be used
     *
     * @see ComponentPin
     */
    public boolean isPinClickable(int pinType) {
        return (clickablePinTypes & pinType) != 0;
    }
    
    /**
     * Returns <code>true</code> if the node can be highlighted and clicked on.
     *
     * @return <code>true</code> if the node can be clicked on
     */
    public boolean isNodeClickable() {
        return (!(isCreatingWire() || isAddingComponent()));
    }
    
    /**
     * Returns <code>true</code> if the gui is currently in the adding a new
     * component from the toybox state.
     *
     * @return <code>true</code> if the user is adding a component, otherwise
     * <code>false</code>
     */
    public boolean isAddingComponent() {
        return componentToAdd != null;
    }
    
    /**
     * Returns <code>true</code> if the gui is currently in simulation mode.
     *
     * @return <code>true</code> if the simulation is running
     */
    public boolean isSimulating() {
        return isSimulating;
    }

    /**
     * Stops executing the simulation. If this is called and the simulation is
     * running, it will have no side effects.
     */
    public void stopSimulation() {
        if (isSimulating()) {
            undoItem.setEnabled(controller.canUndo());
            undoButton.setEnabled(controller.canUndo());
            redoItem.setEnabled(controller.canRedo());
            redoButton.setEnabled(controller.canRedo());
            toybox.setEnabled(true);

            controller.stopSimulation();

            stopSimulationItem.setEnabled(false);
            stopButton.setEnabled(false);
            startSimulationItem.setEnabled(true);
            startButton.setEnabled(true);
            isSimulating = false;
        }
    }
    
    /**
     * Move the <code>Displayable</code> object to the top in the sandbox.
     *
     * @param displayable the obect to bring to the top
     */
    public void bringToFront(Displayable displayable) {
        displayable.bringToFront(sandbox);
    }
    
    /**
     * Returns <code>true</code> nothing is selected in the sandbox.
     *
     * @return <code>true</code> if the selection is empty
     */
    public boolean isSelectionEmpty() {
        return selection.isEmpty();
    }
    
    /**
     * Sets the current selection to the given displayable object.
     *
     * @param d the displayable object to select
     */
    public void select(Displayable d) {
        clearSelection();
        addToSelection(d);
    }
    
    /**
     * Adds the <code>Displayable</code> object to the current selection.
     *
     * @param displayable the displayable object
     */
    public void addToSelection(Displayable displayable) {
        if (!selection.contains(displayable)) {
            displayable.setSelected(true);
            selection.add(displayable);
            deleteItem.setEnabled(true);
        }
    }
    
    /**
     * Adds the list of <code>Displayable</code> objects to the current
     * selection.
     *
     * @param list the list of <code>Displayable</code>s
     */
    public void addToSelection(ArrayList list) {
        for (int i=0; i<list.size(); i++) {
            addToSelection((Displayable)list.get(i));
        }
    }

    /**
     * Removes a group of <code>Displayable</code> objects from the current
     * selection.
     *
     * @param group the <code>Displayable</code>s
     */
    public void removeFromSelection(Collection group) {
        Iterator iter = group.iterator();
        while (iter.hasNext()) removeFromSelection((Displayable)iter.next());
    }
    
    /**
     * Removes the <code>Displayable</code> object from the current selection.
     *
     * @param displayable the <code>Displayable</code>
     */
    public void removeFromSelection(Displayable displayable) {
        displayable.setSelected(false);
        selection.remove(displayable);
        
        if (selection.isEmpty()) { deleteItem.setEnabled(false); }
    }
    
    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        for (int i=0; i<selection.size(); i++) {
            ((Displayable)selection.get(i)).setSelected(false);
        }
        selection.clear();
        deleteItem.setEnabled(false);
    }
    
    /**
     * Moves all the selected objects by a given distance. The distances are
     * in standard units.
     *
     * @param xShift the horizontal distance
     * @param yShift the vertical distance
     */
    public void moveSelection(int xShift, int yShift) {
        for (int i=0; i<selection.size(); i++) {
            ((Displayable)selection.get(i)).shiftLocation(xShift, yShift);
        }
        resizeSandbox();
        repaintSandbox();
    }
    
    /**
     * Add the component to the sandbox. The location should be in standard
     * coordinates.
     *
     * @param location the location for the center of the component
     */
    public void addComponent(Point location) {
        int x = location.x - componentToAdd.getStdWidth() / 2;
        int y = location.y - componentToAdd.getStdHeight() / 2;
        
        // snaps the components by their upper left; comment this out
        // to snap new components by their center point
        x = (int)(x / Sandbox.GRID_SIZE) * Sandbox.GRID_SIZE;
        y = (int)(y / Sandbox.GRID_SIZE) * Sandbox.GRID_SIZE;
        
        controller.addComponent(componentToAdd.getTypeString(), x, y);
        
        if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
        undoItem.setEnabled(true);
        undoButton.setEnabled(true);
        redoItem.setEnabled(false);
        redoButton.setEnabled(false);
        setComponentToAdd(null);
        clearSelection();
    }
    
    /**
     * Notifies the <code>controller</code> that the selection has been moved.
     * The distances are in standard units.
     *
     * @param xShift the horizontal distance
     * @param yShift the vertical distance
     */
    public void selectionMoved(int xShift, int yShift) {
        controller.selectionMoved(selection, xShift, yShift);
        undoItem.setEnabled(true);
        undoButton.setEnabled(true);
        redoItem.setEnabled(false);
        redoButton.setEnabled(false);
        if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
    }
    
    /**
     * Sets the component currently being added to the sandbox. If the
     * component is <code>null</code>, the gui will leave the adding component
     * state.
     *
     * @param component the component image to be adding
     */
    public void setComponentToAdd(ComponentImage component) {
        if (isCreatingWire()) cancelWire();
        
        componentToAdd = component;
        if (component != null) {
            setCursor(component.getAddingCursor());
            clickablePinTypes = ComponentPin.NO_PINS;
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            toybox.clearSelection();
            clickablePinTypes = ComponentPin.ALL_PINS;
        }
    }
    
    /**
     * Instructs the toybox to load the specified component into its custom
     * components category.
     *
     * @param type the <code>typeString</code> of the component to load
     */
    public void loadToToybox(String type) {
        toybox.loadComponent(type);
    }
    
    /**
     * Passes the <code>repaint</code> command to the sandbox.
     */
    public void repaintSandbox() {
        sandbox.repaint();
    }
    
    /**
     * Passes the <code>resize</code> command to the sandbox.
     */
    public void resizeSandbox() {
        sandbox.resize();
    }
    
    /**
     * Adds a <code>Displayable</code> object to the sandbox and redraws it. If
     * the sandbox already contains the object, it will still be added again.
     *
     * @param displayable the <code>Displayable</code> to add
     */
    public void addToSandbox(Displayable displayable) {
        displayable.addToSandbox(sandbox);
    }
    
    /**
     * Removes a <code>Displayable</code> object from the sandbox and redraws
     * it. If the sandbox does not contain the object, nothing is done.
     *
     * @param displayable the <code>Displayable</code> to remove
     */
    public void removeFromSandbox(Displayable displayable) {
        displayable.removeFromSandbox(sandbox);
    }
    
    /**
     * Sychronizes the sandbox contents with the data from a model object. All
     * objects will be cleared from the sandbox before it is rebuilt.
     *
     * @param model the model
     */
    private void syncToModel(Model model) {
        clearSelection();
        sandbox.removeAll();
        
        Iterator componentIter = model.getComponents();
        while (componentIter.hasNext()) {
            ComponentImage ci =
                    ((LogicComponent)componentIter.next()).getComponentImage();
            addToSandbox(ci);
        }
        
        Iterator wireIter = model.getWires();
        while (wireIter.hasNext()) {
            WireImage wireImage = ((Wire)wireIter.next()).getWireImage();
            
            Iterator nodeIter = wireImage.getNodes().iterator();
            while (nodeIter.hasNext()) {
                WireNode wn = (WireNode)nodeIter.next();
            }
            
            addToSandbox(wireImage);
        }
        
        resizeSandbox();
        repaintSandbox();
    }
    
    /**
     * Prompts the user to load a model file with a file dialog. An error
     * message is displayed if the chosen model file can't be loaded.
     */
    private void loadModelDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Circuit Model...");
        chooser.setCurrentDirectory(modelDir);
        chooser.setFileFilter(new ModelFileFilter());
        chooser.setMultiSelectionEnabled(false);
        
        if (chooser.showDialog(this, "Open Model") ==
                JFileChooser.APPROVE_OPTION) {
            try {
                controller.loadModel(chooser.getSelectedFile());
                setWorkingFile(chooser.getSelectedFile());
                syncToModel(controller.getModel());
                undoItem.setEnabled(false);
                undoButton.setEnabled(false);
                redoItem.setEnabled(false);
                redoButton.setEnabled(false);
                setZoomFactor(1.0f);
            }
            catch (Exception e) {
                showError(e.getMessage());
            }
        }
        modelDir = chooser.getCurrentDirectory();
    }
    
    /**
     * Prompts the user to save the current model with a file dialog.
     */
    private void saveModelDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Circuit Model...");
        chooser.setCurrentDirectory(modelDir);
        chooser.setFileFilter(new ModelFileFilter());
        chooser.setMultiSelectionEnabled(false);
        
        if (chooser.showDialog(this, "Save Model") ==
                JFileChooser.APPROVE_OPTION) {
            try {
                // replace whatever extension the file was given, with the
                // model file extension
                File f = Util.replaceExtension(
                        chooser.getSelectedFile(), "csm");
                
                controller.saveModel(f);
                setWorkingFile(f);
            }
            catch (Exception e) {
                showError(e.getMessage());
            }
        }
        modelDir = chooser.getCurrentDirectory();
    }
    
    /**
     * Exits Circuit Sandbox. If the current model has been modified, the user
     * will be prompted to save. If the Cancel button is clicked, the program
     * will not exit.
     */
    private void exitProgram() {
        stopSimulation();
        if ((controller.isModified() && askToSaveAndContinue()) ||
                !controller.isModified()) {
            System.exit(0);
        }
    }

    /**
     * Asks if the user wants to save the model before creating a new model or
     * loading a saved model. If the user clicks 'Yes', he will be prompted
     * with the Save Model dialog. If he clicks 'Cancel', <code>false</code>
     * will be returned, otherwise <code>true</code>.
     *
     * @return <code>false</code> if the user clicked Cancel, otherwise
     * <code>true</code>
     */
    private boolean askToSaveAndContinue() {
        JOptionPane dialog = new JOptionPane();
        
        int choice = dialog.showOptionDialog(this,
                "Would you like to save your changes?",
                "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        
        if (choice == JOptionPane.CANCEL_OPTION ||
                choice == JOptionPane.DEFAULT_OPTION) {
            return false;
        }
        if (choice == JOptionPane.YES_OPTION) {
            if (currentFile.getName().equals(DEFAULT_FILENAME)) {
                // Allow user to choose file save
                saveModelDialog();
            } else {
                try {
                    // Save using the current working file
                    controller.saveModel(currentFile);
                    setWorkingFile(currentFile);
                }
                catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        }
        
        return true;
    }
    
    /**
     * Displays a pop-up box with an error message.
     *
     * @param message the message to display
     */
    public void showError(String message) {
        JOptionPane errorBox = new JOptionPane();
        errorBox.showMessageDialog(this, message,
                "Everything is fine. Nothing is ruined.",
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Returns the zoom factor for viewing the sandbox. 1.0 is no magnification,
     * 2.0 is 200% magnification, etc.
     *
     * @return the zoom factor
     */
    public float getZoomFactor() {
        return zoomFactor;
    }
    
    /**
     * Sets the zoom factor of the sandbox. 1.0 is no magnification, 2.0 is 200%
     * magnification, etc.
     *
     * @param zoom the zoom factor
     */
    public void setZoomFactor(float zoom) {
        zoomFactor = zoom;

        zoomIn.setEnabled(true);
        zoomInItem.setEnabled(true);
        zoomOut.setEnabled(true);
        zoomOutItem.setEnabled(true);
        
        if (zoom <= .5) {
            zoomOut.setEnabled(false);
            zoomOutItem.setEnabled(false);
        } else if (zoom >= 2.0) {
            zoomIn.setEnabled(false);
            zoomInItem.setEnabled(false);
        }
        sandbox.zoom();
    }
    
    /**
     * Sets the current file that will be written if the 'Save' menu choice is
     * selected. Also sets the title for the gui frame. If the file passed is
     * <code>null</code>, the default working file will be set.
     *
     * @param file the current working file
     */
    private void setWorkingFile(File file) {
        currentFile = (file == null) ? new File(DEFAULT_FILENAME) : file;
        setTitle("Circuit Sandbox - " + currentFile.getName());
    }
    
    /**
     * Returns the sandbox. This is for debugging purposes only.
     * // Remove this debugging method
     *
     * @return the sandbox
     */
    public Sandbox getSandbox() {
        return sandbox;
    }
    
    /** 
     * Returns a cursor from an image file for use when creating a wire.
     *
     * @return the wire drawing cursor
     */
    private Cursor getWireCursor() {
        return Toolkit.getDefaultToolkit().createCustomCursor(
                new ImageIcon("images/wirecursor.png").getImage(),
                new Point(0, 0), "Wire Cursor");
    }
    
    /**
     * Returns the simulation delay from the controller.
     *
     * @return the simulation delay
     */
    public long getSimDelay() {
        return controller.getSimDelay();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        javax.swing.JMenuItem aboutItem;
        javax.swing.JMenu aboutMenu;
        javax.swing.JMenu editMenu;
        javax.swing.JMenuItem exportImageItem;
        javax.swing.JMenu fileMenu;
        javax.swing.JSeparator jSeparator1;
        javax.swing.JSeparator jSeparator2;
        javax.swing.JSeparator jSeparator3;
        javax.swing.JMenuItem loadComponentItem;
        javax.swing.JMenuBar menuBar;
        javax.swing.JButton newButton;
        javax.swing.JMenuItem newModelItem;
        javax.swing.JButton openButton;
        javax.swing.JMenuItem openModelItem;
        javax.swing.JMenuItem quitItem;
        javax.swing.JButton resetZoom;
        javax.swing.JMenuItem resetZoomItem;
        javax.swing.JMenu sandboxMenu;
        javax.swing.JButton saveButton;
        javax.swing.JMenuItem saveComponentItem;
        javax.swing.JMenuItem saveModelAsItem;
        javax.swing.JMenuItem saveModelItem;
        javax.swing.JMenu simMenu;
        javax.swing.JMenuItem simulationSpeedItem;
        javax.swing.JPanel spacer0;
        javax.swing.JPanel spacer1;
        javax.swing.JPanel spacer2;
        javax.swing.JSplitPane splitPane;
        javax.swing.JToolBar toolBar;
        javax.swing.JPanel toolbarPanel;
        javax.swing.JScrollPane toyboxScrollPane;

        toolbarPanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        spacer0 = new javax.swing.JPanel();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        spacer1 = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        spacer2 = new javax.swing.JPanel();
        zoomIn = new javax.swing.JButton();
        resetZoom = new javax.swing.JButton();
        zoomOut = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        sandboxScrollPane = new javax.swing.JScrollPane();
        sandbox = new Sandbox(this);
        toyboxScrollPane = new javax.swing.JScrollPane();
        toybox = new Toybox(this);
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newModelItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        openModelItem = new javax.swing.JMenuItem();
        loadComponentItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        saveModelItem = new javax.swing.JMenuItem();
        saveModelAsItem = new javax.swing.JMenuItem();
        saveComponentItem = new javax.swing.JMenuItem();
        exportImageItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        quitItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoItem = new javax.swing.JMenuItem();
        redoItem = new javax.swing.JMenuItem();
        deleteItem = new javax.swing.JMenuItem();
        simMenu = new javax.swing.JMenu();
        startSimulationItem = new javax.swing.JMenuItem();
        stopSimulationItem = new javax.swing.JMenuItem();
        simulationSpeedItem = new javax.swing.JMenuItem();
        sandboxMenu = new javax.swing.JMenu();
        zoomInItem = new javax.swing.JMenuItem();
        zoomOutItem = new javax.swing.JMenuItem();
        resetZoomItem = new javax.swing.JMenuItem();
        showGridItem = new javax.swing.JCheckBoxMenuItem();
        aboutMenu = new javax.swing.JMenu();
        aboutItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Circuit Sandbox - " + DEFAULT_FILENAME);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                Gui.this.windowClosing(evt);
            }
        });

        toolbarPanel.setLayout(new java.awt.BorderLayout());

        newButton.setIcon(new javax.swing.ImageIcon("images/new.png"));
        newButton.setToolTipText("Create a new model");
        newButton.setOpaque(false);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newModelItemActionPerformed(evt);
            }
        });

        toolBar.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon("images/open.png"));
        openButton.setOpaque(false);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openModelItemActionPerformed(evt);
            }
        });

        toolBar.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon("images/save.png"));
        saveButton.setOpaque(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveModelItemActionPerformed(evt);
            }
        });

        toolBar.add(saveButton);

        spacer0.setMaximumSize(new java.awt.Dimension(32, 32767));
        spacer0.setMinimumSize(new java.awt.Dimension(32, 10));
        spacer0.setOpaque(false);
        spacer0.setPreferredSize(new java.awt.Dimension(32, 10));
        toolBar.add(spacer0);

        undoButton.setIcon(new javax.swing.ImageIcon("images/undo.png"));
        undoButton.setDisabledIcon(new javax.swing.ImageIcon("images/undodisabled.png"));
        undoButton.setEnabled(false);
        undoButton.setOpaque(false);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoItemActionPerformed(evt);
            }
        });

        toolBar.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon("images/redo.png"));
        redoButton.setDisabledIcon(new javax.swing.ImageIcon("images/redodisabled.png"));
        redoButton.setEnabled(false);
        redoButton.setOpaque(false);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoItemActionPerformed(evt);
            }
        });

        toolBar.add(redoButton);

        spacer1.setMaximumSize(new java.awt.Dimension(32, 32767));
        spacer1.setMinimumSize(new java.awt.Dimension(32, 10));
        spacer1.setOpaque(false);
        spacer1.setPreferredSize(new java.awt.Dimension(32, 10));
        toolBar.add(spacer1);

        startButton.setIcon(new javax.swing.ImageIcon("images/run.png"));
        startButton.setToolTipText("Run the simulation");
        startButton.setDisabledIcon(new javax.swing.ImageIcon("images/rundisabled.png"));
        startButton.setOpaque(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startSimulationItemActionPerformed(evt);
            }
        });

        toolBar.add(startButton);

        stopButton.setIcon(new javax.swing.ImageIcon("images/stop.png"));
        stopButton.setToolTipText("Stop the simulation");
        stopButton.setDisabledIcon(new javax.swing.ImageIcon("images/stopDisabled.png"));
        stopButton.setEnabled(false);
        stopButton.setOpaque(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopSimulationItemActionPerformed(evt);
            }
        });

        toolBar.add(stopButton);

        spacer2.setMaximumSize(new java.awt.Dimension(32, 32767));
        spacer2.setMinimumSize(new java.awt.Dimension(32, 10));
        spacer2.setOpaque(false);
        spacer2.setPreferredSize(new java.awt.Dimension(32, 10));
        toolBar.add(spacer2);

        zoomIn.setIcon(new javax.swing.ImageIcon("images/zoomIn.png"));
        zoomIn.setDisabledIcon(new javax.swing.ImageIcon("images/zoomInDisabled.png"));
        zoomIn.setOpaque(false);
        zoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInItemActionPerformed(evt);
            }
        });

        toolBar.add(zoomIn);

        resetZoom.setIcon(new javax.swing.ImageIcon("images/zoom100.png"));
        resetZoom.setOpaque(false);
        resetZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetZoomItemActionPerformed(evt);
            }
        });

        toolBar.add(resetZoom);

        zoomOut.setIcon(new javax.swing.ImageIcon("images/zoomOut.png"));
        zoomOut.setDisabledIcon(new javax.swing.ImageIcon("images/zoomOutDisabled.png"));
        zoomOut.setOpaque(false);
        zoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutItemActionPerformed(evt);
            }
        });

        toolBar.add(zoomOut);

        toolbarPanel.add(toolBar, java.awt.BorderLayout.CENTER);

        getContentPane().add(toolbarPanel, java.awt.BorderLayout.NORTH);

        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        sandboxScrollPane.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        sandboxScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sandboxScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sandboxScrollPane.setViewportView(sandbox);

        splitPane.setRightComponent(sandboxScrollPane);

        toyboxScrollPane.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        toyboxScrollPane.setViewportView(toybox);

        splitPane.setLeftComponent(toyboxScrollPane);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        newModelItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newModelItem.setText("New Model");
        newModelItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newModelItemActionPerformed(evt);
            }
        });

        fileMenu.add(newModelItem);

        fileMenu.add(jSeparator1);

        openModelItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openModelItem.setText("Open Model...");
        openModelItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openModelItemActionPerformed(evt);
            }
        });

        fileMenu.add(openModelItem);

        loadComponentItem.setText("Load Custom Component...");
        loadComponentItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadComponentItemActionPerformed(evt);
            }
        });

        fileMenu.add(loadComponentItem);

        fileMenu.add(jSeparator2);

        saveModelItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveModelItem.setText("Save Model");
        saveModelItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveModelItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveModelItem);

        saveModelAsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveModelAsItem.setText("Save Model As...");
        saveModelAsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveModelAsItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveModelAsItem);

        saveComponentItem.setText("Save As Custom Component...");
        saveComponentItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveComponentItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveComponentItem);

        exportImageItem.setText("Export Model As Image...");
        exportImageItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportImageItemActionPerformed(evt);
            }
        });

        fileMenu.add(exportImageItem);

        fileMenu.add(jSeparator3);

        quitItem.setText("Quit");
        quitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitItemActionPerformed(evt);
            }
        });

        fileMenu.add(quitItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");
        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoItem.setText("Undo");
        undoItem.setEnabled(false);
        undoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoItemActionPerformed(evt);
            }
        });

        editMenu.add(undoItem);

        redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoItem.setText("Redo");
        redoItem.setEnabled(false);
        redoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoItemActionPerformed(evt);
            }
        });

        editMenu.add(redoItem);

        deleteItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0));
        deleteItem.setText("Delete");
        deleteItem.setEnabled(false);
        deleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemActionPerformed(evt);
            }
        });

        editMenu.add(deleteItem);

        menuBar.add(editMenu);

        simMenu.setText("Simulation");
        startSimulationItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        startSimulationItem.setText("Start Simulation");
        startSimulationItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startSimulationItemActionPerformed(evt);
            }
        });

        simMenu.add(startSimulationItem);

        stopSimulationItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        stopSimulationItem.setText("Stop Simulation");
        stopSimulationItem.setEnabled(false);
        stopSimulationItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopSimulationItemActionPerformed(evt);
            }
        });

        simMenu.add(stopSimulationItem);

        simulationSpeedItem.setText("Simulation Speed...");
        simulationSpeedItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulationSpeedItemActionPerformed(evt);
            }
        });

        simMenu.add(simulationSpeedItem);

        menuBar.add(simMenu);

        sandboxMenu.setText("Sandbox");
        zoomInItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, 0));
        zoomInItem.setText("Zoom In");
        zoomInItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInItemActionPerformed(evt);
            }
        });

        sandboxMenu.add(zoomInItem);

        zoomOutItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0));
        zoomOutItem.setText("Zoom Out");
        zoomOutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutItemActionPerformed(evt);
            }
        });

        sandboxMenu.add(zoomOutItem);

        resetZoomItem.setText("Reset Zoom");
        resetZoomItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetZoomItemActionPerformed(evt);
            }
        });

        sandboxMenu.add(resetZoomItem);

        showGridItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        showGridItem.setText("Show Grid");
        showGridItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showGridItemActionPerformed(evt);
            }
        });

        sandboxMenu.add(showGridItem);

        menuBar.add(sandboxMenu);

        aboutMenu.setText("About");
        aboutItem.setText("About...");
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });

        aboutMenu.add(aboutItem);

        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        pack();
    }//GEN-END:initComponents

    private void simulationSpeedItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simulationSpeedItemActionPerformed
        controller.setSimDelay(new SimDelayDialog(this).showDialog());
    }//GEN-LAST:event_simulationSpeedItemActionPerformed

    private void exportImageItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportImageItemActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Circuit Image...");
        chooser.setFileFilter(new PNGFileFilter());
        chooser.setMultiSelectionEnabled(false);
        
        if (chooser.showDialog(this, "Save Image") ==
                JFileChooser.APPROVE_OPTION) {
            try {
                sandbox.saveImage(chooser.getSelectedFile());
            } catch(Exception e) {
                showError(e.getMessage());
            }
        }
    }//GEN-LAST:event_exportImageItemActionPerformed

    private void saveComponentItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveComponentItemActionPerformed
        try {
            if (!controller.canModelBeAComponent()) {
                showError("There must be at least one input and one\n" +
                        "output in the circuit.");
                return;
            }
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }            

        float previousZoomFactor = zoomFactor;
        
        NewComponentDialog ncd = new NewComponentDialog(this,
                controller.getInputComponents(),
                controller.getOutputComponents());

        // show the dialog
        if (ncd.showDialog() == NewComponentDialog.OPTION_ACCEPT) {
            // choose a file to save the custom component as
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Custom Component...");
            chooser.setCurrentDirectory(new File(
                    System.getProperty("user.dir") + 
                    System.getProperty("file.separator") +
                    "components" +
                    System.getProperty("file.separator")));
            chooser.setFileFilter(new ComponentFileFilter());
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);

            // If there's an error saving the component, keep asking the
            // user until they save it successfully or cancel
            boolean doneSaveDialog = false;
            while (!doneSaveDialog) {
                if (chooser.showDialog(this, "Save Component") ==
                        JFileChooser.APPROVE_OPTION) {
                    try {
                        // replace whatever extension the file was given,
                        // with the custom component file extension
                        File fileOut = Util.replaceExtension(
                                chooser.getSelectedFile(), "csc");

                        // if the user tried to say something outside of the
                        // components directory...
                        if (!fileOut.getAbsolutePath().startsWith(
                                System.getProperty("user.dir") +
                                System.getProperty("file.separator") +
                                "components" +
                                System.getProperty("file.separator"))) {
                            // reset the chooser to viewing components dir
                            chooser.setCurrentDirectory(new File(
                                    System.getProperty("user.dir") + 
                                    System.getProperty("file.separator") +
                                    "components" +
                                    System.getProperty("file.separator")));
                            // throw an error to be displayed
                            throw new Exception(
                                    "The component must be saved within" +
                                    " the\nCircuit Sandbox components" +
                                    "folder");
                        }

                        // write the custom component files
                        controller.saveAsCustomComponent(
                                fileOut, ncd.getComponentName(),
                                ncd.getImage(), ncd.getInputPins(),
                                ncd.getOutputPins());
                        doneSaveDialog = true;
                    }
                    catch (Exception e) {
                        // error saving
                        showError("The component could not be saved");
                    }
                } else {
                    // user cancelled the saving
                    doneSaveDialog = true;
                }
            }
        }
        setZoomFactor(previousZoomFactor);
        sandbox.repaint();
    }//GEN-LAST:event_saveComponentItemActionPerformed

    private void stopSimulationItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopSimulationItemActionPerformed
        stopSimulation();
        repaintSandbox();
    }//GEN-LAST:event_stopSimulationItemActionPerformed

    private void startSimulationItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startSimulationItemActionPerformed
        if (!isSimulating()) {
            clearSelection();
            undoItem.setEnabled(false);
            undoButton.setEnabled(false);
            redoItem.setEnabled(false);
            redoButton.setEnabled(false);
            toybox.setEnabled(false);
        
            controller.startSimulation();
        
            startSimulationItem.setEnabled(false);
            startButton.setEnabled(false);
            stopSimulationItem.setEnabled(true);
            stopButton.setEnabled(true);
            isSimulating = true;
        
            repaintSandbox();
        }
    }//GEN-LAST:event_startSimulationItemActionPerformed

    private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed
        String message = "Circuit Sandbox v1.0\nCompiled March 14, 2005\n\n" +
                "Daniel Stahl - dstahl@peace.gordon.edu\n"+
                "Kevin Wellwood - kwellwood@peace.gordon.edu";
        JOptionPane msgBox = new JOptionPane();
        msgBox.showMessageDialog(this, message,
                "About Circuit Sandbox",
                JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_aboutItemActionPerformed

    private void showGridItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showGridItemActionPerformed
        sandbox.setShowGrid(showGridItem.isSelected());
        repaintSandbox();
    }//GEN-LAST:event_showGridItemActionPerformed

    private void loadComponentItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadComponentItemActionPerformed
        stopSimulation();
        toybox.loadComponentDialog();
    }//GEN-LAST:event_loadComponentItemActionPerformed

    private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemActionPerformed
        if (isCreatingWire()) {
            // cancel creating a wire (got here by pressing backspace)
            cancelWire();
        } else if (!selection.isEmpty()){
            // we're not creating a wire, just delete the selection
            controller.deleteSelection(selection);
            repaintSandbox();
            undoItem.setEnabled(true);
            undoButton.setEnabled(true);
            redoItem.setEnabled(false);
            redoButton.setEnabled(false);
            if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
        }
    }//GEN-LAST:event_deleteItemActionPerformed

    private void newModelItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newModelItemActionPerformed
        stopSimulation();
        if ((controller.isModified() && askToSaveAndContinue()) ||
                !controller.isModified()) {
            clearSelection();
            sandbox.removeAll();
            controller.newModel();
            undoItem.setEnabled(false);
            undoButton.setEnabled(false);
            redoItem.setEnabled(false);
            redoButton.setEnabled(false);
            setWorkingFile(null);
            setZoomFactor(1.0f);
        }
    }//GEN-LAST:event_newModelItemActionPerformed

    private void saveModelAsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveModelAsItemActionPerformed
        stopSimulation();
        saveModelDialog();
    }//GEN-LAST:event_saveModelAsItemActionPerformed

    private void saveModelItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveModelItemActionPerformed
        stopSimulation();
        if (currentFile.getName().equals(DEFAULT_FILENAME)) {
            // Allow user to choose file save
            saveModelDialog();
        } else {
            try {
                // Save using the current working file
                controller.saveModel(currentFile);
                setWorkingFile(currentFile);
            }
            catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }//GEN-LAST:event_saveModelItemActionPerformed

    private void resetZoomItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetZoomItemActionPerformed
        setZoomFactor(1.0f);        
    }//GEN-LAST:event_resetZoomItemActionPerformed

    private void zoomOutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutItemActionPerformed
        switch ((int)(getZoomFactor()*100)) {
            case 200: setZoomFactor(1.5f);  return;
            case 150: setZoomFactor(1.0f);  return;
            case 100: setZoomFactor(0.75f); return;
            case 75:  setZoomFactor(0.5f);  return;
        }
    }//GEN-LAST:event_zoomOutItemActionPerformed

    private void zoomInItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInItemActionPerformed
        
        
        switch ((int)(getZoomFactor()*100)) {
            case 50:  setZoomFactor(0.75f); return;
            case 75:  setZoomFactor(1.0f);  return;
            case 100: setZoomFactor(1.5f);  return;
            case 150: setZoomFactor(2.0f);  return;
        }
    }//GEN-LAST:event_zoomInItemActionPerformed

    private void redoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoItemActionPerformed
        if (!isSimulating()) {
            controller.redo();
            undoItem.setEnabled(true);
            undoButton.setEnabled(true);
            if (!controller.canRedo()) {
                redoItem.setEnabled(false);
                redoButton.setEnabled(false);
            }
            if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
            repaintSandbox();
        }
    }//GEN-LAST:event_redoItemActionPerformed

    private void undoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoItemActionPerformed
        if (!isSimulating()) {
            controller.undo();
            redoItem.setEnabled(true);
            redoButton.setEnabled(true);
            if (!controller.canUndo()) {
                undoItem.setEnabled(false);
                undoButton.setEnabled(false);
            }
            if (!getTitle().endsWith("*")) { setTitle(getTitle() + " *"); }
            repaintSandbox();
        }
    }//GEN-LAST:event_undoItemActionPerformed

    private void quitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitItemActionPerformed
        exitProgram();
    }//GEN-LAST:event_quitItemActionPerformed

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        exitProgram();
    }//GEN-LAST:event_windowClosing

    private void openModelItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openModelItemActionPerformed
        stopSimulation();
        if ((controller.isModified() && askToSaveAndContinue()) ||
                !controller.isModified()) {
            loadModelDialog();
        }
    }//GEN-LAST:event_openModelItemActionPerformed

    /** the collection of selected displayable objects */
    private ArrayList selection;
    /** the magnification level for the sandbox */
    private float zoomFactor = 1.0f;
    /** the file currently being operated on */
    private File currentFile;
    /** the controller that handles requests from the gui */
    private Controller controller;
    /** the component to be added */
    private ComponentImage componentToAdd;
    /** the type(s) of pins that can be clicked on */
    private int clickablePinTypes = ComponentPin.ALL_PINS;
    /** the wire being created through click and drag; <code>null</code>
     * if no wire is being created */
    private WireImage wireBeingCreated;
    /** flag indicating the simulation is running */
    private boolean isSimulating = false;
    /** current directory of the model file chooser */
    private File modelDir = new File(System.getProperty("user.dir") +
            System.getProperty("file.separator") + "models" +
            System.getProperty("file.separator"));
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem deleteItem;
    private javax.swing.JButton redoButton;
    private javax.swing.JMenuItem redoItem;
    private gui.Sandbox sandbox;
    private javax.swing.JScrollPane sandboxScrollPane;
    private javax.swing.JCheckBoxMenuItem showGridItem;
    private javax.swing.JButton startButton;
    private javax.swing.JMenuItem startSimulationItem;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenuItem stopSimulationItem;
    private gui.Toybox toybox;
    private javax.swing.JButton undoButton;
    private javax.swing.JMenuItem undoItem;
    private javax.swing.JButton zoomIn;
    private javax.swing.JMenuItem zoomInItem;
    private javax.swing.JButton zoomOut;
    private javax.swing.JMenuItem zoomOutItem;
    // End of variables declaration//GEN-END:variables
    
    /** the default filename for an unsaved circuit model */
    private static final String DEFAULT_FILENAME = "Untitled.csm";
} 
