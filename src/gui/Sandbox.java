/*
 * Sandbox.java
 *
 * Created on February 5, 2005, 11:34 AM
 */

package gui;

import circuitsandbox.Util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JViewport;
import model.LogicComponent;


/**
 * <p>Extends <code>JComponent</code> to provide a workspace for the visual
 * circuit model. Onlu <code>Displayable</code> and <code>WireImage</code>
 * objects may be added to the <code>Sandbox</code> class for painting.</p>
 *
 * <p>Dragging component images to the outer edges (right and down) of the
 * sandbox will cause the sandbox area to grow when the move is completed.
 * Dragging objects away from the edges and toward the upper-left will caus
 * the sandbox to shrink.</p>
 *
 * <p>Multiple <code>Displayable</code>s can be selected simultaneously by
 * left-clicking and dragging a bounding box over the desired components.
 * Clicking anywhere on the sandbox without dragging will unselect all
 * <code>Displayable</code>s.</p>
 *
 * <p><code>WireImage</code>s are drawn directly on the sandbox
 * <code>Graphics</code> object, due to their nature. If the sandbox is clicked
 * on, it first checks if the click is on the segment of a wire so it can be
 * selected/unselected.</p>
 *
 * @author  Daniel Stahl, Kevin Wellwood
 */
public class Sandbox extends JComponent {
    
    /**
     * Do not use this constructor, it is for NetBeans compatibility only.
     * Does not create a useful <code>Sandbox</code> object.
     */
    public Sandbox() {
        // do not use this constructor!
        wires = new ArrayList();
    }
    
    /**
     * Constructs a new <code>Sandbox</code> object given a reference to the
     * gui.
     *
     * @param gui the gui
     */
    public Sandbox(Gui gui) {
        initComponents();
        this.gui = gui;
        wires = new ArrayList();
        
        // load the grid image
        MediaTracker mediaTracker = new MediaTracker(this);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        gridImage = toolkit.getImage("images/grid.png");
        mediaTracker.addImage(gridImage, 0);
        // wait for the bitmap to finish loading
        try { mediaTracker.waitForAll(); } catch (InterruptedException e) { }
        
        // set up the automatic high quality repaint timer
        qualityRefreshDeamon = new Timer(true);
        qualityRefreshDeamon.schedule(new TimerTask() {
            public void run() {
                if (!currentlyPainting &&
                        lastPaintTime + HI_QUALITY_REFRESH_DELAY <
                        System.currentTimeMillis() &&
                        lastPaintQuality == DRAFT_PAINT) {
                    nextPaintQuality = HI_QUALITY_PAINT;
                    Sandbox.this.repaint();
                }
            }}, HI_QUALITY_REFRESH_DELAY, HI_QUALITY_REFRESH_DELAY);
    }
    
    /**
     * Enables/disables drawing the grid.
     *
     * @param enabled set <code>true</code> to show the grid
     */
    public void setShowGrid(boolean enabled) {
        showGrid = enabled;
    }
    
    /**
     * Draws the component and all child components.
     *
     * @param graphics the graphics to draw with
     */
    public void paint(Graphics graphics) {
        currentlyPainting = true;
        Graphics2D g2 = (Graphics2D)graphics;
        if (nextPaintQuality == HI_QUALITY_PAINT) {
            // repaint with high quality anti-aliasing
            /*g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);*/
            lastPaintQuality = nextPaintQuality;
            nextPaintQuality = DRAFT_PAINT;
        } else {
            // repaint with low draft quality
            lastPaintQuality = DRAFT_PAINT;
        }
        float zoom = ((gui != null) ? gui.getZoomFactor() : 1.0f);
        g2.scale(zoom, zoom);
        
        // --- clear the background to white ---
        int width = (int)(getSize().width / zoom);
        int height = (int)(getSize().height / zoom);
        if (gui != null && gui.isSimulating()) {
            g2.setBackground(simulationColor);
        } else {
            g2.setBackground(designColor);
        }
        g2.clearRect(0, 0, width, height);
        
        // --- draw the grid ---
        if (showGrid) {
            int gridHeight = (int)Math.ceil(getPreferredSize().height/zoom/200);
            int gridWidth = (int)Math.ceil(getPreferredSize().width/zoom/200);
            
            for (int j = 0; j < gridHeight; j++) {
                for (int i = 0; i < gridWidth; i++) {
                    if (gridImage != null) {
                        g2.drawImage(gridImage, i*200, j*200, 200, 200, null);
                    }
                }
            }
        }
        
        // --- paint the wires ---
        paintWires(graphics);
        
        // --- paint the displayables ---
        g2.scale(1 / zoom, 1 / zoom);
        paintChildren(g2);
        g2.scale(zoom, zoom);
        
        // --- paint the selection box ---
        if (selecting) {
            int x = Math.min(dragStartLocation.x, dragEndLocation.x);
            int y = Math.min(dragStartLocation.y, dragEndLocation.y);
            int w = Math.abs(dragEndLocation.x - dragStartLocation.x);
            int h = Math.abs(dragEndLocation.y - dragStartLocation.y);
            
            g2.setStroke(selectionStroke);
            g2.setColor(selectionFillColor);
            g2.fillRect(x, y, w, h);
            g2.setColor(selectionEdgeColor);
            g2.drawRect(x, y, w, h);
        }
        
        currentlyPainting = false;
        lastPaintTime = System.currentTimeMillis();
    }
    
    /**
     * Draws all of the wires in the sandbox.
     *
     * @param graphics the graphics to draw with
     */
    private void paintWires(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        
        Color color = g2.getColor();
        Stroke stroke = g2.getStroke();
                
        Iterator wireIter = wires.iterator();
        while (wireIter.hasNext()) {
            WireImage wire = (WireImage) wireIter.next();
            
            // set the wire style
            if (gui.isSimulating()) {
                byte value = wire.getWire().getSource().getValueOfOutput(
                        wire.getWire().getSourcePin());
                if (value == LogicComponent.TRUE) {
                    g2.setColor(Color.red);
                } else if (value == LogicComponent.FALSE) {
                    g2.setColor(Color.blue);
                } else {
                    g2.setColor(Color.green);
                }
            } else {
                // not simulating, use the wire's color
                g2.setColor(wire.getColor());
            }
            if (wire.isSelected()) {
                g2.setStroke(selectedWireStroke);
            } else {
                g2.setStroke(wireStroke);
            }
            
            // draw the segments of the wire
            Iterator pointIter = wire.getPoints().iterator();
            Point previous = (Point)pointIter.next();
            while (pointIter.hasNext()) {
                Point current = (Point)pointIter.next();
                g2.drawLine(previous.x, previous.y, current.x,  current.y);
                if (Math.abs(current.x - previous.x) > Math.abs(current.y - previous.y)) {
                    g2.drawLine(previous.x, previous.y-1, current.x, current.y-1);
                } else {
                    g2.drawLine(previous.x-1, previous.y, current.x-1, current.y);
                }
                
                
                previous = current;
            }
        }
        
        // draw the unfinished wire if the user is creating one
        if (gui != null && gui.isCreatingWire()) {
            g2.setColor(newWireColor);
            g2.setStroke(selectedWireStroke);
            ArrayList points = gui.getNewWire().getPoints();
            for (int i=0; i<points.size()-1; i++) {
                // get one point and the next and draw the line
                Point p1 = (Point)points.get(i);
                Point p2 = (Point)points.get(i+1);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                if (Math.abs(p2.x - p1.x) > Math.abs(p2.y - p1.y)) {
                    g2.drawLine(p1.x, p1.y-1, p2.x, p2.y-1);
                } else {
                    g2.drawLine(p1.x-1, p1.y, p2.x-1, p2.y);
                }
            }
        }
        
        g2.setColor(color);
        g2.setStroke(stroke);
    }
    
    /**
     * Writes an image of the sandbox contents to disk as a .PNG file.
     *
     * @param file the image file to write
     *
     * @throws Exception if the file cannot be saved
     */
    public void saveImage(File file) throws Exception {
        // create an image and draw the sandbox onto its graphics
        BufferedImage image = new BufferedImage(getWidth(), getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        paint(image.createGraphics());
        
        File imgFile = Util.replaceExtension(file, "png");
        ImageIO.write(image, "png", imgFile);
    }
    
    /**
     * Resizes the area of the sandbox to fit the layout of the circuits.
     */
    public void resize() {
        // calculate the minimum width and height for the sandbox based on
        // the viewport containing it
        int minWidth = getViewport().getExtentSize().width;
        int minHeight = getViewport().getExtentSize().height;
        
        // calculate the width and height that the components cover
        int circWidth = 0;
        int circHeight = 0;
        Component[] components = this.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].getX()+components[i].getWidth() > circWidth) {
                circWidth = components[i].getX() + components[i].getWidth();
            }
            if (components[i].getY()+components[i].getHeight() > circHeight) {
                circHeight = components[i].getY() + components[i].getHeight();
            }
        }
        circWidth = (int)(circWidth + 100 * gui.getZoomFactor());
        circHeight = (int)(circHeight + 100 * gui.getZoomFactor());
        
        // take the largest of those dimensions and set the new sandbox size
        int newWidth = Math.max(minWidth, circWidth);
        int newHeight = Math.max(minHeight, circHeight);
        
        setPreferredSize(new Dimension(newWidth, newHeight));
        revalidate();
    }
    
    /**
     * Zooms the sandbox to the gui's current level of magnification. The
     * <code>zoom</code> command is passed to all objects contained in the
     * sandbox so they can adjust their own size and location accordingly.
     */
    public void zoom() {
        Component[] components = getComponents();
        
        for (int i=0; i<components.length; i++) {
            ((Displayable)components[i]).zoom();
        }
        
        resize();
        repaint();
    }
    
    /**
     * Returns all the gui components in the given area. The coordinates of
     * the rectangle must be in standard units.
     *
     * @param area the rectangular area
     *
     * @return a collection of <code>Displayable</code>s completely within the
     * area
     */
    private HashSet getDisplayables(Rectangle area) {
        HashSet selection = new HashSet();
        
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (area.contains(Util.toStdCoords(components[i].getBounds(),
                    gui.getZoomFactor()))) {
                // dont include component pins in the selection
                int type = ((Displayable)components[i]).getDisplayableType();
                if (type != Displayable.COMPONENT_PIN) {
                    // add the displayable object to the selection
                    selection.add(components[i]);
                }
            }
        }
        
        for (int i = 0; i < wires.size(); i++) {
            WireImage wireImage = (WireImage)wires.get(i);
            Rectangle wireRect = wireImage.getBounds();
            // check if the wire's bounding rectangle is fully within the
            // selection area
            if (wireRect.x >= area.x && wireRect.y >= area.y &&
                    wireRect.x + wireRect.width <= area.x + area.width &&
                    wireRect.y + wireRect.height <= area.y + area.height) {
                // add the wire to the selection
                selection.add(wireImage);
            }
        }
        
        return selection;
    }
    
    /**
     * Adds the <code>component</code> to the <code>sandbox</code>.
     *
     * @param c component to add
     *
     * @return the component added
     */
    public Component add(Component c) {
        if (c instanceof WireImage) {
            wires.add(c);
        } else {
            super.add(c);
        }
        return c;
    }
    
    /**
     * Removes the <code>component</code> from the <code>sandbox</code>.
     *
     * @param c component to remove
     */
    public void remove(Component c) {
        if (c instanceof WireImage) {
            wires.remove(c);
        } else {
            super.remove(c);
        }
    }
    
    /**
     * Clears the sandbox of components and wires.
     */
    public void removeAll() {
        super.removeAll();
        wires.clear();
    }
    
    /**
     * Returns the wire image of the first wire found with a segment within
     * <code>WIRE_CLICK_TOLERANCE</code> of the given location. The location
     * must be in standard coordinates. If no wire is found, <code>null</code>
     * is returned.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the first wire with a segment found at the location
     */
    private WireImage getWireAt(int x, int y) {
        Point p = new Point(x, y);
        
        // loop backwards through the wires (front to back Z order)
        for (int i = wires.size()-1; i >= 0; i--) {
            // loop through the line segments
            ArrayList points = ((WireImage)wires.get(i)).getPoints();
            for (int j = 1; j < points.size(); j++) {
                // create a line segment between this point and the previous
                Point p1 = (Point)points.get(j-1);
                Point p2 = (Point)points.get(j);
                Line2D.Float segment = new Line2D.Float(p1, p2);
                
                if (segment.ptSegDist(p) <= WIRE_CLICK_TOLERANCE) {
                    // a segment of this wire is within distance of the click
                    // so return the wire image
                    return (WireImage)wires.get(i);
                }
            }
        }
        
        // no wire with a segment close enough
        return null;
    }
    
    /**
     * Returns the segment number of a given wire image that is within
     * clicking distance of the given location. The location must be in
     * standard coordinates. If no segment is close enough, <code>-1</code> is
     * returned.
     *
     * @param wireImage the wire to check
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the first matching wire segment found (base 0, starting from
     * the source end)
     */
    private int getWireSegmentAt(WireImage wireImage, int x, int y) {
        Point p = new Point(x, y);
        
        // loop through the points in the path
        ArrayList points = wireImage.getPoints();
        for (int j = 1; j < points.size(); j++) {
            // create a line segment between this point and the previous
            Line2D.Float segment = new Line2D.Float((Point)points.get(j-1),
                    (Point)points.get(j));
            if (segment.ptSegDist(p) <= WIRE_CLICK_TOLERANCE) {
                // this segment of is within distance of the click so
                // return the segment number (base 0)
                return j - 1;
            }
        }
        
        // no segment close enough
        return -1;
    }
    
    /**
     * Returns the sandbox's parent gui component. The parent is the viewport
     * of the <code>JScrollPane</code> component that surrounds the sandbox.
     *
     * @return the sandbox's viewport
     */
    private JViewport getViewport() {
        return (JViewport)getParent();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(null);

        setPreferredSize(new java.awt.Dimension(450, 450));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Sandbox.this.mouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Sandbox.this.mouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Sandbox.this.mouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Sandbox.this.mousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Sandbox.this.mouseReleased(evt);
            }
        });
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                Sandbox.this.ancestorResized(evt);
            }
        });

    }//GEN-END:initComponents
    
    private void ancestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_ancestorResized
        resize();
        repaint();
    }//GEN-LAST:event_ancestorResized
    
    private void mouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseExited
        if (!gui.isSimulating()) {
            // If the mouse moved outside the sandbox, stop creating a new wire
            if (gui.isCreatingWire() && !contains(evt.getPoint())) {
                gui.cancelWire();
                repaint();
            }
        }
    }//GEN-LAST:event_mouseExited
    
    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (!gui.isSimulating()) {
            // ---- LEFT CLICK ----
            if (Util.isLeftClick(evt)) {
                if (gui.isAddingComponent()) {
                    gui.addComponent(Util.toStdCoords(evt.getPoint(),
                            gui.getZoomFactor()));
                } else if (gui.isCreatingWire()) {
                    // convert screen coordinates to standard coordinates and
                    // then snap them to the grid
                    int x = Math.round((evt.getX() / gui.getZoomFactor()) /
                            GRID_SIZE) * GRID_SIZE;
                    int y = Math.round((evt.getY() / gui.getZoomFactor()) /
                            GRID_SIZE) * GRID_SIZE;
                    gui.placeNewWireNode(x, y);
                }
            }
        }
    }//GEN-LAST:event_mouseClicked
    
    private void mouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseReleased
        if (gui.isSimulating()) { return; }
        
        if (mousePressLeft) {
            
            if (selecting) {
                dragEndLocation = Util.toStdCoords(
                        evt.getPoint(), gui.getZoomFactor());

                int x = Math.min(dragStartLocation.x, dragEndLocation.x);
                int y = Math.min(dragStartLocation.y, dragEndLocation.y);
                int w = Math.abs(dragStartLocation.x - dragEndLocation.x);
                int h = Math.abs(dragStartLocation.y - dragEndLocation.y);

                Collection sel = getDisplayables(new Rectangle(x, y, w, h));

                Iterator iter = sel.iterator();
                while (iter.hasNext()) {
                    Displayable d = (Displayable)iter.next();
                    if (d.isSelected() && shiftClick) {
                        gui.removeFromSelection(d);
                    } else {
                        gui.addToSelection(d);
                    }
                }
                repaint();
            }

            selecting = false;
            shiftClick = false;
            ignoreLeftMouse = false;
        }
    }//GEN-LAST:event_mouseReleased
    
    private void mouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseDragged
        if (gui.isSimulating()) { return; }
        
        if (mousePressLeft && !ignoreLeftMouse) {
            if (selecting) {
                
                dragEndLocation = Util.toStdCoords(evt.getPoint(),
                        gui.getZoomFactor());
                repaint();
                
            } else if (dragStartLocation != null && (
                    (Math.abs(evt.getX() - dragStartLocation.x *
                    gui.getZoomFactor()) >= DRAG_THRESHOLD) ||
                    (Math.abs(evt.getY() - dragStartLocation.y *
                    gui.getZoomFactor()) >= DRAG_THRESHOLD))) {

                dragEndLocation = Util.toStdCoords(evt.getPoint(),
                        gui.getZoomFactor());
                selecting = true;

                gui.setComponentToAdd(null);
                gui.cancelWire();
                repaint();
            }
        }
    }//GEN-LAST:event_mouseDragged
    
    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        int clickX = (int)(evt.getX() / gui.getZoomFactor());
        int clickY = (int)(evt.getY() / gui.getZoomFactor());

        if (gui.isSimulating()) { return; }
        
        if (Util.isLeftClick(evt)) {
            mousePressLeft = true;
            mousePressRight = false;
        } else if (Util.isRightClick(evt)) {
            mousePressLeft = false;
            mousePressRight = true;
        }
        
        // ---- LEFT CLICK ----
        if (mousePressLeft) {
            // check if a wire was clicked on
            WireImage wireImage = getWireAt(clickX, clickY);
            if (wireImage != null) {

                ignoreLeftMouse = false;

                if (!gui.isCreatingWire()) {
                    if (!Util.isShiftClick(evt)) {
                        // shift not being held
                        if (!wireImage.isSelected()) {
                            gui.select(wireImage);
                        }
                    } else {
                        // shift being held
                        if (!wireImage.isSelected()) {
                            // the wire isnt selected, select it
                            gui.addToSelection(wireImage);
                        } else {
                            // the wire is selected, unselect it
                            gui.removeFromSelection(wireImage);
                            // ignore left mousedragged events
                            ignoreLeftMouse = true;
                        }
                    }
                }

                // don't start a selection box now
                dragStartLocation = null;
                repaint();
            } else {

                // if shift is not being held, 
                // then clear the current selection
                if (!Util.isShiftClick(evt) && !gui.isSelectionEmpty()) {
                    gui.clearSelection();
                    repaint();
                } else {
                    shiftClick = true;
                }

                // possibly start dragging a selection box
                dragStartLocation = Util.toStdCoords(evt.getPoint(),
                        gui.getZoomFactor());
            }
        // ---- RIGHT CLICK ----
        } else if (mousePressRight) {
            // cancel adding components and creating wires
            if (gui.isAddingComponent()) gui.setComponentToAdd(null);
            if (gui.isCreatingWire()) gui.cancelWire();
                
            // check if a wire was clicked on
            WireImage wireImage = getWireAt(clickX, clickY);
            if (wireImage != null) {

                // find the segment that was clicked on
                int segment = getWireSegmentAt(wireImage, clickX, clickY);
                if (segment > -1) {
                    // create the new node
                    int nodeX = Math.round(clickX / GRID_SIZE) * GRID_SIZE;
                    int nodeY = Math.round(clickY / GRID_SIZE) * GRID_SIZE;
                    gui.insertWireNode(wireImage, segment, nodeX, nodeY);
                    repaint();
                }
            }
        }
    }//GEN-LAST:event_mousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /** the <code>Gui</code> frame containing this gui component */
    private Gui gui;
    /** the collection of wireImage objects */
    private ArrayList wires;
    /** the location at which the drag selection started */
    private Point dragStartLocation;
    /** the location at which the drag selection ended */
    private Point dragEndLocation;
    /** flag indicating that a drag selection is being made */
    private boolean selecting = false;
    /** flag indicating that shift was held during last mousePressed event */
    private boolean shiftClick = false;
    /** flag indicating that left mouseDragged events should be ignored */
    private boolean ignoreLeftMouse = false;
    /** flag indicating the mouse event was initially a left click */
    private boolean mousePressLeft = false;
    /** flag indicating the mouse event was initially a right click */
    private boolean mousePressRight = false;
    /** flag indicating that the grid should be drawn */
    private boolean showGrid = false;
    /** the time in milliseconds of the last repaint */
    private long lastPaintTime = 0;
    /** the timer for repainting with high quality */
    private Timer qualityRefreshDeamon;
    /** the paint quality that should be used on next repaint */
    private int nextPaintQuality = DRAFT_PAINT;
    /** the paint quality that was used on last repaint */
    private int lastPaintQuality = DRAFT_PAINT;
    /** flag indicating the paint function is currently being executing */
    private boolean currentlyPainting = false;
    
    /** the grid size in standard units */
    public static final int GRID_SIZE = 10;
    /** the minimum distance the mouse must be dragged before selection begins
     * (in screen coordinates) */
    private static final int DRAG_THRESHOLD = 10;
    /** max click distance from a wire segment for selecting it (in screen
     * coordinates) */
    private static final float WIRE_CLICK_TOLERANCE = 6.0f;
    /** the type for a low quality repaint operation */
    private static final int DRAFT_PAINT = 0;
    /** the type for a high quality repaint operation */
    private static final int HI_QUALITY_PAINT = 1;
    /** time in milliseconds before an automatic high quality repaint */
    private static final long HI_QUALITY_REFRESH_DELAY = 100;
    /** background color in design mode */
    private static final Color designColor = Color.white;
    /** background color in simulation mode */
    private static final Color simulationColor = new Color(232, 232, 232);
    /** a 200x200 image of the grid, drawn tiled */
    private Image gridImage;
    /** color for filling the selection box */
    private static final Color selectionFillColor = new Color(200,200,255,128);
    /** color for the edges of the selection box */
    private static final Color selectionEdgeColor = Color.blue;
    /** stroke for drawing the selection box */
    private static final Stroke selectionStroke = new BasicStroke(1.0f);
    /** color for a new wire */
    private static final Color newWireColor = Color.lightGray;
    /** stroke for drawing a selected wire */
    private static final Stroke selectedWireStroke = new BasicStroke(
            1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 8f,
            new float[] { 4f, 3f }, 1);
    /** stroke for drawing a wire that isnt selected */
    private static final Stroke wireStroke = new BasicStroke(1.0f);
}
