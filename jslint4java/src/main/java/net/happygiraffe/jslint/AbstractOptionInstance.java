package net.happygiraffe.jslint;

/**
 * Base class for an {@link Option} binding.
 *
 * @author dom
 * @version $Id$
 */
public abstract class AbstractOptionInstance implements OptionInstance {

    private final Option option;

    /**
     * Create a new instance and associate it with <i>option</i>.
     */
    public AbstractOptionInstance(Option option) {
        this.option = option;
    }

    /**
     * Return the option associated with this instance.
     */
    public Option getOption() {
        return option;
    }

}
