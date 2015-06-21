/*
 * ModelFileFilter.java
 *
 * Created on February 20, 2005, 10:17 AM
 */

package circuitsandbox;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters out circuit model files (*.csm). This filter works with the
 * Swing file chooser and anything else that uses Java's basic
 * <code>FileFilter</code> interface (<code>java.io.FileFilter</code>).
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class ModelFileFilter extends FileFilter implements java.io.FileFilter {

    /**
     * Tests if file <code>f</code> will be accepted by this filter.
     *
     * @param f the <code>File</code> to be tested
     *
     * @return <code>true</code> if the file is accepted by this filter,
     * otherwise <code>false</code>
     */
    public boolean accept(File f)
    {
        if (f.isDirectory() || Util.getExtension(f.getName()).equals("csm")) {
            return true;
        }
        
        return false;
    }

    /**
     * Returns a short description of the file filter.
     *
     * @return the description
     */
    public String getDescription() {
        return "Circuit Model Files";
    }
}