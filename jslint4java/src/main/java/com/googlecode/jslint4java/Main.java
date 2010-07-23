package com.googlecode.jslint4java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Locale;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;

/**
 * A command line interface to {@link JSLint}.
 *
 * @author dom
 * @version $Id$
 */
public class Main {

    /**
     * This is just to avoid calling {@link System#exit(int)} outside of main()…
     */
    @SuppressWarnings("serial")
    private static class DieException extends RuntimeException {
        private final int code;

        public DieException(String message, int code) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private static final String PROGNAME = "jslint";

    /**
     * The main entry point. Try passing in "--help" for more details.
     *
     * @param args
     *            One or more JavaScript files.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            Main main = new Main();
            List<String> files = main.processOptions(args);
            if (files.size() == 0) {
                main.help();
            }
            for (String file : files) {
                main.lintFile(file);
            }
            System.exit(main.isErrored() ? 1 : 0);
        } catch (DieException e) {
            if (e.getMessage() != null) {
                System.err.println(PROGNAME + ": " + e.getMessage());
            }
            System.exit(e.getCode());
        }
    }

    private boolean errored = false;

    private JSLint lint;

    private Charset encoding = Charset.defaultCharset();

    private Main() throws IOException {
        lint = new JSLintBuilder().fromDefault();
    }

    private void die(String message) {
        throw new DieException(message, 1);
    }

    private void err(String message) {
        System.out.println(PROGNAME + ":" + message);
        setErrored(true);
    }

    /**
     * Fetch the named {@link Option}, or null if there is no matching one.
     */
    private Option getOption(String optName) {
        try {
            return Option.valueOf(optName.toUpperCase(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void help() {
        info("usage: jslint [options] file.js ...");
        String fmt = "  --%-" + Option.maximumNameLength() + "s %s";
        for (Option o : Option.values()) {
            String name = o.getLowerName();
            if (o.getType() != Boolean.class) {
                name = name + "=";
            }
            info(String.format(fmt, name, o.getDescription()));
        }
        info("");
        info(String.format(fmt, "encoding=", "Specify the input encoding"));
        info(String.format(fmt, "help", "Show this help"));
        info(String.format(fmt, "jslint=", "Specify an alternative version of jslint.js"));
        info("");
        info("using jslint version " + lint.getEdition());
        throw new DieException(null, 0);
    }

    private void info(String message) {
        System.out.println(message);
    }

    private boolean isErrored() {
        return errored;
    }

    private void lintFile(String file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            JSLintResult result = lint.lint(file, reader);
            for (Issue issue : result.getIssues()) {
                err(issue.toString());
            }
        } catch (FileNotFoundException e) {
            die(file + ": No such file or directory.");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private List<String> processOptions(String[] args) {
        Flags flags = new Flags();
        JSLintFlags jslintFlags = new JSLintFlags();
        JCommander jc = new JCommander(new Object[] { flags, jslintFlags }, args);
        if (flags.help) {
            jc.usage();
            info("");
            info("using jslint version " + lint.getEdition());
            throw new DieException(null, 0);
        }
        if (flags.encoding != null) {
            setEncoding(flags.encoding);
        }
        if (flags.jslint != null) {
            setJSLint(flags.jslint);
        }
        for (ParameterDescription pd : jc.getParameters()) {
            Field field = pd.getField();
            // Is it declared on JSLintFlags?
            if (!field.getDeclaringClass().isAssignableFrom(JSLintFlags.class)) {
                continue;
            }
            // Need to get Option.
            Option o = getOption(field.getName());
            // Need to get value.
            Class<?> type = field.getType();
            if (type.isAssignableFrom(Boolean.class)) {
                lint.addOption(o);
            } else if (type.isAssignableFrom(String.class)) {
                try {
                    String val = (String) field.get(jslintFlags);
                    lint.addOption(o, val);
                } catch (IllegalArgumentException e) {
                    die(e.getMessage());
                } catch (IllegalAccessException e) {
                    die(e.getMessage());
                }
            } else {
                die("unknown type \"" + type + "\" (for " + field.getName() + ")");
            }
        }
        return flags.files;
    }

    private void setJSLint(String jslint) {
        try {
            lint = new JSLintBuilder().fromFile(new File(jslint));
        } catch (IOException e) {
            die(e.getMessage());
        }
    }

    private void setEncoding(String name) {
        try {
            encoding = Charset.forName(name);
        } catch (IllegalCharsetNameException e) {
            die("unknown encoding '" + name + "'");
        } catch (UnsupportedCharsetException e) {
            die("unknown encoding '" + name + "'");
        }
    }

    private void setErrored(boolean errored) {
        this.errored = errored;
    }

}
