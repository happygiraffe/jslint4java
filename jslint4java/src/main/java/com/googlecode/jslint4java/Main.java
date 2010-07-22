package com.googlecode.jslint4java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A command line interface to {@link JSLint}.
 *
 * @author dom
 * @version $Id$
 */
public class Main {

    /** Just a useful utility class. Should probably be top-level. */
    private static class Pair<A, B> {
        public final A a;
        public final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public static <A, B> Pair<A, B> of(A a, B b) {
            return new Pair<A, B>(a, b);
        }
    }

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

    /**
     * Apply a set of options to the current JSLint.
     */
    private void applyOptions(Map<Option, String> options) {
        for (Entry<Option, String> entry : options.entrySet()) {
            String value = entry.getValue();
            try {
                if (value == null) {
                    lint.addOption(entry.getKey());
                } else {
                    lint.addOption(entry.getKey(), value);
                }
            } catch (IllegalArgumentException e) {
                String optName = entry.getKey().getLowerName();
                die("--" + optName + ": " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
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

    /**
     * Parse {@code arg} into two components, separated by equals. If there is
     * no second component, the value will be {@code null}.
     */
    private Pair<String, String> parseArgAndValue(String arg) {
        String[] bits = arg.substring(2).split("=", 2);
        if (bits.length == 2) {
            return Pair.of(bits[0], bits[1]);
        } else {
            return Pair.of(bits[0], null);
        }
    }

    private List<String> processOptions(String[] args) {
        boolean inFiles = false;
        List<String> files = new ArrayList<String>();
        Map<Option, String> options = new HashMap<Option, String>();
        for (String arg : args) {
            if (inFiles) {
                files.add(arg);
            }
            // End of arguments.
            else if ("--".equals(arg)) {
                inFiles = true;
                continue;
            }
            // Hayelp!
            else if ("--help".equals(arg)) {
                help();
            }
            // Specify an alternative jslint.
            else if (arg.startsWith("--jslint")) {
                Pair<String, String> pair = parseArgAndValue(arg);
                if (pair.b == null) {
                    die("Must specify file with --jslint=/some/where/jslint.js");
                }
                try {
                    lint = new JSLintBuilder().fromFile(new File(pair.b));
                } catch (IOException e) {
                    die(e.getMessage());
                }
            }
            // Longopt.
            else if (arg.startsWith("--")) {
                Pair<String, String> pair = parseArgAndValue(arg);
                if (pair.a.equals("encoding")) {
                    setEncoding(pair.b);
                } else {
                    Option o = getOption(pair.a);
                    if (o == null) {
                        die("unknown option " + arg);
                    }
                    options.put(o, pair.b);
                }
            }
            // File
            else {
                inFiles = true;
                files.add(arg);
            }
        }
        applyOptions(options);
        return files;
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
