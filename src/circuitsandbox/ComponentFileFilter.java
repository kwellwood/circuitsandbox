/*
 * ComponentFileFilterr.java
 *
 * Created on February 10, 2005, 7:49 PM
 */

package circuitsandbox;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters out custom component files (*.csc). This filter works with the
 * Swing file chooser and anything else that uses Java's basic
 * <code>FileFilter</code> interface (<code>java.io.FileFilter</code>).
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class ComponentFileFilter extends FileFilter
        implements java.io.FileFilter {
    
    /**
     * Tests if file <code>f</code> will be accepted by this filter.
     *
     * @param file the <code>File</code> to be tested
     *
     * @return <code>true</code> if the file is accepted by this filter,
     * otherwise <code>false</code>
     */
    public boolean accept(File file) {
        if (file.isDirectory() ||
                Util.getExtension(file.getName()).equals("csc")) {
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
        return "Custom Component File (*.csc)";
    }
    
}
