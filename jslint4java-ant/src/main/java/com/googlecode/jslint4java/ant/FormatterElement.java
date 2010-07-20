package com.googlecode.jslint4java.ant;

import java.io.File;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;

/**
 * A sub element of {@link JSLintTask}. Used to specify where the output should
 * go. Handles creation of a {@link ResultFormatter}.
 *
 * <h3>Attributes</h3>
 *
 * <dl>
 * <dt><code>type</code></dt>
 * <dd>Either "plain" or "xml" or "netbeans".</dd>
 * <dt><code>destfile</code></dt>
 * <dd>Optional. A file to write the formatters' output to.</dd>
 * </dl>
 *
 * @author dom
 * @version $Id$
 */
public class FormatterElement {

    /**
     * What kind of formatters are available.
     *
     * @author dom
     */
    public static enum Type {
        plain() {
            @Override
            public ResultFormatter getResultFormatter() {
                return new PlainResultFormatter();
            }
        },
        xml() {
            @Override
            public ResultFormatter getResultFormatter() {
                return new XmlResultFormatter();
            }
        };
        abstract ResultFormatter getResultFormatter();
    }

    private Type type;
    private OutputStream defaultOutputStream = System.out;
    private File destFile;

    /**
     * Return a configured {@link ResultFormatter} corresponding to this
     * element.
     */
    public ResultFormatter getResultFormatter() {
        if (type == null) {
            throw new BuildException("you must specify type");
        }
        ResultFormatter rf = type.getResultFormatter();
        rf.setStdout(defaultOutputStream);
        if (destFile != null) {
            rf.setFile(destFile);
        }
        return rf;
    }

    /**
     * Pass in the value for the default output stream (used when no file is
     * specified).
     *
     * @param defaultOutputStream
     */
    void setDefaultOutputStream(OutputStream defaultOutputStream) {
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
