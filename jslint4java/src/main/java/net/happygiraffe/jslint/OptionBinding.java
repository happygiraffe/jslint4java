package net.happygiraffe.jslint;

/**
 * A binding between an option and a value.
 *
 * @author dom
 * @version $Id$
 */
public class OptionBinding {

    private final Option option;
    private final Object value;

    /**
     * Create a new binding.
     */
    public OptionBinding(Option option, Object value) {
        this.option = option;
        this.value = value;
    }

    /**
     * The option that this instance refers to.
     */
    public Option getOption() {
        return option;
    }

    /**
     * The value of the option.
     */
    public Object getValue() {
        return value;
    }
}
