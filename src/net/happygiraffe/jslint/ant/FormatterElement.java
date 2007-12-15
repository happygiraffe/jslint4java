package net.happygiraffe.jslint.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;

/**
 * A sub element of {@link JSLintTask}. Used to specify where the output should
 * go. Handles creation of a {@link ResultFormatter}.
 *
 * @author dom
 * @version $Id$
 */
public class FormatterElement {

    /**
     * What kind of formatters are available.
     *
     * @author dom
     *
     */
    public static enum Type {
        plain, xml;
    }

    private Type type;
    private OutputStream defaultOutputStream = System.out;
    private File destFile;

    /**
     * Return an output stream for the destFile.
     *
     * @return
     */
    private OutputStream getFileOutputStream() {
        try {
            System.out.println("Writing errors to " + destFile);
            // XXX encoding?
            return new FileOutputStream(destFile);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Return a configured {@link ResultFormatter} corresponding to this
     * element.
     *
     * @return
     */
    public ResultFormatter getResultFormatter() {
        if (type == null)
            throw new BuildException("you must specify type");
        ResultFormatter rf = null;
        switch (type) {
        case plain:
            rf = new PlainResultFormatter();
            break;
        case xml:
            throw new RuntimeException("Unimplemented");
        }
        if (destFile != null) {
            rf.setOut(getFileOutputStream());
        } else {
            rf.setOut(defaultOutputStream);
        }
        return rf;
    }

    /**
     * Pass in the value for the default output stream (used when no file is
     * specified).
     *
     * @param defaultOutputStream
     */
    public void setDefaultOutputStream(OutputStream defaultOutputStream) {
        this.defaultOutputStream = defaultOutputStream;
    }

    /**
     * Specifies the location of the report file.
     *
     * @param destFile
     */
    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }

    /**
     * Specify the type of this formatter.
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

}
