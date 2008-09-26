package net.happygiraffe.jslint;

/**
 * An option with an associated boolean value.
 *
 * @author dom
 * @version $Id$
 */
public class BooleanOptionInstance implements OptionInstance {

    private final Option option;

    public BooleanOptionInstance(Option option) {
        this.option = option;
    }

    public Option getOption() {
        return option;
    }

    /**
     * Always returns true.
     */
    public Object getValue() {
        return true;
    }

}
