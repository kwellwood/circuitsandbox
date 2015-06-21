/*
 * Util.java
 *
 * Created on January 27, 2005, 11:42 AM
 */

package circuitsandbox;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import org.w3c.dom.Node;

/**
 * Contains a collection of stand-alone methods for performing miscellaneous
 * tasks. The <code>Util</code> class never needs to be instantiated because
 * all methods are class methods.
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class Util {

    /**
     * Finds the first <code>node</code> of a given name beneath a given
     * root <code>node</code>. If no matching node is found, <code>null</code>
     * is returned.
     *
     * @param root the root <code>node</code> to search within
     * @param name the name of the <code>node</code>
     *
     * @return the first matching <code>node</code>
     */
    public static Node findFirstNode(Node root, String name) {
        if (root == null) {
            return null;
        }
        else if (root.getNodeType() == Node.ELEMENT_NODE &&
                    (name == null || root.getNodeName().equals(name))) {
            return root;
        }
        else if (root.hasChildNodes()) {
            Node child = root.getFirstChild();
            while (child != null) {
                Node found = findFirstNode(child, name);
                if (found != null) {
                    return found;
                }
                child = child.getNextSibling();
            }
            return null;
        }
        else {
            return null;
        }
    }
    
    /**
     * Finds the first sibling of a given <code>node</code> that matches its
     * type and name. This is used instead of the <code>getNextSibling</code>
     * method because <code>getNextSibling</code> occasionally returns text
     * nodes between <code>nodes</code> of the same type. If no
     * matching <code>node</code> is found, <code>null</code> is returned.
     *
     * @param node the <code>node</code> to begin searching from
     *
     * @return the first matching sibling, otherwise <code>null</code>
     */
    public static Node findNextSameSibling(Node node) {
        if (node == null) { return null; }
        
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == node.getNodeType() &&
                    sibling.getNodeName().equals(node.getNodeName())) {
                return sibling;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }
    
    /**
     * Removes the file extension from a file path.
     *
     * @param path the file path
     *
     * @return a copy of <code>filepath</code> without an extension
     */
    public static String getPathWithoutExtension(String path) {
        if (path.lastIndexOf(".") <= path.lastIndexOf("/")) { return path; }
        return path.substring(0, path.lastIndexOf("."));
    }
    
    /**
     * Returns a path's file extension. If no extension is found, an empty
     * string is returned.
     *
     * @param path a file path
     *
     * @return the extension of file name
     */
    public static String getExtension(String path) {
        if (path.lastIndexOf(".") <= path.lastIndexOf("/")) { return ""; }
        return path.substring(path.lastIndexOf(".") + 1);
    }

    /**
     * Returns a file path with the file extension replaced. If no extension
     * existed, one will be added.
     *
     * @param f the file
     * @param ext the extension, without the dot
     *
     * @return a new path with the desired extension
     */
    public static File replaceExtension(File f, String ext) {
        return new File(getPathWithoutExtension(f.getPath()) + "." + ext);
    }
    
    /**
     * Returns the part of a given <code>file</code>'s path relative to the
     * current working directory. If the <code>file</code> is not relative to
     * the current working directory, <code>null</code> is returned.
     *
     * @param file the file
     *
     * @return the portion of the file's path relative to the current working
     * directory
     */
    static public String getRelativePath(File file) {
        String absolutePath = file.getAbsolutePath();
        String currentPath = new java.io.File(".").getAbsolutePath();
        currentPath = currentPath.substring(0, currentPath.length() - 1);
        
        if (absolutePath.startsWith(currentPath)) {
            return absolutePath.substring(currentPath.length());
        }
        
        return null;
    }
    
    /**
     * Returns <code>true</code> if the right mouse button was involved in a
     * mouse event. A return value of <code>true</code> would mean the right
     * mouse button was clicked, held, or released during the event.
     *
     * @param evt the mouse event to check
     *
     * @return <code>true</code> if the event involves the right mouse button,
     * otherwise <code>false</code>.
     */
    static public boolean isRightClick(MouseEvent evt) {
        return (evt.getButton() == MouseEvent.BUTTON1 && evt.isMetaDown()) ||
                evt.getButton() == MouseEvent.BUTTON3;
    }

    /**
     * Returns <code>true</code> if the left mouse button was involved in a
     * mouse event. A return value of <code>true</code> would mean the left
     * mouse button was clicked, held, or released during the event.
     *
     * @param evt the mouse event to check
     *
     * @return <code>true</code> if the event involves the left mouse button,
     * otherwise <code>false</code>.
     */
    static public boolean isLeftClick(MouseEvent evt) {
        return evt.getButton() == MouseEvent.BUTTON1 && !evt.isMetaDown();
    }
    
    /**
     * Returns <code>true</code> if the shift key was involved in a
     * mouse event. A return value of <code>true</code> would mean the shift
     * key was held during the event.
     *
     * @param evt the mouse event to check
     *
     * @return <code>true</code> if the event involves the shift key
     */
    static public boolean isShiftClick(MouseEvent evt) {
        return (evt.getModifiers() & InputEvent.SHIFT_MASK) != 0;
    }
    
    /**
     * Converts a point from screen coordinates to standard coordinates
     * using the given zoom level.
     *
     * @param p the point to convert
     * @param zoom the magnification level
     *
     * @return the converted point
     */
    static public Point toStdCoords(Point p, float zoom) {
        return new Point((int)(p.x / zoom), (int)(p.y / zoom));
    }

    /**
     * Converts a rectangle from screen coordinates to standard coordinates
     * using the given zoom level.
     *
     * @param r the rectangle to convert
     * @param zoom the magnification level
     *
     * @return the converted rectangle
     */
    static public Rectangle toStdCoords(Rectangle r, float zoom) {
        return new Rectangle((int)(r.x / zoom), (int)(r.y / zoom),
                (int)(r.width / zoom), (int)(r.height / zoom));
    }

}
