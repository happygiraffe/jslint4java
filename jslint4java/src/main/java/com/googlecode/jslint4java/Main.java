package com.googlecode.jslint4java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A command line interface to {@link JSLint}.
 *
 * @author dom
 * @version $Id$
 */
public class Main {

    private static final String PROGNAME = "jslint";

    /**
     * The main entry point. Try passing in "--help" for more details.
     *
     * @param args
     *            One or more JavaScript files.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        List<String> files = main.processOptions(args);
        if (files.size() == 0) {
            main.help();
        }
        for (String file : files) {
            main.lintFile(file);
        }
        System.exit(main.isErrored() ? 1 : 0);
    }

    private boolean errored = false;

    private JSLint lint;

    private Main() throws IOException {
        lint = new JSLintBuilder().fromDefault();
    }

    private void die(String message) {
        System.err.println(PROGNAME + ": " + message);
        System.exit(1);
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
            info(String.format(fmt, o.getLowerName(), o.getDescription()));
        }
        info("");
        info(String.format(fmt, "help", "Show this help"));
        info("");
        info("using jslint version " + lint.getEdition());
        System.exit(0);
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
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            List<Issue> issues = lint.lint(file, reader);
            for (Issue issue : issues) {
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
        boolean inFiles = false;
        List<String> files = new ArrayList<String>();
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
                String[] bits = arg.substring(2).split("=", 2);
                if (bits.length != 2) {
                    die("Must specify file with --jslint=/some/where/jslint.js");
                }
                try {
                    // TODO Don't wipe out existing options that have been set.
                    lint = new JSLintBuilder().fromFile(new File(bits[1]));
                } catch (IOException e) {
                    die(e.getMessage());
                }
            }
            // Longopt.
            else if (arg.startsWith("--")) {
                String[] bits = arg.substring(2).split("=", 2);
                try {
                    Option o = getOption(bits[0]);
                    if (o == null) {
                        die("unknown option " + arg);
                    }
                    if (bits.length == 2) {
                        lint.addOption(o, bits[1]);
                    } else {
                        lint.addOption(o);
                    }
                } catch (IllegalArgumentException e) {
                    die(bits[0] + ": " + e.getClass().getName() + ": "
                            + e.getMessage());
                }
            }
            // File
            else {
                inFiles = true;
                files.add(arg);
            }
        }
        return files;
    }

    private void setErrored(boolean errored) {
        this.errored = errored;
    }

}
