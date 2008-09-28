package net.happygiraffe.jslint;

/**
 * A binding between an option and a value. An implementation must also have a
 * constructor which accepts a single {@link Option}.
 *
 * @author dom
 * @version $Id$
 */
public interface OptionBinding {

    /**
     * The option that this instance refers to.
     */
    Option getOption();

    /**
     * The value of the option.
     */
    Object getValue();
}
