package net.happygiraffe.jslint;

/**
 * A binding between an option and a value. An implementation must also have a
 * constructor which accepts a single {@link Option}.
 *
 * @author dom
 * @version $Id$
 */
public class OptionBinding {

    private final Option option;
    private final Object value;

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
