/*
 * PNGFileFilter.java
 *
 * Created on April 21, 2005, 10:27 PM
 */

package circuitsandbox;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A FileFilter class for filtering out .PNG image files. This filter works
 * with the Swing file chooser and anything else that uses Java's basic
 * FileFilter interface (java.io.FileFilter).
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class PNGFileFilter extends FileFilter implements java.io.FileFilter {
    
    /**
     * Tests if file <CODE>f</CODE> will be accepted by this filter.
     * @param f the <CODE>File</CODE> to be tested
     * @return <CODE>true</CODE> if the file is accepted by this filter,
     * otherwise <CODE>false</CODE>.
     */
    public boolean accept(File f)
    {
        if (f.isDirectory()) return true;
        String extension = Util.getExtension(f.getName());
        if (extension != null && extension.equals("png")) return true;
        return false;
    }

    /**
     * Gets a short description of this file filter.
     * @return the description of this file filter class.
     */
    public String getDescription() {
        return "PNG Files";
    }
}
