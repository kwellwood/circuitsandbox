/*
 * Toybox.java
 *
 * Created on February 5, 2005, 12:03 PM
 */

package gui;

import circuitsandbox.ComponentFileFilter;
import circuitsandbox.Util;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import model.CustomComponent;

/**
 * <p>Extends <code>JTree</code> to display components in groups for the user
 * to select and add to the <code>Sandbox</code>. <code>Toybox</code> also
 * includes a pop-up menu that is activated by right clicking anywhere on
 * the component.</p>
 *
 * <p>Custom components can be added and removed from the "Custom Components"
 * category in the list, using the pop-up menu. Additionally, all custom
 * component files in the <code>components/</code> directory are
 * automatically loaded into the list by the constructor.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Toybox extends JTree implements TreeSelectionListener,
        FocusListener, PopupMenuListener {
    
    /**
     * Constructs a new <code>Toybox</code> object. This constructor should not
     * be called except by the <code>Toybox(Gui)</code> constructor.
     */
    public Toybox() {
        setRootVisible(false);
        loadedComponents = new ArrayList();
        
        // add all built-in components to the tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode category = new DefaultMutableTreeNode("Inputs");
        category.add(new DefaultMutableTreeNode(new ButtonImage()));
        category.add(new DefaultMutableTreeNode(new SwitchImage()));
        category.add(new DefaultMutableTreeNode(new GroundImage()));
        category.add(new DefaultMutableTreeNode(new VccImage()));
        //category.add(new DefaultMutableTreeNode("Clock"));
        //category.add(new DefaultMutableTreeNode("Multi-Input"));
        root.add(category);
        
        category = new DefaultMutableTreeNode("Outputs");
        category.add(new DefaultMutableTreeNode(new LEDImage()));
        category.add(new DefaultMutableTreeNode(new HexDigitImage()));
        //category.add(new DefaultMutableTreeNode("Buzzer"));
        root.add(category);
        
        category = new DefaultMutableTreeNode("Logic Components");
        category.add(new DefaultMutableTreeNode(new NotImage()));
        category.add(new DefaultMutableTreeNode(new And2Image()));
        category.add(new DefaultMutableTreeNode(new And3Image()));
        category.add(new DefaultMutableTreeNode(new And4Image()));
        category.add(new DefaultMutableTreeNode(new Or2Image()));
        category.add(new DefaultMutableTreeNode(new Or3Image()));
        category.add(new DefaultMutableTreeNode(new Or4Image()));
        category.add(new DefaultMutableTreeNode(new Nand2Image()));
        category.add(new DefaultMutableTreeNode(new Nand3Image()));
        category.add(new DefaultMutableTreeNode(new Nand4Image()));
        category.add(new DefaultMutableTreeNode(new Nor2Image()));
        category.add(new DefaultMutableTreeNode(new Nor3Image()));
        category.add(new DefaultMutableTreeNode(new Nor4Image()));
        category.add(new DefaultMutableTreeNode(new Xor2Image()));
        category.add(new DefaultMutableTreeNode(new JKFlipFlopImage()));
        category.add(new DefaultMutableTreeNode(new DFlipFlopImage()));
        root.add(category);
        
        customNode = new DefaultMutableTreeNode("Custom Components");
        root.add(customNode);
        
        // create the tree model and pass it the root category
        treeModel = new DefaultTreeModel(root);
        setModel(treeModel);
        
        // visually expand all the category nodes in the tree
        for (int i = root.getChildCount(); i >= 0; i--) { expandRow(i); }
        
        // automatically load all components in the components directory
        preloadCustomComponents();
        
        // create the popup menu
        loadMenuItem = new JMenuItem("Load Component...");
        loadMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadComponentDialog();
            }
        });
        unloadMenuItem = new JMenuItem("Unload");
        unloadMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unloadComponent();
            }
        });
        cancelMenuItem = new JMenuItem("Cancel");
        popupMenu = new JPopupMenu();
        popupMenu.add(loadMenuItem);
        popupMenu.add(unloadMenuItem);
        popupMenu.add(cancelMenuItem);
        popupMenu.addPopupMenuListener(this);
        
        // set up the event listeners
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (Util.isRightClick(e)) { displayPopupMenu(e); }
            }
        });
        addTreeSelectionListener(this);
        addFocusListener(this);
        
        // set the custom class for rendering the tree nodes
        setCellRenderer(new ToyboxCellRenderer());
    }
    
    /**
     * Constructs a new <code>Toybox</code> object, passing a reference to the
     * <code>Gui</code>. Call this constructor to create a new
     * <code>Toybox</code> object.
     *
     * @param gui the gui
     */
    public Toybox(Gui gui) {
        this();
        this.gui = gui;
    }
    
    /**
     * Called when the selection changes. This method ensures that only
     * component nodes may be selected and that only one at a time may be
     * selected. The current working component in the gui (the one that can be
     * added by clicking on the sandbox) is updated appropriately.
     *
     * @param treeSelectionEvent the tree selection event
     */
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        // set the tree's entire selection to the single selection that
        // was just made (ensures only one node may be selected)
        setSelectionPath(treeSelectionEvent.getNewLeadSelectionPath());
        
        // see if a node is selected
        if (getSelectionPath() != null) {
            // get the selected object
            Object o = ((DefaultMutableTreeNode)getSelectionPath().
                    getLastPathComponent()).getUserObject();
            
            if (!(o instanceof String)) {
                // the object is a ComponentImage, so set the current
                // working component
                ComponentImage ci = (ComponentImage)o;
                gui.setComponentToAdd(ci);
            } else {
                // selected a category name, not a component. clear the
                // working component.
                gui.setComponentToAdd(null);
                setSelectionPath(null);
            }
        } else {
            // the selection path was null so something may have been
            // unselected. clear the current working component in case.
            gui.setComponentToAdd(null);
        }
    }
    
    /**
     * Necessary for a <code>FocusListener</code> but not used.
     *
     * @param focusEvent the focus event
     */
    public void focusGained(FocusEvent focusEvent) { }
    
    /**
     * Called when the <code>Toybox</code> loses focus. Unselects the
     * currently selected component if there was one.
     *
     * @param focusEvent the focus event
     */
    public void focusLost(FocusEvent focusEvent) { clearSelection(); }
    
    /**
     * Looks in the <code>components/</code folder and loads any
     * custom component files found there. Does not check subdirectories.
     * If a component file is unable to be loaded, it is silently
     * skipped.
     */
    private void preloadCustomComponents() {
        File componentDir = new File("components/");
        if (!componentDir.exists()) { return; }
        
        File[] files = componentDir.listFiles(new ComponentFileFilter());
        
        for (int i = 0; i < files.length; i ++) {
            if (files[i].isFile()) {
                // If a component fails to load automatically, ignore it.
                // an error will be displayed later if the user tries to
                // manually load it again.
                try { loadComponent(files[i]); } catch (Exception e) { }
            }
        }
    }
    
    /**
     * Loads a custom component into the tree, given a type string of the
     * component. If the component can't be loaded, no error is given.
     *
     * @param type the component's type string
     */
    public void loadComponent(String type) {
        try {
            loadComponent(new File(
                    type.substring(CustomComponent.TYPE_STRING.length() + 1)
                    ));
        }
        catch (Exception e) { }
    }
    
    /**
     * Loads a custom component into the tree, given a <code>file</code>.
     *
     * @param file the file
     *
     * @throws java.lang.Exception if the custom component file couldn't be
     * loaded
     */
    private void loadComponent(File file) throws Exception {
        
        CustomImage image = new CustomImage(file.getPath());
        
        if (!componentIsLoaded(image)) {
            loadedComponents.add(image);
            
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(image);
            treeModel.insertNodeInto(node, customNode,
                    customNode.getChildCount());
            
            scrollPathToVisible(new TreePath(node.getPath()));
        }
    }
    
    /**
     * Displays a dialog to load a custom component. If the file chosen is not
     * within the <code>components/</code> directory tree, an error message
     * is sent to the <code>gui</code>.
     */
    public void loadComponentDialog() {
        // create the file chooser dialog
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Custom Component...");
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir") + 
                "/components/"));
        chooser.setFileFilter(new ComponentFileFilter());
        chooser.setMultiSelectionEnabled(false);
        
        // show it and get a single file path
        if (chooser.showDialog(gui, "Load") == JFileChooser.APPROVE_OPTION) {
            String filePath = Util.getRelativePath(chooser.getSelectedFile());
            if (filePath == null) {
                gui.showError("Component not within Circuit Sandbox directory");
            } else {
                try { loadComponent(new File(filePath)); }
                catch (Exception e) {
                    // TODO handle exception here
                    e.printStackTrace();
                    System.out.println("Error loading component: " + 
                            e.getMessage());
                }
            }
        }
    }
    
    /**
     * Removes the custom component that was right clicked on from the tree
     * and from the <code>loadedComponents</code> collection. This is called by
     * the pop-up menu when the "Unload" option is
     * chosen.
     */
    private void unloadComponent() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                getPathForLocation(lastRightClick.x, lastRightClick.y).
                getLastPathComponent();
        
        loadedComponents.remove(node.getUserObject());
        treeModel.removeNodeFromParent(node);
    }
    
    /**
     * Returns <code>true</code> if a custom component of the given type has
     * already been loaded into the tree. Otherwise <code>false</code> is
     * returned. Searches the <code>loadedComponents</code> collection.
     *
     * @param ci the custom component
     *
     * @return <code>true</code> if the custom component has been loaded
     */
    private boolean componentIsLoaded(ComponentImage ci) {
        for (int i = 0; i < loadedComponents.size(); i++) {
            if (ci.getTypeString().equals(
                    ((ComponentImage)loadedComponents.get(i)).getTypeString()
                    )) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Triggers the pop-up menu. If the user triggered it over a custom
     * component, the Unload option is enabled, otherwise it is disabled.
     * <code>lastRightClick</code> is set at the location given in the mouse
     * event.
     *
     * @param e the mouse event
     */
    private void displayPopupMenu(MouseEvent e) {
        lastRightClick = e.getPoint();
        
        for (int i = 0; i < loadedComponents.size(); i++) {
            ((CustomImage)loadedComponents.get(i)).setSelected(false);
        }
        repaint();
        
        Object o = getObjectAt(lastRightClick);
        if (o != null && (o instanceof CustomImage)) {
            ((CustomImage)o).setSelected(true);
            repaint();
            unloadMenuItem.setEnabled(true);
            unloadMenuItem.setText("Unload " + o);
        } else {
            unloadMenuItem.setEnabled(false);
            unloadMenuItem.setText("Unload");
        }
        
        popupMenu.show(this, e.getX(), e.getY());
    }
    
    /**
     * Returns the user object of the tree node corresponding with the given
     * pixel location on the <code>Toybox</code> component. If no tree node
     * is at the location, <code>null</code> is returned.
     *
     * @param location the location, in pixels
     *
     * @return the user object
     */
    private Object getObjectAt(Point location) {
        DefaultMutableTreeNode node = getNodeAt(location);
        if (node != null) {
            return node.getUserObject();
        }
        return null;
    }
    
    /**
     * Returns the tree node corresponding with the given pixel location
     * on the <code>Toybox</code> component. If no tree node is at the
     * location, <code>null</code> is returned.
     *
     * @param location the location, in pixels
     *
     * @return the tree node
     */
    private DefaultMutableTreeNode getNodeAt(Point location) {
        TreePath path = getPathForLocation(location.x, location.y);
        if (path != null) {
            return (DefaultMutableTreeNode)path.getLastPathComponent();
        }
        return null;
    }
    
    /**
     * Called when the pop-up menu has been cancelled. Does nothing.
     *
     * @param e the pop-up event
     */
    public void popupMenuCanceled(PopupMenuEvent e) { }
    
    /**
     * Called when the pop-up menu will become visible. Does nothing.
     *
     * @param e the pop-up event
     */
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
    
    /**
     * Called when the pop-up menu will become invisible. If a custom
     * component was originally clicked on to make the pop-up appear, it
     * will become unselected again to make its colored background disappear.
     *
     * @param e the pop-up event
     */
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        Object o = getObjectAt(lastRightClick);
        if (o != null && (o instanceof CustomImage)) {
            ((CustomImage)o).setSelected(false);
            repaint();
        }
    }
    
    /** the <code>Gui</code> containing this <code>Toybox</code> */
    private Gui gui;
    /** a collection of custom components that have already been loaded */
    private ArrayList loadedComponents;
    /** the data model displayed by the <code>Toybox</code> */
    private DefaultTreeModel treeModel;
    /** the tree node for the custom component category */
    private DefaultMutableTreeNode customNode;
    /** the popup menu */
    private JPopupMenu popupMenu;
    /** the unload component option on the popup menu */
    private JMenuItem unloadMenuItem;
    /** the load component option on the popup menu */
    private JMenuItem loadMenuItem;
    /** the cancel option on the popup menu */
    private JMenuItem cancelMenuItem;
    /** the last location that was right clicked */
    private Point lastRightClick;
    
    /**
     * Controls the visual style of rows drawn in the <code>Toybox</code>.
     * This class controls the font style of the components' names and sets
     * the icons drawn next to each name.
     */
    private class ToyboxCellRenderer extends DefaultTreeCellRenderer {
        
        /**
         * Called for each row in the tree when it is drawn, to set the visual
         * style.
         *
         * @param tree the <code>JTree</code> being drawn
         * @param value the tree node on the row
         * @param sel whether the row is selected or not
         * @param expanded whether the node is expanded or not
         * @param leaf whether the node is a leaf or not
         * @param row the row number
         * @param hasFocus whether the row has focus or not
         *
         * @return a graphical <code>Component</code> object (basically a
         * <code>Label</code>) that will be drawn on the <code>Toybox</code>
         * at the current row
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                    row, hasFocus);
            
            if (!(((DefaultMutableTreeNode)value).getUserObject() instanceof
                    String)) {
                ComponentImage componentImage = (ComponentImage)
                ((DefaultMutableTreeNode)value).getUserObject();
                setIcon(componentImage.getIcon());
                setFont(getFont().deriveFont(Font.PLAIN));
                
                if (componentImage.isSelected()) {
                    setBackgroundNonSelectionColor(Color.orange);
                } else {
                    setBackgroundNonSelectionColor(Color.white);
                }
            } else {
                setFont(getFont().deriveFont(Font.BOLD));
                setBackgroundNonSelectionColor(Color.white);
                
            }

            return this;
        }
    }
}
