package net.happygiraffe.jslint;

/**
 * An option with an associated boolean value.
 *
 * @author dom
 * @version $Id$
 */
public class BooleanOptionInstance extends AbstractOptionInstance implements OptionInstance {

    public BooleanOptionInstance(Option option) {
        super(option);
    }

    /**
     * Always returns true.
     */
    public Object getValue() {
        return true;
    }

}
