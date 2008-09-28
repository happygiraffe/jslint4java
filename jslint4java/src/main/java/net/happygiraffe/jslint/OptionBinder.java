package net.happygiraffe.jslint;

/**
 * Provide a way of creating an {@link OptionBinding}.
 *
 * @author dom
 * @version $Id$
 */
public enum OptionBinder {
    /**
     * An option with a boolean value. We count null as a true value because the
     * presence of the option implies it.
     */
    BOOLEAN {
        @Override
        protected Object parse(String source) {
            // XXX Temp hack.
            if (source == null) {
                return true;
            }
            return Boolean.valueOf(source);
        }
    };

    /**
     * Turn a String <i>source</i> into an {@link OptionBinding} by parsing it
     * appropriately.
     */
    public OptionBinding bind(Option option, String source) {
        return new OptionBinding(option, parse(source));
    }

    protected abstract Object parse(String source);
}
