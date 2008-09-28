package net.happygiraffe.jslint;

import java.io.BufferedReader;
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

    private final JSLint lint;

    private Main() throws IOException {
        lint = new JSLint();
    }

    private void addOption(Option o) {
        lint.addOption(o.getInstance(null));
    }

    private void die(String message) {
        System.err.println(PROGNAME + ": " + message);
        System.exit(1);
    }

    private void err(String message) {
        System.out.println(PROGNAME + ":" + message);
        setErrored(true);
    }

    private void help() {
        info("usage: jslint [options] file.js ...");
        Option[] values = Option.values();
        int maxOptLen = Option.maximumNameLength();
        for (Option o : values) {
            String fmt = "  --%-" + maxOptLen + "s %s";
            info(String.format(fmt, o.getLowerName(), o.getDescription()));
        }
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
            // Longopt.
            else if (arg.startsWith("--")) {
                try {
                    String opt = arg.substring(2).toUpperCase(
                            Locale.getDefault());
                    addOption(Option.valueOf(opt));
                } catch (IllegalArgumentException e) {
                    die("unknown option " + arg);
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
