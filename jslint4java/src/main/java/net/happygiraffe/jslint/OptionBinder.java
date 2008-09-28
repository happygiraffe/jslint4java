package net.happygiraffe.jslint;

/**
 * Provide a way of creating an {@link OptionInstance}.
 *
 * @author dom
 * @version $Id$
 */
public enum OptionBinder {
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
     * Turn a String source into an {@link OptionInstance} by parsing it
     * appropriately.
     */
    public OptionInstance bind(final Option option, String source) {
        final Object value = parse(source);
        return new OptionInstance() {
            public Option getOption() {
                return option;
            }

            public Object getValue() {
                return value;
            }
        };
    }

    protected abstract Object parse(String source);
}
